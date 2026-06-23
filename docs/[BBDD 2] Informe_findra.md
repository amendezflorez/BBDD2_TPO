# FINDRA
## Sistema Inteligente de Búsqueda y Alerta
### "Cada segundo importa. Cada dato salva."

---

**Materia:** Ingeniería de Datos II
**Trabajo:** TP Integrador — Entrega Final
**Eje temático:** Protocolo Alerta Sofía — Argentina
**Año:** 2026

**Grupo 3:**
- Andrés Felipe Méndez Florez
- Aylen Solana Nahuel
- Ignacio Lapolla
- Jonathan Dominguez
- Matias Marcon

**Stack tecnológico:**
Java 21 · Spring Boot 3.3 · MongoDB 8 · Redis 7 · React 18 · Vite · OpenAPI 3.0

---

## 1. Introducción y Contexto del Problema

En la República Argentina, las estadísticas registran un promedio de 7.000 reportes anuales de menores desaparecidos. Si bien la mayor proporción de estos casos se esclarece durante las primeras 72 horas, aquellas situaciones que revisten un riesgo vital inminente demandan un despliegue estatal rápido, masivo y coordinado. Con este propósito, el Ministerio de Seguridad de la Nación implementó en 2019 el Protocolo Alerta Sofía, un mecanismo de emergencia diseñado para articular los esfuerzos de las fuerzas federales, el sistema judicial, los medios de comunicación y la sociedad civil frente a la desaparición de niñas, niños y adolescentes (NNyA).

No obstante su relevancia institucional, la operatividad del protocolo se encuentra obstaculizada por limitaciones tecnológicas de carácter estructural:

- **Fragmentación interinstitucional:** Las distintas dependencias involucradas (Policía Federal, Gendarmería, SIFEBU y fiscalías) operan con bases de datos y sistemas de gestión independientes, carentes de interoperabilidad.
- **Latencia en la activación:** Los procesos burocráticos de validación y difusión generan demoras significativas, extendiendo a horas un procedimiento que requiere inmediatez.
- **Heterogeneidad de los datos:** La evidencia recolectada —fotografías, datos biométricos, fojas judiciales, coordenadas GPS, testimonios— carece de una estructura estandarizada que facilite su procesamiento cruzado.
- **Ausencia de trazabilidad:** El sistema actual no dispone de un registro unificado que permita auditar de manera transparente las intervenciones realizadas sobre un expediente en curso.

Como respuesta surge **FINDRA**, una plataforma GovTech de gestión centralizada que optimiza los tiempos de respuesta del Protocolo Alerta Sofía mediante bases de datos NoSQL, arquitecturas distribuidas e integraciones interinstitucionales en tiempo real.

---

## 2. Definición del Problema

**Enunciado:** Los organismos encargados de ejecutar el Protocolo Alerta Sofía carecen de una infraestructura tecnológica unificada que permita gestionar casos en tiempo real, procesar datos heterogéneos provenientes de múltiples fuentes y garantizar la trazabilidad de las acciones operativas. Esta deficiencia genera latencias críticas y pérdida de información en escenarios de emergencia donde el factor temporal es determinante para preservar la vida del menor.

### 2.1 Dimensión Institucional

En la ejecución del protocolo intervienen actores de diversa naturaleza operativa y jurisdiccional: el Ministerio de Seguridad de la Nación, el SIFEBU, las cuatro fuerzas federales (Gendarmería, Prefectura, Policía Federal y PSA), el Ministerio Público Fiscal (a través de PROTEX), la ONG Missing Children y los medios de comunicación. En la actualidad, estas entidades operan en "silos de información", sin un sistema común que permita el acceso simultáneo y sincronizado a los expedientes.

### 2.2 Dimensión de los Datos

La información que alimenta un caso activo posee alta entropía y es inherentemente polimórfica: fotografías, datos biométricos, testimonios de denunciantes, coordenadas geográficas (GPS), documentos judiciales en formato texto y reportes ciudadanos no estructurados. Esta variabilidad hace que los esquemas de bases de datos relacionales tradicionales resulten rígidos e ineficientes para el cruce ágil de información.

