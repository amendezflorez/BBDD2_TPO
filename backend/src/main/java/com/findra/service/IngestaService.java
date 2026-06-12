package com.findra.service;

import com.findra.dto.ingesta.IngestaRequest;
import com.findra.dto.ingesta.IngestaResponse;
import com.findra.model.Caso;
import com.findra.repository.CasoRepository;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class IngestaService {

    private final IngestaMapper mapper;
    private final CasoService casoService;
    private final CasoRepository casoRepository;

    public IngestaService(IngestaMapper mapper, CasoService casoService, CasoRepository casoRepository) {
        this.mapper = mapper;
        this.casoService = casoService;
        this.casoRepository = casoRepository;
    }

    public IngestaResponse procesar(IngestaRequest request) {
        String organismo = request.organismo().toUpperCase();
        String tipo = request.tipoFuente().toLowerCase();
        Map<String, Object> payload = request.payload();

        return switch (tipo) {
            case "denuncia_formal" -> {
                Caso caso = mapper.mapDenunciaFormal(organismo, payload);
                Caso guardado = casoService.crear(caso);
                yield new IngestaResponse(guardado.getCasoId(), "caso_creado", organismo, Instant.now());
            }
            case "notificacion_alerta" -> {
                String casoRef = str(payload, "caso_ref");
                if (casoRef == null) throw new IllegalArgumentException("payload debe incluir caso_ref");
                Caso caso = casoService.obtenerPorCasoId(casoRef);
                mapper.mapNotificacionAlerta(caso, organismo, payload);
                casoRepository.save(caso);
                yield new IngestaResponse(casoRef, "alerta_agregada", organismo, Instant.now());
            }
            case "notificacion_judicial" -> {
                String casoRef = str(payload, "caso_ref");
                Caso caso = casoService.obtenerPorCasoId(casoRef);
                mapper.mapNotificacionJudicial(caso, organismo, payload);
                casoRepository.save(caso);
                yield new IngestaResponse(casoRef, "judicial_actualizado", organismo, Instant.now());
            }
            case "reporte_avistamiento" -> {
                String casoRef = str(payload, "caso_ref");
                Caso caso = casoService.obtenerPorCasoId(casoRef);
                mapper.mapReporteAvistamiento(caso, organismo, payload);
                casoRepository.save(caso);
                yield new IngestaResponse(casoRef, "reporte_agregado", organismo, Instant.now());
            }
            default -> throw new IllegalArgumentException("tipoFuente no reconocido: " + tipo);
        };
    }

    private String str(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v != null ? v.toString() : null;
    }
}
