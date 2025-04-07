package com.example.formacomtrello.repository;

import com.example.formacomtrello.model.Tareas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareasRepository extends JpaRepository<Tareas, Integer>{
}
