package com.example.formacomtrello.controller;

import com.example.formacomtrello.model.Comentario;
import com.example.formacomtrello.model.Tareas;
import com.example.formacomtrello.model.Usuarios;
import com.example.formacomtrello.repository.ComentarioRepository;
import com.example.formacomtrello.repository.TareasRepository;
import com.example.formacomtrello.repository.UsuariosRepository;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/comentarios")
public class ComentarioController {
    @Autowired
    private UsuariosRepository usuariosRepository;
    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private TareasRepository tareaRepository;

    @PostMapping("/agregar")
    public String agregarComentario(@RequestParam("tareaId") Integer tareaId,
                                    @RequestParam("texto") String texto,
                                    Authentication authentication) {

        Optional<Tareas> tareaOptional = tareaRepository.findById(tareaId);
        if (tareaOptional.isEmpty()) {
            return "redirect:/error";
        }
        Tareas tarea = tareaOptional.get();

        Comentario comentario = new Comentario();
        comentario.setTexto(texto);
        comentario.setFechaCreacion(Timestamp.valueOf(LocalDateTime.now()));

        // Obtener el email del usuario logueado
        String email = authentication.getName();
        Optional<Usuarios> autorOptional = usuariosRepository.findByEmail(email);

        if (autorOptional.isEmpty()) {
            return "redirect:/error"; // Maneja el caso donde no se encuentra el usuario
        }

        Usuarios autor = autorOptional.get();
        comentario.setAutor(autor);
        comentario.setTarea(tarea);

        comentarioRepository.save(comentario);

        return "redirect:/tareas/" + tareaId;
    }

    @GetMapping("/tarea/{tareaId}")
    public String verComentarios(@PathVariable("tareaId") Integer tareaId, Model model) {
        List<Comentario> comentarios = comentarioRepository.findByTareaId(tareaId);
        model.addAttribute("comentarios", comentarios);
        model.addAttribute("tareaId", tareaId); // Pasar el ID de la tarea al formulario de comentarios
        return "comentarios"; // El nombre de la vista HTML
    }
}