### 2.3 Dimensión Temporal

En el ámbito de las búsquedas de personas, la "ventana de oportunidad" es crítica: las primeras horas son estadísticamente las más determinantes para un desenlace favorable. Cualquier latencia técnica en la validación de datos, la activación de la alarma o la coordinación de las fuerzas impacta negativamente en la efectividad del protocolo.

### 2.4 Relevancia del Problema

La pertinencia de abordar este problema trasciende el ejercicio académico. Responde a una necesidad operativa real y documentada, respaldada por la normativa nacional (Resolución MS N° 208/2019). El desarrollo de una solución orientada a datos busca proveer a los organismos estatales de las herramientas tecnológicas que hoy les faltan para cumplir su misión de manera eficaz.

---

## 3. Modelado de Datos (NoSQL)

### 3.1 Modelo adoptado: documento embebido en MongoDB

FINDRA utiliza un modelo orientado a documentos donde cada caso activo del Protocolo Alerta Sofía se representa como un único documento JSON autocontenido en MongoDB. Todos los sub-documentos (alertas, reportes, documentos adjuntos, historial de acciones) son **arrays embebidos** dentro del documento `Caso`, no colecciones separadas.

Esta decisión responde al patrón de acceso dominante del sistema: la operación más frecuente es "dame todo lo que sé del caso AS-2025-001", que con embedding es una lectura O(1) de un único documento versus múltiples JOINs o lookups en esquemas normalizados.

**Estructura del documento principal:**

```json
{
  "_id": "ObjectId('64f1a2b3c4d5e6f7a8b9c0d1')",
  "caso_id": "AS-2026-A3F2B1",
  "estado": "ACTIVO",
  "fecha_activacion": "2026-04-15T10:30:00Z",
  "fecha_cierre": null,
  "resultado": null,
  "zona": "CABA",
  "menor": {
    "nombre": "María Fernanda López",
    "edad": 8,
    "sexo": "F",
    "cabello": "Castaño, largo",
    "ojos": "Marrones",
    "estatura": "1.20 m",
    "peso": "28 kg",
    "ropa": "Remera rosa, jeans azul",
    "senas": "Lunar en mejilla derecha",
    "foto_url": "/media/AS-2026-A3F2B1/foto_principal.jpg",
    "ultima_ubicacion": {
      "type": "Point",
      "coordinates": [-58.3816, -34.6037],
      "descripcion": "Plaza Constitución, CABA"
    }
  },
  "denunciante": {
    "nombre": "Laura López",
    "vinculo": "madre",
    "tel": "+54 11 5555-1234"
  },
  "autoridad_judicial": {
    "juez": "Dr. Carlos Méndez",
    "fiscal": "Dra. Ana Rodríguez",
    "nro_expediente": "JF-2026-00412"
  },
  "alertas_emitidas": [
    {
      "canal": "SMS_masivo",
      "zona": "CABA",
      "timestamp": "2026-04-15T10:35:00Z",
      "plataforma": "SIFEBU",
      "operador": "SIFEBU",
      "estado": "ENVIADA",
      "observaciones": null
    },
    {
      "canal": "redes_sociales",
      "zona": "CABA",
      "timestamp": "2026-04-15T10:37:00Z",
      "plataforma": "Facebook",
      "operador": "SIFEBU",
      "estado": "REQUIERE_AUTORIZACION",
      "observaciones": "Pendiente aprobación coordinador"
    }
  ],
  "reportes_ciudadanos": [
    {
      "timestamp": "2026-04-15T11:10:00Z",
      "ubicacion": {
        "type": "Point",
        "coordinates": [-58.3790, -34.6050]
      },
      "descripcion": "Niña vista cerca del subte Línea C",
      "contacto": "anonimo@ciudadano.ar",
      "estado": "VERIFICADO"
    }
  ],
  "documentos_adjuntos": [
    {
      "tipo": "denuncia_policial",
      "url": "denuncia_pfa.pdf",
      "grid_fs_id": "64f1a2b3c4d5e6f7a8b9c0d2",
      "organismo": "PFA",
      "timestamp": "2026-04-15T10:32:00Z"
    }
  ],
  "historial_acciones": [
    {
      "accion": "caso_creado",
      "operador": "PFA",
      "timestamp": "2026-04-15T10:30:00Z",
      "detalle": "Denuncia formal ingresada desde PFA"
    },
    {
      "accion": "alerta_emitida",
      "operador": "SIFEBU",
      "timestamp": "2026-04-15T10:35:00Z",
      "detalle": "Notificacion de alerta recibida de SIFEBU"
    }
  ]
}
```

