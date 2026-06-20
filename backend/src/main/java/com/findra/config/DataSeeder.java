package com.findra.config;

import com.findra.model.AccionHistorial;
import com.findra.model.Alerta;
import com.findra.model.AutoridadJudicial;
import com.findra.model.Caso;
import com.findra.model.Denunciante;
import com.findra.model.DocumentoAdjunto;
import com.findra.model.EstadoAlerta;
import com.findra.model.EstadoCaso;
import com.findra.model.EstadoReporte;
import com.findra.model.Menor;
import com.findra.model.ReporteCiudadano;
import com.findra.model.Ubicacion;
import com.findra.model.Usuario;
import com.findra.repository.CasoRepository;
import com.findra.repository.UsuarioRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CasoRepository casoRepository;
    private final UsuarioRepository usuarioRepository;
    private final boolean seedEnabled;

    public DataSeeder(CasoRepository casoRepository,
            UsuarioRepository usuarioRepository,
            @Value("${findra.seed.enabled}") boolean seedEnabled) {
        this.casoRepository = casoRepository;
        this.usuarioRepository = usuarioRepository;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }

        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    usuario("Op. Lopez", "OPERADOR", "PFA"),
                    usuario("Op. Garcia", "OPERADOR", "SIFEBU"),
                    usuario("Juez Torres", "FISCAL", "PROTEX"),
                    usuario("Dra. Rodriguez", "FISCAL", "PROTEX"),
                    usuario("Coord. Mendez", "COORDINADOR", "SIFEBU"),
                    usuario("Op. Sanchez", "OPERADOR", "GENDARMERIA"),
                    usuario("Sup. Vargas", "SUPERVISOR", "PFA")));
        }

        if (casoRepository.count() > 0) {
            return;
        }

        casoRepository.saveAll(List.of(
                caso("AS-2025-001", "Valentina R.", 9, "F", "CABA", 3,
                        -58.3816, -34.6037, true),
                caso("AS-2025-002", "Mateo G.", 12, "M", "Cordoba", 1,
                        -64.1888, -31.4201, false),
                caso("AS-2025-003", "Sofia P.", 7, "F", "Rosario", 0,
                        -60.6393, -32.9468, true),
                caso("AS-2025-004", "Lucas F.", 15, "M", "Mendoza", 6,
                        -68.8458, -32.8895, false),
                caso("AS-2025-005", "Camila A.", 11, "F", "La Plata", 2,
                        -57.9545, -34.9205, false),
                caso("AS-2025-006", "Tomas M.", 8, "M", "Tucuman", 0,
                        -65.2226, -26.8083, false)));
    }

    private Caso caso(String casoId, String nombre, int edad, String sexo, String zona,
            int horasActivo, double longitude, double latitude, boolean conAlerta) {
        long elapsedMinutes = horasActivo * 60L + 22L;
        Instant activacion = Instant.now().minus(elapsedMinutes, ChronoUnit.MINUTES);
        Caso caso = new Caso();
        caso.setCasoId(casoId);
        caso.setEstado(EstadoCaso.ACTIVO);
        caso.setFechaActivacion(activacion);
        caso.setZona(zona);
        caso.setMenor(menor(nombre, edad, sexo, longitude, latitude, zona));
        caso.setDenunciante(new Denunciante("Maria Ruiz", "madre", "+54 11 5555-1234"));
        caso.setAutoridadJudicial(new AutoridadJudicial(
                "Dra. Torres E.", "Dr. Marquez H.", "2025-CBA-00412"));
        caso.getDocumentosAdjuntos().addAll(List.of(
                new DocumentoAdjunto("IMG", "foto_escolar.jpg", "PFA",
                        activacion.plus(offset(elapsedMinutes, 12), ChronoUnit.MINUTES)),
                new DocumentoAdjunto("PDF", "denuncia.pdf", "Fiscalia",
                        activacion.plus(offset(elapsedMinutes, 18), ChronoUnit.MINUTES))));
        caso.getReportesCiudadanos().add(reporte(longitude + 0.0026, latitude - 0.0013));
        caso.getHistorialAcciones().add(new AccionHistorial(
                "caso_activado", "Op. Lopez", activacion, "Activacion inicial del caso"));
        caso.getHistorialAcciones().add(new AccionHistorial(
                "informacion_validada",
                "Juez Torres",
                activacion.plus(offset(elapsedMinutes, 70), ChronoUnit.MINUTES),
                "Validacion legal completada"));

        if (conAlerta) {
            caso.getAlertasEmitidas().add(alerta(
                    "SMS masivo",
                    zona,
                    "Op. Garcia",
                    activacion.plus(offset(elapsedMinutes, 75), ChronoUnit.MINUTES)));
            caso.getAlertasEmitidas().add(alerta(
                    "Redes sociales",
                    zona,
                    "Op. Garcia",
                    activacion.plus(offset(elapsedMinutes, 80), ChronoUnit.MINUTES)));
        }
        return caso;
    }

    private Menor menor(String nombre, int edad, String sexo, double longitude, double latitude, String zona) {
        Menor menor = new Menor();
        menor.setNombre(nombre);
        menor.setEdad(edad);
        menor.setSexo(sexo);
        menor.setCabello("Castano, largo");
        menor.setOjos("Marrones");
        menor.setEstatura("1,25 m");
        menor.setPeso("30 kg");
        menor.setRopa("Remera rosa, jeans");
        menor.setSenas("Lunar en mejilla derecha");
        menor.setFotoUrl("/media/" + nombre.toLowerCase().replace(" ", "-") + ".jpg");
        menor.setUltimaUbicacion(new Ubicacion(longitude, latitude, "Ultima ubicacion conocida - " + zona));
        return menor;
    }

    private Alerta alerta(String canal, String zona, String operador, Instant timestamp) {
        Alerta alerta = new Alerta();
        alerta.setCanal(canal);
        alerta.setZona(zona);
        alerta.setOperador(operador);
        alerta.setTimestamp(timestamp);
        alerta.setEstado(EstadoAlerta.ENVIADA);
        return alerta;
    }

    private ReporteCiudadano reporte(double longitude, double latitude) {
        ReporteCiudadano reporte = new ReporteCiudadano();
        reporte.setTimestamp(Instant.now().minus(34, ChronoUnit.MINUTES));
        reporte.setUbicacion(new Ubicacion(longitude, latitude, "Avistamiento ciudadano"));
        reporte.setDescripcion("Menor visto cerca de estacion de transporte publico");
        reporte.setEstado(EstadoReporte.VERIFICADO);
        reporte.setContacto("linea 134");
        return reporte;
    }

    private Usuario usuario(String nombre, String rol, String organismo) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setRol(rol);
        u.setOrganismo(organismo);
        return u;
    }

    private long offset(long elapsedMinutes, long requestedOffset) {
        return Math.max(1, Math.min(requestedOffset, elapsedMinutes - 3));
    }
}
