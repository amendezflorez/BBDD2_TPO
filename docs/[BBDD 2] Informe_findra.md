FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
FINDRA
Sistema Inteligente de Búsqueda y Alerta
"Cada segundo importa. Cada dato salva."
Información del Documento
Materia: Ingeniería de Datos II
Trabajo: TP Integrador — Entrega Final
Eje temático: Protocolo Alerta Sofía — Argentina
Enfoque evaluado: Producto final, arquitectura, calidad técnica y documentación
Año: 2026

Stack tecnológico implementado:
- Backend: Java 21 + Spring Boot 3.3
- Frontend: React 18 + Vite
- Persistencia: MongoDB 8 (Replica Set rs0 en producción)
- Caché: Redis 7 (Spring Cache, TTL 30s, invalidación reactiva)
- API: REST documentada con OpenAPI 3.0 / Swagger UI
Grupo 3:
- Andrés Felipe Méndez Florez
- Aylen Solana Nahuel
- Ignacio Lapolla
- Jonathan Dominguez
- Matias Marcon
Ingeniería de Datos II · TPO · 2026

FINDRA | Informe Técnico — Entrega Final
1. Introducción y Contexto del Problema
En la República Argentina, las estadísticas registran un promedio de 7.000 reportes anuales
de menores desaparecidos. Si bien la mayor proporción de estos casos se esclarece
durante las primeras 72 horas, aquellas situaciones que revisten un riesgo vital inminente
demandan un despliegue estatal rápido, masivo y coordinado. Con este propósito, el
Ministerio de Seguridad de la Nación implementó en 2019 el Protocolo Alerta Sofía, un
mecanismo de emergencia diseñado para articular los esfuerzos de las fuerzas federales, el
sistema judicial, los medios de comunicación y la sociedad civil frente a la desaparición de
niñas, niños y adolescentes (NNyA).
No obstante su relevancia institucional, la operatividad del protocolo se encuentra
actualmente obstaculizada por limitaciones tecnológicas de carácter estructural:
● Fragmentación interinstitucional: Las distintas dependencias involucradas (Policía
Federal, Gendarmería, SIFEBU y fiscalías) operan con bases de datos y sistemas de
gestión independientes, carentes de interoperabilidad.
● Latencia en la activación: Los procesos burocráticos de validación y difusión
generan demoras significativas, extendiendo a horas un procedimiento que requiere
inmediatez.
● Heterogeneidad de los datos: La evidencia recolectada —que incluye material
audiovisual, fojas judiciales, registros de telecomunicaciones y datos de
geolocalización— carece de una estructura estandarizada que facilite su
procesamiento cruzado.
● Ausencia de trazabilidad: El sistema actual no dispone de un registro unificado que
permita auditar de manera transparente las intervenciones realizadas sobre un
expediente en curso.
Como respuesta a esta problemática operativa surge FINDRA, un proyecto de base
tecnológica orientado al sector público (Gov Tech). Su finalidad es proponer una plataforma
de gestión centralizada que optimice los tiempos de respuesta del Protocolo Alerta Sofía. A
través de la implementación de bases de datos NoSQL, arquitecturas distribuidas e
integraciones interinstitucionales en tiempo real, este desarrollo busca resolver la
fragmentación de la información y dotar de mayor eficiencia a la búsqueda de menores en
situación de riesgo.
Ingeniería de Datos II · TPO · 2026

FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
2. Definición del Problema
El problema central que aborda FINDRA puede formularse de la siguiente manera:
Enunciado del Problema
Los organismos encargados de ejecutar el Protocolo Alerta Sofía carecen de una
infraestructura tecnológica unificada que permita gestionar casos en tiempo real, procesar
datos heterogéneos provenientes de múltiples fuentes y garantizar la trazabilidad de las
acciones operativas. Esta deficiencia genera latencias críticas y pérdida de información en
escenarios de emergencia donde el factor temporal es determinante para preservar la vida
del menor.
La complejidad de este problema abarca cuatro dimensiones principales:
2.1 Dimensión Institucional En la ejecución del protocolo intervienen actores de diversa
naturaleza operativa y jurisdiccional: el Ministerio de Seguridad de la Nación, el SIFEBU, las
cuatro fuerzas federales (Gendarmería, Prefectura, Policía Federal y PSA), el Ministerio
Público Fiscal (a través de PROTEX), la ONG Missing Children y los medios de
comunicación. En la actualidad, estas entidades operan en "silos de información", sin un
sistema común que permita el acceso simultáneo y sincronizado a los expedientes.
2.2 Dimensión de los Datos La información que alimenta un caso activo posee una alta
entropía y es inherentemente polimórfica. Incluye fotografías, datos biométricos, testimonios
de denunciantes, coordenadas geográficas (GPS), documentos judiciales en formato texto y
reportes ciudadanos no estructurados. Esta variabilidad de formatos y fuentes hace que los
esquemas de bases de datos relacionales tradicionales resulten rígidos e ineficientes para
el cruce ágil de información.
2.3 Dimensión Temporal En el ámbito de las búsquedas de personas, la "ventana de
oportunidad" es crítica: las primeras horas tras una desaparición con riesgo inminente son
estadísticamente las más determinantes para un desenlace favorable. Cualquier latencia
técnica en la validación de datos, la activación de la alarma o la coordinación de las fuerzas
impacta negativamente en la efectividad del protocolo.
2.4 Relevancia del Problema La pertinencia de abordar este problema trasciende el
ejercicio académico teórico. Responde a una necesidad operativa real y documentada,
respaldada por la normativa nacional (Resolución MS N° 208/2019). El desarrollo de una
solución orientada a datos busca proveer a los organismos estatales de las herramientas
tecnológicas adecuadas que hoy les faltan para cumplir su misión de manera eficaz.
Ingeniería de Datos II · TPO · 2026

FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
3. Modelado de Datos Inicial (NoSQL)
La elección del modelo de datos es una de las decisiones técnicas más importantes del
sistema. A continuación se presenta el modelo adoptado para FINDRA junto con su
justificación.
3.1 Modelo Adoptado: Orientado a Documentos (MongoDB)
FINDRA utiliza un modelo de base de datos orientada a documentos, donde cada caso
activo del Protocolo Alerta Sofía se representa como un documento JSON autocontenido.
Este documento agrupa toda la información relevante del caso en una estructura flexible y
jerárquica.
Estructura del documento principal: Caso
{
"_id": "ObjectId('64f1a2b3c4d5e6f7a8b9c0d1')",
"caso_id": "AS-2025-001",
"estado": "activo",
"fecha_activacion": "2025-04-15T10:30:00Z",
"menor": {
"nombre": "María Fernanda López",
"edad": 8,
"sexo": "F",
"ultima_ubicacion": {
"type": "Point",
"coordinates": [-58.3816, -34.6037],
"descripcion": "Plaza Constitución, CABA"
},
"foto_url": "/media/AS-2025-001/foto_principal.jpg"
},
"denunciante": {
"nombre": "Laura López",
"vinculo": "madre",
"tel": "+54 11 5555-1234"
},
"autoridad_judicial": {
"juez": "Dr. Carlos Méndez",
"fiscal": "Dra. Ana Rodríguez",
"nro_expediente": "JF-2025-00412"
},
"alertas_emitidas": [
{
"canal": "SMS_masivo",
Ingeniería de Datos II · TPO · 2026

FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
"timestamp": "2025-04-15T10:35:00Z",
"zona": "CABA",
"operador": "SIFEBU_op1"
},
{
"canal": "redes_sociales",
"timestamp": "2025-04-15T10:37:00Z",
"plataforma": "Facebook"
}
],
"reportes_ciudadanos": [
{
"timestamp": "2025-04-15T11:10:00Z",
"ubicacion": {
"type": "Point",
"coordinates": [-58.3790, -34.6050]
},
"descripcion": "Niña vista cerca del subte Línea C",
"estado": "verificado"
}
],
"documentos_adjuntos": [
{
"tipo": "denuncia_policial",
"url": "/docs/AS-2025-001/denuncia_pfa.pdf",
"subido_por": "PFA",
"timestamp": "2025-04-15T10:32:00Z"
}
],
"historial_acciones": [
{
"accion": "alerta_activada",
"usuario": "SIFEBU_op1",
"timestamp": "2025-04-15T10:35:00Z"
}
],
"resultado": null
}
{
"_id": "ObjectId('64f1a2b3c4d5e6f7a8b9c0d1')",
"caso_id": "AS-2025-001",
"estado": "activo",
Ingeniería de Datos II · TPO · 2026

FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
"fecha_activacion": "T10:30:00Z",
"menor": {
"nombre": "María Fernanda López",
"edad": 8,
"sexo": "F",
"ultima_ubicacion": {
"type": "Point",
"coordinates": [-58.3816, -34.6037],
"descripcion": "Plaza Constitución, CABA"
},
"foto_url": "/media/AS-2025-001/foto_principal.jpg"
},
"denunciante": {
"nombre": "Laura López",
"vinculo": "madre",
"tel": "+54 11 5555-1234"
},
"autoridad_judicial": {
"juez": "Dr. Carlos Méndez",
"fiscal": "Dra. Ana Rodríguez",
"nro_expediente": "JF-2025-00412"
},
"alertas_emitidas": [
{
"canal": "SMS_masivo",
"timestamp": "T10:35:00Z",
"zona": "CABA",
"operador": "SIFEBU_op1"
},
{
"canal": "redes_sociales",
"timestamp": "T10:37:00Z",
"plataforma": "Facebook"
}
],
"reportes_ciudadanos": [
{
"timestamp": "T11:10:00Z",
"ubicacion": {
"type": "Point",
"coordinates": [-58.3790, -34.6050]
},
"descripcion": "Niña vista cerca del subte Línea C",
"estado": "verificado"
}
],
Ingeniería de Datos II · TPO · 2026

FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
"documentos_adjuntos": [
{
"tipo": "denuncia_policial",
"url": "/docs/AS-2025-001/denuncia_pfa.pdf",
"subido_por": "PFA",
"timestamp": "T10:32:00Z"
}
],
"historial_acciones": [
{
"accion": "alerta_activada",
"usuario": "SIFEBU_op1",
"timestamp": "T10:35:00Z"
}
],
"resultado": null
}
// Búsqueda por ID de caso (acceso principal O(1))
db.casos.createIndex({ "caso_id": 1 }, { unique: true })
// Búsqueda geoespacial -- última ubicación del menor
db.casos.createIndex({ "menor.ultima_ubicacion": "2dsphere" })
// Búsqueda geoespacial -- reportes ciudadanos
db.casos.createIndex({ "reportes_ciudadanos.ubicacion": "2dsphere" })
// Filtros operativos frecuentes
db.casos.createIndex({ "estado": 1, "fecha_activacion": -1 })
// Búsqueda por edad y sexo (perfilado del menor)
db.casos.createIndex({ "menor.edad": 1, "menor.sexo": 1 })
// Auditoría -- historial de acciones por usuario
db.casos.createIndex({ "historial_acciones.usuario": 1 })
Colecciones del sistema
El modelo se organiza en las siguientes colecciones principales:
• casos: documento central por cada caso activo o cerrado del protocolo.
• usuarios: operadores del sistema con roles (SIFEBU, fiscal, fuerza federal,
ciudadano).
• organismos: instituciones participantes del protocolo con sus datos de contacto e
integración.
• alertas: registro independiente de cada alerta emitida (para auditoría y analítica).
Ingeniería de Datos II · TPO · 2026

FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
• reportes_ciudadanos: avistamientos recibidos, con estado de verificación.
3.2 Justificación del Modelo
La elección del modelo orientado a documentos responde a características estructurales del
problema:
• Cada caso tiene una cantidad variable de alertas, reportes, documentos
adjuntos y acciones. En un modelo relacional, esto implicaría múltiples tablas
con JOINs costosos. En MongoDB, todo convive dentro de un único
documento anidado.: Variabilidad del esquema
• La información proveniente de fuentes heterogéneas (reportes ciudadanos,
SMS, redes sociales, documentos judiciales) no sigue un esquema fijo. El
modelo de documentos absorbe esta variabilidad de forma natural.: Datos
semi-estructurados
• El patrón de acceso principal es "dame todo lo que sé del caso AS-2025-001".
Esta consulta es O(1) en MongoDB (una sola lectura de documento) versus
múltiples JOINs en SQL.: Consultas por caso
• MongoDB soporta sharding nativo, permitiendo distribuir la carga entre
múltiples nodos a medida que el sistema crece hacia otras provincias o
países.: Escalabilidad horizontal
• MongoDB ofrece índices 2dsphere nativos para las coordenadas de última
ubicación y de reportes ciudadanos, críticos para las búsquedas por zona
geográfica.: Indexación geoespacial
Ingeniería de Datos II · TPO · 2026

FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
4. Arquitectura Propuesta
La arquitectura de FINDRA está diseñada en capas, priorizando la disponibilidad, la
consistencia eventual controlada y la capacidad de integración con múltiples sistemas
externos.
4.1 Capas de la Arquitectura
Capa de Ingesta y Fuentes de Datos
Esta capa recibe datos de múltiples orígenes mediante APIs REST y conectores específicos
por organismo. Incluye: denuncias formalizadas desde comisarías, notificaciones judiciales
desde fiscalías y la línea 134.
Capa de Procesamiento y Lógica de Negocio
Un servidor de aplicaciones central (API Gateway) válida, enriquece y persiste los datos
entrantes en MongoDB. Implementa las reglas del protocolo: verificación de requisitos de
activación y registro automático del historial de acciones.
Capa de Persistencia NoSQL
MongoDB como motor principal, configurado en Replica Set de 3 nodos para alta
disponibilidad. El nodo primario acepta escrituras y lecturas en tiempo real; los secundarios
proveen redundancia y lecturas de consultas analíticas.
Capa de Caché
Redis como capa de caché para sesiones de usuario activas y datos de casos en curso que
se consultan frecuentemente. Esto reduce la carga sobre MongoDB para operaciones de
lectura repetitivas.
Capa de Presentación
Dashboard web para operadores institucionales (SIFEBU, fuerzas federales, fiscalías) que
consume la API REST central.
Ingeniería de Datos II · TPO · 2026

FINDRA   |   Informe de Pensamiento, Modelado y Decisiones Iniciales
4.2 Decisiones Arquitectónicas Clave
•  Se utiliza un Replica Set de MongoDB (mínimo 3 nodos) en lugar de una
instancia  única, garantizando que si el nodo primario falla, un secundario
asuma  automáticamente  el  rol.  En  un  sistema  de  emergencias,  la
disponibilidad no es negociable.: Replica Set sobre Standalone
•  Para  las  escrituras  críticas  (activación  de  alerta,  cierre  de  caso)  se  usa
writeConcern: majority, asegurando que la operación se confirme en al menos
dos nodos antes de responder al cliente. Para lecturas analíticas no críticas se
permite readPreference: secondary.: Consistencia eventual controlada
•  En la fase de escalado, los datos se particionan (shard key: zona geográfica)
para distribuir la carga de escritura entre nodos correspondientes a distintas
regiones del país.: Sharding por zona geográfica
•  Toda  la  lógica  de  negocio  es accesible mediante API REST documentada
(OpenAPI 3.0), lo que facilita la integración con los sistemas existentes de
cada organismo participante sin requerir migraciones de sus propios datos.:
API-first

5. Selección y Justificación Tecnológica
La  selección  de  MongoDB  como  motor  principal  de persistencia no fue arbitraria. Se
evaluaron todas las familias de bases de datos NoSQL disponibles y se las comparó en
función de los requerimientos concretos del sistema.
5.1 Tabla Comparativa de Motores NoSQL

| Motor NoSQL  | Tipo        |              |                    |         |                 | Decisión  |
| ------------ | ----------- | ------------ | ------------------ | ------- | --------------- | --------- |
|              |             | Caso de uso  | Limitación en      |         |                 |           |
|              |             | ideal        |                    | FINDRA  |                 |           |
| MongoDB      | Documentos  |              | Ninguna relevante  |         | ✔ SELECCIONADO  |           |
Datos
semi-estructurad
os y variables
| Apache     | Columnar  | Escrituras    | Consultas                  |     | ✘ Descartado  |     |
| ---------- | --------- | ------------- | -------------------------- | --- | ------------- | --- |
| Cassandra  |           | masivas       | complejas costosas;        |     |               |     |
|            |           | distribuidas  | (IoT,  esquema rígido por  |     |               |     |
|            |           | logs)         | columna                    |     |               |     |
| Neo4j      | Grafos    | Relaciones    | Overhead                   |     | ✘ Descartado  |     |
|            |           | complejas     | entre  innecesario;        |     | casos         |     |
entidades
de FINDRA no son
grafos
| Redis  | Clave-Valor  |        |         |               | ✘   |           |
| ------ | ------------ | ------ | ------- | ------------- | --- | --------- |
|        |              | Caché  | y  Sin  | persistencia  |     | Como  BD  |
principal
|     |     | sesiones de alta  | estructurada         |     | ni  |     |
| --- | --- | ----------------- | -------------------- | --- | --- | --- |
|     |     | velocidad         | consultas complejas  |     |     |     |
HBase  Columnar  Analítica  de  big  Complejidad  ✘ Descartado
|     | (Hadoop)  | data  a  | escala  operativa  | excesiva     |     |     |
| --- | --------- | -------- | ------------------ | ------------ | --- | --- |
|     |           | masiva   | para               | el  volumen  |     |     |
actual

Ingeniería de Datos II  ·  TPO  ·  2026

FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
5.2 Análisis por Alternativa Descartada
Apache Cassandra (Columnar)
Cassandra es una solución excelente para escenarios de escritura masiva distribuida, como
telemetría de dispositivos IoT o logs de millones de eventos por segundo. Sin embargo, su
modelo de datos basado en familias de columnas obliga a definir el esquema de consulta en
el momento del diseño de la tabla (query-driven design). En FINDRA, los patrones de
consulta son variados e imprevisibles: un operador puede necesitar filtrar por zona, por
estado, por edad del menor o por fecha de activación. Adaptar este modelo a Cassandra
implicaría duplicar datos en múltiples tablas, aumentando la complejidad operativa sin
beneficio real dado el volumen de datos esperado.
Neo4j (Grafos)
Neo4j es el motor ideal cuando las relaciones entre entidades son el dato más valioso:
redes sociales, detección de fraude financiero, mapeo de conexiones entre sospechosos. Si
bien existe un caso teórico para usar grafos en FINDRA (mapear relaciones entre
denunciantes, sospechosos y casos históricos), este requerimiento es secundario y no
justifica la complejidad operativa de mantener una base de datos de grafos como sistema
principal. En su lugar, esta funcionalidad puede implementarse como un módulo analítico
independiente sobre los datos exportados desde MongoDB.
Redis (Clave-Valor)
Redis es adoptado en la arquitectura de FINDRA, pero en su rol natural: caché de alta
velocidad y gestión de sesiones. Usar Redis como base de datos principal implicaría
almacenar documentos complejos como strings serializados, perdiendo capacidad de
consulta, indexación y persistencia estructurada. Redis no está diseñado para ser la fuente
de verdad de un sistema transaccional.
HBase (Columnar sobre Hadoop)
HBase es una solución de big data pensada para organizaciones que ya operan en el
ecosistema Hadoop y necesitan procesar petabytes de datos históricos. El overhead
operativo de desplegar y mantener un cluster Hadoop es desproporcionado para el volumen
de datos de FINDRA en sus fases iniciales. Además, HBase carece de las capacidades de
consulta ad-hoc y la flexibilidad de esquema que el sistema requiere.
5.3 Por qué MongoDB es la Elección Correcta
MongoDB satisface todos los requerimientos técnicos del sistema de forma directa y sin
compromisos:
• Permite incorporar nuevos tipos de datos (ej.: biometría facial en futuras
versiones) sin migraciones de base de datos.: Esquema flexible (schemaless)
• Soporta filtros por campos anidados, búsquedas geoespaciales, aggregation
pipelines y full-text search de forma nativa.: Consultas ricas y expresivas
• Sharding integrado en el motor, sin necesidad de middleware adicional.:
Escalabilidad horizontal nativa
• Replica Set con failover automático, crítico para un sistema de emergencias.:
Alta disponibilidad
• Drivers oficiales para Node.js, Python, Java; MongoDB Atlas como opción
cloud gestionada; Compass para administración visual.: Ecosistema maduro
Ingeniería de Datos II · TPO · 2026

FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
• A partir de MongoDB 4.0, las transacciones multi-documento garantizan
consistencia ACID para operaciones críticas como la activación de una alerta.:
ACID en operaciones de documento
6. Conclusión
FINDRA aborda un problema real, urgente y con impacto social directo: la falta de
infraestructura tecnológica que potencie la eficacia del Protocolo Alerta Sofía en Argentina.
El análisis presentado en este informe demuestra que:
• El problema está bien definido, contextualizado y es de alcance acotado para la fase
inicial del proyecto.
• El modelo de datos orientado a documentos (MongoDB) es la elección óptima dada
la heterogeneidad estructural de los datos del protocolo.
• La arquitectura en capas con Replica Set garantiza disponibilidad y trazabilidad, dos
requisitos no negociables en sistemas de emergencia.
• La selección tecnológica es sólida: se evaluaron todas las alternativas NoSQL y se
descartaron con argumentos técnicos específicos, no por desconocimiento.
Las próximas etapas del trabajo integrador profundizarán en la implementación del prototipo
funcional, la estrategia de replicación y particionamiento (Unidades IV y VI), y la integración
del sistema con fuentes de datos externas.
Ingeniería de Datos II · TPO · 2026

---

7. Pipeline de Datos End-to-End (E2E)

El pipeline de FINDRA integra dos flujos principales: ingesta institucional desde organismos externos y operación interna desde el frontend. Ambos convergen en MongoDB como fuente de verdad única, con Redis como capa de caché reactiva.

