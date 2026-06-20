package com.findra.dto;

import com.findra.model.Alerta;
import java.time.Instant;
import java.util.List;

public record AlertaResumenDto(
        String casoId,
        String menorNombre,
        int menorEdad,
        String zona,
        String estadoCaso,
        List<Alerta> alertas,
        Instant ultimaAlerta,
        int totalAlertas) {}
