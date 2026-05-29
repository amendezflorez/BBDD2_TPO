package com.findra.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record EmitirAlertaRequest(
        @NotEmpty List<String> canales,
        String observaciones,
        @NotNull Boolean requiereAutorizacion,
        String operador) {
}
