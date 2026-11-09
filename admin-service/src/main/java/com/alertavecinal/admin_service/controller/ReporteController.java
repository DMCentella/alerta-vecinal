package com.alertavecinal.admin_service.controller;

import com.alertavecinal.admin_service.dto.GenericResponseDto;
import com.alertavecinal.admin_service.dto.IncidenteDTO;
import com.alertavecinal.admin_service.dto.ReporteEstadisticoDTO;
import com.alertavecinal.admin_service.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/reportes/estadisticas")
    public ResponseEntity<GenericResponseDto<ReporteEstadisticoDTO>> obtenerEstadisticas() {
        ReporteEstadisticoDTO reporte = reporteService.generarReporte();
        return ResponseEntity.ok(new GenericResponseDto<>(reporte));
    }

    @GetMapping("/reportes/incidentes")
    public ResponseEntity<GenericResponseDto<List<IncidenteDTO>>> listarTodosIncidentes() {
        List<IncidenteDTO> incidentes = reporteService.listarTodosIncidentes();
        return ResponseEntity.ok(new GenericResponseDto<>(incidentes));
    }

    @GetMapping("/reportes/incidentes/filtrar")
    public ResponseEntity<GenericResponseDto<List<IncidenteDTO>>> filtrarPorEstado(@RequestParam String estado) {
        List<IncidenteDTO> incidentes = reporteService.listarPorEstado(estado);
        return ResponseEntity.ok(new GenericResponseDto<>(incidentes));
    }
}
