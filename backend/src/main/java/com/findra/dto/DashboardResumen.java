package com.findra.dto;

import java.io.Serializable;

public record DashboardResumen(
        long alertasActivas,
        long casosActivos,
        long alertasEmitidasHoy,
        long casosResueltosMes,
        long tiempoPromedioActivacionMinutos) implements Serializable {
}