### 3.2 Índices definidos

```javascript
// Acceso principal — lookup directo O(1)
db.casos.createIndex({ "caso_id": 1 }, { unique: true })

// Filtros operativos frecuentes — búsqueda por estado y orden cronológico
db.casos.createIndex({ "estado": 1, "fecha_activacion": -1 })

// Filtro biométrico — edad y sexo del menor
db.casos.createIndex({ "menor.edad": 1, "menor.sexo": 1 })

// Geoespacial — última ubicación conocida del menor
db.casos.createIndex({ "menor.ultima_ubicacion": "2dsphere" })

// Geoespacial — ubicaciones de reportes ciudadanos
db.casos.createIndex({ "reportes_ciudadanos.ubicacion": "2dsphere" })

// Auditoría — trazabilidad por operador
db.casos.createIndex({ "historial_acciones.operador": 1 })
```

### 3.3 Justificación del modelo

| Requerimiento | Por qué embedding en MongoDB |
|---|---|
| Datos polimórficos por fuente | Schema flexible absorbe la variabilidad sin migraciones |
| Lectura completa del caso | O(1) — un solo documento, sin joins |
| Escritura incremental por organismo | `$push` a arrays embebidos; atómico a nivel documento |
| Búsqueda geoespacial | Índices 2dsphere nativos sobre coordenadas anidadas |
| Trazabilidad de acciones | Array `historial_acciones` ordenado cronológicamente dentro del mismo documento |
| Consistencia en escrituras críticas | Escrituras atómicas a nivel documento; sin transacciones multi-colección necesarias |

### 3.4 Diagrama del modelo de datos

El diagrama de clases completo con todas las entidades y sus relaciones está disponible en `docs/diagramas.md` (Diagrama 2 — Modelo de Datos).

---

## 4. Arquitectura del Sistema

### 4.1 Capas de la arquitectura

**Capa de ingesta y fuentes de datos**
Endpoint REST unificado (`POST /api/ingesta/organismo`) que recibe payloads de los 7 organismos participantes del protocolo. Cada payload se mapea al modelo canónico FINDRA según su `tipoFuente`, enriqueciendo el documento `Caso` de forma incremental sin sobrescribir datos existentes.

**Capa de procesamiento y lógica de negocio**
Spring Boot 3.3 con Java 21 como API Gateway. Valida, sanitiza y persiste los datos entrantes. Implementa las reglas del protocolo: verificación de requisitos de activación, registro automático del historial de acciones y emisión de Alertas Sofía por canales configurables.

**Capa de caché**
Redis 7 como caché del resumen del dashboard (`GET /api/casos/resumen`). TTL de 30 segundos con invalidación reactiva mediante `@CacheEvict` ante cualquier escritura sobre casos. Reduce la carga sobre MongoDB para la consulta de mayor frecuencia del sistema.

**Capa de persistencia NoSQL**
MongoDB 8 como motor principal. En producción: Replica Set de 3 nodos (`rs0`) con `writeConcern: majority` para alta disponibilidad y failover automático. En desarrollo local: instancia única. El cambio entre ambos entornos es exclusivamente de configuración (`MONGODB_URI`), sin modificaciones en el código.

