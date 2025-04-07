package com.example.formacomtrello.model;

import jakarta.persistence.*;
import org.hibernate.annotations.IdGeneratorType;

@Entity
@Table(name = "tareas")
public class Tareas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String titulo;
    private String descripcion;
    private String fecha_vencimientoin;
    private String estado;
    private Integer proyecto_id;
    private Integer colaborador_id;

    public Tareas() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha_vencimientoin() {
        return fecha_vencimientoin;
    }

    public void setFecha_vencimientoin(String fecha_vencimientoin) {
        this.fecha_vencimientoin = fecha_vencimientoin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getProyecto_id() {
        return proyecto_id;
    }

    public void setProyecto_id(Integer proyecto_id) {
        this.proyecto_id = proyecto_id;
    }

    public Integer getColaborador_id() {
        return colaborador_id;
    }

    public void setColaborador_id(Integer colaborador_id) {
        this.colaborador_id = colaborador_id;
    }
}