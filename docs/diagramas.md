# FINDRA — Diagramas Técnicos

Este archivo centraliza los diagramas del sistema FINDRA. Cada sección indica qué criterios de la rúbrica impacta.

---

## Diagrama 1 — Arquitectura del Sistema

**Impacto en rúbrica:** Criterio 3 (Arquitectura propuesta), Criterio 11 (Arquitectura final), Criterio 13 (Documentación final)

Vista de componentes del sistema en producción, mostrando frontend, backend, capa de caché y persistencia distribuida.

```mermaid
C4Context
    title FINDRA — Arquitectura de Componentes (Producción)

    Person(operador, "Operador FINDRA", "Usuario institucional: PFA, SIFEBU, PROTEX, etc.")
    Person(ciudadano, "Ciudadano", "Reporte de avistamiento vía canal externo")

    System_Boundary(findra, "FINDRA Platform") {
        Container(frontend, "Frontend", "React 18 + Vite", "Dashboard, buscador, detalle de caso, formulario de alta, mapa de coordenadas reales")
        Container(backend, "Backend API", "Java 21 + Spring Boot 3.3", "REST API, lógica de negocio, validación, auditoría")
        Container(redis, "Cache", "Redis 7", "Cache del resumen dashboard (TTL 30s). Invalidación reactiva ante cambios de estado")
        ContainerDb(mongo1, "MongoDB Primary", "MongoDB 8", "Escrituras + lecturas. Nodo líder del Replica Set rs0")
        ContainerDb(mongo2, "MongoDB Secondary 1", "MongoDB 8", "Réplica de datos. Failover automático")
        ContainerDb(mongo3, "MongoDB Secondary 2", "MongoDB 8", "Réplica de datos. Failover automático")
    }

    System_Ext(pfa, "PFA / Gendarmería / Prefectura / PSA", "Organismos fuente: envían denuncia_formal vía POST /api/ingesta/organismo")
    System_Ext(sifebu, "SIFEBU", "Envía notificacion_alerta con canales activados")
    System_Ext(protex, "PROTEX", "Envía notificacion_judicial con expediente")
    System_Ext(missing, "Missing Children", "Envía reporte_avistamiento ciudadano")

    Rel(operador, frontend, "Usa", "HTTPS")
    Rel(ciudadano, missing, "Reporta avistamiento")
    Rel(frontend, backend, "API REST", "HTTP/JSON")
    Rel(backend, redis, "Lee/Escribe cache", "Redis protocol")
    Rel(backend, mongo1, "Lee y escribe", "MongoDB Wire Protocol")
    Rel(mongo1, mongo2, "Replica oplog", "Replica Set rs0")
    Rel(mongo1, mongo3, "Replica oplog", "Replica Set rs0")
    Rel(pfa, backend, "POST /api/ingesta/organismo", "HTTPS/JSON")
    Rel(sifebu, backend, "POST /api/ingesta/organismo", "HTTPS/JSON")
    Rel(protex, backend, "POST /api/ingesta/organismo", "HTTPS/JSON")
    Rel(missing, backend, "POST /api/ingesta/organismo", "HTTPS/JSON")
```

> **Nota dev local:** MongoDB corre como instancia única en `localhost:27017`. La connection string de producción se configura via `MONGODB_URI` sin cambios en el código.

---

## Diagrama 2 — Modelo de Datos

**Impacto en rúbrica:** Criterio 2 (Modelado de datos NoSQL), Criterio 13 (Documentación final)

El modelo central es el documento `Caso` en MongoDB. Todos los sub-documentos son embebidos (document embedding) salvo referencias explícitas. La elección de embedding sobre referencias se justifica por la naturaleza de lectura del sistema: cada vista de detalle necesita todos los datos del caso sin joins.

