package com.alertavecinal.incident_service.service;

import com.alertavecinal.incident_service.entity.EstadoIncidente;
import com.alertavecinal.incident_service.entity.Incidente;
import com.alertavecinal.incident_service.repository.IncidenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Incidente registrarIncidente(Incidente incidente) {
        Incidente guardado = incidenteRepository.save(incidente);
        try {
            // Emite al hub MQTT de WebSockets
            messagingTemplate.convertAndSend("/topic/alertas", guardado);
        } catch (Exception e) {
            System.err.println("Error al notificar agentes por websocket: " + e.getMessage());
        }
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Incidente> obtenerTodos() {
        return incidenteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Incidente> obtenerPorId(Long id) {
        return incidenteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Incidente> obtenerPorEstado(EstadoIncidente estado) {
        return incidenteRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<Incidente> obtenerPorPrioridad(String prioridad) {
        return incidenteRepository.findByPrioridad(prioridad);
    }

    @Transactional
    public Optional<Incidente> actualizarEstadoYComentario(Long id, EstadoIncidente nuevoEstado, String comentario) {
        return incidenteRepository.findById(id).map(incidente -> {
            incidente.setEstado(nuevoEstado);
            if (comentario != null && !comentario.trim().isEmpty()) {
                incidente.setComentarioSerenazgo(comentario);
            }
            return incidenteRepository.save(incidente);
        });
    }

    @Transactional
    public boolean eliminarIncidente(Long id) {
        if (incidenteRepository.existsById(id)) {
            incidenteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}