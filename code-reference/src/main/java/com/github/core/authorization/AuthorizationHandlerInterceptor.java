package com.github.core.authorization;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.github.core.modules.cache.SimpleCacheManage;
import org.github.core.modules.mapper.JsonMapper;
import org.github.core.modules.utils.Identities;
import org.github.core.modules.utils.Reflections;
import org.github.core.modules.web.Servlets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 权限拦截器
 *
 * @author 章磊
 */
public class AuthorizationHandlerInterceptor extends HandlerInterceptorAdapter {
    protected final Logger logger = LoggerFactory.getLogger(AuthorizationHandlerInterceptor.class);
    //授权接口，由使用者实现
    private Authrizer authrizer;
    private Traffic traffic;
    @Autowired
    private SimpleCacheManage ehcacheManage;
    //EHCACHE缓存的名称
    private String cacheName;

    private FunctionLogHandler functionLogHandler;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String methodName = Servlets.getMethodNameByRequest(request);
        //重复提交检查
        if (!checkToken(request, response, handler, methodName)) {
            return false;
        }
        if (!authrizer.isLogin(request, response)) {
            return false;
        }
        String url = Servlets.getRequestUriWithoutParm(request);// 去掉请求参数后的路径
        //校验用户流量 begin
        if (traffic != null) {
            traffic.checkTrafficOverProof(url);
        }
        //校验用户流量 end

        if (Reflections.getAnnotation(handler.getClass(), Permission.class) != null) {
            return super.preHandle(request, response, handler);
        }
        Controller controller = Reflections.getAnnotation(handler.getClass(), Controller.class);
        if (controller == null) {
            return super.preHandle(request, response, handler);
        }
        String moduleNo = controller.mkbh();
        if (StringUtils.isBlank(moduleNo)) {
            throw new AuthorizationException("必须在Controller配置模块编号[mkbh]!");
        }

        request.setAttribute("moduleNo", moduleNo);//存放功能信息，页面需要使用这个来控制按钮显示

        if (StringUtils.isBlank(methodName)) {
            throw new AuthorizationException("非法请求!");
        }

        AuthorizationInfo authorizationInfo =
                (AuthorizationInfo) ehcacheManage.get(cacheName, request.getSession().getId() + "_" + moduleNo);
        try {
            //当前模块编号的所有功能没有缓存的时候，要重新获取权限
            //根据sessionid和模块编号进行缓存，没有找到说明切换了模块要重新获取数据，找到了那么就直接从缓存里面判断，避免多次查询数据库
            if (authorizationInfo == null) {
                authorizationInfo = new AuthorizationInfo(moduleNo);
                authrizer.getFunction(authorizationInfo);
                // authorizationInfo.saveModule();
                ehcacheManage.put(cacheName, request.getSession().getId() + "_" + moduleNo, authorizationInfo, 600);
            }
            String json = JsonMapper.nonEmptyMapper().toJson(authorizationInfo.getModuleMap());
            request.setAttribute("_functionNos", json);    //存放功能信息，页面需要使用这个来控制按钮显示
            request.setAttribute("functionMap", authorizationInfo.getFunctionMap());    //存放功能信息，页面需要使用这个来控制按钮显示

        } catch (Exception e) {
            logger.error("模块功能权限获取错误", e);
        }
        if (methodName.startsWith("view")) {//访问jsp不拦截
            return super.preHandle(request, response, handler);
        }
        Method method = Reflections.getAccessibleMethodByName(handler, methodName);
        if (method == null) {
            throw new AuthorizationException("非法请求,无效的方法!");
        }
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        String gnbh = requestMapping.gnbh();
        String sjbh = requestMapping.sjbh();
        String gnmc = requestMapping.gnmc();
        String gnfun = handler.getClass().getSimpleName() + "." + method.getName();

        if (StringUtils.isBlank(gnbh)) {
            throw new AuthorizationException("必须在RequestMapping配置功能编号[gnbh]!");
        }
        request.setAttribute("functionNo", gnbh);

