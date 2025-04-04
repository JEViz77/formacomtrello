package com.example.formacomtrello.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ColaboratorController {
    @GetMapping("/colaborator")
    public String colaborator() {
        return "colaborator";
    }
}