package com.doctorappointment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() { return "index"; }

    @GetMapping("/login")
    public String login() { return "auth/login"; }

    @GetMapping("/error/403")
    public String forbidden() { return "error/403"; }
}
