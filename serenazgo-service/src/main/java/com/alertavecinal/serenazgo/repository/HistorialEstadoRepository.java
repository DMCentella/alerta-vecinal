package com.alertavecinal.serenazgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alertavecinal.serenazgo.model.HistorialEstado;

import java.util.List;

@Repository
public interface HistorialEstadoRepository
        extends JpaRepository<HistorialEstado, Long> {

    List<HistorialEstado> findByIncidenteId(Long incidenteId);

    List<HistorialEstado> findBySerenazgoId(Long serenazgoId);

}