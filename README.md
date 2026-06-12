# FINDRA

FINDRA es un prototipo GovTech para centralizar casos del Protocolo Alerta Sofia, reducir latencias operativas y mantener trazabilidad de alertas, reportes ciudadanos y acciones institucionales.

## Stack

- Frontend: React JS + Vite
- Backend: Java 21 + Spring Boot 3.3
- Persistencia: MongoDB 8
- Cache: Redis 7 (Spring Cache, TTL 30s)
- Documentacion API: Swagger/OpenAPI 3.0

## Requisitos

- Java 21
- Maven 3.9+
- Node.js 20+ y npm
- Docker (recomendado para MongoDB + Redis) o MongoDB local en `localhost:27017`

Variables de entorno con sus defaults:

```properties
MONGODB_URI=mongodb://localhost:27017/findra
REDIS_HOST=localhost
REDIS_PORT=6379
```

No hace falta definirlas si se usa Docker Compose.

## Ejecucion

Backend:

```powershell
cd backend
mvn spring-boot:run
```

Frontend:

```powershell
cd frontend
npm install
npm run dev
```

URLs principales:

- Frontend: `http://localhost:5173`
- API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- MongoDB local: `mongodb://localhost:27017/findra`

## Pipeline E2E

1. MongoDB local se ejecuta en `localhost:27017`.
2. El backend inicia y crea indices para `casos`.
3. Si la coleccion esta vacia, se cargan casos seed similares al informe y wireframes.
4. React consume la API REST para dashboard, listado, filtros y detalle.
5. Al emitir una alerta o registrar un reporte, el backend persiste el cambio y agrega una accion de auditoria.
6. La persistencia se puede verificar desde Compass en la base `findra`.

## Validacion Rapida

```powershell
cd backend
mvn test
```

```powershell
cd frontend
npm run lint
npm run build
```

## Docker (recomendado)

El `docker-compose.yml` levanta MongoDB 8, Mongo Express y Redis 7:

```powershell
docker compose up -d
```

Servicios disponibles:

| Servicio       | URL / puerto          |
|----------------|-----------------------|
| MongoDB        | `localhost:27017`     |
| Mongo Express  | `http://localhost:8081` |
| Redis          | `localhost:6379`      |

Para verificar que el cache funciona tras levantar el dashboard:

```powershell
docker exec -it findra-redis redis-cli KEYS "*"
# → "dashboard-resumen"
```

## Funcionalidades implementadas

- Dashboard con metricas en vivo (casos activos, alertas emitidas hoy, resueltos este mes)
- Buscador de casos con filtros por estado, zona y edad
- Detalle de caso: datos del menor, legales, mapa con coordenadas reales de MongoDB, alertas y timeline
- Registrar nuevo caso desde formulario completo
- Cerrar o archivar un caso activo
- Emitir Alerta Sofia con seleccion de canales
- Registrar reporte ciudadano
- Cache Redis del dashboard (TTL 30s, invalidacion reactiva)
- 5 tests unitarios del servicio de casos

## Fuera de alcance

- Autenticacion real (operador simulado `OP_FINDRA`)
- Subida binaria de adjuntos (se modela como metadata)
- Integraciones reales con organismos externos
- Replica Set MongoDB (instancia unica en dev; sin cambios de codigo para produccion)

Los ajustes frente al diseno original estan documentados en la seccion 7 del [informe](docs/%5BBBDD%202%5D%20Informe_findra.md) y en [docs/decisiones-tecnicas.md](docs/decisiones-tecnicas.md).
