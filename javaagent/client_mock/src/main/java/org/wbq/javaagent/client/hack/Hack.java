package org.wbq.javaagent.client.hack;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wbq.javaagent.client.controller.Hello;

@RestController
public class Hack {
    @GetMapping(value = "hack")
    public String hack() {
        ByteBuddyAgent.install();
        new ByteBuddy()
                .redefine(Hello.class)
                .method(ElementMatchers.named("hello"))
                .intercept(FixedValue.value("hello hack world"))
                .make()
                .load(Hello.class.getClassLoader(),
                        ClassReloadingStrategy.fromInstalledAgent());
        return "Done";
    }
}
