package com.findra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReporteRequest(
        @NotNull Double longitude,
        @NotNull Double latitude,
        @NotBlank String descripcion,
        String contacto,
        String operador) {
}
