package com.findra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findra.dto.audio.AudioExtractionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AudioService {

    private static final Logger log = LoggerFactory.getLogger(AudioService.class);

    private final String whisperUrl;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public AudioService(
            @Value("${whisper.url:http://localhost:8001}") String whisperUrl,
            ObjectMapper objectMapper) {
        this.whisperUrl = whisperUrl;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create();
    }

    public AudioExtractionResponse procesarAudio(MultipartFile file) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "audio.webm";
                }
            });

            log.info("Enviando audio ({}, {} bytes) al servicio Whisper...", file.getContentType(), file.getSize());

            String response = restClient.post()
                    .uri(whisperUrl + "/transcribir")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode node = objectMapper.readTree(response);
            String texto = node.path("texto").asText();
            log.info("Transcripción recibida: {}", texto);
            return new AudioExtractionResponse(texto);

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de audio: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de transcripción: " + e.getMessage());
        }
    }
}