```mermaid
classDiagram
    class Caso {
        +String casoId
        +String zona
        +EstadoCaso estado
        +Instant fechaActivacion
        +Instant fechaCierre
        +String resultado
        +Menor menor
        +Denunciante denunciante
        +AutoridadJudicial autoridadJudicial
        +List~Alerta~ alertasEmitidas
        +List~ReporteCiudadano~ reportesCiudadanos
        +List~DocumentoAdjunto~ documentosAdjuntos
        +List~AccionHistorial~ historialAcciones
    }

    class Menor {
        +String nombre
        +Integer edad
        +String sexo
        +String senas
        +String fotoUrl
        +Ubicacion ultimaUbicacion
    }

    class Ubicacion {
        +Double lng
        +Double lat
        +String descripcion
    }

    class Denunciante {
        +String nombre
        +String vinculo
        +String tel
    }

    class AutoridadJudicial {
        +String juez
        +String fiscal
        +String nroExpediente
    }

    class Alerta {
        +String canal
        +String zona
        +Instant timestamp
        +String plataforma
        +String operador
        +EstadoAlerta estado
    }

    class ReporteCiudadano {
        +Instant timestamp
        +Ubicacion ubicacion
        +String descripcion
        +String contacto
        +EstadoReporte estado
    }

    class DocumentoAdjunto {
        +String tipo
        +String url
        +String organismo
        +Instant timestamp
    }

    class AccionHistorial {
        +String accion
        +String operador
        +Instant timestamp
        +String detalle
    }

    class EstadoCaso {
        <<enumeration>>
        ACTIVO
        CERRADO
        ARCHIVADO
    }

    class EstadoAlerta {
        <<enumeration>>
        ENVIADA
        CONFIRMADA
        FALLIDA
    }

    class EstadoReporte {
        <<enumeration>>
        RECIBIDO
        VERIFICADO
    }

    class OrganismoFuente {
        <<enumeration>>
        PFA
        GENDARMERIA
        PREFECTURA
        PSA
        SIFEBU
        PROTEX
        MISSING_CHILDREN
    }

    Caso "1" *-- "1" Menor : embebido
    Caso "1" *-- "1" Denunciante : embebido
    Caso "1" *-- "0..1" AutoridadJudicial : embebido
    Caso "1" *-- "0..*" Alerta : embebido
    Caso "1" *-- "0..*" ReporteCiudadano : embebido
    Caso "1" *-- "0..*" DocumentoAdjunto : embebido
    Caso "1" *-- "0..*" AccionHistorial : embebido
    Menor "1" *-- "0..1" Ubicacion : ultimaUbicacion
    ReporteCiudadano "1" *-- "0..1" Ubicacion : avistamiento
    Caso --> EstadoCaso
    Alerta --> EstadoAlerta
    ReporteCiudadano --> EstadoReporte
```

**Decisión de diseño:** Todo embebido en un único documento MongoDB. Lectura O(1) por `casoId`. Sin joins. Adecuado para el patrón de acceso dominante (lectura completa del caso en detalle).

**Índices definidos:**
- `casoId` — único, lookup directo
- `estado` — filtro por estado activo/cerrado
- `menor.nombre` — búsqueda textual
- `zona` — filtro geográfico por zona

---

## Diagrama 3 — Pipeline E2E

**Impacto en rúbrica:** Criterio 6 (Pipeline de datos E2E), Criterio 9 (Documentación técnica), Criterio 13 (Documentación final)

Dos flujos principales: ingesta institucional (organismos externos) y operación interna (operador FINDRA desde el frontend).

### Flujo A — Ingesta multi-organismo

```mermaid
sequenceDiagram
    actor Organismo as Organismo (PFA/SIFEBU/PROTEX/etc.)
    participant API as Spring Boot API
    participant IngestaService
    participant IngestaMapper
    participant MongoDB
    participant Redis

    Organismo->>API: POST /api/ingesta/organismo<br/>{organismo, tipoFuente, payload}
    API->>IngestaService: procesar(request)
    IngestaService->>IngestaService: OrganismoFuente.valueOf(organismo)<br/>↳ valida contra enum, rechaza desconocidos
    
    alt tipoFuente = denuncia_formal
        IngestaService->>IngestaMapper: mapDenunciaFormal(organismo, payload)
        IngestaMapper->>IngestaMapper: safeUrl(), safeText(), safeOrganismo()
        IngestaMapper-->>IngestaService: Caso nuevo
        IngestaService->>MongoDB: casoService.crear(caso)
        MongoDB-->>IngestaService: Caso guardado (casoId generado)
        IngestaService->>Redis: @CacheEvict dashboard-resumen
    else tipoFuente = notificacion_alerta / judicial / reporte
        IngestaService->>MongoDB: obtenerPorCasoId(caso_ref)
        MongoDB-->>IngestaService: Caso existente
        IngestaService->>IngestaMapper: map*(caso, organismo, payload)
        IngestaMapper-->>IngestaService: Caso enriquecido
        IngestaService->>MongoDB: casoRepository.save(caso)
        IngestaService->>Redis: @CacheEvict dashboard-resumen
    end

    API-->>Organismo: 201 Created<br/>{casoId, accion, organismo, timestamp}
```

### Flujo B — Operación desde frontend

```mermaid
sequenceDiagram
    actor Operador
    participant React as React Frontend
    participant API as Spring Boot API
    participant CasoService
    participant MongoDB
    participant Redis

    Operador->>React: Abre dashboard
    React->>API: GET /api/casos/resumen
    API->>Redis: GET dashboard-resumen
    alt cache hit (TTL 30s)
        Redis-->>API: Métricas cacheadas
    else cache miss
        API->>CasoService: resumen()
        CasoService->>MongoDB: aggregation pipeline<br/>(activos, alertas hoy, resueltos mes)
        MongoDB-->>CasoService: ResultSet
        CasoService-->>API: DashboardResumen
        API->>Redis: SET dashboard-resumen (TTL 30s)
    end
    API-->>React: DashboardResumen JSON
    React-->>Operador: Dashboard renderizado

    Operador->>React: Emite Alerta Sofía
    React->>API: POST /api/casos/{id}/alertas
    API->>CasoService: emitirAlertas(id, canales)
    CasoService->>MongoDB: actualiza caso (alertas + historial)
    CasoService->>Redis: @CacheEvict dashboard-resumen
    API-->>React: Alerta emitida
    React-->>Operador: UI actualizada

    Operador->>React: Cierra caso
    React->>API: PATCH /api/casos/{id}/estado<br/>{estado: "CERRADO", resultado: "..."}
    API->>CasoService: actualizarEstado(id, estado, resultado)
    CasoService->>MongoDB: actualiza estado + fechaCierre + historial
    CasoService->>Redis: @CacheEvict dashboard-resumen
    API-->>React: Caso actualizado
    React-->>Operador: Botones cerrar/archivar ocultos
```

