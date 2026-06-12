FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
FINDRA
Sistema Inteligente de Búsqueda y Alerta
"Cada segundo importa. Cada dato salva."
Información del Documento
Materia: Ingeniería de Datos II
Trabajo: TP Integrador — Presentación Inicial
Eje temático: Protocolo Alerta Sofía — Argentina
Enfoque evaluado: Pensamiento, Modelado y Decisiones Iniciales
Año: 2026
Grupo 3:
- Andrés Felipe Méndez Florez
- Aylen Solana Nahuel
- Ignacio Lapolla
- Jonathan Dominguez
- Matias Marcon
Ingeniería de Datos II · TPO · 2026

FINDRA | Informe de Pensamiento, Modelado y Decisiones Iniciales
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

7. Ajustes sobre el Diseño Original (Entrega Final)

Durante la fase de implementación se tomaron decisiones pragmáticas que ajustan el diseño
propuesto en el Parcial 1. Cada ajuste responde a un trade-off técnico explícito.

7.1 Redis integrado en Parcial 2
Diseño original: Redis propuesto como capa de caché de sesiones y datos frecuentes.
Implementación: Redis integrado como caché del endpoint /api/dashboard/resumen mediante
Spring Cache (@Cacheable con TTL de 30 segundos). El caché se invalida automáticamente
(@CacheEvict) ante cualquier escritura sobre casos (crear, cambiar estado, emitir alerta,
registrar reporte). Docker Compose incluye el servicio redis:7-alpine.
Justificación: El dashboard ejecuta 4 queries a MongoDB en cada carga. Con Redis, la
respuesta es O(1) desde memoria para consultas repetidas. La invalidación reactiva garantiza
consistencia sin TTL largo.

7.2 Mapa operativo con coordenadas reales
Diseño original: Dashboard con visualización geográfica de casos activos.
Implementación: Los pins del mapa se posicionan proyectando las coordenadas reales
almacenadas en MongoDB (menor.ultimaUbicacion.coordinates) sobre el bounding box
geográfico de Argentina (lng: -73 a -53, lat: -55 a -22). No se integró un proveedor
cartográfico externo (Google Maps, Leaflet) para evitar dependencias de API keys en el
entorno de evaluación.
Justificación: Las coordenadas 2dsphere ya estaban en MongoDB con índices geoespaciales.
La proyección simple es suficiente para demostrar la capacidad del sistema sin agregar
complejidad operativa.

7.3 Autenticación simulada
Diseño original: Sistema de roles (SIFEBU, fiscal, fuerza federal, ciudadano).
Implementación: Operador fijo "OP_FINDRA" hardcodeado en cada acción del historial.
Justificación: El foco evaluado en Parcial 2 es pipeline de datos e implementación técnica,
no seguridad. La arquitectura de roles está modelada en la entidad Usuario; la autenticación
real (JWT/OAuth2) es extensión directa del diseño actual.

7.4 Documentos adjuntos como metadata
Diseño original: Upload de documentos judiciales y evidencia fotográfica.
Implementación: Los adjuntos se modelan como metadata estructurada (tipo, url, subidoPor,
timestamp) sin almacenamiento binario. El campo foto_url del menor sigue el mismo patrón.
Justificación: El almacenamiento binario (GridFS, S3) requiere infraestructura adicional fuera
del alcance del MVP. El modelo de metadata preserva la estructura de datos y permite
integrar el almacenamiento real sin cambios de esquema.

7.5 Replica Set simplificado a instancia única (desarrollo local)
Diseño original: MongoDB en Replica Set de 3 nodos (rs0) con writeConcern: majority y
failover automático, tal como se especifica en la sección 4.2 del informe.
Implementación: Instancia única MongoDB 8.0 en Docker para el entorno de desarrollo local.
En producción, la variable de entorno MONGODB_URI se configura con la connection string del
Replica Set (mongodb://mongo1:27017,mongo2:27017,mongo3:27017/findra?replicaSet=rs0)
sin ningún cambio en el código de la aplicación, dado que el driver usa MongoOperations
que es agnóstico al modo de despliegue.
Justificación: Levantar un Replica Set de 3 nodos localmente introduce complejidad
operativa (inicialización del conjunto, resolución de hostnames entre contenedores, gestión
de elecciones) que no aporta valor al prototipo académico. La arquitectura de alta
disponibilidad está diseñada, documentada y lista para activarse con un cambio de
configuración de infraestructura.

Ingeniería de Datos II · TPO · 2026