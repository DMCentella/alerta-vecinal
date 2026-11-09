package com.alertavecinal.serenazgo.client;

import com.alertavecinal.serenazgo.dto.GenericResponseDto;
import com.alertavecinal.serenazgo.dto.IncidenteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IncidentClient {

    private final RestTemplate restTemplate;

    @Value("${incident.service.url}")
    private String incidentServiceUrl;

    public List<IncidenteDTO> listarIncidentes() {
        String url = incidentServiceUrl + "/api/incidentes";
        ResponseEntity<GenericResponseDto<List<IncidenteDTO>>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<GenericResponseDto<List<IncidenteDTO>>>() {}
        );
        return response.getBody() != null && response.getBody().getResponse() != null
                ? response.getBody().getResponse()
                : List.of();
    }

    public IncidenteDTO obtenerIncidente(Long id) {
        String url = incidentServiceUrl + "/api/incidentes/" + id;
        ResponseEntity<GenericResponseDto<IncidenteDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<GenericResponseDto<IncidenteDTO>>() {}
        );
        return response.getBody() != null ? response.getBody().getResponse() : null;
    }

    public IncidenteDTO cambiarEstado(Long id, String estado, String comentario) {
        String url = incidentServiceUrl + "/api/incidentes/" + id + "/atencion";
        Map<String, String> payload = Map.of(
                "estado", estado,
                "comentario", comentario != null ? comentario : ""
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload);
        ResponseEntity<GenericResponseDto<IncidenteDTO>> response = restTemplate.exchange(
                url, HttpMethod.PUT, request, new ParameterizedTypeReference<GenericResponseDto<IncidenteDTO>>() {}
        );
        return response.getBody() != null ? response.getBody().getResponse() : null;
    }
}