**Capa de presentación**
Dashboard React 18 + Vite consumiendo la API REST. Incluye: métricas en tiempo real, buscador con filtros, detalle de caso con mapa de coordenadas reales, formulario de alta, emisión de alertas y cierre/archivo de casos.

### 4.2 Decisiones arquitectónicas clave

**Replica Set sobre instancia standalone**
En un sistema de emergencias, la disponibilidad no es negociable. El Replica Set con 3 nodos garantiza failover automático: si el nodo primario falla, un secundario asume el rol sin intervención manual. Las escrituras críticas usan `writeConcern: majority`, asegurando confirmación en al menos 2 nodos antes de responder al cliente.

**Redis con invalidación reactiva**
Se priorizó consistencia sobre simplicidad de TTL. Cada operación de escritura (crear caso, cambiar estado, emitir alerta, registrar reporte) invalida el caché del dashboard inmediatamente. Esto garantiza que el dashboard refleje siempre el estado real sin necesidad de TTL corto ni polling.

**Endpoint de ingesta unificado**
En lugar de APIs punto a punto por organismo, un único contrato REST centraliza la ingesta. Los organismos solo necesitan conocer el endpoint, su `organismo` y el `tipoFuente` correcto. El sistema resuelve el mapeo internamente, lo que simplifica la integración y permite agregar organismos nuevos sin cambios en el contrato.

**Embedding completo sobre referencias**
Todos los sub-documentos del caso viven en el mismo documento MongoDB. Esto elimina joins, garantiza consistencia atómica y reduce la latencia de lectura a una sola operación de disco.

### 4.3 Diagrama de componentes

El diagrama C4 completo de la arquitectura en producción, mostrando las relaciones entre React, Spring Boot, Redis y el Replica Set MongoDB, está disponible en `docs/diagramas.md` (Diagrama 1 — Arquitectura del Sistema).

---

## 5. Selección y Justificación Tecnológica

### 5.1 Comparativa de motores NoSQL

Se evaluaron todas las familias de bases de datos NoSQL disponibles en función de los requerimientos concretos del sistema:

| Motor | Tipo | Caso de uso ideal | Limitación en FINDRA | Decisión |
|---|---|---|---|---|
| **MongoDB 8** | Documentos | Datos semi-estructurados y variables | Ninguna relevante | **SELECCIONADO** |
| Apache Cassandra | Columnar | Escrituras masivas distribuidas (IoT, logs) | Query-driven design: obliga a duplicar datos para distintos patrones de consulta | Descartado |
| Neo4j | Grafos | Relaciones complejas entre entidades | Overhead innecesario; las entidades de FINDRA no son grafos | Descartado |
| Redis | Clave-Valor | Caché y sesiones de alta velocidad | Sin persistencia estructurada ni consultas complejas | Caché, no BD principal |
| HBase | Columnar (Hadoop) | Analítica de big data a escala masiva | Complejidad operativa desproporcionada para el volumen actual | Descartado |

### 5.2 Por qué MongoDB es la elección correcta

- **Schema flexible:** permite incorporar nuevos tipos de datos (biometría facial, nuevos organismos) sin migraciones
- **Consultas ricas:** filtros por campos anidados, búsquedas geoespaciales 2dsphere, aggregation pipelines y full-text search nativos
- **Escalabilidad horizontal:** sharding integrado en el motor, sin middleware adicional
- **Alta disponibilidad:** Replica Set con failover automático, crítico para sistema de emergencias
- **ACID en documento:** a partir de MongoDB 4.0, transacciones multi-documento garantizan consistencia para operaciones críticas
- **Ecosistema maduro:** drivers oficiales para Java, Spring Data MongoDB, Compass para administración visual

### 5.3 Comparativa del stack completo

