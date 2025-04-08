package com.example.formacomtrello.repository;

import com.example.formacomtrello.model.Tareas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TareasRepository extends JpaRepository<Tareas, Integer>{

    List<Tareas> findByProyectoId(Integer proyectoId);

}
