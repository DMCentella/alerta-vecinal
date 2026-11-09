package com.alertavecinal.admin_service.service;

import com.alertavecinal.admin_service.dto.GenericResponseDto;
import com.alertavecinal.admin_service.dto.IncidenteDTO;
import com.alertavecinal.admin_service.dto.ReporteEstadisticoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final RestTemplate restTemplate;

    @Value("${incident.service.url}")
    private String incidentServiceUrl;

    public ReporteEstadisticoDTO generarReporte() {
        List<IncidenteDTO> incidentes = listarTodosIncidentes();

        long pendientes = incidentes.stream().filter(i -> "PENDIENTE".equalsIgnoreCase(i.getEstado())).count();
        long enProceso = incidentes.stream().filter(i -> "EN_PROCESO".equalsIgnoreCase(i.getEstado())).count();
        long atendidos = incidentes.stream().filter(i -> "ATENDIDO".equalsIgnoreCase(i.getEstado())).count();
        long anulados = incidentes.stream().filter(i -> "ANULADO".equalsIgnoreCase(i.getEstado())).count();
        Map<String, Long> porPrioridad = incidentes.stream().filter(i -> i.getPrioridad() != null).collect(Collectors.groupingBy(i -> i.getPrioridad().toUpperCase(), Collectors.counting()));
        
        return new ReporteEstadisticoDTO(incidentes.size(), pendientes, enProceso, atendidos, anulados, porPrioridad);
    }

    public List<IncidenteDTO> listarTodosIncidentes() {
        String url = incidentServiceUrl + "/api/incidentes";
        ResponseEntity<GenericResponseDto<List<IncidenteDTO>>> respuesta = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<GenericResponseDto<List<IncidenteDTO>>>() {}
        );
        return respuesta.getBody() != null && respuesta.getBody().getResponse() != null
                ? respuesta.getBody().getResponse()
                : List.of();
    }

    public List<IncidenteDTO> listarPorEstado(String estado) {
        return listarTodosIncidentes().stream()
                .filter(i -> estado.equalsIgnoreCase(i.getEstado()))
                .collect(Collectors.toList());
    }
}