7.1 Flujo A — Ingesta multi-organismo

Los organismos participantes del Protocolo Alerta Sofía (PFA, Gendarmería, Prefectura, PSA, SIFEBU, PROTEX, Missing Children) ingresan datos al sistema mediante un único endpoint REST:

```
POST /api/ingesta/organismo
{
  "organismo": "PFA",
  "tipoFuente": "denuncia_formal",
  "payload": { ... }
}
```

El campo `tipoFuente` determina la acción sobre el documento `Caso` en MongoDB:

| tipoFuente | Organismo típico | Acción |
|---|---|---|
| `denuncia_formal` | PFA, Gendarmería, Prefectura, PSA | Crea un nuevo documento Caso |
| `notificacion_alerta` | SIFEBU | Agrega Alerta al array `alertasEmitidas` |
| `notificacion_judicial` | PROTEX | Actualiza `autoridadJudicial` + agrega `documentosAdjuntos` |
| `reporte_avistamiento` | Missing Children | Agrega ReporteCiudadano al array `reportesCiudadanos` |

Cada ingesta enriquece el mismo documento Caso de forma incremental. Un caso puede recibir payloads de múltiples organismos en cualquier orden, permitiendo la coordinación interinstitucional real que es el objetivo central del protocolo.

Validaciones de seguridad aplicadas en el pipeline de ingesta:
- El campo `organismo` se valida contra el enum `OrganismoFuente` (rechaza organismos no reconocidos)
- Las URLs de documentos y fotos se validan con patrón `SAFE_URL` (bloquea esquemas `javascript:`, `file:`, `data:`)
- Los campos de texto libre del historial de auditoría se sanitizan eliminando caracteres de control (`\r\n\t`) y con límite de longitud

7.2 Flujo B — Operación desde el frontend

```
Operador → Dashboard (React)
  → GET /api/casos/resumen
    → Redis cache hit (TTL 30s) → respuesta inmediata
    → Redis cache miss → MongoDB aggregation pipeline → SET cache → respuesta
  → Emite Alerta Sofía
    → POST /api/casos/{id}/alertas
      → CasoService actualiza documento (alertas + historial)
      → @CacheEvict invalida dashboard-resumen en Redis
  → Cierra o archiva caso
    → PATCH /api/casos/{id}/estado
      → CasoService actualiza estado + fechaCierre + resultado + historial
      → @CacheEvict invalida dashboard-resumen en Redis
```

La invalidación reactiva del caché garantiza que el dashboard refleje el estado real tras cualquier escritura, sin depender de TTL largo ni polling.

7.3 Diagrama de secuencia

Los diagramas de secuencia detallados de ambos flujos están disponibles en `docs/diagramas.md` (Diagrama 3 — Pipeline E2E).

---

8. Arquitectura Final Implementada

8.1 Diagrama de componentes

El diagrama C4 completo de la arquitectura en producción está disponible en `docs/diagramas.md` (Diagrama 1 — Arquitectura del Sistema). Muestra las relaciones entre React, Spring Boot, Redis y el Replica Set MongoDB rs0.

8.2 Modelo de datos final

El diagrama de clases del modelo de datos implementado está disponible en `docs/diagramas.md` (Diagrama 2 — Modelo de Datos). La estructura central es el documento `Caso` con todos sus sub-documentos embebidos.

Diferencias respecto al diseño inicial del Parcial 1:
- Los sub-documentos `historialAcciones`, `alertasEmitidas`, `reportesCiudadanos` y `documentosAdjuntos` pasaron de ser colecciones independientes a arrays embebidos en `Caso`. Esto reduce las lecturas a una sola operación O(1) por caso.
- Se agregó el campo `resultado` en `Caso` para registrar el desenlace al cerrar o archivar.
- Se agregó el campo `fechaCierre` complementando `fechaActivacion`.
- El campo `operador` en `AccionHistorial` reemplaza a `usuario` para reflejar que puede ser un organismo o un operador humano.

8.3 Análisis de dependencias del sistema

Con el objetivo de validar la cohesión arquitectónica del código implementado, se realizó un análisis estático completo del repositorio. El grafo resultante comprende **396 nodos** (364 archivos de código + 32 conceptos y documentos) y **667 relaciones**, agrupados en **32 comunidades** detectadas mediante el algoritmo de Louvain.

