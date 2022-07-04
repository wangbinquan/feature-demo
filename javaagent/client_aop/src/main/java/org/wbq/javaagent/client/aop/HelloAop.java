package org.wbq.javaagent.client.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Component
public class HelloAop {
    volatile public static String text = "hello world";

    public void begin() {
        System.out.println("BEGIN");
    }

    public Object around(ProceedingJoinPoint point) {
        System.out.println("AOP BEGIN");
        try {
            System.out.println(point.proceed());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("AOP END");
        return HelloAop.text;
    }

    public void end() {
        System.out.println("END");
    }
}
