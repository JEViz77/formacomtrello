package com.example.formacomtrello.repository;

import com.example.formacomtrello.model.Tareas;
import com.example.formacomtrello.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TareasRepository extends JpaRepository<Tareas, Integer> {
    List<Tareas> findByColaborador(Optional<Usuarios> colaborador);

    List<Tareas> findByProyectoId(Integer proyectoId);

}
