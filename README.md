# FermentCraft API

API REST construida con Quarkus 3 y Java 21 para administrar lotes de fermentos y crear cajas de suscripción. Incluye persistencia PostgreSQL con Panache, validación Bean Validation, tarifación de envío con fallback, control transaccional de inventario y eventos Kafka.

## Requisitos

- JDK 21
- Docker Desktop iniciado (Docker Engine activo)
- Docker Compose v2 (`docker compose`)

## Credenciales compartidas

La configuración local de `quarkus:dev` y el servicio `postgres` de Docker Compose usa los mismos valores de desarrollo:

| Parámetro | Valor |
|---|---|
| Base de datos | `fermentcraft_db` |
| Usuario | `quarkus` |
| Contraseña | `quarkus_password` |


## Opción A: levantar solo PostgreSQL y Kafka, ejecutar Quarkus en modo dev

Desde la raíz del proyecto, arranca Docker Desktop y ejecuta:

```powershell
docker compose up -d postgres kafka
```

Comprueba el estado:

```powershell
docker compose ps
```

Docker Desktop mostrará los contenedores `fermentcraft-postgres` y `fermentcraft-kafka` como activos. PostgreSQL queda publicado en `localhost:5432`; Kafka está disponible desde la máquina local en `localhost:19092` y desde la red de Compose en `kafka:9092`.

Después, en otra terminal de PowerShell, ejecuta la aplicación en modo desarrollo:

```powershell
.\mvnw.cmd quarkus:dev
```

La aplicación local escucha en `http://localhost:8080`. Para detener Quarkus, pulsa `Ctrl+C` en su terminal.

Para detener PostgreSQL y Kafka sin borrar los datos persistidos:

```powershell
docker compose stop
```

Para detener y eliminar los contenedores y la red, conservando el volumen de PostgreSQL:

```powershell
docker compose down
```

### Reinicio limpio y reconstrucción completa

Si necesitas borrar también el volumen de PostgreSQL y comenzar con una base de datos limpia, ejecuta desde la raíz del proyecto. **`docker compose down -v` elimina permanentemente los datos almacenados en el volumen.**

```powershell
docker compose down -v
.\mvnw.cmd clean package -DskipTests
docker compose up --build -d
```

El primer comando detiene y elimina los contenedores, la red y el volumen; el segundo compila y empaqueta la aplicación sin ejecutar pruebas; el tercero reconstruye la imagen de la app y levanta los servicios en segundo plano. Comprueba su estado con:

```powershell
docker compose ps
```

## Opción B: levantar toda la aplicación en Docker Desktop

Primero empaqueta el proyecto, porque el Dockerfile copia el artefacto de `target/quarkus-app`:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Luego construye y levanta los servicios:

```powershell
docker compose up -d --build
```

En Docker Desktop aparecerá el grupo del proyecto con los contenedores `fermentcraft-postgres`, `fermentcraft-kafka`, `fermentcraft-kafka-ui` y `fermentcraft-app`. Cuando estén activos, abre `http://localhost:8080`. Kafka UI queda disponible en `http://localhost:8085`.

Para ver estado y logs desde PowerShell:

```powershell
docker compose ps
docker compose logs -f app
```

Para apagar y retirar contenedores/red, manteniendo la base de datos:

```powershell
docker compose down
```

Para apagar y además borrar los datos PostgreSQL guardados en el volumen (acción destructiva):

```powershell
docker compose down -v
```

> Hibernate está configurado con `drop-and-create`; al iniciar la aplicación recrea el esquema y vuelve a cargar `import.sql`. Por eso, aunque el volumen exista, los datos de tablas se reinicializan al arrancar la app.

### Visualizar mensajes de Kafka con Kafka UI

El servicio `kafka-ui` del Compose se conecta al broker `kafka` usando `kafka:9092` dentro de la red Docker y publica la interfaz en el puerto `8085`.

