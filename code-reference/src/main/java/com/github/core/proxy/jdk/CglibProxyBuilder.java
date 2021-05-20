package com.github.core.proxy.jdk;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Cglib 万能代理
 *
 * @author 欧阳洁
 * @date 2021/5/20 22:52
 */
public class CglibProxyBuilder<T> implements MethodInterceptor {

    private T target;
    private Consumer<T> before;
    private Consumer<T> after;

    private CglibProxyBuilder(T target, Consumer<T> before, Consumer<T> after) {
        this.target = target;
        this.before = before;
        this.after = after;
    }

    /**
     * 通用代理方法实现
     *
     * @param object 目标对象代理类的实例
     * @param method 代理实例上 调用父类方法的Method实例
     * @param args   传入到代理实例上方法参数值的对象数组
     * @param methodProxy   使用它调用父类的方法
     * @return 方法的返回值，没有返回值是 null
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Optional.ofNullable(before).ifPresent(consumer -> consumer.accept(target));
        Object result = method.invoke(target, args);
        Optional.ofNullable(after).ifPresent(consumer -> consumer.accept(target));
        return result;
    }

    public static <T> T newProxy(T target, Consumer<T> before, Consumer<T> after) {
        MethodInterceptor handler = new CglibProxyBuilder(target, before, after);
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(handler);
        enhancer.setSuperclass(target.getClass());
        T proxy = (T) enhancer.create();
        return proxy;
    }
}