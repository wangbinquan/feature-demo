package org.wbq.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class MyInterceptor implements MethodInterceptor {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = args.length; i < length; i++){
            sb.append(args[i].toString());
            if (length - i > 1) {
                sb.append(", ");
            }
        }
        LOG.info("Before[" + obj.getClass() + "][" + method + "]");
        Object result = proxy.invokeSuper(obj, args);
        LOG.info("After[" + obj.getClass() + "][" + method + "][" + result + "]");
        return result;
    }
}