        //自动维护功能编号
        //authrizer.saveRep_Mkgn(moduleNo, gnbh, gnmc, gnfun, sjbh);
        try {
            /**
             * 根据controller的所在模块编号获取所有有权限的功能编号，然后再根据请求的方法上面的功能编号判断是否有权限
             */
            logger.debug("当前URL:" + url);
            logger.debug("当前模块编号:" + moduleNo);
            logger.debug("当前功能编号:" + gnbh);
            logger.debug("当前有权限的功能:" + authorizationInfo.getModuleMap());
            //只有上级编号为none的才进行权限检查
            if ("none".equals(sjbh)) {
                if (!authorizationInfo.has(gnbh)) {
                    throw new AuthorizationException("请求的" + url + "没有权限,请检查权限是否分配");
                }
            } else if (!"00".equals(sjbh)) {//如果是子功能，则需要判断上级功能是否有权限
                if (!authorizationInfo.has(sjbh)) {
                    throw new AuthorizationException("请求的" + url + "没有权限,请检查权限是否分配");
                }
            }
        } catch (AuthorizationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("判断功能权限错误", e);

        }
        if (functionLogHandler != null) {
            functionLogHandler
                    .createLog(request, response, handler, moduleNo, gnbh, authorizationInfo.getFunction(gnbh));
        }
        return super.preHandle(request, response, handler);
    }

    /**
     * 每次加载都会在request中放置一个vetoken的属性，在下次提交的时候需要带入
     * <p>
     * key中有controller的类名，必须保证，上一个页面的controller和保存的是同一个
     *
     * @param request
     *
     * @throws AuthorizationException
     */
    private boolean checkToken(HttpServletRequest request, HttpServletResponse response, Object handler,
                               String methodName) throws AuthorizationException, IOException {
        String vetokenkey = "VETOKEN_" + handler.getClass().getSimpleName() + "_";
        //判断是否重复提交
        String vetoken = request.getParameter("vetoken");
        //指定控制controller中方法的名字
        String vetokenmethod = request.getParameter("vetokenmethod");
        if (StringUtils.isNotBlank(vetoken)) {
            if (StringUtils.isBlank(vetokenmethod) || vetokenmethod.equalsIgnoreCase(methodName)) {
                String cacheVetoken = (String) ehcacheManage.get("HOT_DATA_CACHE", vetokenkey + vetoken);
                if (StringUtils.isBlank(cacheVetoken)) {
                    String vetokenurl = request.getParameter("vetokenurl");//重复提交跳转的页面
                    if (StringUtils.isNotBlank(vetokenurl)) {
                        response.sendRedirect(vetokenurl);
                        return false;
                    }
                    String vetokenerror = request.getParameter("vetokenerror");//重复提交的提示文字
                    if (StringUtils.isBlank(vetokenerror)) {
                        throw new AuthorizationException("重复请求!");
                    } else {
                        throw new AuthorizationException(vetokenerror);
                    }
                }
                ehcacheManage.remove("HOT_DATA_CACHE", vetokenkey + vetoken);
            }
        }
        //产生新的token
        vetoken = Identities.uuid2();
        request.setAttribute("vetoken", vetoken);
        ehcacheManage.put("HOT_DATA_CACHE", vetokenkey + vetoken, vetoken, 60 * 60);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        if (functionLogHandler != null) {
            //写日志
            functionLogHandler.submit();
        }
        super.afterCompletion(request, response, handler, ex);

        /**
         * 移除异动日志当前会话变量。
         * web容器底层的线程采用线程池方式，导致可能出现多次会话操作被分配到同一个线程，
         * 异动日志没有显示地remove线程绑定变量，导致异动日志查询结果有误。
         */
        //        T_table_czrzUtils.removeThreadId();
    }

    public void setAuthrizer(Authrizer authrizer) {
        this.authrizer = authrizer;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public void setFunctionLogHandler(FunctionLogHandler functionLogHandler) {
        this.functionLogHandler = functionLogHandler;
    }

    public void setTraffic(Traffic traffic) {
        this.traffic = traffic;
    }
}
