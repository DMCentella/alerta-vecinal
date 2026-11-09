package com.alertavecinal.incident_service.repository;

import com.alertavecinal.incident_service.entity.EstadoIncidente;
import com.alertavecinal.incident_service.entity.Incidente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    List<Incidente> findByEstado(EstadoIncidente estado);

    List<Incidente> findByPrioridad(String prioridad);

    List<Incidente> findByTipo(String tipo);

    long countByEstado(EstadoIncidente estado);
}