---

## Diagrama 4 — Comparativa Tecnológica

**Impacto en rúbrica:** Criterio 4 (Selección y justificación tecnológica)

| Dimensión | Alternativa A | Alternativa B | Decisión FINDRA | Justificación |
|-----------|--------------|--------------|-----------------|---------------|
| **Base de datos principal** | PostgreSQL (relacional) | **MongoDB 8** (documento) | MongoDB | Datos del caso son polimórficos (foto, GPS, docs judiciales, testimonios). Schema flexible evita migraciones ante nuevos tipos de fuente. |
| **Caché** | Caffeine (in-process) | **Redis 7** (distribuido) | Redis | Permite invalidación reactiva entre múltiples instancias del backend. Preparado para escala horizontal. TTL configurable por tipo de dato. |
| **Alta disponibilidad DB** | Sharding por zona geográfica | **Replica Set rs0** (3 nodos) | Replica Set | Sharding agrega complejidad operativa alta para el volumen actual. Replica Set provee failover automático con `writeConcern: majority` a costo operativo menor. |
| **Frontend** | Angular 17 | **React 18 + Vite** | React | Menor curva de aprendizaje, ecosistema más amplio para prototipado rápido. Vite reduce tiempos de build y HMR. |
| **Runtime backend** | Java 17 LTS | **Java 21 LTS** | Java 21 | Soporte LTS hasta 2031. Virtual Threads (Loom) disponibles para futura mejora de concurrencia sin refactor. |
| **Modelo de datos** | Referencias entre colecciones | **Embedding completo** | Embedding | Patrón de acceso dominante es lectura completa del caso. Embedding elimina joins, lectura O(1) por `casoId`. |

---

## Diagrama 5 — Grafo de Dependencias del Sistema

**Impacto en rúbrica:** Criterio 12 (Calidad global), Criterio 13 (Documentación final)

El grafo interactivo del sistema fue generado mediante análisis estático del repositorio completo con [graphify](https://github.com/graphify-ai/graphify). Permite navegar visualmente las relaciones entre todos los módulos, clases y conceptos del sistema.

**Artefacto:** [`docs/graph.html`](graph.html) — abrir en navegador para exploración interactiva.

### Métricas del grafo

| Métrica | Valor |
|---------|-------|
| Nodos totales | 396 |
| Relaciones (links) | 667 |
| Comunidades detectadas | 32 |
| Archivos de código analizados | 364 |
| Documentos y conceptos | 27 |
| Método de extracción | AST estático |

### Interpretación para la defensa

Las **32 comunidades** detectadas por el algoritmo de Louvain corresponden a los módulos lógicos del sistema: controllers, services, mappers, modelos, DTOs, repositorios, configuración y frontend. La alta cohesión intra-comunidad y el bajo acoplamiento entre capas confirman que la arquitectura en capas (Controller → Service → Repository → MongoDB) se refleja fielmente en la estructura del código.

Los **667 links** con **396 nodos** dan una densidad de ~1.7 relaciones por nodo, consistente con un sistema modular donde cada clase tiene responsabilidades acotadas (principio de responsabilidad única).

### Texto para incluir en el informe técnico

> **Análisis de dependencias del sistema**
>
> Con el objetivo de validar la cohesión arquitectónica del código implementado, se realizó un análisis estático completo del repositorio mediante graphify. El grafo resultante comprende **396 nodos** (364 archivos de código + 32 conceptos/documentos) y **667 relaciones**, agrupados en **32 comunidades** detectadas mediante el algoritmo de Louvain.
>
> Las comunidades identificadas se corresponden directamente con las capas de la arquitectura propuesta: capa de presentación (React + Vite), capa de API (Spring Controllers), capa de negocio (Services + Mappers), capa de datos (Repositories + MongoDB) y capa de caché (Redis). La densidad de relaciones inter-capa es consistente con el patrón de dependencia unidireccional diseñado: el frontend no conoce la persistencia, los services no conocen los controllers, y los mappers no tienen dependencias circulares.
>
> El grafo interactivo está disponible como artefacto adjunto en `docs/graph.html` y puede explorarse en cualquier navegador sin dependencias adicionales.
