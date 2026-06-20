package com.findra.dto;

import com.findra.model.ReporteCiudadano;
import java.time.Instant;
import java.util.List;

public record ReporteResumenDto(
        String casoId,
        String menorNombre,
        int menorEdad,
        String zona,
        String estadoCaso,
        List<ReporteCiudadano> reportes,
        Instant ultimoReporte,
        int totalReportes) {}
