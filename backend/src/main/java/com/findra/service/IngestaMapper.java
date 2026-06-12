package com.findra.service;

import com.findra.model.AccionHistorial;
import com.findra.model.Alerta;
import com.findra.model.AutoridadJudicial;
import com.findra.model.Caso;
import com.findra.model.Denunciante;
import com.findra.model.DocumentoAdjunto;
import com.findra.model.EstadoAlerta;
import com.findra.model.EstadoReporte;
import com.findra.model.Menor;
import com.findra.model.ReporteCiudadano;
import com.findra.model.Ubicacion;
import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@SuppressWarnings("unchecked")
@Component
public class IngestaMapper {

    public Caso mapDenunciaFormal(String organismo, Map<String, Object> payload) {
        String casoId = "AS-" + Year.now() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();

        Menor menor = new Menor();
        menor.setNombre(str(payload, "menor_nombre"));
        Integer edad = intVal(payload, "menor_edad");
        if (edad != null) menor.setEdad(edad);
        menor.setSexo(str(payload, "menor_sexo"));
        menor.setFotoUrl(str(payload, "menor_foto") != null ? str(payload, "menor_foto") : "/media/" + casoId + "/foto_principal.jpg");

        String descripcionFisica = str(payload, "descripcion_fisica");
        if (descripcionFisica != null) {
            menor.setSenas(descripcionFisica);
        }

        Double lat = dbl(payload, "ultima_lat");
        Double lng = dbl(payload, "ultima_lng");
        if (lat != null && lng != null) {
            menor.setUltimaUbicacion(new Ubicacion(lng, lat, str(payload, "ultima_ubicacion_desc")));
        }

        Denunciante denunciante = new Denunciante();
        denunciante.setNombre(str(payload, "denunciante_nombre"));
        denunciante.setVinculo(str(payload, "denunciante_vinculo"));
        denunciante.setTel(str(payload, "denunciante_tel"));

        AutoridadJudicial autoridad = null;
        String juez = str(payload, "juez");
        String fiscal = str(payload, "fiscal");
        String nroExp = str(payload, "nro_expediente");
        if (juez != null || fiscal != null || nroExp != null) {
            autoridad = new AutoridadJudicial();
            autoridad.setJuez(juez);
            autoridad.setFiscal(fiscal);
            autoridad.setNroExpediente(nroExp);
        }

        Caso caso = new Caso();
        caso.setCasoId(casoId);
        caso.setZona(str(payload, "zona"));
        caso.setMenor(menor);
        caso.setDenunciante(denunciante);
        caso.setAutoridadJudicial(autoridad);

        Object docs = payload.get("documentos");
        if (docs instanceof List<?> lista) {
            for (Object item : lista) {
                if (item instanceof Map<?, ?> docMap) {
                    Map<String, Object> doc = (Map<String, Object>) docMap;
                    caso.getDocumentosAdjuntos().add(new DocumentoAdjunto(
                            str(doc, "tipo"),
                            str(doc, "url"),
                            organismo,
                            instant(doc, "timestamp")));
                }
            }
        }

        return caso;
    }

    public void mapNotificacionAlerta(Caso caso, String organismo, Map<String, Object> payload) {
        String operador = str(payload, "operador");
        Instant now = Instant.now();

        Object canales = payload.get("canales_activados");
        if (canales instanceof List<?> lista) {
            for (Object item : lista) {
                if (item instanceof Map<?, ?> canalMap) {
                    Map<String, Object> c = (Map<String, Object>) canalMap;
                    Alerta alerta = new Alerta();
                    alerta.setCanal(str(c, "canal"));
                    alerta.setZona(str(c, "zona"));
                    alerta.setTimestamp(instant(c, "timestamp"));
                    alerta.setPlataforma(str(c, "plataforma"));
                    alerta.setOperador(operador != null ? operador : organismo);
                    alerta.setEstado(EstadoAlerta.ENVIADA);
                    caso.getAlertasEmitidas().add(alerta);
                }
            }
        }

        caso.getHistorialAcciones().add(new AccionHistorial(
                "alerta_emitida",
                organismo,
                now,
                "Notificación de alerta recibida de " + organismo));
    }

    public void mapNotificacionJudicial(Caso caso, String organismo, Map<String, Object> payload) {
        String fiscal = str(payload, "fiscal_actuante");
        String juez = str(payload, "juez_interviniente");
        String nroExp = str(payload, "nro_expediente");

        if (fiscal != null || juez != null || nroExp != null) {
            AutoridadJudicial autoridad = caso.getAutoridadJudicial();
            if (autoridad == null) {
                autoridad = new AutoridadJudicial();
                caso.setAutoridadJudicial(autoridad);
            }
            if (fiscal != null) autoridad.setFiscal(fiscal);
            if (juez != null) autoridad.setJuez(juez);
            if (nroExp != null) autoridad.setNroExpediente(nroExp);
        }

        Object docs = payload.get("documentos_judiciales");
        if (docs instanceof List<?> lista) {
            for (Object item : lista) {
                if (item instanceof Map<?, ?> docMap) {
                    Map<String, Object> doc = (Map<String, Object>) docMap;
                    caso.getDocumentosAdjuntos().add(new DocumentoAdjunto(
                            str(doc, "tipo"),
                            str(doc, "url"),
                            organismo,
                            instant(doc, "timestamp")));
                }
            }
        }

        caso.getHistorialAcciones().add(new AccionHistorial(
                "actualizacion_judicial",
                organismo,
                Instant.now(),
                "Actualización judicial recibida de " + organismo));
    }

    public void mapReporteAvistamiento(Caso caso, String organismo, Map<String, Object> payload) {
        Double lat = dbl(payload, "avistamiento_lat");
        Double lng = dbl(payload, "avistamiento_lng");

        ReporteCiudadano reporte = new ReporteCiudadano();
        reporte.setTimestamp(instant(payload, "timestamp"));
        if (lat != null && lng != null) {
            reporte.setUbicacion(new Ubicacion(lng, lat, null));
        }
        reporte.setDescripcion(str(payload, "descripcion"));
        reporte.setContacto(str(payload, "contacto"));

        String estadoStr = str(payload, "estado");
        reporte.setEstado("verificado".equalsIgnoreCase(estadoStr) ? EstadoReporte.VERIFICADO : EstadoReporte.RECIBIDO);

        caso.getReportesCiudadanos().add(reporte);

        caso.getHistorialAcciones().add(new AccionHistorial(
                "reporte_ciudadano_registrado",
                organismo,
                Instant.now(),
                str(payload, "descripcion")));
    }

    private String str(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v != null ? v.toString() : null;
    }

    private Double dbl(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return null;
        return v instanceof Number n ? n.doubleValue() : Double.parseDouble(v.toString());
    }

    private Integer intVal(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return null;
        return v instanceof Number n ? n.intValue() : Integer.parseInt(v.toString());
    }

    private Instant instant(Map<String, Object> m, String key) {
        String s = str(m, key);
        return s != null ? Instant.parse(s) : Instant.now();
    }
}
