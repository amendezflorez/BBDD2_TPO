package com.findra.dto;

public record DashboardResumen(
        long alertasActivas,
        long casosActivos,
        long alertasEmitidasHoy,
        long casosResueltosMes,
        long tiempoPromedioActivacionMinutos) {
}
