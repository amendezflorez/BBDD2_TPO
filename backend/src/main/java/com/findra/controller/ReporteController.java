package com.findra.controller;

import com.findra.dto.ReporteResumenDto;
import com.findra.service.CasoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final CasoService casoService;

    public ReporteController(CasoService casoService) {
        this.casoService = casoService;
    }

    @GetMapping
    public List<ReporteResumenDto> obtenerTodos() {
        return casoService.obtenerResumenReportes();
    }
}
