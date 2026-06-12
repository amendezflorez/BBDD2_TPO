package com.findra.dto.ingesta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record IngestaRequest(
        @NotBlank String organismo,
        @NotBlank String tipoFuente,
        @NotNull Map<String, Object> payload) {
}
