package com.example.formacomtrello.controller;

import com.example.formacomtrello.model.Proyectos;
import com.example.formacomtrello.model.Usuarios;
import com.example.formacomtrello.model.Tareas;
import com.example.formacomtrello.repository.UsuariosRepository;
import com.example.formacomtrello.service.ProyectosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class AdminController {


    private final ProyectosService proyectosService;
    private final UsuariosRepository usuariosRepository;

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
    @GetMapping("/viewtasks/{proyectoId}")
    public String viewTasks(@PathVariable Integer proyectoId, Model model) {
        List<Tareas> tareas = proyectosService.obtenerTareasPorProyecto(proyectoId);  // Cambiar método para filtrar por proyecto
        model.addAttribute("tasks", tareas);
        return "viewtasks";  // Vista de tareas
    }

    @GetMapping("/addtask")
    public String mostrarFormulario(Model model) {
        // Creamos un nuevo objeto Tareas para enlazarlo con el formulario
        model.addAttribute("tarea", new Tareas());

        // Obtener los colaboradores desde el repositorio de usuarios
        List<Usuarios> colaboradores = usuariosRepository.findAll();  // O utiliza un método de tu repositorio que filtre solo los colaboradores
        List<Proyectos> proyectos = proyectosService.findAll();
        // Pasamos las listas de colaboradores y proyectos al modelo
        model.addAttribute("colaboradores", colaboradores);
        model.addAttribute("proyectos", proyectos);

        // Retornamos la vista del formulario
        return "addtask";
    }

    @PostMapping("/addtask")
    public String agregarTarea(@ModelAttribute Tareas tarea, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Obtenemos el email del usuario autenticado
        String email = userDetails.getUsername();

        // Buscar al gestor en la base de datos (puedes no usarlo, pero es útil si deseas asignar el gestor a la tarea)
        Usuarios gestor = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Asignamos el estado "Pendiente" a la tarea (si no lo asignas en el formulario)
        if (tarea.getEstado() == null) {
            tarea.setEstado("Pendiente");
        }

        // Guardamos la tarea en la base de datos
        proyectosService.saveTarea(tarea);  // Necesitas un método en tu servicio que guarde tareas

        // Redirigimos a la página de proyectos después de agregar la tarea
        return "redirect:/viewprojects";
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
        colaborador.setGestorId(gestor.getId());

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

        // Aquí puede ser necesario un chequeo adicional para asegurar que los campos del proyecto estén bien configurados
        if (proyecto.getTitulo() == null || proyecto.getDescripcion() == null) {
            return "error"; // Puedes redirigir a una página de error si faltan datos importantes
        }

        proyectosService.saveProyecto(proyecto);  // Guardar el proyecto en la base de datos

        return "redirect:/dashboard"; // Redirigir al dashboard después de guardar
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
