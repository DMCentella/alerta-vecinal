package com.alertavecinal.serenazgo.service;

import com.alertavecinal.serenazgo.client.IncidentClient;
import com.alertavecinal.serenazgo.dto.CambioEstadoRequest;
import com.alertavecinal.serenazgo.dto.HistorialEstadoResponse;
import com.alertavecinal.serenazgo.dto.IncidenteDTO;
import com.alertavecinal.serenazgo.enums.EstadoIncidente;
import com.alertavecinal.serenazgo.model.HistorialEstado;
import com.alertavecinal.serenazgo.repository.HistorialEstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SerenazgoService {

    private final IncidentClient incidentClient;
    private final HistorialEstadoRepository historialEstadoRepository;

    public List<IncidenteDTO> listarIncidentes() {
        return incidentClient.listarIncidentes();
    }

    public IncidenteDTO obtenerIncidente(Long id) {
        return incidentClient.obtenerIncidente(id);
    }

    public IncidenteDTO cambiarEstado(Long incidenteId, Long serenazgoId, CambioEstadoRequest request) {
        // Obtener el estado actual antes de cambiarlo
        IncidenteDTO incidente = incidentClient.obtenerIncidente(incidenteId);
        EstadoIncidente estadoAnterior = EstadoIncidente.valueOf(incidente.getEstado());

        // Enviar el cambio al incident-service
        IncidenteDTO actualizado = incidentClient.cambiarEstado(
                incidenteId,
                request.getNuevoEstado().name(),
                request.getComentario()
        );

        // Registrar en historial local
        HistorialEstado historial = new HistorialEstado();
        historial.setIncidenteId(incidenteId);
        historial.setSerenazgoId(serenazgoId);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(request.getNuevoEstado());
        historialEstadoRepository.save(historial);

        return actualizado;
    }

    public List<HistorialEstadoResponse> obtenerHistorial(Long incidenteId) {
        return historialEstadoRepository.findByIncidenteId(incidenteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private HistorialEstadoResponse toResponse(HistorialEstado h) {
        HistorialEstadoResponse r = new HistorialEstadoResponse();
        r.setId(h.getId());
        r.setIncidenteId(h.getIncidenteId());
        r.setSerenazgoId(h.getSerenazgoId());
        r.setEstadoAnterior(h.getEstadoAnterior());
        r.setEstadoNuevo(h.getEstadoNuevo());
        r.setFechaCambio(h.getFechaCambio());
        return r;
    }
}