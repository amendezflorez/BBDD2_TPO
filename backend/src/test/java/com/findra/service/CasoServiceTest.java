package com.findra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.findra.dto.EmitirAlertaRequest;
import com.findra.model.Caso;
import com.findra.model.EstadoAlerta;
import com.findra.model.EstadoCaso;
import com.findra.repository.CasoRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;

class CasoServiceTest {

    @Test
    void emitirAlertasRegistraAlertaYHistorial() {
        CasoRepository repository = Mockito.mock(CasoRepository.class);
        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
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
}
