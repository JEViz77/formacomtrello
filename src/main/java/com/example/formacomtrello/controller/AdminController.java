package com.example.formacomtrello.controller;

import com.example.formacomtrello.model.Proyectos;
import com.example.formacomtrello.model.Usuarios;
import com.example.formacomtrello.repository.UsuariosRepository;
import com.example.formacomtrello.service.ProyectosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class AdminController {


    private ProyectosService proyectosService;
    private UsuariosRepository usuariosRepository;

    @Autowired
    public AdminController(ProyectosService proyectosService, UsuariosRepository usuariosRepository) {
        this.proyectosService = proyectosService;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping("/createproject")
    public String createProject(Model model) {
        model.addAttribute("proyecto", new Proyectos()); // Asegura que el formulario tiene un objeto vacío
        return "createproject";
    }
    @GetMapping("/createcolaborator")
    public String createColaborator(Model model) {
        return "createcolaborator";
    }
    @GetMapping("/viewprojects")
    public String viewProjects(Model model) {
        List<Proyectos> proyectos = proyectosService.findAll(); // o tu método que los obtiene
        model.addAttribute("projects", proyectos);
        return "viewprojects"; // este nombre debe coincidir con el HTML: viewprojects.html
    }
    @GetMapping("/addtask")
    public String addTask(Model model) {
        return "addtask"; // este nombre debe coincidir con el HTML: addtask.html
    }

    @PostMapping("/createcolaborator")
    public String createColaborator(@ModelAttribute Usuarios colaborador,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        // Obtener el email del gestor autenticado
        String email = userDetails.getUsername();

        // Buscar al gestor (aunque no lo uses, lo puedes dejar por si quieres registrar el id del creador)
        Usuarios gestor = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Asignar el rol de "USER" al nuevo colaborador
        colaborador.setRol("ROLE_USER");

        // Aquí podrías relacionarlo con el gestor si es necesario
        // colaborador.setGestorId(gestor.getId()); <-- si tienes ese campo

        // Guardar el colaborador en la base de datos
        usuariosRepository.save(colaborador);

        return "redirect:/dashboard";
    }

    @PostMapping("/createproject")
    public String createProject(Proyectos proyecto,  @AuthenticationPrincipal UserDetails userDetails) {
        // Obtener el email del usuario autenticado
        String email = userDetails.getUsername();

        // Buscar el usuario en la base de datos
        Usuarios user = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Asignar el ID del gestor
        proyecto.setGestor_id(user.getId());
        proyectosService.saveProyecto(proyecto); // Guardar el proyecto en la base de datos

        return "redirect:/dashboard"; // Redirigir al dashboard después de guardar
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
