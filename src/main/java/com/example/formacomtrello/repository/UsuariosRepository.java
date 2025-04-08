package com.example.formacomtrello.repository;

import com.example.formacomtrello.model.Proyectos;
import com.example.formacomtrello.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
    Optional<Usuarios> findByEmail(String email);  // Nuevo método para buscar por email
    List<Usuarios> findByRol(String rol);
}

