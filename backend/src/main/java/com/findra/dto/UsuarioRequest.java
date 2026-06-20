package com.findra.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
        @NotBlank String nombre,
        @NotBlank String rol,
        @NotBlank String organismo) {}
