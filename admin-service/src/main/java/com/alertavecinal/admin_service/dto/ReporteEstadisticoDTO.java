package com.alertavecinal.admin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteEstadisticoDTO {

    private long totalIncidentes;
    private long pendientes;
    private long enProceso;
    private long atendidos;
    private long anulados;

    // Cantidad de incidentes por prioridad
    private Map<String, Long> porPrioridad;
}
