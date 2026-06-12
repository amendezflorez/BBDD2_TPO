package com.findra.dto.ingesta;

import java.time.Instant;

public record IngestaResponse(
        String casoId,
        String accion,
        String organismo,
        Instant timestamp) {
}