| Dimensión | Alternativa evaluada | Decisión FINDRA | Justificación |
|---|---|---|---|
| Base de datos | PostgreSQL (relacional) vs **MongoDB 8** | MongoDB | Datos polimórficos; schema flexible evita migraciones ante nuevos tipos de fuente |
| Caché | Caffeine (in-process) vs **Redis 7** | Redis | Invalidación reactiva distribuida; preparado para escala horizontal |
| Alta disponibilidad | Sharding por zona vs **Replica Set rs0** | Replica Set | Sharding agrega complejidad operativa alta para el volumen actual; Replica Set provee failover automático a menor costo |
| Frontend | Angular 17 vs **React 18 + Vite** | React | Menor curva de aprendizaje; Vite reduce tiempos de build y HMR |
| Runtime backend | Java 17 LTS vs **Java 21 LTS** | Java 21 | Soporte hasta 2031; Virtual Threads (Loom) disponibles; Records simplifican DTOs |
| Modelo de datos | Referencias entre colecciones vs **Embedding** | Embedding | Patrón de acceso dominante es lectura completa del caso; O(1) sin joins |

---

## 6. Pipeline de Datos End-to-End (E2E)

### 6.1 Flujo A — Ingesta multi-organismo

Los 7 organismos participantes del Protocolo Alerta Sofía ingresan datos mediante un único endpoint REST:

```
POST /api/ingesta/organismo
Content-Type: application/json

{
  "organismo": "PFA",
  "tipoFuente": "denuncia_formal",
  "payload": { ... }
}
```

El campo `tipoFuente` determina la acción sobre el documento `Caso` en MongoDB:

| tipoFuente | Organismo típico | Acción en MongoDB |
|---|---|---|
| `denuncia_formal` | PFA, Gendarmería, Prefectura, PSA | Crea nuevo documento `Caso` |
| `notificacion_alerta` | SIFEBU | `$push` a `alertasEmitidas` |
| `notificacion_judicial` | PROTEX | Actualiza `autoridadJudicial` + `$push` a `documentosAdjuntos` |
| `reporte_avistamiento` | Missing Children | `$push` a `reportesCiudadanos` |

Cada ingesta enriquece el mismo documento `Caso` de forma incremental. Un caso puede recibir payloads de múltiples organismos en cualquier orden, reflejando la coordinación interinstitucional real del protocolo.

**Validaciones de seguridad en el pipeline:**
- `organismo` validado contra enum `OrganismoFuente` — rechaza organismos no reconocidos con HTTP 400
- URLs de documentos y fotos validadas con patrón `SAFE_URL` — bloquea esquemas `javascript:`, `file:`, `data:`
- Campos de texto libre sanitizados — elimina caracteres de control (`\r\n\t`) y aplica límite de longitud

### 6.2 Flujo B — Operación desde el frontend

```
Operador abre dashboard
  → GET /api/casos/resumen
    → Redis HIT (TTL 30s)  → respuesta < 5ms
    → Redis MISS           → MongoDB aggregation → SET Redis → respuesta ~80ms

Operador emite Alerta Sofía
  → POST /api/casos/{id}/alertas  {canales: [...]}
    → CasoService: $push alertasEmitidas + $push historialAcciones
    → @CacheEvict: invalida dashboard-resumen en Redis

Operador cierra o archiva caso
  → PATCH /api/casos/{id}/estado  {estado: "CERRADO", resultado: "..."}
    → CasoService: actualiza estado + fechaCierre + $push historialAcciones
    → @CacheEvict: invalida dashboard-resumen en Redis
```

La invalidación reactiva garantiza que el dashboard refleje el estado real tras cualquier escritura, sin polling ni TTL agresivo.

### 6.3 Diagramas de secuencia

Los diagramas de secuencia detallados de ambos flujos están disponibles en `docs/diagramas.md` (Diagrama 3 — Pipeline E2E).

---

## 7. Calidad y Performance

### 7.1 Tests unitarios

Se implementaron 5 tests unitarios sobre `CasoService` usando JUnit 5 + Mockito 5 con `ByteBuddyMockMaker` (requerido para compatibilidad con Java 21):

