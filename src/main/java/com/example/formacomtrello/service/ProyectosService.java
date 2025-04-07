package com.example.formacomtrello.service;

import com.example.formacomtrello.model.Proyectos;
import com.example.formacomtrello.repository.ProyectosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProyectosService {

    @Autowired
    private ProyectosRepository proyectosRepository;

    public void saveProyecto(Proyectos proyecto) {
        proyectosRepository.save(proyecto); // Guardar el proyecto en la base de datos
    }
}