package org.wbq.javaagent.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {
    @GetMapping(value = "/hello")
    // 这里final表现不同
    public /*final*/ String hello() {
        return "hello world";
    }
}