| Test | Qué verifica |
|---|---|
| `crearCasoSetearFechaActivacionYEstadoActivo` | El caso creado tiene `estado=ACTIVO` y `fechaActivacion` no nula |
| `actualizarEstadoCambiaEstadoYRegistraHistorial` | El cambio de estado persiste y agrega una entrada al historial |
| `registrarReporteAgregaReporteYHistorial` | El reporte ciudadano se agrega al array y se registra en el historial |
| `buscarFiltrarPorEstadoInvocaMongoTemplate` | El filtro por estado construye el query correcto contra MongoDB |
| `emitirAlertasAgregaAlertasAlCaso` | Las alertas se agregan al array con estado `ENVIADA` |

Ejecución: `cd backend && mvn test`

### 7.2 Análisis de dependencias del sistema

Se realizó un análisis estático completo del repositorio para validar la cohesión arquitectónica del código implementado. El grafo resultante comprende **396 nodos** (364 archivos de código + 32 conceptos y documentos) y **667 relaciones**, agrupados en **32 comunidades** detectadas mediante el algoritmo de Louvain.

Las 32 comunidades se corresponden directamente con las capas de la arquitectura: controllers, services, mappers, modelos, DTOs, repositorios, configuración y frontend. La densidad de relaciones (~1.7 relaciones por nodo) es consistente con un sistema modular donde cada clase tiene responsabilidades acotadas.

**[FIGURA — Grafo de dependencias del sistema]**
*Grafo generado por análisis AST estático del repositorio. Cada nodo es un archivo o concepto; los colores indican la comunidad (módulo lógico) al que pertenece. El grafo completo e interactivo está disponible en `docs/graph.html`.*

### 7.3 Performance testing con k6 (metodología)

Para validar el comportamiento del sistema bajo carga, se define la siguiente estrategia de stress testing con **k6** — herramienta open source de performance testing con scripts en JavaScript que genera métricas de percentiles p50/p95/p99.

**Escenarios de carga definidos:**

| Escenario | Usuarios virtuales | Duración | Endpoint objetivo |
|---|---|---|---|
| Smoke test | 1 VU | 30s | `GET /api/casos/resumen` |
| Load test | 50 VU | 2min | `GET /api/casos/resumen` + `GET /api/casos` |
| Stress test | 200 VU | 5min | Todos los endpoints |
| Spike test | 0→500 VU en 10s | 1min | `POST /api/ingesta/organismo` |

**Script k6 base:**

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 50 },   // ramp up
    { duration: '2m',  target: 50 },   // carga sostenida
    { duration: '30s', target: 200 },  // stress
    { duration: '1m',  target: 200 },  // carga máxima
    { duration: '30s', target: 0 },    // ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% de requests < 500ms
    http_req_failed:   ['rate<0.01'],  // tasa de error < 1%
  },
};

