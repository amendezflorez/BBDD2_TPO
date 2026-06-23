package com.findra.controller;

import com.findra.dto.EmitirAlertaRequest;
import com.findra.dto.EstadoCasoRequest;
import com.findra.dto.ReporteRequest;
import com.findra.model.Caso;
import com.findra.model.EstadoCaso;
import com.findra.service.CasoService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/casos")
public class CasoController {

    private final CasoService casoService;

    public CasoController(CasoService casoService) {
        this.casoService = casoService;
    }

    @GetMapping
    public List<Caso> buscar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoCaso estado,
            @RequestParam(required = false) String zona,
            @RequestParam(required = false) Integer edadMin,
            @RequestParam(required = false) Integer edadMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return casoService.buscar(texto, estado, zona, edadMin, edadMax, page, size);
    }

    @GetMapping("/{casoId}")
    public Caso obtener(@PathVariable String casoId) {
        return casoService.obtenerPorCasoId(casoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Caso crear(@Valid @RequestBody Caso caso) {
        return casoService.crear(caso);
    }

    @PatchMapping("/{casoId}/estado")
    public Caso actualizarEstado(
            @PathVariable String casoId,
            @Valid @RequestBody EstadoCasoRequest request) {
        return casoService.actualizarEstado(casoId, request);
    }

    @PostMapping("/{casoId}/alertas")
    public Caso emitirAlertas(
            @PathVariable String casoId,
            @Valid @RequestBody EmitirAlertaRequest request) {
        return casoService.emitirAlertas(casoId, request);
    }

    @PostMapping("/{casoId}/reportes")
    public Caso registrarReporte(
            @PathVariable String casoId,
            @Valid @RequestBody ReporteRequest request) {
        return casoService.registrarReporte(casoId, request);
    }

    @PostMapping("/{casoId}/documentos")
    @ResponseStatus(HttpStatus.CREATED)
    public Caso subirDocumento(
            @PathVariable String casoId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "tipo", defaultValue = "Documento") String tipo,
            @RequestParam(value = "operador", required = false) String operador) throws IOException {
        return casoService.subirDocumento(casoId, file, tipo, operador);
    }

    @GetMapping("/{casoId}/documentos/{gridFsId}")
    public ResponseEntity<Resource> descargarDocumento(
            @PathVariable String casoId,
            @PathVariable String gridFsId) throws IOException {
        GridFsResource resource = casoService.descargarDocumento(gridFsId);
        String contentType = resource.getContentType();
        MediaType mediaType = (contentType != null)
                ? MediaType.parseMediaType(contentType)
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(resource.getFilename(), StandardCharsets.UTF_8).build().toString())
                .body(resource);
    }
}
