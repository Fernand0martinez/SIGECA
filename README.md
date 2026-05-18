# Sistema Gestión de Canchas (SIGECA)

## Descripción

`SistemaGestionCanchas` es una aplicación **Spring Boot** (Java 21) que permite gestionar reservas y administración de canchas deportivas. En este repositorio hemos añadido una **infraestructura Docker** completa para facilitar el desarrollo, pruebas y despliegue.

---
## 📦 Contenido del proyecto

- **Dockerfile** – build multietapa que compila el proyecto con Maven y genera una imagen ligera basada en OpenJDK.
- **.dockerignore** – evita copiar artefactos innecesarios al contexto de Docker.
- **docker‑compose.yml** – orquesta tres contenedores:
  1. `app` – la aplicación Spring Boot.
  2. `db` – MySQL 8.0 (base de datos `db_sigeca`).
  3. `phpmyadmin` – interfaz web para administrar la base de datos.
- Código fuente bajo `src/` (clásico proyecto Maven).

---
## ⚙️ Requisitos previos

- **Docker Desktop** instalado y en ejecución (Windows 10/11 + WSL 2 recomendado).
- **Git** para clonar el repositorio.
- Opcional: `make` o cualquier terminal que pueda ejecutar los comandos `docker`.

---
## 🚀 Cómo levantar el proyecto

### 1️⃣ Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/sistema-gestion-canchas.git
cd sistema-gestion-canchas/SistemaGestionCanchas
```

### 2️⃣ Construir y ejecutar con Docker Compose
```bash
# Desde la carpeta que contiene docker‑compose.yml
docker compose up --build -d
```
- **`-d`** ejecuta los contenedores en segundo plano.
- La primera vez Docker descargará la imagen `phpmyadmin/phpmyadmin` y compilará la aplicación Spring Boot.

### 3️⃣ Verificar que todo está corriendo
```bash
# Ver estado de los contenedores
docker compose ps
```
Deberías ver tres contenedores **Up** (app, db, phpmyadmin).

### 4️⃣ Acceder a los servicios
| Servicio | URL | Credenciales por defecto |
|----------|-----|--------------------------|
| Aplicación Spring Boot | `http://localhost:8080` | — |
| Endpoint de salud (Spring Actuator) | `http://localhost:8080/actuator/health` | — |
| phpMyAdmin | `http://localhost:8081` | **Usuario:** `root` <br> **Password:** `rootpass` |
| MySQL (para conectar desde otro cliente) | `localhost:3306` | **User:** `appuser` <br> **Password:** `apppass` |

### 5️⃣ Detener y limpiar el entorno
```bash
docker compose down -v   # -v elimina también el volumen que almacena los datos de MySQL
```
Esto elimina contenedores, red y volumen.

---
## 🛠️ Variables de entorno (configurables)
Los valores pueden sobrescribirse creando un archivo `.env` en la raíz del proyecto o exportándolos antes de ejecutar `docker compose`.
```dotenv
# .env (ejemplo)
MYSQL_ROOT_PASSWORD=rootpass
MYSQL_DATABASE=db_sigeca
MYSQL_USER=appuser
MYSQL_PASSWORD=apppass
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/db_sigeca
SPRING_DATASOURCE_USERNAME=appuser
SPRING_DATASOURCE_PASSWORD=apppass
```
- **`SPRING_DATASOURCE_URL`**: la URL JDBC que usa Spring Boot para conectar con MySQL. En el `docker‑compose.yml` está predefinida como `jdbc:mysql://db:3306/db_sigeca`.
- **`SPRING_JPA_HIBERNATE_DDL_AUTO`** está establecida en `update` para que Hibernate cree/actualice automáticamente el esquema.

---
## 📂 Detalle de los archivos Docker
### Dockerfile (multietapa)
```dockerfile
# ---------- Build Stage ----------
FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
```
- Primera etapa **builder** compila el proyecto con Maven.
- Segunda etapa utiliza una imagen Alpine ligera con solo la JRE.
- La aplicación escucha en el puerto **8080** (expuesto por Docker Compose).

### .dockerignore
```text
target/
.git/
.gitignore
.idea/
**/src/test/**
**/src/main/resources/static/**
```
Evita copiar artefactos de compilación, historial Git y directorios de pruebas al contexto.

---
## 🧪 Pruebas rápidas
```bash
# Ver la respuesta del health endpoint
curl -s http://localhost:8080/actuator/health | jq
```
Debería devolver algo similar a:
```json
{"status":"UP"}
```

---
## 📚 Buenas prácticas y notas adicionales
- **Persistencia**: el volumen `mysql_data` mantiene los datos entre reinicios. No lo elimines si deseas conservar la información.
- **Actualizaciones**: para aplicar cambios en el código, vuelve a correr `docker compose up --build -d`.
- **Logs**: usa `docker logs -f <nombre_contenedor>` para inspeccionar la salida en tiempo real.
- **Seguridad**: en producción reemplaza las contraseñas por variables secretas y desactiva `SPRING_JPA_HIBERNATE_DDL_AUTO=update` (usar migrations).

---
## 📌 Atajos rápidos
```bash
# Levantar (construir) →   docker compose up --build -d
# Parar y limpiar       →   docker compose down -v
# Ver logs (app)        →   docker logs -f sistema_gestion_canchas
# Acceder a MySQL desde host →  mysql -h 127.0.0.1 -P 3306 -u appuser -pappuser
```

---
### 🎉 ¡Listo!
Con estos pasos tienes una entorno Docker completo para **desarrollo** y **pruebas** de `SistemaGestionCanchas`. Modifica las variables de entorno o el `docker-compose.yml` según tus necesidades y despliega en cualquier servidor que tenga Docker.
