# FINDRA — Sistema de Gestión del Protocolo Alerta Sofía

> "Cada segundo importa. Cada dato salva."

FINDRA es una plataforma GovTech que centraliza la gestión de casos de menores desaparecidos bajo el Protocolo Alerta Sofía del Ministerio de Seguridad de la Nación. Unifica en tiempo real los datos de 7 organismos participantes — PFA, Gendarmería, Prefectura, PSA, SIFEBU, PROTEX y Missing Children — que hoy operan en silos sin interoperabilidad.

**Stack:** Java 21 · Spring Boot 3.3 · MongoDB 8 · Redis 7 · React 18 · Vite · OpenAPI 3.0

---

## El problema que resuelve

En Argentina se reportan ~7.000 menores desaparecidos por año. Las primeras horas son críticas. El problema no es falta de organismos ni de personal — es que cada uno opera con su propio sistema, sin un registro compartido. Un caso puede existir en la PFA y SIFEBU sin que PROTEX sepa que existe. FINDRA resuelve eso con un documento único en MongoDB que todos enriquecen incrementalmente.

---

## Funcionalidades

| Funcionalidad | Descripción |
|---|---|
| **Dashboard en tiempo real** | Métricas de casos activos, alertas del día, resueltos del mes. Respuesta < 5ms con Redis activo. |
| **Registro de casos** | Formulario con datos biométricos del menor (cabello, ojos, estatura, peso, ropa, señas, GPS), denunciante y autoridad judicial. |
| **Buscador con filtros** | Búsqueda por texto libre, estado, zona geográfica y rango de edad. |
| **Detalle de caso** | Ficha completa con mapa interactivo (OpenStreetMap), alertas emitidas, documentos descargables y línea de tiempo de acciones. |
| **Alerta Sofía** | Emisión por canales configurables (SMS masivo, redes sociales, cadena nacional TV/Radio, app ciudadana). Flag de autorización judicial. |
| **Ciclo de vida** | Transiciones ACTIVO → RESUELTO / ARCHIVADO con `fecha_cierre` automática y registro en historial. |
| **Documentos adjuntos** | Upload y descarga de evidencia (imágenes, PDF, DOC) almacenada en GridFS nativo de MongoDB. |
| **Ingesta multi-organismo** | `POST /api/ingesta/organismo` — endpoint único para los 7 organismos con validación de origen y enriquecimiento incremental. |
| **Mapa interactivo** | Vista nacional con todos los casos activos, sidebar navegable y acceso directo al detalle desde el popup. |
| **Reportes ciudadanos** | Registro de avistamientos con geolocalización y estado (RECIBIDO / VERIFICADO / DESCARTADO). |
| **Gestión de usuarios** | Alta de operadores con rol (OPERADOR / FISCAL / COORDINADOR / SUPERVISOR) y organismo validado. |
| **Auditoría completa** | Cada acción queda registrada en `historial_acciones` con operador, timestamp y detalle. |

---

## Arquitectura

```
React 18 + Vite
      │
      ▼
Spring Boot 3.3 (API REST)
      │
      ├── Redis 7 ──── caché dashboard (TTL 30s, invalidación reactiva)
      │
      └── MongoDB 8 ── documento Caso con embedding completo
                        ├── alertas_emitidas[]
                        ├── reportes_ciudadanos[]
                        ├── documentos_adjuntos[] → GridFS
                        └── historial_acciones[]
```

**Por qué MongoDB con embedding:** el patrón de acceso dominante en una emergencia es "dame todo lo que sé del caso AS-2026-001 ahora mismo". Con embedding eso es una lectura O(1). Con referencias y joins serían 4-8 operaciones encadenadas — inaceptable en tiempo crítico.

**Por qué Redis:** el dashboard es la pantalla que tiene abierta el coordinador de guardia. 50 operadores mirando métricas cada 30 segundos son 150 aggregations MongoDB por minuto. Redis los absorbe todos en 4ms con invalidación reactiva por cada escritura.

