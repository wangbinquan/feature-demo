package org.wbq.cglib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.cglib.proxy.Enhancer;

//http://blog.csdn.net/zghwaicsdn/article/details/50957474
//http://blog.csdn.net/danchu/article/details/70238002
public class Main {
    private static Logger LOG = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SourceClass.class);
        enhancer.setCallback(new MyInterceptor());
        SourceClass sourceClass = (SourceClass)enhancer.create();
        LOG.info(sourceClass.getClass().getCanonicalName());
        LOG.info(sourceClass.fun1("abc"));
        LOG.info(sourceClass.fun2(new ClassA()).toString());
    }
}
