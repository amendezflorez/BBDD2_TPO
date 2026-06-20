package com.findra.controller;

import com.findra.dto.AlertaResumenDto;
import com.findra.service.CasoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final CasoService casoService;

    public AlertaController(CasoService casoService) {
        this.casoService = casoService;
    }

    @GetMapping
    public List<AlertaResumenDto> obtenerTodas() {
        return casoService.obtenerResumenAlertas();
    }
}
