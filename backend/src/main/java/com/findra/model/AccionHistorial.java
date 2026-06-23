package com.findra.model;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Field;

public class AccionHistorial {

    private String accion;

    @Field("operador")
    private String operador;

    private Instant timestamp;
    private String detalle;

    public AccionHistorial() {
    }

    public AccionHistorial(String accion, String operador, Instant timestamp, String detalle) {
        this.accion = accion;
        this.operador = operador;
        this.timestamp = timestamp;
        this.detalle = detalle;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
