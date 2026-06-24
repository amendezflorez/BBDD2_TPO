package com.findra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {

    private static final Logger log = LoggerFactory.getLogger(GeocodingService.class);
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private static final String USER_AGENT = "Findra/1.0 (findra.gob.ar)";
    // Sentinel para zonas que no pudieron resolverse, evita llamadas repetidas a la API
    private static final String SIN_RESULTADO = "";

    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeocodingService(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * Resuelve a qué provincia argentina pertenece una zona (ciudad, barrio, localidad).
     * Llama a Nominatim la primera vez y cachea el resultado para peticiones posteriores.
     * Retorna null si la zona no pudo resolverse.
     */
    public String resolverProvincia(String zona) {
        if (zona == null || zona.isBlank()) {
            return null;
        }
        String resultado = cache.computeIfAbsent(zona.trim(), this::fetchProvincia);
        return resultado.isEmpty() ? null : resultado;
    }

    private String fetchProvincia(String zona) {
        try {
            String query = URLEncoder.encode(zona + ", Argentina", StandardCharsets.UTF_8);
            String url = NOMINATIM_URL + "?q=" + query
                    + "&format=json&addressdetails=1&limit=1&countrycodes=ar&accept-language=es";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode results = objectMapper.readTree(response.body());

            if (results.isArray() && !results.isEmpty()) {
                JsonNode state = results.get(0).path("address").path("state");
                if (!state.isMissingNode() && !state.asText().isBlank()) {
                    log.debug("Zona '{}' resuelta a provincia '{}'", zona, state.asText());
                    return state.asText();
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo geocodificar zona '{}': {}", zona, e.getMessage());
        }
        return SIN_RESULTADO;
    }
}
