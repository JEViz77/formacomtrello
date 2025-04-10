package com.example.formacomtrello.controller;

import com.example.formacomtrello.model.Tareas;
import com.example.formacomtrello.model.Usuarios;
import com.example.formacomtrello.repository.TareasRepository;
import com.example.formacomtrello.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ColaboratorController {

    private final TareasRepository tareasRepository;
    private final UsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ColaboratorController(TareasRepository tareasRepository,
                                 UsuariosRepository usuariosRepository,
                                 PasswordEncoder passwordEncoder) {
        this.tareasRepository = tareasRepository;
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/colaborator")
    public String colaborator(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<Usuarios> colaborador = usuariosRepository.findByEmail(email);

        if (colaborador.isPresent()) {
            Usuarios usuario = colaborador.get();
            String nombreCompleto = usuario.getNombre() + " " + usuario.getApellidos();
            model.addAttribute("nombreColaborador", nombreCompleto);

            List<Tareas> tareas = tareasRepository.findByColaborador(colaborador);
            model.addAttribute("tareas", tareas);
        } else {
            throw new RuntimeException("Colaborador no encontrado");
        }

        return "colaborator";
    }

    @PostMapping("/colaborator/tarea/finalizar")
    public String finalizarTarea(@RequestParam("tareaId") Integer tareaId) {
        Tareas tarea = tareasRepository.findById(tareaId).orElse(null);
        if (tarea != null) {
            tarea.setEstado("Completada");
            tareasRepository.save(tarea);
        }
        return "redirect:/colaborator";
    }

    @PostMapping("/colaborator/tarea/procesar")
    public String cambiarEstadoEnProceso(@RequestParam("tareaId") Integer tareaId) {
        Tareas tarea = tareasRepository.findById(tareaId).orElse(null);
        if (tarea != null) {
            tarea.setEstado("En Proceso");
            tareasRepository.save(tarea);
        }
        return "redirect:/colaborator";
    }

    // ✅ Mostrar formulario de edición
    @GetMapping("/colaborator/editprofile")
    public String mostrarFormularioEdicionColaborador(Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuarios colaborador = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Colaborador no encontrado"));

        model.addAttribute("colaborador", colaborador);
        return "editcolaboratorprofile";
    }

    // ✅ Procesar formulario
    @PostMapping("/colaborator/editprofile")
    public String actualizarPerfilColaborador(@RequestParam("confirmPassword") String confirmPassword,
                                              @ModelAttribute Usuarios colaboradorForm,
                                              Authentication authentication,
                                              Model model) {
        String email = authentication.getName();
        Usuarios colaboradorExistente = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Colaborador no encontrado"));

        // Si se ingresó una nueva contraseña
        if (colaboradorForm.getPassword() != null && !colaboradorForm.getPassword().isEmpty()) {
            if (!colaboradorForm.getPassword().equals(confirmPassword)) {
                model.addAttribute("colaborador", colaboradorForm);
                model.addAttribute("error", "Las contraseñas no coinciden.");
                return "editcolaboratorprofile";
            }
            String encodedPassword = passwordEncoder.encode(colaboradorForm.getPassword());
            colaboradorExistente.setPassword(encodedPassword);
        }

        // Actualizar otros campos
        colaboradorExistente.setNombre(colaboradorForm.getNombre());
        colaboradorExistente.setApellidos(colaboradorForm.getApellidos());
        colaboradorExistente.setTelefono(colaboradorForm.getTelefono());


        usuariosRepository.save(colaboradorExistente);

        return "redirect:/colaborator";
    }
}
