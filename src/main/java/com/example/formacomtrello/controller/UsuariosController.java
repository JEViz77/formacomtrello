package com.example.formacomtrello.controller;

import com.example.formacomtrello.model.Usuarios;
import com.example.formacomtrello.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuariosController {
    private PasswordEncoder encoder;
    private UsuariosRepository usuariosRepository;

    @Autowired
    public UsuariosController(PasswordEncoder encoder, UsuariosRepository usuariosRepository) {
        this.encoder = encoder;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(Usuarios user) {
        user.setPassword(encoder.encode(user.getPassword()));
        // Cambia 'ROL_USER' por uno de los valores válidos
        user.setRol("ADMIN"); // O 'ADMIN' según corresponda
        usuariosRepository.save(user);
        return "redirect:/login";
    }
}
