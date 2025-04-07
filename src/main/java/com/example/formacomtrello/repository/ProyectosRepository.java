package com.example.formacomtrello.repository;

import com.example.formacomtrello.model.Proyectos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectosRepository extends JpaRepository<Proyectos, Integer> {
    // Puedes agregar consultas personalizadas aquí si las necesitas
}