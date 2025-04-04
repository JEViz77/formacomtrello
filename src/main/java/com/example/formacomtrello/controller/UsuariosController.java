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

    // Página principal
    @GetMapping("/")
    public String index() {
        return "home";  // Aquí redirigimos a la vista 'index.html'
    }

    // Página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Página de registro
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // Endpoint para el registro de usuarios
    @PostMapping("/register")
    public String register(Usuarios user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRol("ROLE_ADMIN"); // O 'USER' según corresponda
        usuariosRepository.save(user);
        return "redirect:/login";  // Después de registrarse, redirigimos al login
    }
    // Página de Dashboard (Panel del Gestor)
    @GetMapping("/dashboard_gestor")
    public String dashboardGestor() {
        return "dashboard_gestor";  // Redirige a la vista dashboard_gestor.html
    }
}

