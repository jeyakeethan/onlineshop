package com.example.onlineshop.content.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticContentController {

    @GetMapping("/")
    public String homePage() {
        return "index";  // This will return the 'index.html' page from your templates folder
    }
}
