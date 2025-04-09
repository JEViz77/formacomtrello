package com.example.formacomtrello.repository;

import com.example.formacomtrello.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

    List<Comentario> findByTareaId(Integer tareaId);

    List<Comentario> findByTareaIdOrderByFechaCreacionDesc(Integer tareaId);

}
