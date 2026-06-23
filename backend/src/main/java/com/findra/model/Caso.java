package com.findra.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "casos")
public class Caso {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("caso_id")
    private String casoId;

    private EstadoCaso estado;

    @Field("fecha_activacion")
    private Instant fechaActivacion;

    @Field("fecha_cierre")
    private Instant fechaCierre;

    private String zona;
    private Menor menor;
    private Denunciante denunciante;

    @Field("autoridad_judicial")
    private AutoridadJudicial autoridadJudicial;

    @Field("alertas_emitidas")
    private List<Alerta> alertasEmitidas = new ArrayList<>();

    @Field("reportes_ciudadanos")
    private List<ReporteCiudadano> reportesCiudadanos = new ArrayList<>();

    @Field("documentos_adjuntos")
    private List<DocumentoAdjunto> documentosAdjuntos = new ArrayList<>();

    @Field("historial_acciones")
    private List<AccionHistorial> historialAcciones = new ArrayList<>();

    private String resultado;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCasoId() {
        return casoId;
    }

    public void setCasoId(String casoId) {
        this.casoId = casoId;
    }

    public EstadoCaso getEstado() {
        return estado;
    }

    public void setEstado(EstadoCaso estado) {
        this.estado = estado;
    }

    public Instant getFechaActivacion() {
        return fechaActivacion;
    }

    public void setFechaActivacion(Instant fechaActivacion) {
        this.fechaActivacion = fechaActivacion;
    }

    public Instant getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Instant fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public Menor getMenor() {
        return menor;
    }

    public void setMenor(Menor menor) {
        this.menor = menor;
    }

    public Denunciante getDenunciante() {
        return denunciante;
    }

    public void setDenunciante(Denunciante denunciante) {
        this.denunciante = denunciante;
    }

    public AutoridadJudicial getAutoridadJudicial() {
        return autoridadJudicial;
    }

    public void setAutoridadJudicial(AutoridadJudicial autoridadJudicial) {
        this.autoridadJudicial = autoridadJudicial;
    }

    public List<Alerta> getAlertasEmitidas() {
        return alertasEmitidas;
    }

    public void setAlertasEmitidas(List<Alerta> alertasEmitidas) {
        this.alertasEmitidas = alertasEmitidas;
    }

    public List<ReporteCiudadano> getReportesCiudadanos() {
        return reportesCiudadanos;
    }

    public void setReportesCiudadanos(List<ReporteCiudadano> reportesCiudadanos) {
        this.reportesCiudadanos = reportesCiudadanos;
    }

    public List<DocumentoAdjunto> getDocumentosAdjuntos() {
        return documentosAdjuntos;
    }

    public void setDocumentosAdjuntos(List<DocumentoAdjunto> documentosAdjuntos) {
        this.documentosAdjuntos = documentosAdjuntos;
    }

    public List<AccionHistorial> getHistorialAcciones() {
        return historialAcciones;
    }

    public void setHistorialAcciones(List<AccionHistorial> historialAcciones) {
        this.historialAcciones = historialAcciones;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