Para levantar Kafka UI desde la raíz del proyecto:

```powershell
docker compose up -d kafka-ui
```

Compose inicia también el servicio `kafka`, que es dependencia de Kafka UI. Si vas a ejecutar la aplicación localmente con `quarkus:dev`, inicia además PostgreSQL:

```powershell
docker compose up -d postgres
```

Con la API ya iniciada, abre [http://localhost:8085](http://localhost:8085). En la interfaz, selecciona **Topics** → **box-dispatched** → **Messages**. Si el tópico aún no aparece, primero crea una caja con `POST /api/v1/subscription-boxes` (también puedes ejecutar **Crear orden de despacho (exitoso)** desde la colección de Postman) y actualiza la vista de Topics/Messages.

Cada creación exitosa publica un mensaje JSON con `orderNumber`, `customerEmail`, `totalAmount`, `status` y `timestamp`. El número de orden y la marca de tiempo se generan por solicitud; por eso los valores exactos dependen de la orden creada. Un ejemplo de la forma del mensaje es:

```json
{
  "orderNumber": "ORD-9C6A960E",
  "customerEmail": "cliente.fermentos@example.com",
  "totalAmount": 173.50,
  "status": "CONFIRMED",
  "timestamp": "2026-10-01T16:30:00"
}
```

El `orderNumber` y el `timestamp` son ilustrativos. El total de `173.50` corresponde al ejemplo con dos unidades del lote 1, una del lote 2 y la tarifa fallback de `45.00`; el valor real depende de la cotización recibida. Desde Docker Desktop puedes abrir los logs del contenedor `fermentcraft-kafka-ui` si necesitas diagnosticar la conexión. Para apagarlo y los servicios de los que depende, ejecuta `docker compose down`.

## Probar la API con Postman

Importa [`postman/FermentCraft_API.postman_collection.json`](postman/FermentCraft_API.postman_collection.json) en Postman (**Import > File**). Inicia primero la API en `http://localhost:8080`; luego ejecuta las solicitudes de la colección. Las pruebas CRUD del lote se deben correr en orden: crear, actualizar y desactivar. La solicitud de creación de caja guarda automáticamente su ID y número de orden para las consultas y el cambio de estado posteriores. La colección incluye también ejemplos para validación HTTP 400 y stock insuficiente HTTP 422.

## Ejecutar pruebas

```powershell
.\mvnw.cmd test
```

## Endpoints de la aplicación

- Swagger UI: <http://localhost:8080/q/swagger-ui/#/>
- OpenAPI JSON: <http://localhost:8080/q/openapi>
- Dev UI (solo con `quarkus:dev`): <http://localhost:8080/q/dev-ui/configuration-form-editor>
- Health global: <http://localhost:8080/q/health>
- Liveness (`AppLivenessCheck`): <http://localhost:8080/q/health/live>
- Readiness (`DatabaseReadinessCheck`): <http://localhost:8080/q/health/ready>

### Recursos REST

- `GET /api/v1/product-batches?page=0&size=10&category=KOMBUCHA` — consulta paginada de lotes activos.
- `GET /api/v1/product-batches/{id}` — detalle del lote.
- `POST /api/v1/product-batches` — crea lote.
- `PUT /api/v1/product-batches/{id}` — actualiza lote.
- `DELETE /api/v1/product-batches/{id}` — desactiva el lote (borrado lógico).
- `POST /api/v1/subscription-boxes` — crea caja, descuenta inventario y emite evento Kafka.
- `GET /api/v1/subscription-boxes?page=0&size=10&status=CONFIRMED` — consulta paginada de cajas.
- `GET /api/v1/subscription-boxes/{id}` — detalle de caja.
- `GET /api/v1/subscription-boxes/by-order/{orderNumber}` — consulta por número de orden.
- `PATCH /api/v1/subscription-boxes/{id}/status?newStatus=SHIPPED` — cambia estado.