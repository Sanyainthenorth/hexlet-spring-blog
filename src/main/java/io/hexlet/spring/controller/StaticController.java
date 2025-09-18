package io.hexlet.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {

    @GetMapping("/about")
    public String about() {
        return "forward:/about.html";
    }
}