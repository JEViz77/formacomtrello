package com.example.formacomtrello.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Tareas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String titulo;
    private String descripcion;
    private LocalDate fecha_vencimiento;
    private String estado;


    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyectos proyecto;

    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Usuarios colaborador;  // Relación con Usuarios, que tiene el rol de colaborador

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

    public LocalDate getFecha_vencimiento() {
        return fecha_vencimiento;
    }

    public void setFecha_vencimiento(LocalDate fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Proyectos getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyectos proyecto) {
        this.proyecto = proyecto;
    }

    public Usuarios getColaborador() {
        return colaborador;
    }

    public void setColaborador(Usuarios colaborador) {
        this.colaborador = colaborador;
    }
}
