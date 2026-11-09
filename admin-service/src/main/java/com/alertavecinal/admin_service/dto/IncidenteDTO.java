package com.alertavecinal.admin_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidenteDTO {
    private Long id;
    private String tipo;
    private String descripcion;
    private String ubicacionExacta;
    private String prioridad;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String comentarioSerenazgo;
    private String nombresCiudadano;
    private String apellidosCiudadano;
}
