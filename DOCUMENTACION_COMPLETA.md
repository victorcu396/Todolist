# Documentación Completa - API REST de Gestión de Tareas

## 📋 Tabla de Contenidos

1. [Introducción](#introducción)
2. [Características Principales](#características-principales)
3. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
4. [Requisitos Previos](#requisitos-previos)
5. [Instalación y Configuración](#instalación-y-configuración)
6. [Estructura del Proyecto](#estructura-del-proyecto)
7. [Componentes Principales](#componentes-principales)
8. [Modelos de Base de Datos](#modelos-de-base-de-datos)
9. [Especificación de Endpoints](#especificación-de-endpoints)
10. [Guía de Uso](#guía-de-uso)
11. [Casos de Uso](#casos-de-uso)
12. [Seguridad](#seguridad)
13. [Gestión de Errores](#gestión-de-errores)
14. [Ejemplos de Uso Práctico](#ejemplos-de-uso-práctico)

---

## Introducción

**Todo REST API** es una aplicación backend construida con Spring Boot 3.4.1 que proporciona una interfaz REST completa para la gestión de tareas. Esta API permite que los usuarios registren cuentas, autentiquen sus credenciales y realicen operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre sus tareas personales.

La aplicación está diseñada siguiendo principios de arquitectura limpia y utiliza las mejores prácticas de desarrollo con Spring Boot. Incluye documentación interactiva mediante Swagger/OpenAPI que facilita el testing y la integración con clientes.

**Versión:** 1.0  
**Licencia:** CC BY  
**Contacto:** admin@taskapi.example.com

---

## Características Principales

### 1. Autenticación y Seguridad
- Autenticación basada en credenciales (username/password)
- Autorización granular mediante roles (USER, ADMIN)
- Validación de propiedad de recursos (solo el propietario puede editar/eliminar)
- Contraseñas cifradas mediante codificador de seguridad de Spring

### 2. Gestión de Tareas
- Crear nuevas tareas con título, descripción y fecha de vencimiento
- Listar todas las tareas del usuario autenticado
- Obtener detalles específicos de una tarea
- Editar tareas existentes
- Eliminar tareas completadas

### 3. Gestión de Usuarios
- Registro de nuevos usuarios
- Tipos de usuarios: Usuario regular y Administrador
- Cada usuario tiene su propio conjunto de tareas

### 4. Documentación Interactiva
- Swagger UI integrado para explorar y probar todos los endpoints
- Documentación automática de la API
- Ejemplos de solicitudes y respuestas incluidos

### 5. Base de Datos Embebida
- Base de datos H2 para desarrollo y testing
- Datos de ejemplo precargados
- Generación automática de schemas

---

## Arquitectura del Proyecto

La aplicación sigue una arquitectura en capas que separa las responsabilidades:

```
┌─────────────────────────────────────────┐
│        Controladores (REST API)          │
│  (TaskController, UserController)        │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│      Servicios (Lógica de Negocio)       │
│    (TaskService, UserService)            │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│   Repositorios (Acceso a Datos)          │
│ (TaskRepository, UserRepository)         │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│      Base de Datos (H2 Database)         │
│     (Tablas: task, user_entity)          │
└─────────────────────────────────────────┘
```

### Capas de la Aplicación

- **Capa de Presentación (Controllers):** Maneja las solicitudes HTTP y proporciona respuestas
- **Capa de Negocio (Services):** Contiene la lógica de la aplicación
- **Capa de Persistencia (Repositories):** Gestiona la interacción con la base de datos
- **Modelos de Datos (Models):** Representan las entidades del dominio

---

## Requisitos Previos

### Software Requerido
- Java 17 o superior (JDK)
- Maven 3.8.1 o superior
- Git (opcional, para control de versiones)

### Conocimientos
- Conceptos básicos de APIs REST
- Comprensión de HTTP y JSON
- Familiaridad con herramientas como Postman o curl

---

## Instalación y Configuración

### Paso 1: Clonar o Descargar el Proyecto

```bash
cd "d:\2º DAW\Desarrollo Web Entorno Servidor\Proyectos_Empresa\todo-rest\todo-rest"
```

### Paso 2: Construir el Proyecto

```bash
mvn clean compile
# o usando el wrapper
.\mvnw.cmd clean compile
```

### Paso 3: Ejecutar la Aplicación

```bash
mvn spring-boot:run
# o
.\mvnw.cmd spring-boot:run
```

La aplicación iniciará en `http://localhost:8080`

### Paso 4: Acceder a la Documentación

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

### Credenciales de Prueba

- **Usuario:** testuser
- **Contraseña:** password123

---

## Estructura del Proyecto

```
todo-rest/
├── src/
│   ├── main/
│   │   ├── java/com/openwebinars/todo/
│   │   │   ├── TodoRestApplication.java          # Clase principal
│   │   │   ├── controller/
│   │   │   │   └── TaskController.java           # Endpoints de tareas
│   │   │   ├── service/
│   │   │   │   └── TaskService.java              # Lógica de tareas
│   │   │   ├── repos/
│   │   │   │   └── TaskRepository.java           # Acceso a datos
│   │   │   ├── model/
│   │   │   │   └── Task.java                     # Entidad de tareas
│   │   │   ├── dto/
│   │   │   │   ├── TaskUpdateRequestDto.java     # DTO para actualizar
│   │   │   │   └── TaskResponseDto.java          # DTO de respuesta
│   │   │   ├── security/
│   │   │   │   ├── SecurityConfig.java           # Configuración de seguridad
│   │   │   │   ├── CustomUserDetailsService.java # Servicio de usuarios
│   │   │   │   └── PasswordEncoderConfig.java    # Codificador de contraseñas
│   │   │   ├── users/
│   │   │   │   ├── User.java                     # Entidad de usuario
│   │   │   │   ├── UserController.java           # Endpoints de usuarios
│   │   │   │   ├── UserService.java              # Lógica de usuarios
│   │   │   │   ├── UserRepository.java           # Acceso a datos de usuarios
│   │   │   │   ├── CreateUserRequestDto.java     # DTO de registro
│   │   │   │   ├── UserRegistrationResponseDto.java # DTO de respuesta
│   │   │   │   └── OwnerCheck.java               # Validador de propiedad
│   │   │   ├── config/
│   │   │   │   ├── SpringdocConfig.java          # Configuración de Swagger
│   │   │   │   └── WebConfig.java                # Configuración web
│   │   │   └── error/
│   │   │       ├── GlobalErrorController.java    # Manejo global de errores
│   │   │       ├── TaskNotFoundException.java     # Excepción personalizada
│   │   │       ├── CustomAuthenticationEntryPoint.java
│   │   │       └── CustomAccessDeniedHandler.java
│   │   └── resources/
│   │       ├── application.properties             # Configuración
│   │       └── import.sql                         # Datos iniciales
│   └── test/
│       └── java/.../TodoRestApplicationTests.java
├── pom.xml                                        # Configuración de Maven
└── README.md                                      # Este archivo
```

---

## Componentes Principales

### 1. Clase Principal: TodoRestApplication

```java
@SpringBootApplication
@OpenAPIDefinition(...)
public class TodoRestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoRestApplication.class, args);
    }
}
```

Punto de entrada de la aplicación que inicializa el contexto de Spring Boot.

### 2. TaskController

Controlador encargado de manejar todas las operaciones relacionadas con tareas.

**Responsabilidades:**
- Validar solicitudes HTTP
- Delegar lógica de negocio al servicio
- Convertir entidades a DTOs
- Retornar respuestas con códigos HTTP apropiados

### 3. TaskService

Servicio que implementa la lógica de negocio para tareas.

**Métodos principales:**
- `findAll()` - Obtiene todas las tareas
- `findByAuthor(User)` - Obtiene tareas de un usuario
- `findById(Long)` - Búsqueda de tarea por ID
- `save(TaskUpdateRequestDto, User)` - Crear nueva tarea
- `edit(TaskUpdateRequestDto, Long)` - Actualizar tarea
- `delete(Long)` - Eliminar tarea

### 4. UserController

Controlador para operaciones de usuario.

**Endpoints:**
- `POST /auth/register` - Registrar nuevo usuario

### 5. UserService

Servicio para gestión de usuarios.

**Métodos:**
- `register(CreateUserRequestDto)` - Registrar nuevo usuario

### 6. Entidades JPA

#### Task (Tarea)

```java
@Entity
public class Task {
    @Id
    private Long id;
    private LocalDateTime createdAt;    // Fecha de creación
    private String title;                // Título
    @Lob
    private String description;          // Descripción detallada
    private LocalDateTime deadline;      // Fecha de vencimiento
    @ManyToOne
    private User author;                 // Usuario propietario
}
```

#### User (Usuario)

```java
@Entity
@Table(name = "user_entity")
public class User implements UserDetails {
    @Id
    private Long id;
    private String username;            // Nombre de usuario
    private String email;               // Correo electrónico
    private String password;            // Contraseña cifrada
    private boolean isAdmin;            // Flag de administrador
}
```

### 7. DTOs (Data Transfer Objects)

**TaskUpdateRequestDto:** Utilizado para créar y actualizar tareas
```java
public record TaskUpdateRequestDto(
    String title,
    String description,
    LocalDateTime deadline
)
```

**TaskResponseDto:** Respuesta cuando se solicita información de tareas
```java
public record TaskResponseDto(
    Long id,
    String title,
    String description,
    LocalDateTime createdAt,
    LocalDateTime deadline,
    UserRegistrationResponseDto author
)
```

**CreateUserRequestDto:** Registro de nuevos usuarios
```java
public record CreateUserRequestDto(
    String username,
    String email,
    String password
)
```

**UserRegistrationResponseDto:** Respuesta de usuarios
```java
public record UserRegistrationResponseDto(
    Long id,
    String username,
    String email
)
```

---

## Modelos de Base de Datos

### Tabla: task

| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | BIGINT | Identificador único (PRIMARY KEY) |
| created_at | TIMESTAMP | Fecha y hora de creación (valor por defecto: ahora) |
| title | VARCHAR | Título de la tarea (máx. 255 caracteres) |
| description | CLOB | Descripción detallada |
| deadline | TIMESTAMP | Fecha de vencimiento |
| author_id | BIGINT | Usuario propietario (FOREIGN KEY) |

### Tabla: user_entity

| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | BIGINT | Identificador único (PRIMARY KEY) |
| username | VARCHAR | Nombre de usuario único |
| email | VARCHAR | Correo electrónico |
| password | VARCHAR | Contraseña cifrada |
| is_admin | BOOLEAN | Indica si es administrador (por defecto: false) |

### Relaciones

- **Relación 1:N entre User y Task**
  - Un usuario puede tener múltiples tareas
  - Cada tarea pertenece a un único usuario
  - Esta relación asegura que cada user solo pueda ver/editar sus propias tareas

---

## Especificación de Endpoints

### 1. Endpoints de Tareas

#### 1.1 Obtener Todas las Tareas del Usuario

```
GET /task/
Autenticación: Requerida (Basic Auth)
Respuesta: 200 OK
```

**Descripción:** Retorna la lista de todas las tareas asociadas al usuario autenticado.

**Respuesta exitosa (200):**
```json
[
  {
    "id": 1,
    "title": "Comprar alimentos",
    "description": "Hacer una lista de compras para el supermercado.",
    "createdAt": "2025-01-13T16:12:11.295172",
    "deadline": "2025-01-20T16:12:11.295172",
    "author": {
      "id": 1,
      "username": "testuser",
      "email": "testuser@example.com"
    }
  }
]
```

#### 1.2 Obtener una Tarea Específica

```
GET /task/{id}
Autenticación: Requerida (Basic Auth)
Parámetro: id (Long) - Identificador de la tarea
Respuesta: 200 OK, 404 Not Found, 403 Forbidden
```

**Descripción:** Retorna los detalles de una tarea específica. Solo el propietario puede acceder.

**Respuesta exitosa (200):**
```json
{
  "id": 1,
  "title": "Comprar alimentos",
  "description": "Hacer una lista de compras para el supermercado.",
  "createdAt": "2025-01-13T16:12:11.295172",
  "deadline": "2025-01-20T16:12:11.295172",
  "author": {
    "id": 1,
    "username": "testuser",
    "email": "testuser@example.com"
  }
}
```

**Errores posibles:**
- **404 Not Found:** La tarea no existe
- **403 Forbidden:** No tienes permisos para acceder a esta tarea

#### 1.3 Crear una Nueva Tarea

```
POST /task/
Autenticación: Requerida (Basic Auth)
Content-Type: application/json
Respuesta: 201 Created
```

**Descripción:** Crea una nueva tarea asociada al usuario autenticado.

**Body de la solicitud:**
```json
{
  "title": "Aprender Spring Boot",
  "description": "Hacer todos los cursos de Spring Boot en taskapi.example.com",
  "deadline": "2025-12-31T23:59:59"
}
```

**Respuesta exitosa (201):**
```json
{
  "id": 52,
  "title": "Aprender Spring Boot",
  "description": "Hacer todos los cursos de Spring Boot en taskapi.example.com",
  "createdAt": "2025-01-13T17:30:00.000000",
  "deadline": "2025-12-31T23:59:59",
  "author": {
    "id": 1,
    "username": "testuser",
    "email": "testuser@example.com"
  }
}
```

#### 1.4 Actualizar una Tarea

```
PUT /task/{id}
Autenticación: Requerida (Basic Auth)
Parámetro: id (Long) - Identificador de la tarea
Content-Type: application/json
Respuesta: 200 OK, 404 Not Found, 403 Forbidden
```

**Descripción:** Actualiza los datos de una tarea existente. Solo el propietario puede editar.

**Body de la solicitud:**
```json
{
  "title": "Aprender Spring Boot Avanzado",
  "description": "Cursos avanzados de Spring Boot",
  "deadline": "2025-06-30T23:59:59"
}
```

**Respuesta exitosa (200):**
```json
{
  "id": 1,
  "title": "Aprender Spring Boot Avanzado",
  "description": "Cursos avanzados de Spring Boot",
  "createdAt": "2025-01-13T16:12:11.295172",
  "deadline": "2025-06-30T23:59:59",
  "author": {
    "id": 1,
    "username": "testuser",
    "email": "testuser@example.com"
  }
}
```

#### 1.5 Eliminar una Tarea

```
DELETE /task/{id}
Autenticación: Requerida (Basic Auth)
Parámetro: id (Long) - Identificador de la tarea
Respuesta: 204 No Content, 404 Not Found, 403 Forbidden
```

**Descripción:** Elimina una tarea. Solo el propietario puede deletear.

**Respuesta exitosa (204):** Sin contenido (tarea eliminada correctamente)

**Errores posibles:**
- **404 Not Found:** La tarea no existe
- **403 Forbidden:** No tienes permisos para eliminar esta tarea

### 2. Endpoints de Usuarios

#### 2.1 Registrar Nuevo Usuario

```
POST /auth/register
Autenticación: No requerida
Content-Type: application/json
Respuesta: 201 Created
```

**Descripción:** Crea una nueva cuenta de usuario.

**Body de la solicitud:**
```json
{
  "username": "juan",
  "email": "john@example.com",
  "password": "contraseña123"
}
```

**Respuesta exitosa (201):**
```json
{
  "id": 2,
  "username": "juan",
  "email": "john@example.com"
}
```

**Restricciones:**
- El username debe ser único
- La contraseña se cifra automáticamente
- Los nuevos usuarios tienen rol de USER

---

## Guía de Uso

### Acceso a la API

La API está protegida mediante autenticación HTTP Basic. Todas las solicitudes (excepto registro) requieren credenciales.

### Usando Swagger UI

1. Abre `http://localhost:8080/swagger-ui/index.html` en tu navegador
2. Verás todos los endpoints disponibles
3. Haz clic en el botón de candado de cualquier endpoint
4. Introduce credenciales (usuario: testuser, contraseña: password123)
5. Prueba los endpoints directamente desde la interfaz

### Usando curl

**Autenticación:**
```bash
# Obtener todas las tareas
curl -u testuser:password123 http://localhost:8080/task/

# Obtener una tarea específica
curl -u testuser:password123 http://localhost:8080/task/1

# Crear una tarea
curl -u testuser:password123 -X POST http://localhost:8080/task/ \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nueva tarea",
    "description": "Descripción de la tarea",
    "deadline": "2025-12-31T23:59:59"
  }'

# Actualizar una tarea
curl -u testuser:password123 -X PUT http://localhost:8080/task/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tarea actualizada",
    "description": "Descripción modificada",
    "deadline": "2025-12-31T23:59:59"
  }'

# Eliminar una tarea
curl -u testuser:password123 -X DELETE http://localhost:8080/task/1

# Registrar nuevo usuario
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo_usuario",
    "email": "user@example.com",
    "password": "contraseña_segura"
  }'
```

### Usando Postman

1. Abre Postman
2. Crea una nueva solicitud
3. Selecciona el método (GET, POST, PUT, DELETE)
4. Ingresa la URL (ej: http://localhost:8080/task/)
5. Ve a la pestaña "Authorization"
6. Selecciona "Basic Auth"
7. Ingresa usuario y contraseña
8. Haz clic en Send

---

## Casos de Uso

### Caso de Uso 1: Un Usuario Nuevo se Registra

**Actor:** Nuevo usuario  
**Precondiciones:** La API está funcionando

**Flujo:**
1. El usuario envía una solicitud POST a `/auth/register`
2. Incluye su username, email y contraseña
3. El sistema valida que el username sea único
4. La contraseña se cifra automáticamente
5. Se crea el usuario en la base de datos con rol de USER
6. Se retorna la información del usuario creado (sin la contraseña)

**Resultado:** El nuevo usuario puede iniciar sesión

---

### Caso de Uso 2: Ver Todas mis Tareas

**Actor:** Usuario autenticado  
**Precondiciones:** El usuario ha iniciado sesión

**Flujo:**
1. El usuario envía una solicitud GET a `/task/`
2. Se valida la autenticación
3. El sistema obtiene todas las tareas del usuario
4. Se convierten a DTOs
5. Se retorna la lista de tareas con código 200

**Resultado:** El usuario visualiza su lista de tareas

---

### Caso de Uso 3: Crear una Nueva Tarea

**Actor:** Usuario autenticado  
**Precondiciones:** El usuario ha iniciado sesión

**Flujo:**
1. El usuario envía una solicitud POST a `/task/`
2. Incluye título, descripción y fecha de vencimiento
3. Se valida la autenticación
4. Se crear la tarea en la base de datos con el usuario actual como autor
5. Se asigna automáticamente la fecha de creación al momento actual
6. Se retorna la tarea creada con código 201

**Resultado:** Se crea la nueva tarea y el usuario puede verla

---

### Caso de Uso 4: Editar una Tarea Existente

**Actor:** Usuario autenticado  
**Precondiciones:** El usuario ha iniciado sesión y es propietario de la tarea

**Flujo:**
1. El usuario envía una solicitud PUT a `/task/{id}`
2. Incluye los nuevos datos de la tarea
3. Se valida la autenticación
4. Se verifica que el usuario es propietario de la tarea
5. Se actualizan los datos en la base de datos
6. Se retorna la tarea actualizada con código 200

**Resultado:** La tarea se actualiza correctamente

---

### Caso de Uso 5: Eliminar una Tarea Completada

**Actor:** Usuario autenticado  
**Precondiciones:** El usuario ha iniciado sesión y es propietario de la tarea

**Flujo:**
1. El usuario envía una solicitud DELETE a `/task/{id}`
2. Se valida la autenticación
3. Se verifica que el usuario es propietario
4. Se elimina la tarea de la base de datos
5. Se retorna código 204 (sin contenido)

**Resultado:** La tarea se elimina correctamente

---

### Caso de Uso 6: Intentar Acceder a una Tarea de otro Usuario

**Actor:** Usuario autenticado  
**Precondiciones:** El usuario intenta acceder a una tarea que no le pertenece

**Flujo:**
1. El usuario envía una solicitud GET a `/task/{id}` de otra tarea
2. Se valida la autenticación
3. Se verifica la propiedad de la tarea
4. Se valida que el usuario no es el propietario
5. Se retorna error 403 Forbidden

**Resultado:** El acceso es denegado por razones de seguridad

---

## Seguridad

### Mecanismos de Seguridad Implementados

#### 1. Autenticación HTTP Basic

Todas las solicitudes (excepto registro) requieren autenticación mediante:
- Username
- Contraseña

Las credenciales se envían en el header Authorization en formato Base64.

#### 2. Encriptado de Contraseñas

- Las contraseñas se cifran utilizando el codificador de Spring Security
- Las contraseñas nunca se almacenan en texto plano
- Se usa el algoritmo bcrypt para mayor seguridad

#### 3. Autorización Basada en Roles

Dos roles disponibles:
- **USER:** Acceso a operaciones básicas de tareas
- **ADMIN:** Acceso completo (para futuras expansiones)

#### 4. Validación de Propiedad

- Los usuarios solo pueden ver sus propias tareas
- Los usuarios solo pueden editar/eliminar sus propias tareas
- La validación se realiza en el controlador mediante `@PreAuthorize`

#### 5. Manejo de Excepciones Personalizado

- TaskNotFoundException: Cuando una tarea no existe
- CustomAuthenticationEntryPoint: Manejo de fallos de autenticación
- CustomAccessDeniedHandler: Manejo de fallos de autorización

#### 6. HTTPS (Recomendado en Producción)

Para entornos de producción, se recomienda:
- Usar HTTPS en lugar de HTTP
- Implementar certificados SSL/TLS
- Habilitar CORS apropiadamente

---

## Gestión de Errores

### Códigos de Error HTTP

| Código | Significado | Causa |
|--------|------------|-------|
| 200 | OK | Solicitud exitosa |
| 201 | Created | Recurso creado exitosamente |
| 204 | No Content | Operación exitosa sin contenido |
| 400 | Bad Request | Datos inválidos en la solicitud |
| 401 | Unauthorized | Falta autenticación o credenciales inválidas |
| 403 | Forbidden | Falta autorización para acceder al recurso |
| 404 | Not Found | El recurso no existe |
| 500 | Internal Server Error | Error del servidor |

### Ejemplos de Respuestas de Error

**401 Unauthorized - Credenciales inválidas:**
```json
{
  "status": 401,
  "message": "Credenciales inválidas"
}
```

**403 Forbidden - No eres el propietario:**
```json
{
  "status": 403,
  "message": "No tienes permiso para acceder a este recurso"
}
```

**404 Not Found - Tarea no existe:**
```json
{
  "status": 404,
  "message": "La tarea con ID 999 no existe"
}
```

### Excepciones Personalizadas

**TaskNotFoundException**
- Se lanza cuando una tarea no se encuentra
- Retorna código 404

**CustomAuthenticationEntryPoint**
- Se ejecuta cuando falla la autenticación
- Retorna código 401

**CustomAccessDeniedHandler**
- Se ejecuta cuando un usuario intenta acceder a recursos sin autorización
- Retorna código 403

---

## Ejemplos de Uso Práctico

### Ejemplo 1: Flujo Completo de Nuevo Usuario

```bash
# Paso 1: Registrar nuevo usuario
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "carlos",
    "email": "carlos@example.com",
    "password": "micontraseña123"
  }'

# Respuesta:
# {
#   "id": 3,
#   "username": "carlos",
#   "email": "carlos@example.com"
# }

# Paso 2: Crear una tarea
curl -u carlos:micontraseña123 -X POST http://localhost:8080/task/ \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Proyecto Spring Boot",
    "description": "Completar el proyecto final de Spring Boot",
    "deadline": "2025-06-30T23:59:59"
  }'

# Respuesta:
# {
#   "id": 52,
#   "title": "Proyecto Spring Boot",
#   "description": "Completar el proyecto final de Spring Boot",
#   "createdAt": "2025-01-15T10:30:00.000000",
#   "deadline": "2025-06-30T23:59:59",
#   "author": {
#     "id": 3,
#     "username": "carlos",
#     "email": "carlos@example.com"
#   }
# }

# Paso 3: Ver todas las tareas
curl -u carlos:micontraseña123 http://localhost:8080/task/

# Respuesta:
# [
#   {
#     "id": 52,
#     "title": "Proyecto Spring Boot",
#     ...
#   }
# ]
```

### Ejemplo 2: Actualización de Tarea

```bash
# Obtener la tarea actual
curl -u testuser:password123 http://localhost:8080/task/1

# Actualizar la tarea con nuevo título
curl -u testuser:password123 -X PUT http://localhost:8080/task/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Comprar alimentos - URGENTE",
    "description": "Hacer una lista de compras para el supermercado.",
    "deadline": "2025-01-20T16:12:11.295172"
  }'

# Respuesta confirmando los cambios
```

### Ejemplo 3: Manejo de Error de Acceso No Autorizado

```bash
# Usuario testuser intenta acceder a tarea de otro usuario (suponiendo que existe)
curl -u testuser:password123 http://localhost:8080/task/999

# Respuesta:
# HTTP/1.1 404 Not Found
# {
#   "status": 404,
#   "message": "La tarea con ID 999 no existe"
# }

# O si la tarea existe pero pertenece a otro usuario:
# HTTP/1.1 403 Forbidden
# {
#   "status": 403,
#   "message": "No tienes permiso para acceder a este recurso"
# }
```

### Ejemplo 4: Ciclo Completo de una Tarea

```bash
# 1. Crear tarea
curl -u testuser:password123 -X POST http://localhost:8080/task/ \
  -H "Content-Type: application/json" \
  -d '{"title": "Estudiar", "description": "Java", "deadline": "2025-12-31T23:59:59"}'
# Respuesta: Tarea creada con ID 100

# 2. Verificar tarea creada
curl -u testuser:password123 http://localhost:8080/task/100

# 3. Editar la tarea
curl -u testuser:password123 -X PUT http://localhost:8080/task/100 \
  -H "Content-Type: application/json" \
  -d '{"title": "Estudiar", "description": "Java Avanzado", "deadline": "2025-12-31T23:59:59"}'

# 4. Listar todas las tareas
curl -u testuser:password123 http://localhost:8080/task/

# 5. Eliminar la tarea
curl -u testuser:password123 -X DELETE http://localhost:8080/task/100

# 6. Verificar que fue eliminada
curl -u testuser:password123 http://localhost:8080/task/100
# Respuesta: 404 Not Found
```

---

## Configuración Avanzada

### Archivo application.properties

```properties
# Nombre de la aplicación
spring.application.name=todo-rest

# Configuración de Base de Datos H2
spring.datasource.url=jdbc:h2:./db/database
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop

# Consola H2 (solo desarrollo)
spring.h2.console.enabled=true

# Mostrar SQL generado
spring.jpa.show-sql=true
```

**Explicación:**
- `ddl-auto=create-drop`: Crea la estructura en cada inicio y la elimina al cerrar
- `spring.jpa.show-sql=true`: Muestra las consultas SQL en la consola (útil para debugging)

### Datos de Inicialización

El archivo `import.sql` precarga:
- 10 tareas de ejemplo
- 1 usuario de demo (testuser/password123)

Esto permite probar la API inmediatamente sin crear datos manualmente.

---

## Mejoras Futuras

### Funcionalidades Planeadas
- [ ] Autenticación JWT
- [ ] Integración con OAuth2
- [ ] Búsqueda avanzada de tareas
- [ ] Filtrado por estado (pendiente, completada)
- [ ] Categorización de tareas
- [ ] Recordatorios por email
- [ ] Sincronización con aplicación móvil
- [ ] Exportación a PDF/CSV

### Mejoras Técnicas
- [ ] Implementar paginación en listados
- [ ] Agregar caché de aplicación
- [ ] Trazabilidad de auditoría
- [ ] Testes unitarios completos
- [ ] Testes de integración

---

## Soporte y Contacto

Para reportar bugs o sugerencias:
- **Email:** admin@taskapi.example.com
- **Licencia:** CC BY

---

## Resumen

Esta API REST de gestión de tareas proporciona una solución completa y segura para organizar actividades personales. Con su arquitectura clara, documentación exhaustiva y mecanismos de seguridad robustos, es una base sólida para construir aplicaciones colaborativas más complejas.

Los usuarios pueden:
- Registrarse y crear sus propias cuentas
- Gestionar tareas personales de forma segura
- Acceder a una interfaz REST bien documentada
- Integrar fácilmente con clientes frontend

La aplicación está lista para desarrollo y testing, con datos de ejemplo precargados para experimentación inmediata.

---

**Versión del documento:** 1.0  
**Última actualización:** Enero 2025  
**Estado:** Completo y Verificado
