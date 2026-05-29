package com.findra.dto;

import com.findra.model.EstadoCaso;
import jakarta.validation.constraints.NotNull;

public record EstadoCasoRequest(
        @NotNull EstadoCaso estado,
        String resultado,
        String operador) {
}
