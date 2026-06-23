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

    @Field("subido_por")
    private String subidoPor;

    private Instant timestamp;

    public DocumentoAdjunto() {
    }

    public DocumentoAdjunto(String tipo, String url, String subidoPor, Instant timestamp) {
        this.tipo = tipo;
        this.url = url;
        this.subidoPor = subidoPor;
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

    public String getSubidoPor() {
        return subidoPor;
    }

    public void setSubidoPor(String subidoPor) {
        this.subidoPor = subidoPor;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
