package com.findra.controller;

import com.findra.dto.ingesta.IngestaRequest;
import com.findra.dto.ingesta.IngestaResponse;
import com.findra.service.IngestaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingesta")
public class IngestaController {

    private final IngestaService ingestaService;

    public IngestaController(IngestaService ingestaService) {
        this.ingestaService = ingestaService;
    }

    @PostMapping("/organismo")
    @ResponseStatus(HttpStatus.CREATED)
    public IngestaResponse ingestar(@Valid @RequestBody IngestaRequest request) {
        return ingestaService.procesar(request);
    }
}
