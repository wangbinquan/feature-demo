package org.wbq.javaagent.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {
    @GetMapping(value = "/hello")
    public String hello() {
        System.out.println("Call Hello Func");
        return "hello world";
    }
}
