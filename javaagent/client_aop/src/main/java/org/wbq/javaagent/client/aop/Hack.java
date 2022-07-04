package org.wbq.javaagent.client.aop;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hack {
    @GetMapping(value = "/hack")
    public String hack() {
        HelloAop.text = "Hello Hack World";
        return "Done";
    }
}
