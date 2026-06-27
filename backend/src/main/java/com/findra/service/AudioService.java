package com.findra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findra.dto.audio.AudioExtractionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AudioService {

    private static final Logger log = LoggerFactory.getLogger(AudioService.class);

    private final String whisperUrl;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public AudioService(
            @Value("${whisper.url:http://localhost:8001}") String whisperUrl,
            ObjectMapper objectMapper) {
        this.whisperUrl = whisperUrl;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public AudioExtractionResponse procesarAudio(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "audio.webm";
            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() { return filename; }
            };

            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(contentType));

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new HttpEntity<>(resource, fileHeaders));

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, requestHeaders);

            log.info("Enviando audio ({}, {} bytes) al servicio Whisper...", contentType, file.getSize());

            ResponseEntity<String> response = restTemplate.postForEntity(
                    whisperUrl + "/transcribir", request, String.class);

            JsonNode node = objectMapper.readTree(response.getBody());
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
