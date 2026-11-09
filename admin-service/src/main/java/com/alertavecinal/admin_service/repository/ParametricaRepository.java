package com.alertavecinal.admin_service.repository;

import com.alertavecinal.admin_service.entity.Parametrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParametricaRepository extends JpaRepository<Parametrica, Long> {

    List<Parametrica> findByTipo(String tipo);
}
