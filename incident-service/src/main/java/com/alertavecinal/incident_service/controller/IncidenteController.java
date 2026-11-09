package com.alertavecinal.incident_service.controller;

import com.alertavecinal.incident_service.dto.GenericResponseDto;
import com.alertavecinal.incident_service.entity.EstadoIncidente;
import com.alertavecinal.incident_service.entity.Incidente;
import com.alertavecinal.incident_service.exception.ResourceNotFoundException;
import com.alertavecinal.incident_service.service.IncidenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/incidentes")
public class IncidenteController {

    private final IncidenteService incidenteService;

    @PostMapping
    public ResponseEntity<GenericResponseDto<Incidente>> crearIncidente(@RequestBody Incidente incidente) {
        Incidente nuevo = incidenteService.registrarIncidente(incidente);
        return new ResponseEntity<>(new GenericResponseDto<>(nuevo), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<GenericResponseDto<List<Incidente>>> listarIncidentes() {
        List<Incidente> incidentes = incidenteService.obtenerTodos();
        if (incidentes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new GenericResponseDto<>(incidentes), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Incidente>> buscarPorId(@PathVariable Long id) {
        Incidente incidente = incidenteService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente con ID = " + id + " no existe."));
        return new ResponseEntity<>(new GenericResponseDto<>(incidente), HttpStatus.OK);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<GenericResponseDto<List<Incidente>>> listarPorEstado(@PathVariable String estado) {
        EstadoIncidente estadoEnum = EstadoIncidente.valueOf(estado.toUpperCase());
        List<Incidente> incidentes = incidenteService.obtenerPorEstado(estadoEnum);
        return new ResponseEntity<>(new GenericResponseDto<>(incidentes), HttpStatus.OK);
    }

    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<GenericResponseDto<List<Incidente>>> listarPorPrioridad(@PathVariable String prioridad) {
        List<Incidente> incidentes = incidenteService.obtenerPorPrioridad(prioridad.toUpperCase());
        return new ResponseEntity<>(new GenericResponseDto<>(incidentes), HttpStatus.OK);
    }

    @PutMapping("/{id}/atencion")
    public ResponseEntity<GenericResponseDto<Incidente>> atenderIncidente(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        EstadoIncidente estado = EstadoIncidente.valueOf(payload.get("estado").toUpperCase());
        String comentario = payload.get("comentario");
        Incidente actualizado = incidenteService.actualizarEstadoYComentario(id, estado, comentario)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente con ID = " + id + " no existe."));
        return new ResponseEntity<>(new GenericResponseDto<>(actualizado), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarIncidente(@PathVariable Long id) {
        if (incidenteService.eliminarIncidente(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("Incidente con ID = " + id + " no existe.");
    }
}