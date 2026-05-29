# FINDRA

FINDRA es un prototipo GovTech para centralizar casos del Protocolo Alerta Sofia, reducir latencias operativas y mantener trazabilidad de alertas, reportes ciudadanos y acciones institucionales.

## Stack

- Frontend: React JS + Vite
- Backend: Java 17 + Spring Boot
- Persistencia: MongoDB local
- Documentacion API: Swagger/OpenAPI

## Requisitos

- Java 17
- Maven 3.9+
- Node.js 20+ y npm
- MongoDB local activo en `localhost:27017`
- MongoDB Compass opcional para inspeccionar la base `findra`

La aplicacion usa MongoDB local por defecto:

```properties
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/findra}
```

No hace falta crear `MONGODB_URI` si MongoDB corre en el puerto estandar.

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

## Docker Opcional

Si no se desea usar MongoDB local, existe un `docker-compose.yml` opcional para levantar MongoDB y Mongo Express.

```powershell
docker compose up -d
```

## Alcance MVP

- Incluido: dashboard, busqueda/filtros, detalle de caso, emision de alertas, reportes ciudadanos, seed, API documentada, historial de acciones.
- Fuera de alcance: autenticacion real, subida binaria de adjuntos, integraciones reales con organismos externos.

Los ajustes frente al diseno original estan documentados en [docs/decisiones-tecnicas.md](docs/decisiones-tecnicas.md).
