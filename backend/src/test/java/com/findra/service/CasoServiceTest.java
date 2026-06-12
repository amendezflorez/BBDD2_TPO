package com.findra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.findra.dto.EmitirAlertaRequest;
import com.findra.dto.EstadoCasoRequest;
import com.findra.dto.ReporteRequest;
import com.findra.model.Caso;
import com.findra.model.EstadoAlerta;
import com.findra.model.EstadoCaso;
import com.findra.repository.CasoRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

class CasoServiceTest {

    @Test
    void emitirAlertasRegistraAlertaYHistorial() {
        CasoRepository repository = Mockito.mock(CasoRepository.class);
        MongoOperations mongoTemplate = Mockito.mock(MongoOperations.class);
        Caso caso = new Caso();
        caso.setCasoId("AS-2025-001");
        caso.setEstado(EstadoCaso.ACTIVO);
        caso.setFechaActivacion(Instant.now());
        caso.setZona("CABA");

        when(repository.findByCasoId("AS-2025-001")).thenReturn(Optional.of(caso));
        when(repository.save(caso)).thenReturn(caso);

        CasoService service = new CasoService(repository, mongoTemplate);
        Caso actualizado = service.emitirAlertas("AS-2025-001",
                new EmitirAlertaRequest(List.of("SMS masivo"), "Prueba", false, "Op. Test"));

        assertThat(actualizado.getAlertasEmitidas()).hasSize(1);
        assertThat(actualizado.getAlertasEmitidas().get(0).getEstado()).isEqualTo(EstadoAlerta.ENVIADA);
        assertThat(actualizado.getHistorialAcciones()).hasSize(1);
        assertThat(actualizado.getHistorialAcciones().get(0).getAccion()).isEqualTo("alerta_emitida");
    }

    @Test
    void registrarReporteAgregaReporteYHistorial() {
        CasoRepository repository = Mockito.mock(CasoRepository.class);
        MongoOperations mongoTemplate = Mockito.mock(MongoOperations.class);
        Caso caso = new Caso();
        caso.setCasoId("AS-2025-002");
        caso.setEstado(EstadoCaso.ACTIVO);
        caso.setFechaActivacion(Instant.now());

        when(repository.findByCasoId("AS-2025-002")).thenReturn(Optional.of(caso));
        when(repository.save(caso)).thenReturn(caso);

        CasoService service = new CasoService(repository, mongoTemplate);
        Caso actualizado = service.registrarReporte("AS-2025-002",
                new ReporteRequest(-58.38, -34.60, "Visto en plaza", "134", "Op. Test"));

        assertThat(actualizado.getReportesCiudadanos()).hasSize(1);
        assertThat(actualizado.getReportesCiudadanos().get(0).getDescripcion()).isEqualTo("Visto en plaza");
        assertThat(actualizado.getHistorialAcciones()).hasSize(1);
        assertThat(actualizado.getHistorialAcciones().get(0).getAccion()).isEqualTo("reporte_ciudadano_registrado");
    }

    @Test
    void actualizarEstadoCambiaEstadoYRegistraHistorial() {
        CasoRepository repository = Mockito.mock(CasoRepository.class);
        MongoOperations mongoTemplate = Mockito.mock(MongoOperations.class);
        Caso caso = new Caso();
        caso.setCasoId("AS-2025-003");
        caso.setEstado(EstadoCaso.ACTIVO);
        caso.setFechaActivacion(Instant.now());

        when(repository.findByCasoId("AS-2025-003")).thenReturn(Optional.of(caso));
        when(repository.save(caso)).thenReturn(caso);

        CasoService service = new CasoService(repository, mongoTemplate);
        Caso actualizado = service.actualizarEstado("AS-2025-003",
                new EstadoCasoRequest(EstadoCaso.RESUELTO, "Menor localizado", "Op. Test"));

        assertThat(actualizado.getEstado()).isEqualTo(EstadoCaso.RESUELTO);
        assertThat(actualizado.getResultado()).isEqualTo("Menor localizado");
        assertThat(actualizado.getHistorialAcciones()).hasSize(1);
        assertThat(actualizado.getHistorialAcciones().get(0).getAccion()).isEqualTo("estado_actualizado");
    }

    @Test
    void crearCasoSetearFechaActivacionYEstadoActivo() {
        CasoRepository repository = Mockito.mock(CasoRepository.class);
        MongoOperations mongoTemplate = Mockito.mock(MongoOperations.class);
        Caso caso = new Caso();
        caso.setCasoId("AS-2025-004");

        when(repository.save(any(Caso.class))).thenAnswer(inv -> inv.getArgument(0));

        CasoService service = new CasoService(repository, mongoTemplate);
        Caso creado = service.crear(caso);

        assertThat(creado.getEstado()).isEqualTo(EstadoCaso.ACTIVO);
        assertThat(creado.getFechaActivacion()).isNotNull();
        assertThat(creado.getHistorialAcciones()).hasSize(1);
        assertThat(creado.getHistorialAcciones().get(0).getAccion()).isEqualTo("caso_creado");
    }

    @Test
    void buscarFiltrarPorEstadoInvocaMongoTemplate() {
        CasoRepository repository = Mockito.mock(CasoRepository.class);
        MongoOperations mongoTemplate = Mockito.mock(MongoOperations.class);
        Caso caso = new Caso();
        caso.setCasoId("AS-2025-005");
        caso.setEstado(EstadoCaso.ACTIVO);

        when(mongoTemplate.find(any(Query.class), eq(Caso.class))).thenReturn(List.of(caso));

        CasoService service = new CasoService(repository, mongoTemplate);
        List<Caso> resultado = service.buscar(null, EstadoCaso.ACTIVO, null, null, null, 0, 10);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCasoId()).isEqualTo("AS-2025-005");
    }
}