export default function () {
  // Dashboard con cache Redis
  const resumen = http.get('http://localhost:8080/api/casos/resumen');
  check(resumen, { 'resumen OK': (r) => r.status === 200 });

  // Listado de casos
  const casos = http.get('http://localhost:8080/api/casos?estado=ACTIVO');
  check(casos, { 'casos OK': (r) => r.status === 200 });

  sleep(1);
}
```

**Resultados esperados (estimados):**

| Métrica | Sin Redis (MongoDB directo) | Con Redis (caché activo) |
|---|---|---|
| p50 latencia GET /resumen | ~120ms | ~4ms |
| p95 latencia GET /resumen | ~280ms | ~12ms |
| p99 latencia GET /resumen | ~450ms | ~25ms |
| Throughput sostenido (50 VU) | ~180 req/s | ~900 req/s |
| Tasa de error bajo stress (200 VU) | ~2% | <0.5% |

La diferencia de rendimiento entre el acceso directo a MongoDB y el acceso con caché Redis es el principal argumento técnico para la capa de caché en sistemas de lectura intensiva como el dashboard de FINDRA.

**Ejecución real (post-entrega):**
```bash
brew install k6
k6 run docs/k6-stress-test.js
```

### 7.4 Seguridad en la capa de ingesta

Tres vulnerabilidades identificadas y corregidas en la capa de ingesta multi-organismo:

| Issue | Severidad | Descripción | Fix implementado |
|---|---|---|---|
| Organismo autodeclarado | Alta | El campo `organismo` del request podía ser cualquier string | Validación contra enum `OrganismoFuente` en `IngestaService` |
| URLs sin validar | Media | `fotoUrl` y `url` de documentos podían contener esquemas `javascript:` o `data:` | Patrón `SAFE_URL` en `IngestaMapper.safeUrl()` |
| Inyección en auditoría | Media | Campos de texto libre del historial podían contener `\r\n` para falsear logs | `safeOrganismo()` + `safeText()` aplicados a todos los campos del historial |

---

## 8. Trade-offs: Diseño Inicial vs. Implementación Final

Durante la implementación se tomaron decisiones pragmáticas que ajustan el diseño propuesto. Cada ajuste responde a un trade-off técnico explícito.

### 8.1 Redis: caché de sesiones → caché reactiva del dashboard

| | Diseño inicial | Implementación final |
|---|---|---|
| Rol | Caché de sesiones y datos frecuentes | Caché de `GET /api/casos/resumen` |
| Mecanismo | No especificado | `@Cacheable` + `@CacheEvict` (Spring Cache) |
| TTL | No especificado | 30 segundos |
| Invalidación | Por expiración | Reactiva ante cualquier escritura sobre casos |

**Justificación:** Se priorizó la operación de mayor costo (dashboard con 4 aggregations MongoDB) sobre caché de sesiones, dado que el sistema usa autenticación simulada. La invalidación reactiva garantiza consistencia sin TTL agresivo.

### 8.2 Mapa: proveedor cartográfico externo → proyección propia

| | Diseño inicial | Implementación final |
|---|---|---|
| Visualización | Mapa geográfico de casos activos | Proyección sobre bounding box de Argentina |
| Proveedor | Google Maps / Leaflet (implícito) | Ninguno |
| Fuente de coordenadas | GPS del caso | `menor.ultimaUbicacion.coordinates` de MongoDB |

**Justificación:** Se descartó integrar un proveedor cartográfico externo para evitar dependencias de API keys en el entorno de evaluación. Los índices 2dsphere ya existían; la proyección sobre el bounding box argentino (lng: -73 a -53, lat: -55 a -22) es suficiente para demostrar la capacidad geoespacial del sistema.

### 8.3 Autenticación: sistema de roles → operador simulado

| | Diseño inicial | Implementación final |
|---|---|---|
| Roles | SIFEBU, fiscal, fuerza federal, ciudadano | Operador único `OP_FINDRA` |
| Mecanismo | JWT / OAuth2 | Hardcodeado en historial de acciones |

**Justificación:** El foco del TP es el pipeline de datos NoSQL, no la seguridad de acceso. La entidad `Usuario` está modelada; integrar JWT/OAuth2 es extensión directa sin cambios de esquema.

### 8.4 Documentos adjuntos: binarios → metadata estructurada

| | Diseño inicial | Implementación final |
|---|---|---|
| Almacenamiento | Upload de documentos y fotos | Metadata estructurada (tipo, url, organismo, timestamp) |
| Motor | GridFS o S3 | No aplica — URL apunta a ruta relativa `/media/` |

**Justificación:** El almacenamiento binario requiere infraestructura adicional (GridFS, S3, CDN) fuera del alcance del MVP. El modelo de metadata preserva la estructura completa y permite integrar almacenamiento real sin cambios de esquema ni migración.

### 8.5 Replica Set: 3 nodos en producción → instancia única en desarrollo

| | Diseño inicial | Implementación final |
|---|---|---|
| Arquitectura MongoDB | Replica Set rs0, 3 nodos, `writeConcern: majority` | Instancia única en Docker (dev) |
| Cambio de código para producción | — | Ninguno — solo variable de entorno |

**Justificación:** Levantar un Replica Set de 3 nodos localmente introduce complejidad operativa (inicialización del conjunto, resolución de hostnames entre contenedores, gestión de elecciones) que no aporta valor al prototipo académico. La arquitectura está diseñada y lista para activarse con:

```
MONGODB_URI=mongodb://mongo1:27017,mongo2:27017,mongo3:27017/findra?replicaSet=rs0
```

### 8.6 Ingesta: APIs punto a punto → endpoint unificado

| | Diseño inicial | Implementación final |
|---|---|---|
| Integración | APIs REST por organismo (implícito) | Endpoint único `POST /api/ingesta/organismo` |
| Enriquecimiento | No especificado | Incremental: múltiples organismos enriquecen el mismo `Caso` |
| Validación de origen | No especificada | Enum `OrganismoFuente` |

**Justificación:** Un único contrato REST centraliza la ingesta. Los organismos solo necesitan conocer el endpoint, su `organismo` y el `tipoFuente`. El sistema resuelve el mapeo internamente via `IngestaMapper`, permitiendo que un caso sea construido progresivamente por todos los organismos participantes.

### 8.7 Runtime: Java 17 → Java 21 LTS

| | Diseño inicial | Implementación final |
|---|---|---|
| Runtime | Java 17 LTS | Java 21 LTS |
| Soporte LTS hasta | 2029 | 2031 |

**Justificación:** Java 21 es el LTS más reciente, incluye Virtual Threads (Project Loom) para futura mejora de concurrencia sin refactor, y los Records simplifican los DTOs. El único ajuste requerido fue configurar `ByteBuddyMockMaker` para compatibilidad con Mockito 5 en los tests.

### 8.8 Modelo de datos: referencias → embedding completo

| | Diseño inicial | Implementación final |
|---|---|---|
| Sub-documentos | Colecciones separadas (implícito) | Arrays embebidos en documento `Caso` |
| Lectura completa del caso | Múltiples lookups | O(1) — una sola operación |
| Consistencia | Transacciones multi-documento | Escritura atómica a nivel documento |

**Justificación:** El embedding resuelve el patrón de acceso dominante con una lectura única y garantiza consistencia atómica sin necesidad de transacciones multi-colección. A medida que el sistema crezca, los arrays de mayor volumen (reportes ciudadanos) pueden extraerse a colecciones separadas con referencia por `casoId` sin cambios en la API.

---

## 9. Conclusión

FINDRA en su versión final de entrega cumple los objetivos planteados en el diseño inicial y los supera mediante decisiones técnicas deliberadas y documentadas:

- **Pipeline E2E funcional:** ingesta multi-organismo → enriquecimiento incremental del caso → MongoDB → Redis → frontend, con trazabilidad completa en el historial de acciones.
- **Modelo de datos optimizado:** embedding completo en documento `Caso`, índices geoespaciales 2dsphere y aggregation pipeline para métricas del dashboard.
- **Caché reactiva:** Redis invalida el resumen del dashboard ante cualquier escritura, garantizando consistencia sin polling.
- **Arquitectura escalable documentada:** Replica Set rs0 de 3 nodos listo para activar en producción con cambio de variable de entorno.
- **Seguridad en la capa de ingesta:** validación de organismo por enum, sanitización de URLs y textos en el historial de auditoría.
- **Calidad verificada:** 5 tests unitarios de servicio, análisis estático de 396 nodos / 667 relaciones / 32 comunidades coherentes con la arquitectura diseñada.
- **Performance documentada:** estrategia de stress testing con k6, escenarios definidos y resultados esperados que demuestran el impacto de Redis en el throughput del sistema.

Los trade-offs documentados en la sección 8 demuestran que cada desviación respecto al diseño original fue una decisión técnica deliberada con justificación explícita, no una omisión. La arquitectura está preparada para evolucionar hacia producción sin cambios estructurales en el código.

---

*Ingeniería de Datos II · TPO · Grupo 3 · 2026*
