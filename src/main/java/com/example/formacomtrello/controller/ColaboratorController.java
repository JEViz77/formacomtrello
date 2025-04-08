package com.example.formacomtrello.controller;

import com.example.formacomtrello.model.Tareas;
import com.example.formacomtrello.model.Usuarios;
import com.example.formacomtrello.repository.TareasRepository;
import com.example.formacomtrello.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ColaboratorController {
    @Autowired
    private TareasRepository tareaRepository;
    private final TareasRepository tareasRepository;
    private final UsuariosRepository usuariosRepository;

    public ColaboratorController(TareasRepository tareasRepository, UsuariosRepository usuariosRepository) {
        this.tareasRepository = tareasRepository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping("/colaborator")
    public String colaborator(Model model, Authentication authentication) {
        String email = authentication.getName(); // Email del usuario autenticado
        Optional<Usuarios> colaborador = usuariosRepository.findByEmail(email);

        List<Tareas> tareas = tareasRepository.findByColaborador(colaborador);

        model.addAttribute("tareas", tareas);
        return "colaborator";
    }
    @PostMapping("/colaborator/tarea/finalizar")
    public String finalizarTarea(@RequestParam("tareaId") Integer tareaId) {
        Tareas tarea = tareaRepository.findById(tareaId).orElse(null);
        if (tarea != null) {
            tarea.setEstado("Completada");
            tareaRepository.save(tarea);
        }
        return "redirect:/colaborator";
    }

}
