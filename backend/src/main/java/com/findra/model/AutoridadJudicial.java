package com.findra.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AutoridadJudicial {

    private String juez;
    private String fiscal;

    @Field("nro_expediente")
    private String nroExpediente;

    public AutoridadJudicial() {
    }

    public AutoridadJudicial(String juez, String fiscal, String nroExpediente) {
        this.juez = juez;
        this.fiscal = fiscal;
        this.nroExpediente = nroExpediente;
    }

    public String getJuez() {
        return juez;
    }

    public void setJuez(String juez) {
        this.juez = juez;
    }

    public String getFiscal() {
        return fiscal;
    }

    public void setFiscal(String fiscal) {
        this.fiscal = fiscal;
    }

    public String getNroExpediente() {
        return nroExpediente;
    }

    public void setNroExpediente(String nroExpediente) {
        this.nroExpediente = nroExpediente;
    }
}
