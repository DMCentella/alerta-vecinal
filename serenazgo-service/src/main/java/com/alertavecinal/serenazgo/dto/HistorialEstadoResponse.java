package com.alertavecinal.serenazgo.dto;

import com.alertavecinal.serenazgo.enums.EstadoIncidente;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistorialEstadoResponse {
    private Long id;
    private Long incidenteId;
    private Long serenazgoId;
    private EstadoIncidente estadoAnterior;
    private EstadoIncidente estadoNuevo;
    private LocalDateTime fechaCambio;
}