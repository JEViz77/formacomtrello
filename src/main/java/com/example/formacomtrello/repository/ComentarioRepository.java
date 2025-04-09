package com.example.formacomtrello.repository;

import com.example.formacomtrello.model.Comentario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

    List<Comentario> findByTareaId(Integer tareaId);

    List<Comentario> findByTareaIdOrderByFechaCreacionDesc(Integer tareaId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Comentario c WHERE c.tarea.id = :tareaId")
    void deleteByTareaId(@Param("tareaId") Integer tareaId);

}
