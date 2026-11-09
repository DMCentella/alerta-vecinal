package com.alertavecinal.incident_service.repository;

import com.alertavecinal.incident_service.entity.EstadoIncidente;
import com.alertavecinal.incident_service.entity.Incidente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class IncidenteRepositoryTest {

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Test
    public void testInsertarIncidente() {
        Incidente incidente = new Incidente();
        incidente.setTipo("ROBO");
        incidente.setDescripcion("Robo en la cuadra 5");
        incidente.setUbicacionExacta("Av. Lima 123");
        incidente.setPrioridad("ALTA");

        Incidente guardado = incidenteRepository.save(incidente);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isGreaterThan(0);
    }

    @Test
    public void testListarIncidentes() {
        Incidente i1 = new Incidente();
        i1.setTipo("PANDILLAJE");
        i1.setDescripcion("Grupo sospechoso");
        incidenteRepository.save(i1);

        Incidente i2 = new Incidente();
        i2.setTipo("ACCIDENTE");
        i2.setDescripcion("Choque vehicular");
        incidenteRepository.save(i2);

        List<Incidente> lista = incidenteRepository.findAll();

        assertThat(lista.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void testActualizarIncidente() {
        Incidente incidente = new Incidente();
        incidente.setTipo("ROBO");
        incidente.setDescripcion("Robo inicial");
        Incidente guardado = incidenteRepository.save(incidente);

        guardado.setEstado(EstadoIncidente.ATENDIDO);
        guardado.setComentarioSerenazgo("Caso resuelto en campo");
        Incidente actualizado = incidenteRepository.save(guardado);

        assertThat(actualizado.getEstado()).isEqualTo(EstadoIncidente.ATENDIDO);
        assertThat(actualizado.getComentarioSerenazgo()).isEqualTo("Caso resuelto en campo");
    }

    @Test
    public void testEliminarIncidente() {
        Incidente incidente = new Incidente();
        incidente.setTipo("INCENDIO");
        incidente.setDescripcion("Quema de basura");
        Incidente guardado = incidenteRepository.save(incidente);

        incidenteRepository.deleteById(guardado.getId());
        Optional<Incidente> buscado = incidenteRepository.findById(guardado.getId());

        assertThat(buscado).isEmpty();
    }
}