**Índices definidos:**
```javascript
{ "caso_id": 1 }                              // único — lookup O(1)
{ "estado": 1, "fecha_activacion": -1 }       // filtros operativos
{ "menor.edad": 1, "menor.sexo": 1 }          // biométrico
{ "menor.ultima_ubicacion": "2dsphere" }       // geoespacial
{ "reportes_ciudadanos.ubicacion": "2dsphere"} // geoespacial
{ "historial_acciones.operador": 1 }           // auditoría
```

---

## Requisitos

- Java 21
- Maven 3.9+
- Node.js 20+ y npm
- Docker (recomendado para MongoDB + Redis)

Variables de entorno con sus defaults:

```properties
MONGODB_URI=mongodb://localhost:27017/findra
REDIS_HOST=localhost
REDIS_PORT=6379
```

No hace falta definirlas si se usa Docker Compose.

---

## Levantar el entorno

**Opción 1 — Docker Compose (recomendado):**

```bash
docker compose up -d
```

Levanta MongoDB 8, Mongo Express (`:8081`) y Redis 7. Luego iniciar backend y frontend por separado.

**Opción 2 — Local:**

MongoDB en `localhost:27017` y Redis en `localhost:6379` corriendo localmente.

---

## Ejecución

**Backend:**

```bash
cd backend
mvn spring-boot:run
```

**Frontend:**

```bash
cd frontend
npm install
npm run dev
```

En Windows con PowerShell, si aparece el error de `npm.ps1` no firmado:

```powershell
cd frontend
npm.cmd run dev
# o bien:
./start-dev.ps1
```

**URLs:**

| Servicio | URL |
|---|---|
| Frontend | `http://localhost:5173` |
| API REST | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Mongo Express | `http://localhost:8081` |

---

## Verificación rápida

```bash
# Tests unitarios (5 tests sobre CasoService)
cd backend && mvn test

# Lint + build del frontend
cd frontend && npm run lint && npm run build

# Verificar que Redis cacheó el dashboard
docker exec -it findra-redis redis-cli KEYS "*"
# → "dashboard-resumen"
```

---

## Pipeline E2E de un caso real

1. PFA registra la denuncia → `POST /api/ingesta/organismo` con `tipoFuente: "denuncia_formal"` → MongoDB crea el documento `Caso` con ID secuencial `AS-2026-001`
2. SIFEBU recibe la notificación → `POST /api/ingesta/organismo` con `tipoFuente: "notificacion_alerta"` → `$push` a `alertasEmitidas` en el mismo documento
3. PROTEX adjunta la resolución judicial → `POST /api/ingesta/organismo` con `tipoFuente: "notificacion_judicial"` → actualiza `autoridadJudicial` y `$push` a `documentosAdjuntos` (GridFS)
4. Missing Children registra un avistamiento → `POST /api/ingesta/organismo` con `tipoFuente: "reporte_avistamiento"` → `$push` a `reportesCiudadanos`
5. Coordinador ve el dashboard actualizado en < 5ms (Redis) con el caso activo y sus alertas
6. Operador emite Alerta Sofía desde la UI → `POST /api/casos/{id}/alertas` → `@CacheEvict` invalida Redis → dashboard refleja el cambio inmediatamente

---

## Documentación

- [Informe técnico completo](docs/%5BBBDD%202%5D%20Informe_findra.md) — modelo de datos, arquitectura, trade-offs, análisis de performance, walkthrough funcional
- [Decisiones técnicas](docs/decisiones-tecnicas.md) — registro de decisiones arquitectónicas

---

## Alcance del prototipo

FINDRA es un prototipo académico. Las siguientes funcionalidades están fuera del alcance actual pero la arquitectura está preparada para incorporarlas sin cambios estructurales:

| Fuera de alcance | Path de producción |
|---|---|
| Autenticación real | Spring Security + JWT. El modelo `Usuario` ya existe. |
| Replica Set MongoDB | Solo cambiar `MONGODB_URI`. Cero cambios en el código. |
| Canales de alerta reales | Integrar API de SIFEBU y medios. El modelo de datos no cambia. |
| Integración real con organismos | Los contratos REST ya están definidos y documentados en Swagger. |

---

**Grupo 3 — Ingeniería de Datos II · UADE · 2026**
Andrés Felipe Méndez Florez · Aylen Solana Nahuel · Ignacio Lapolla · Jonathan Dominguez · Matias Marcon
