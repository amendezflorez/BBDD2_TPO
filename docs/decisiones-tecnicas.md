# Decisiones Tecnicas FINDRA

## Objetivo

El MVP prioriza una implementacion funcional y demostrable para la instancia "Parcial 2 - Implementacion y Validacion". La meta es probar el flujo completo de datos con MongoDB, API Java y una interfaz React alineada a los wireframes.

## Persistencia

MongoDB local es la opcion principal porque ya esta instalado en la maquina de desarrollo y puede inspeccionarse con Compass. La variable `MONGODB_URI` queda como override opcional; si no existe, Spring usa `mongodb://localhost:27017/findra`.

## Ajustes Sobre el Wireframe

- El mapa es una visualizacion operativa simulada con coordenadas reales almacenadas en MongoDB. No se integra un proveedor cartografico externo para evitar dependencias de API keys.
- Los adjuntos se modelan como metadata (`tipo`, `url`, `subidoPor`, `timestamp`) sin upload binario.
- La autenticacion se reemplaza por un operador simulado para auditoria (`OP_FINDRA`) porque el foco del parcial es implementacion, pipeline y validacion.
- La emision de Alerta Sofia registra canales, estado e historial, pero no envia SMS, TV ni redes reales.

## Buenas Practicas Equivalentes a PEP 8

PEP 8 aplica a Python, por lo que se adopta una equivalencia por stack:

- Java: clases y servicios pequenos, nombres descriptivos, capas separadas, DTOs para entrada/salida y estilo cercano a Google Java Style.
- React: componentes acotados, hooks claros, ESLint/Prettier, nombres expresivos y CSS modularizado por responsabilidad.
- MongoDB: indices explicitos para consultas frecuentes y campos geoespaciales.

## Riesgos y Trade-offs

- Docker queda como alternativa reproducible, pero no como requisito de desarrollo.
- El seed automatico facilita demostracion, aunque en produccion se reemplazaria por migraciones controladas.
- El modelo embebe informacion operativa dentro del caso para optimizar la consulta principal: "dame todo lo que se del caso".
