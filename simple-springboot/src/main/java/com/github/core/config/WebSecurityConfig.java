package com.github.core.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.github.core.auth.AppUserDetailsService;
import com.github.core.utils.SystemUtil;

/**
 * 用户登陆相关安全配置
 *
 * @author 欧阳洁
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 全局忽略
     */
    private static final String IGNORURISTR = "/static/**,/**/*.ico,/**/*.css,/**/*.js,/**/open/**,/error,/login/verifyCode,/login/verifyCodeCheck";
    /**
     * 设置cookie有效期 1天
     */
    private static final int tokenSecond = 60 * 60 * 24;
    /**
     * 获取用户信息
     */
    @Autowired
    private AppUserDetailsService detailsService;
    /**
     * 登陆成功跳转的地址
     */
    @Value("${web.loginsuccess.uri}")
    private String webLoginSuccessUri;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] ignorUri = IGNORURISTR.split(",");
        http.authorizeRequests()
                // 访问：ignorUri 配置的 无需登录认证权限
                .antMatchers(ignorUri).permitAll()
                // 其他所有资源都需要认证，登陆后访问
                .anyRequest().authenticated()
                // 登陆后之后拥有“ADMIN”权限才可以访问 /admin/index 方法，否则系统会出现“403”权限不足的提示
                // .antMatchers("/admin/index").hasAuthority("ADMIN")
                // .antMatchers("/admin/**").access("hasRole('ADMIN')")
                // 指定登录页是”/login”
                .and().formLogin().loginPage("/login")
                .permitAll()
                // 登录成功后可使用loginSuccessHandler()存储用户信息，可选。
                .successHandler(new RestAuthenticationSuccessHandler())
                .and()
                .logout().logoutUrl("/logout").logoutSuccessHandler(new RestLogoutSuccessHandler())
                .permitAll()
                // .invalidateHttpSession(true)
                .and()
                .rememberMe()//登录后记住用户，下次自动登录,数据库中必须存在名为persistent_logins的表
                .tokenValiditySeconds(tokenSecond);
        // 由于使用的是JWT，我们这里不需要csrf
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.httpBasic();
        // 添加 JWT filter
        // http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        http.headers().cacheControl();
        // 禁用缓存
        http.headers().contentTypeOptions().disable();
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("111111").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("dba").password("123456").roles("ADMIN","DBA");
    }

    /**
     * 自定义 DaoAuthenticationProvider
     *
     * @return DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(detailsService);
        provider.setPasswordEncoder(new PasswordMd5());
        return provider;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用简单的MD5
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     * 登出成功后的处理
     */
    public static class RestLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            deleteAccessAuthority(request, response, authentication);
            setDefaultTargetUrl("/login");
            super.onLogoutSuccess(request, response, authentication);
        }

        /**
         * 删除缓存里面的用户权限
         *
         * @param request        request
         * @param response       response
         * @param authentication 权限对象
         */
        private void deleteAccessAuthority(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
            try {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String username = userDetails.getUsername();

                // 删除缓存
            } catch (Exception exception) {
            }
        }
    }

    /**
     * 权限不通过的处理
     */
    public static class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + authException.getMessage());
        }
    }

    /**
     * 登陆成功后的处理
     */
    public class RestAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
            String ip = SystemUtil.getRemoteIp(request);
            logger.info(ip + "登陆成功");
            setDefaultTargetUrl(webLoginSuccessUri);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // 用 UserDetail 做一些其他操作
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}