
package com.example.formacomtrello.controller;

import com.example.formacomtrello.model.Proyectos;
import com.example.formacomtrello.model.Usuarios;
import com.example.formacomtrello.model.Tareas;
import com.example.formacomtrello.repository.UsuariosRepository;
import com.example.formacomtrello.service.ProyectosService;
import com.example.formacomtrello.service.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {


    private final ProyectosService proyectosService;
    private final UsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public AdminController(ProyectosService proyectosService,
                           UsuariosRepository usuariosRepository,
                           PasswordEncoder passwordEncoder) {
        this.proyectosService = proyectosService;
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/createproject")
    public String createProject(Model model) {
        model.addAttribute("proyecto", new Proyectos()); // Asegura que el formulario tiene un objeto vacío
        return "createproject";
    }
    @PostMapping("/createproject")
    public String createProject(Proyectos proyecto,  @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Usuarios user = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (proyecto.getTitulo() == null || proyecto.getDescripcion() == null) {
            return "error";
        }

        // Asignar el gestor ID
        proyecto.setGestor(user);

        proyectosService.saveProyecto(proyecto);
        return "redirect:/dashboard";
    }


    @GetMapping("/createcolaborator")
    public String createColaborator(Model model) {
        return "createcolaborator";
    }
    @PostMapping("/createcolaborator")
    public String createColaborator(@ModelAttribute Usuarios colaborador,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Usuarios gestor = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        colaborador.setRol("ROLE_USER");
        colaborador.setGestorId(gestor.getId());

        // 🛡️ Codificar la contraseña aquí
        String contraseñaCodificada = passwordEncoder.encode(colaborador.getPassword());
        colaborador.setPassword(contraseñaCodificada);

        usuariosRepository.save(colaborador);
        return "redirect:/dashboard";
    }
    @GetMapping("/viewprojects")
    public String viewProjects(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Usuarios usuario = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nombreCompleto = usuario.getNombre() + " " + usuario.getApellidos();
        model.addAttribute("username", nombreCompleto);

        // 🔥 Solo proyectos del administrador autenticado
        List<Proyectos> proyectos = proyectosService.findByGestorId(usuario.getId());
        model.addAttribute("projects", proyectos);

        return "viewprojects";
    }


    @GetMapping("/viewtasks/{proyectoId}")
    public String viewTasks(@PathVariable Integer proyectoId, Model model) {
        List<Tareas> tareas = proyectosService.obtenerTareasPorProyecto(proyectoId);  // Cambiar método para filtrar por proyecto
        model.addAttribute("tasks", tareas);
        return "viewtasks" ;  // Vista de tareas
    }
    @GetMapping("/addtask/{proyectoId}")
    public String mostrarFormulario(Model model,@PathVariable Integer proyectoId) {
        // Creamos un nuevo objeto Tareas para enlazarlo con el formulario
        Tareas tarea = new Tareas();
        // Asignar el proyecto automáticamente
        Proyectos proyecto = proyectosService.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        tarea.setProyecto(proyecto);
        model.addAttribute("tarea", tarea);

        // Obtener los colaboradores desde el repositorio de usuarios
        List<Usuarios> colaboradores = usuariosRepository.findByRol("ROLE_USER");// O utiliza un método de tu repositorio que filtre solo los colaboradores
        // Pasamos las listas de colaboradores y proyectos al modelo
        model.addAttribute("colaboradores", colaboradores);

        // Retornamos la vista del formulario
        return "addtask";
    }

    @PostMapping("/addtask/{proyectoId}")
    public String agregarTarea(@ModelAttribute Tareas tarea,@PathVariable Integer proyectoId, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Obtenemos el email del usuario autenticado
        String email = userDetails.getUsername();


        if (tarea.getProyecto() == null || tarea.getProyecto().getId() == null) {
            Proyectos proyecto = proyectosService.findById(proyectoId)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            tarea.setProyecto(proyecto);
        }
        // Asignamos el estado "Pendiente" a la tarea (si no lo asignas en el formulario)
        if (tarea.getEstado() == null) {
            tarea.setEstado("Pendiente");
        }

        // Guardamos la tarea en la base de datos
        proyectosService.saveTarea(tarea);  // Necesitas un método en tu servicio que guarde tareas


        return "redirect:/viewtasks/" + proyectoId;
    }

    @GetMapping("/edittask/{taskId}")
    public String editTask(@PathVariable Integer taskId, Model model) {
        // Buscar la tarea por su ID
        Tareas tarea = proyectosService.findTareaById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Obtener solo los colaboradores (usuarios con rol ROLE_USER)
        List<Usuarios> colaboradores = usuariosRepository.findByRol("ROLE_USER");


        // Agregar la tarea, colaboradores y proyectos al modelo
        model.addAttribute("tarea", tarea);
        model.addAttribute("colaboradores", colaboradores);


        // Retornar la vista para editar la tarea
        return "edittask";
    }



    @PostMapping("/edittask/{taskId}")
    public String updateTask(@PathVariable Integer taskId, @ModelAttribute Tareas tarea) {
        // Buscar la tarea por su ID
        Tareas tareaExistente = proyectosService.findTareaById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Buscar el proyecto y el colaborador a partir del ID (porque vienen incompletos en el form)
        Proyectos proyecto = proyectosService.findProyectoById(tarea.getProyecto().getId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        Usuarios colaborador = proyectosService.findUsuarioById(tarea.getColaborador().getId())
                .orElseThrow(() -> new RuntimeException("Colaborador no encontrado"));

        // Actualizar campos
        tareaExistente.setTitulo(tarea.getTitulo());
        tareaExistente.setDescripcion(tarea.getDescripcion());
        tareaExistente.setFecha_vencimiento(tarea.getFecha_vencimiento());
        tareaExistente.setEstado(tarea.getEstado());
        tareaExistente.setProyecto(proyecto);
        tareaExistente.setColaborador(colaborador);

        // Guardar la tarea actualizada
        proyectosService.saveTarea(tareaExistente);

        return "redirect:/viewtasks/" + proyecto.getId();
    }


    @GetMapping("/deletetask/{taskId}")
    public String deleteTask(@PathVariable Integer taskId) {
        // Buscar la tarea por su ID
        Tareas tarea = proyectosService.findTareaById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Eliminar la tarea
        proyectosService.deleteTarea(tarea);

        // ✅ Redirigir al ID real del proyecto
        Integer proyectoId = tarea.getProyecto().getId();
        return "redirect:/viewtasks/" + proyectoId;
    }
    @PostMapping("/deleteproject/{proyectoId}")
    public String deleteProject(@PathVariable Integer proyectoId) {
        proyectosService.deleteProjectById(proyectoId);
        return "redirect:/viewprojects"; // Redirige a la lista de proyectos después de eliminar
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        Usuarios usuario=null;
        String nombreCompleto = "";
        if (authentication != null) {
            username = authentication.getName(); // Obtén el nombre de usuario
            usuario= usuariosRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        }
        if(usuario!=null){
            nombreCompleto = usuario.getNombre() + " " + usuario.getApellidos();;
        }
        model.addAttribute("username", nombreCompleto) ;
        return "dashboard";
    }
    @GetMapping("/editprofile")
    public String mostrarFormularioEdicionPerfil(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Usuarios usuario = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        return "editprofile";
    }

    @PostMapping("/editprofile")
    public String actualizarPerfil(@ModelAttribute Usuarios usuarioForm,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   @org.springframework.web.bind.annotation.RequestParam("confirmPassword") String confirmPassword,
                                   Model model) {

        String email = userDetails.getUsername();
        Usuarios usuarioExistente = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validaciones simples
        if (usuarioForm.getPassword() != null && !usuarioForm.getPassword().isEmpty()) {
            if (!usuarioForm.getPassword().equals(confirmPassword)) {
                model.addAttribute("usuario", usuarioForm);
                model.addAttribute("error", "Las contraseñas no coinciden.");
                return "editprofile";
            }
            String passwordCodificada = passwordEncoder.encode(usuarioForm.getPassword());
            usuarioExistente.setPassword(passwordCodificada);
        }

        usuarioExistente.setNombre(usuarioForm.getNombre());
        usuarioExistente.setApellidos(usuarioForm.getApellidos());
        usuarioExistente.setTelefono(usuarioForm.getTelefono());


        usuariosRepository.save(usuarioExistente);

        return "redirect:/dashboard";
    }
    @GetMapping("/editproject/{id}")
    public String showEditProjectForm(@PathVariable Integer id, Model model) {
        // Buscar el proyecto por su ID
        Proyectos proyecto = proyectosService.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Pasar el proyecto al modelo para que se pueda mostrar en el formulario
        model.addAttribute("proyecto", proyecto);
        return "editproject"; // Vista que contiene el formulario de edición
    }
    @PostMapping("/editproject/{id}")
    public String updateProject(@PathVariable Integer id, @ModelAttribute Proyectos proyecto) {
        // Buscar el proyecto por su ID
        Proyectos proyectoExistente = proyectosService.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Actualizar los campos del proyecto
        proyectoExistente.setTitulo(proyecto.getTitulo());
        proyectoExistente.setDescripcion(proyecto.getDescripcion());
        proyectoExistente.setFecha_inicio(proyecto.getFecha_inicio());
        proyectoExistente.setFecha_vencimiento(proyecto.getFecha_vencimiento());

        // Guardar el proyecto actualizado
        proyectosService.saveProyecto(proyectoExistente);

        // Redirigir al dashboard o donde lo desees
        return "redirect:/viewprojects";
    }


}
