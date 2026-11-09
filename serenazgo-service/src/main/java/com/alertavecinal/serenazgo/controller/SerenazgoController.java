package com.alertavecinal.serenazgo.controller;

import com.alertavecinal.serenazgo.dto.CambioEstadoRequest;
import com.alertavecinal.serenazgo.dto.GenericResponseDto;
import com.alertavecinal.serenazgo.dto.HistorialEstadoResponse;
import com.alertavecinal.serenazgo.dto.IncidenteDTO;
import com.alertavecinal.serenazgo.exception.ResourceNotFoundException;
import com.alertavecinal.serenazgo.service.SerenazgoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/serenazgo")
public class SerenazgoController {

    private final SerenazgoService serenazgoService;

    @GetMapping("/incidentes")
    public ResponseEntity<GenericResponseDto<List<IncidenteDTO>>> listarIncidentes() {
        List<IncidenteDTO> incidentes = serenazgoService.listarIncidentes();
        if (incidentes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        GenericResponseDto<List<IncidenteDTO>> response =
                GenericResponseDto.<List<IncidenteDTO>>builder()
                        .response(incidentes)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/incidentes/{id}")
    public ResponseEntity<GenericResponseDto<IncidenteDTO>> obtenerIncidente(@PathVariable Long id) {
        IncidenteDTO incidente = serenazgoService.obtenerIncidente(id);
        if (incidente == null) {
            throw new ResourceNotFoundException("El incidente con ID = " + id + " no existe.");
        }
        GenericResponseDto<IncidenteDTO> response =
                GenericResponseDto.<IncidenteDTO>builder()
                        .response(incidente)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/incidentes/{id}/estado")
    public ResponseEntity<GenericResponseDto<IncidenteDTO>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Long serenazgoId,
            @RequestBody CambioEstadoRequest request) {
        IncidenteDTO actualizado = serenazgoService.cambiarEstado(id, serenazgoId, request);
        GenericResponseDto<IncidenteDTO> response =
                GenericResponseDto.<IncidenteDTO>builder()
                        .response(actualizado)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/incidentes/{id}/historial")
    public ResponseEntity<GenericResponseDto<List<HistorialEstadoResponse>>> obtenerHistorial(@PathVariable Long id) {
        List<HistorialEstadoResponse> historial = serenazgoService.obtenerHistorial(id);
        GenericResponseDto<List<HistorialEstadoResponse>> response =
                GenericResponseDto.<List<HistorialEstadoResponse>>builder()
                        .response(historial)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}