Las comunidades identificadas se corresponden directamente con las capas de la arquitectura: capa de presentación (React + Vite), capa de API (Spring Controllers), capa de negocio (Services + Mappers), capa de datos (Repositories + MongoDB) y capa de caché (Redis). La densidad de relaciones inter-capa es consistente con el patrón de dependencia unidireccional diseñado: el frontend no conoce la persistencia, los services no conocen los controllers, y los mappers no tienen dependencias circulares.

**[FIGURA — Grafo interactivo de dependencias del sistema]**
*Captura del grafo de conocimiento generado por análisis AST estático del repositorio. Cada nodo es un archivo o concepto; los colores indican la comunidad (módulo lógico) al que pertenece. El grafo completo e interactivo está disponible en `docs/graph.html`.*

---

9. Ajustes sobre el Diseño Original (Trade-offs Parcial 1 → Entrega Final)

Durante la fase de implementación se tomaron decisiones pragmáticas que ajustan el diseño
propuesto en el Parcial 1. Cada ajuste responde a un trade-off técnico explícito, documentado
con el diseño original, la implementación real y la justificación de la decisión.

9.1 Redis: de caché de sesiones a caché reactiva del dashboard

| | Diseño original (Parcial 1) | Implementación final |
|---|---|---|
| Rol de Redis | Caché de sesiones y datos frecuentes | Caché del endpoint `/api/casos/resumen` |
| Mecanismo | No especificado | Spring Cache `@Cacheable` + `@CacheEvict` |
| TTL | No especificado | 30 segundos |
| Invalidación | Manual / por expiración | Reactiva: cualquier escritura sobre casos invalida automáticamente |

Trade-off: Se priorizó la caché de la operación de mayor costo (dashboard con 4 queries MongoDB) sobre la caché de sesiones, dado que el sistema usa autenticación simulada. La invalidación reactiva garantiza consistencia sin necesidad de TTL corto.

9.2 Mapa operativo con coordenadas reales

| | Diseño original (Parcial 1) | Implementación final |
|---|---|---|
| Visualización | Mapa geográfico de casos activos | Proyección sobre bounding box de Argentina |
| Proveedor cartográfico | Google Maps / Leaflet (implícito) | Ninguno (proyección matemática propia) |
| Fuente de coordenadas | GPS / coordenadas del caso | `menor.ultimaUbicacion.coordinates` de MongoDB |

Trade-off: Se descartó integrar un proveedor cartográfico externo para evitar dependencias de API keys en el entorno de evaluación. Los índices 2dsphere ya existían en MongoDB; la proyección sobre el bounding box argentino (lng: -73 a -53, lat: -55 a -22) es suficiente para demostrar la capacidad geoespacial del sistema.

9.3 Autenticación simulada vs. sistema de roles

| | Diseño original (Parcial 1) | Implementación final |
|---|---|---|
| Roles | SIFEBU, fiscal, fuerza federal, ciudadano | Operador único `OP_FINDRA` |
| Mecanismo | JWT / OAuth2 (implícito) | Hardcodeado en historial de acciones |
| Modelo de datos | Entidad Usuario con roles | Entidad Usuario modelada; autenticación no conectada |

Trade-off: El foco evaluado en el Parcial 2 es el pipeline de datos y la implementación técnica, no la seguridad de acceso. La arquitectura de roles está modelada en la entidad `Usuario`; la integración real de JWT/OAuth2 es extensión directa sin cambios de esquema.

9.4 Documentos adjuntos: metadata estructurada vs. binarios

| | Diseño original (Parcial 1) | Implementación final |
|---|---|---|
| Almacenamiento | Upload de documentos y fotos | Metadata estructurada (tipo, url, organismo, timestamp) |
| Motor | GridFS o S3 (implícito) | No aplica; URL apunta a ruta relativa `/media/` |
| Validación de seguridad | No especificada | Patrón `SAFE_URL` valida esquemas permitidos |

Trade-off: El almacenamiento binario requiere infraestructura adicional (GridFS, S3, CDN) fuera del alcance del MVP. El modelo de metadata preserva la estructura de datos completa y permite integrar almacenamiento real sin cambios de esquema ni migración.

9.5 Replica Set: 3 nodos en producción, instancia única en desarrollo

| | Diseño original (Parcial 1) | Implementación final |
|---|---|---|
| Arquitectura MongoDB | Replica Set rs0, 3 nodos, `writeConcern: majority` | Instancia única en Docker (dev); Replica Set en producción |
| Failover | Automático | Automático en producción vía `MONGODB_URI` |
| Cambio de código para producción | — | Ninguno: solo cambiar variable de entorno |

