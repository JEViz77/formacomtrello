package com.example.formacomtrello.service;

import com.example.formacomtrello.model.Proyectos;
import com.example.formacomtrello.model.Tareas;
import com.example.formacomtrello.model.Usuarios;
import com.example.formacomtrello.repository.ProyectosRepository;
import com.example.formacomtrello.repository.TareasRepository;
import com.example.formacomtrello.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ProyectosService {


    private final ProyectosRepository proyectosRepository;
    private final TareasRepository tareasRepository;
    private final UsuariosRepository usuariosRepository;

    @Autowired
    public ProyectosService(ProyectosRepository proyectosRepository, TareasRepository tareasRepository, UsuariosRepository usuariosRepository) {
        this.proyectosRepository = proyectosRepository;
        this.tareasRepository = tareasRepository;
        this.usuariosRepository = usuariosRepository;
    }

    public void saveProyecto(Proyectos proyecto) {
        proyectosRepository.save(proyecto); // Guardar el proyecto en la base de datos
    }

    public List<Proyectos> findAll() {
        return proyectosRepository.findAll(); // Obtener todos los proyectos
    }

    public void saveTarea(Tareas tarea) {

        tareasRepository.save(tarea); // Guardar la tarea en la base de datos
    }

    public Object obtenerTodos() {
        return null;
    }

    public List<Tareas> obtenerTareas() {
        return tareasRepository.findAll();
    }

    public List<Tareas> obtenerTareasPorProyecto(Integer proyectoId) {
        return tareasRepository.findByProyectoId(proyectoId);  // Asegúrate de tener este método en tu repositorio
    }

    public Optional<Tareas> findTareaById(Integer taskId) {
        return tareasRepository.findById(taskId);
    }

    public void deleteTarea(Tareas tarea) {
        tareasRepository.delete(tarea);
    }

    public Optional<Proyectos> findById(Integer proyectoId) {
        return proyectosRepository.findById(proyectoId);
    }

    public void deleteProjectById(Integer id) {
        proyectosRepository.deleteById(id); // Esto elimina el proyecto de la base de datos
    }
    public List<Proyectos> findByGestorId(Integer gestorId) {
        return proyectosRepository.findByGestorId(gestorId);
    }
    public Optional<Proyectos> findProyectoById(Integer id) {
        return proyectosRepository.findById(id);
    }

    public Optional<Usuarios> findUsuarioById(Integer id) {
        return usuariosRepository.findById(id);
    }

}