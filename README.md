# SIGECA

Sistema web de gestion deportiva para administrar canchas, reservas, pagos y torneos. La aplicacion centraliza la gestion operativa de un complejo deportivo e incluye modulos de usuarios, equipos, arbitros, productos, patrocinios y facturacion en PDF.

## Stack Tecnologico

- Java 21
- Spring Boot 3
- Spring MVC
- Spring Data JPA / Hibernate
- Thymeleaf
- MySQL 8
- Maven
- Docker y Docker Compose
- JavaMailSender
- OpenPDF / Flying Saucer

## Funcionalidades Principales

- Gestion de usuarios con validacion y verificacion por correo.
- Administracion de canchas con disponibilidad, ubicacion, superficie y precio por hora.
- Registro y actualizacion de reservas.
- Procesamiento de pagos y consulta de historial.
- Generacion y envio de facturas PDF por correo.
- Administracion de torneos, equipos, jugadores y arbitros.
- Gestion de productos y patrocinios.

## Arquitectura

El proyecto sigue una arquitectura MVC por capas dentro de un monolito Spring Boot:

- `controller`: flujo HTTP y vistas.
- `logic` / `service`: reglas de negocio y validaciones.
- `repository` / `JPA`: acceso a datos.
- `domain`: entidades del sistema.
- `templates` y `static`: frontend renderizado del lado del servidor con Thymeleaf, CSS y JavaScript.

## Requisitos

Para probar el proyecto necesitas:

- Docker Desktop
- Git
- Un navegador web

Opcional para desarrollo local fuera de Docker:

- Java 21
- Maven 3.9+

## Como Probar El Proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/Fernand0martinez/SIGECA.git
cd SIGECA
```

### 2. Crear el archivo `.env`

Usa `.env.example` como base:

```bash
cp .env.example .env
```

En Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

### 3. Configurar variables de entorno

```

### 4. Levantar el entorno con Docker

```bash
docker compose up --build -d
```

### 5. Verificar los contenedores

```bash
docker compose ps
```

Deberias ver activos:

- `app`
- `db`
- `phpmyadmin`

### 6. Acceder a la aplicacion

Servicios disponibles:

- Aplicacion: `http://localhost:8080`
- Health check: `http://localhost:8080/actuator/health`
- phpMyAdmin: `http://localhost:8081`

## Configuracion Del Correo

El sistema puede enviar correos de verificacion y comprobantes PDF. Para que esa funcionalidad opere con Gmail debes usar una contrasena de aplicacion, no tu contrasena normal.

### Pasos para Gmail

1. Activa la verificacion en dos pasos en tu cuenta de Google.
2. Entra a la seccion de seguridad de tu cuenta.
3. Busca la opcion `Contrasenas de aplicaciones`.
4. Genera una nueva contrasena para correo.
5. Usa esa contrasena en `SPRING_MAIL_PASSWORD`.

## Credenciales Y Acceso

Las credenciales dependen de los valores definidos en tu `.env`.

Ejemplo comun:

- MySQL host: `localhost:3306`
- Base de datos: `db_sigeca`
- Usuario aplicacion: `appuser`
- phpMyAdmin: `http://localhost:8081`

## Comandos Utiles

Levantar el entorno:

```bash
docker compose up --build -d
```

Ver logs:

```bash
docker compose logs -f app
```

Detener contenedores:

```bash
docker compose down
```

Detener y eliminar volumenes:

```bash
docker compose down -v
```

## Levantar proyecto 

.\mvnw.cmd spring-boot:run

Cuando la aplicacion este corriendo, puedes validar el estado con:

```bash
curl http://localhost:8080/actuator/health
```

Respuesta esperada:

```json
{"status":"UP"}
```

## Pruebas

El repositorio incluye pruebas con JUnit, Mockito y MockMvc.

Si quieres ejecutarlas localmente:

```bash
./mvnw test
```

En Windows PowerShell:

```powershell
.\mvnw.cmd test
```


## Estado Del Proyecto

Proyecto academico/full stack orientado a portafolio, enfocado en modelado de dominio, backend MVC, persistencia relacional, integracion con correo y despliegue local con Docker.
