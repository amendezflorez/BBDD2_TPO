package com.findra.dto.audio;

public class AudioExtractionResponse {

    private String transcripcion;

    public AudioExtractionResponse() {}

    public AudioExtractionResponse(String transcripcion) {
        this.transcripcion = transcripcion;
    }

    public String getTranscripcion() { return transcripcion; }
    public void setTranscripcion(String transcripcion) { this.transcripcion = transcripcion; }
}
