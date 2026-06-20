package com.findra.service;

import com.findra.dto.EmitirAlertaRequest;
import com.findra.dto.EstadoCasoRequest;
import com.findra.dto.ReporteRequest;
import com.findra.exception.NotFoundException;
import com.findra.model.AccionHistorial;
import com.findra.model.Alerta;
import com.findra.model.Caso;
import com.findra.model.EstadoAlerta;
import com.findra.model.EstadoCaso;
import com.findra.model.EstadoReporte;
import com.findra.model.ReporteCiudadano;
import com.findra.model.Ubicacion;
import com.findra.repository.CasoRepository;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CasoService {

    private static final String OPERADOR_DEFAULT = "OP_FINDRA";

    private final CasoRepository casoRepository;
    private final MongoOperations mongoTemplate;

    public CasoService(CasoRepository casoRepository, MongoOperations mongoTemplate) {
        this.casoRepository = casoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Caso> buscar(
            String texto,
            EstadoCaso estado,
            String zona,
            Integer edadMin,
            Integer edadMax,
            int page,
            int size) {
        Query query = new Query();

        if (texto != null && !texto.isBlank()) {
            Pattern pattern = Pattern.compile(Pattern.quote(texto.trim()), Pattern.CASE_INSENSITIVE);
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("caso_id").regex(pattern),
                    Criteria.where("menor.nombre").regex(pattern),
                    Criteria.where("autoridad_judicial.nro_expediente").regex(pattern)));
        }
        if (estado != null) {
            query.addCriteria(Criteria.where("estado").is(estado));
        }
        if (zona != null && !zona.isBlank()) {
            query.addCriteria(Criteria.where("zona").regex(
                    Pattern.compile(Pattern.quote(zona.trim()), Pattern.CASE_INSENSITIVE)));
        }
        if (edadMin != null || edadMax != null) {
            Criteria edadCriteria = Criteria.where("menor.edad");
            if (edadMin != null) {
                edadCriteria.gte(edadMin);
            }
            if (edadMax != null) {
                edadCriteria.lte(edadMax);
            }
            query.addCriteria(edadCriteria);
        }

        int boundedSize = Math.min(Math.max(size, 1), 50);
        query.with(PageRequest.of(Math.max(page, 0), boundedSize));
        query.with(Sort.by(Sort.Direction.DESC, "fecha_activacion"));
        return mongoTemplate.find(query, Caso.class);
    }

    public Caso obtenerPorCasoId(String casoId) {
        return casoRepository.findByCasoId(casoId)
                .orElseThrow(() -> new NotFoundException("Caso no encontrado: " + casoId));
    }

    @CacheEvict(value = "dashboard-resumen", allEntries = true)
    public Caso crear(Caso caso) {
        int year = Instant.now().atOffset(ZoneOffset.UTC).getYear();
        long siguiente = mongoTemplate.count(
                Query.query(Criteria.where("caso_id").regex("^AS-" + year + "-")),
                Caso.class) + 1;
        caso.setCasoId(String.format("AS-%d-%03d", year, siguiente));
        caso.setFechaActivacion(Instant.now());
        caso.setEstado(EstadoCaso.ACTIVO);
        caso.getHistorialAcciones().add(new AccionHistorial(
                "caso_creado",
                OPERADOR_DEFAULT,
                Instant.now(),
                "Caso registrado desde API"));
        return casoRepository.save(caso);
    }

    @CacheEvict(value = "dashboard-resumen", allEntries = true)
    public Caso actualizarEstado(String casoId, EstadoCasoRequest request) {
        Caso caso = obtenerPorCasoId(casoId);
        caso.setEstado(request.estado());
        caso.setResultado(request.resultado());
        caso.getHistorialAcciones().add(new AccionHistorial(
                "estado_actualizado",
                operador(request.operador()),
                Instant.now(),
                "Estado cambiado a " + request.estado()));
        return casoRepository.save(caso);
    }

    @CacheEvict(value = "dashboard-resumen", allEntries = true)
    public Caso emitirAlertas(String casoId, EmitirAlertaRequest request) {
        Caso caso = obtenerPorCasoId(casoId);
        Instant now = Instant.now();
        EstadoAlerta estado = Boolean.TRUE.equals(request.requiereAutorizacion())
                ? EstadoAlerta.REQUIERE_AUTORIZACION
                : EstadoAlerta.ENVIADA;

        request.canales().forEach(canal -> {
            Alerta alerta = new Alerta();
            alerta.setCanal(canal);
            alerta.setTimestamp(now);
            alerta.setZona(caso.getZona());
            alerta.setOperador(operador(request.operador()));
            alerta.setEstado(estado);
            alerta.setObservaciones(request.observaciones());
            caso.getAlertasEmitidas().add(alerta);
        });

        caso.getHistorialAcciones().add(new AccionHistorial(
                "alerta_emitida",
                operador(request.operador()),
                now,
                String.join(", ", request.canales())));
        return casoRepository.save(caso);
    }

    @CacheEvict(value = "dashboard-resumen", allEntries = true)
    public Caso registrarReporte(String casoId, ReporteRequest request) {
        Caso caso = obtenerPorCasoId(casoId);
        ReporteCiudadano reporte = new ReporteCiudadano();
        reporte.setTimestamp(Instant.now());
        reporte.setUbicacion(new Ubicacion(request.longitude(), request.latitude(), "Reporte ciudadano"));
        reporte.setDescripcion(request.descripcion());
        reporte.setContacto(request.contacto());
        reporte.setEstado(EstadoReporte.RECIBIDO);
        caso.getReportesCiudadanos().add(reporte);

        caso.getHistorialAcciones().add(new AccionHistorial(
                "reporte_ciudadano_registrado",
                operador(request.operador()),
                Instant.now(),
                request.descripcion()));
        return casoRepository.save(caso);
    }

    private String operador(String operador) {
        return operador == null || operador.isBlank() ? OPERADOR_DEFAULT : operador;
    }
}
