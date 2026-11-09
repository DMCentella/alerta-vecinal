package com.alertavecinal.incident_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidentes")
@Data
public class Incidente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombresCiudadano;
    private String apellidosCiudadano;

    private String tipo;
    private String descripcion;
    private String ubicacionExacta;
    private String prioridad;

    @Enumerated(EnumType.STRING)
    private EstadoIncidente estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    private String comentarioSerenazgo;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoIncidente.PENDIENTE;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
