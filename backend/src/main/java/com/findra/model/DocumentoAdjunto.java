package com.findra.model;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Field;

public class DocumentoAdjunto {

    private String tipo;

    /** Nombre original del archivo. */
    private String url;

    /** ID del objeto en GridFS. */
    @Field("grid_fs_id")
    private String gridFsId;

    private String organismo;

    private Instant timestamp;

    private String transcripcion;

    public DocumentoAdjunto() {
    }

    public DocumentoAdjunto(String tipo, String url, String organismo, Instant timestamp) {
        this.tipo = tipo;
        this.url = url;
        this.organismo = organismo;
        this.timestamp = timestamp;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGridFsId() {
        return gridFsId;
    }

    public void setGridFsId(String gridFsId) {
        this.gridFsId = gridFsId;
    }

    public String getOrganismo() {
        return organismo;
    }

    public void setOrganismo(String organismo) {
        this.organismo = organismo;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getTranscripcion() {
        return transcripcion;
    }

    public void setTranscripcion(String transcripcion) {
        this.transcripcion = transcripcion;
    }
}
