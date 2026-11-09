package com.alertavecinal.serenazgo.dto;

import com.alertavecinal.serenazgo.enums.EstadoIncidente;
import lombok.Data;

@Data
public class CambioEstadoRequest {
    private EstadoIncidente nuevoEstado;
    private String comentario;
}