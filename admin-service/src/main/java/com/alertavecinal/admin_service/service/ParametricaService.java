package com.alertavecinal.admin_service.service;

import com.alertavecinal.admin_service.entity.Parametrica;
import com.alertavecinal.admin_service.repository.ParametricaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParametricaService {

    private final ParametricaRepository parametricaRepository;

    @Transactional
    public Parametrica crearParametrica(Parametrica parametrica) {
        return parametricaRepository.save(parametrica);
    }

    @Transactional(readOnly = true)
    public List<Parametrica> obtenerTodas() {
        return parametricaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Parametrica> obtenerPorId(Long id) {
        return parametricaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Parametrica> obtenerPorTipo(String tipo) {
        return parametricaRepository.findByTipo(tipo);
    }

    @Transactional
    public Optional<Parametrica> actualizarParametrica(Long id, Parametrica detalles) {
        return parametricaRepository.findById(id).map(parametrica -> {
            parametrica.setTipo(detalles.getTipo());
            parametrica.setValor(detalles.getValor());
            parametrica.setDescripcion(detalles.getDescripcion());
            parametrica.setActivo(detalles.isActivo());
            return parametricaRepository.save(parametrica);
        });
    }

    @Transactional
    public boolean eliminarParametrica(Long id) {
        if (parametricaRepository.existsById(id)) {
            parametricaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
