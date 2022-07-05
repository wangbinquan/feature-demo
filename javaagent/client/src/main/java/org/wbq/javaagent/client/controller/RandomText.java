package org.wbq.javaagent.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RandomText {
    private static final String text = UUID.randomUUID().toString();

    @GetMapping(path = "/text")
    public String text() {
        System.out.println("Call Text Func");
        return text;
    }
}
