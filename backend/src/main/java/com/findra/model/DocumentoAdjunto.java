package com.findra.model;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Field;

public class DocumentoAdjunto {

    private String tipo;
    private String url;

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
