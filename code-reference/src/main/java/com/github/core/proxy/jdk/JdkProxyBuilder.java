package com.github.core.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * JDK万能代理
 *
 * @author 欧阳洁
 * @date 2021/5/20 22:28
 */
public class JdkProxyBuilder<Interface, T extends Interface> implements InvocationHandler {

    private T target;
    private Consumer<T> before;
    private Consumer<T> after;

    private JdkProxyBuilder(T target, Consumer<T> before, Consumer<T> after) {
        this.target = target;
        this.before = before;
        this.after = after;
    }

    /**
     * 通用代理方法实现
     *
     * @param proxy  目标对象的代理类实例
     * @param method 对应于在代理实例上调用接口方法的 Method 实例
     * @param args   传入到代理实例上方法参数值的对象数组
     * @return 方法的返回值，没有返回值是 null
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Optional.ofNullable(before).ifPresent(consumer -> consumer.accept(target));
        Object result = method.invoke(target, args);
        Optional.ofNullable(after).ifPresent(consumer -> consumer.accept(target));
        return result;
    }

    public static <Interface, T extends Interface> Interface newProxy(T target, Consumer<T> before, Consumer<T> after) {
        InvocationHandler handler = new JdkProxyBuilder(target, before, after);
        ClassLoader loader = handler.getClass().getClassLoader();
        Class<?>[] interfaces = target.getClass().getInterfaces();
        Interface proxy = (Interface) Proxy.newProxyInstance(loader, interfaces, handler);
        return proxy;
    }
}