Trade-off: Levantar un Replica Set de 3 nodos localmente introduce complejidad operativa (inicialización del conjunto, resolución de hostnames entre contenedores, gestión de elecciones) que no aporta valor al prototipo académico. La arquitectura de alta disponibilidad está diseñada, documentada y lista para activarse con un cambio de configuración:

```
MONGODB_URI=mongodb://mongo1:27017,mongo2:27017,mongo3:27017/findra?replicaSet=rs0
```

9.6 Ingesta multi-organismo: endpoint unificado vs. integraciones punto a punto

| | Diseño original (Parcial 1) | Implementación final |
|---|---|---|
| Integración institucional | APIs REST por organismo (implícito) | Endpoint único `POST /api/ingesta/organismo` |
| Modelo de payload | No especificado | JSON canónico FINDRA con `organismo`, `tipoFuente` y `payload` |
| Validación de origen | No especificada | Enum `OrganismoFuente` (PFA, Gendarmería, Prefectura, PSA, SIFEBU, PROTEX, Missing Children) |
| Enriquecimiento incremental | No especificado | Múltiples organismos enriquecen el mismo documento Caso sin sobrescribir |

Trade-off: El diseño original planteaba integraciones directas con los sistemas de cada organismo. Se implementó un endpoint unificado que centraliza la ingesta y mapea el payload heterogéneo de cada fuente al modelo canónico FINDRA. Esto simplifica la integración (un solo contrato de API para todos los organismos) y permite que un caso sea construido incrementalmente: la PFA crea el caso, SIFEBU agrega alertas, PROTEX agrega información judicial y Missing Children registra avistamientos, todo en el mismo documento MongoDB.

9.7 Runtime backend: Java 17 → Java 21 LTS

| | Diseño original (Parcial 1) | Implementación final |
|---|---|---|
| Runtime | Java 17 LTS | Java 21 LTS |
| Soporte LTS hasta | 2029 | 2031 |
| Características nuevas aprovechadas | — | Records, pattern matching, switch expressions |
| Compatibilidad de testing | Mockito inline | ByteBuddy MockMaker (configurado en `mockito-extensions/`) |

Trade-off: Java 21 es el LTS más reciente con soporte hasta 2031, incluye Virtual Threads (Project Loom) para futura mejora de concurrencia sin refactor, y los Records de Java 16+ simplifican los DTOs. El único ajuste requerido fue configurar el `MockMaker` de Mockito para compatibilidad con Java 21 en los tests.

9.8 Modelo de datos: referencias entre colecciones → embedding completo

| | Diseño original (Parcial 1) | Implementación final |
|---|---|---|
| Sub-documentos | Colecciones separadas (implícito) | Arrays embebidos en documento `Caso` |
| Patrón de acceso | JOINs / lookups | Lectura O(1) por `casoId` |
| Consistencia | Transacciones multi-documento | Atomic write en documento único |

Trade-off: El diseño inicial modelaba alertas, reportes y documentos como colecciones independientes. El patrón de acceso dominante del sistema es "dame todo lo que sé del caso AS-2025-001", que en un esquema con referencias requeriría múltiples lookups. El embedding resuelve esto con una lectura única y garantiza consistencia atómica sin necesidad de transacciones multi-documento.

---

10. Conclusión Final

FINDRA en su versión final de entrega cumple los objetivos planteados en el Parcial 1 y supera varios de ellos mediante decisiones técnicas justificadas:

- **Pipeline E2E funcional**: ingesta multi-organismo → MongoDB → Redis → frontend, con trazabilidad completa en el historial de acciones de cada caso.
- **Modelo de datos optimizado**: embedding completo en documento `Caso`, con índices geoespaciales 2dsphere y aggregation pipeline para métricas del dashboard.
- **Caché reactiva**: Redis invalida el resumen del dashboard ante cualquier escritura, garantizando consistencia sin polling.
- **Arquitectura escalable documentada**: Replica Set rs0 de 3 nodos listo para activar en producción con cambio de variable de entorno.
- **Seguridad en la capa de ingesta**: validación de organismo por enum, sanitización de URLs y textos en el historial de auditoría.
- **Calidad verificada**: 5 tests unitarios de servicio, análisis estático de 396 nodos y 667 relaciones con 32 comunidades coherentes con la arquitectura diseñada.

Los trade-offs documentados en la sección 9 demuestran que cada desviación respecto al diseño original fue una decisión técnica deliberada, no una omisión. La arquitectura está preparada para evolucionar hacia producción sin cambios estructurales.

Ingeniería de Datos II · TPO · 2026