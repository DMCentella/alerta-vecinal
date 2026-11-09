package com.alertavecinal.serenazgo.model;

import com.alertavecinal.serenazgo.enums.EstadoIncidente;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "historial_estados")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long incidenteId;
    private Long serenazgoId;

    @Enumerated(EnumType.STRING)
    private EstadoIncidente estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoIncidente estadoNuevo;

    private LocalDateTime fechaCambio;

    @PrePersist
    protected void onCreate() {
        this.fechaCambio = LocalDateTime.now();
    }
}