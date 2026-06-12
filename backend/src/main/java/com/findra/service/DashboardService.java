package com.findra.service;

import com.findra.dto.DashboardResumen;
import com.findra.model.Caso;
import com.findra.model.EstadoCaso;
import com.findra.repository.CasoRepository;
import java.time.Instant;
import java.time.ZoneOffset;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final CasoRepository casoRepository;
    private final MongoOperations mongoTemplate;

    public DashboardService(CasoRepository casoRepository, MongoOperations mongoTemplate) {
        this.casoRepository = casoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Cacheable("dashboard-resumen")
    public DashboardResumen resumen() {
        long casosActivos = casoRepository.countByEstado(EstadoCaso.ACTIVO);
        long alertasActivas = mongoTemplate.count(new Query(Criteria.where("alertas_emitidas.0").exists(true)
                .and("estado").is(EstadoCaso.ACTIVO)), Caso.class);
        long alertasHoy = contarAlertasDesde(inicioDelDiaUtc());
        long resueltosMes = mongoTemplate.count(new Query(Criteria.where("estado").is(EstadoCaso.RESUELTO)
                .and("fecha_activacion").gte(inicioDelMesUtc())), Caso.class);

        return new DashboardResumen(alertasActivas, casosActivos, alertasHoy, resueltosMes, 18);
    }

    private long contarAlertasDesde(Instant desde) {
        Query query = new Query(Criteria.where("alertas_emitidas.timestamp").gte(desde));
        return mongoTemplate.find(query, Caso.class).stream()
                .flatMap(caso -> caso.getAlertasEmitidas().stream())
                .filter(alerta -> alerta.getTimestamp() != null && !alerta.getTimestamp().isBefore(desde))
                .count();
    }

    private Instant inicioDelDiaUtc() {
        return Instant.now().atOffset(ZoneOffset.UTC).toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant inicioDelMesUtc() {
        var now = Instant.now().atOffset(ZoneOffset.UTC);
        return now.withDayOfMonth(1).toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
