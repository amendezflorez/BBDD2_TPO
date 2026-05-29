package com.findra.model;

import java.time.Instant;

public class AccionHistorial {

    private String accion;
    private String usuario;
    private Instant timestamp;
    private String detalle;

    public AccionHistorial() {
    }

    public AccionHistorial(String accion, String usuario, Instant timestamp, String detalle) {
        this.accion = accion;
        this.usuario = usuario;
        this.timestamp = timestamp;
        this.detalle = detalle;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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
