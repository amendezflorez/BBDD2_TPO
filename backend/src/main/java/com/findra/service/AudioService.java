package com.findra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findra.dto.audio.AudioExtractionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class AudioService {

    private static final Logger log = LoggerFactory.getLogger(AudioService.class);

    private final String geminiApiKey;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public AudioService(
            @Value("${gemini.api.key:}") String geminiApiKey,
            ObjectMapper objectMapper) {
        this.geminiApiKey = geminiApiKey;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create();
    }

    public AudioExtractionResponse procesarAudio(MultipartFile file) {
        if (geminiApiKey == null || geminiApiKey.trim().isEmpty() || geminiApiKey.contains("RELLENO") || geminiApiKey.contains("KEY")) {
            log.info("Gemini API Key no configurada o vacía. Ejecutando en Modo Simulación.");
            return obtenerRespuestaSimulada();
        }

        try {
            byte[] fileBytes = file.getBytes();
            String base64Data = Base64.getEncoder().encodeToString(fileBytes);
            String mimeType = file.getContentType();
            if (mimeType == null || mimeType.isEmpty()) {
                mimeType = "audio/mp3"; // Fallback
            }

            log.info("Enviando archivo de audio ({}, {} bytes) a la API de Gemini 1.5 Flash...", mimeType, fileBytes.length);

            String promptText = "Analiza este audio donde un operador o denunciante relata la desaparición de un menor en el marco del protocolo Alerta Sofía en Argentina. "
                    + "Extrae la información en formato JSON con la siguiente estructura exacta:\n"
                    + "{\n"
                    + "  \"menorNombre\": \"Nombre completo del menor (sin números ni caracteres raros)\",\n"
                    + "  \"menorEdad\": 12 (Edad en formato numérico entero),\n"
                    + "  \"menorSexo\": \"M\" o \"F\",\n"
                    + "  \"menorCabello\": \"Descripción corta del cabello\",\n"
                    + "  \"menorOjos\": \"Descripción corta de ojos\",\n"
                    + "  \"menorEstatura\": 1.45 (Estatura en metros, formato decimal),\n"
                    + "  \"menorRopa\": \"Detalle de la ropa que vestía\",\n"
                    + "  \"menorSenas\": \"Señas particulares relevantes\",\n"
                    + "  \"zona\": \"Provincia o localidad de desaparición\",\n"
                    + "  \"menorLat\": -34.6037 (Latitud en formato decimal, aproximada para la zona si no se da una exacta),\n"
                    + "  \"menorLng\": -58.3816 (Longitud en formato decimal, aproximada para la zona si no se da una exacta),\n"
                    + "  \"denuncianteNombre\": \"Nombre completo del denunciante\",\n"
                    + "  \"denuncianteVinculo\": \"Relación con el menor, ej: madre, padre, tío\",\n"
                    + "  \"denuncianteTel\": \"Teléfono de contacto\",\n"
                    + "  \"juez\": \"Nombre del Juez interviniente\",\n"
                    + "  \"fiscal\": \"Nombre del Fiscal interviniente\",\n"
                    + "  \"nroExpediente\": \"Número de expediente judicial\"\n"
                    + "}\n"
                    + "Completa solo los campos de los que haya información en el audio. Si no hay datos claros sobre un campo de texto, déjalo vacío (\"\"). "
                    + "Si no hay latitud/longitud dadas, aproxima las coordenadas para la provincia o localidad mencionada. Devuelve SOLAMENTE el objeto JSON sin envoltorios de código markdown.";

            // Build request body matching Gemini API specification
            Map<String, Object> inlineData = Map.of(
                    "mimeType", mimeType,
                    "data", base64Data
            );
            Map<String, Object> partAudio = Map.of("inlineData", inlineData);
            Map<String, Object> partText = Map.of("text", promptText);
            Map<String, Object> content = Map.of("parts", List.of(partAudio, partText));
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(content),
                    "generationConfig", Map.of("responseMimeType", "application/json")
            );

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey.trim();

            String responseBody = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            if (responseBody == null) {
                throw new RuntimeException("Respuesta vacía de la API de Gemini");
            }

            JsonNode rootNode = objectMapper.readTree(responseBody);
            String jsonText = rootNode.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            log.info("JSON extraído exitosamente de la API de Gemini: {}", jsonText);

            return objectMapper.readValue(jsonText, AudioExtractionResponse.class);

        } catch (IOException e) {
            log.error("Error al leer el archivo de audio subido", e);
            throw new RuntimeException("Error al procesar los bytes del archivo de audio: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error al invocar o parsear la API de Gemini", e);
            throw new RuntimeException("Error en el procesamiento de inteligencia artificial: " + e.getMessage());
        }
    }

    private AudioExtractionResponse obtenerRespuestaSimulada() {
        AudioExtractionResponse response = new AudioExtractionResponse();
        response.setMenorNombre("Martín Gómez");
        response.setMenorEdad(8);
        response.setMenorSexo("M");
        response.setMenorCabello("castaño corto");
        response.setMenorOjos("marrones");
        response.setMenorEstatura(1.25);
        response.setMenorRopa("buzo rojo con capucha, jean azul y zapatillas blancas");
        response.setMenorSenas("pequeña cicatriz sobre la ceja derecha");
        response.setZona("Palermo, CABA");
        response.setMenorLat(-34.5826);
        response.setMenorLng(-58.4184);
        response.setDenuncianteNombre("Laura Fernández");
        response.setDenuncianteVinculo("madre");
        response.setDenuncianteTel("11-4567-8901");
        response.setJuez("Dr. Ariel Lijo");
        response.setFiscal("Dra. Paula Asaro");
        response.setNroExpediente("EXP-98765/2026");
        return response;
    }
}
