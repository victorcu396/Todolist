# Arquitectura Técnica Detallada

## 1. Diagrama de Capas de Arquitectura

```
┌────────────────────────────────────────────────────────────┐
│                    Clientes (Cliente REST)                  │
│        (Navegador, Postman, curl, Aplicaciones)             │
└──────────────────────┬───────────────────────────────────────┘
                       │ HTTP/HTTPS
                       │
┌──────────────────────▼───────────────────────────────────────┐
│                  CAPA DE PRESENTACIÓN                        │
│  ┌──────────────────┬──────────────────┐                    │
│  │ TaskController   │ UserController   │                    │
│  │                  │                  │                    │
│  │ - GET /task/     │ - POST /auth/    │                    │
│  │ - POST /task/    │   register       │                    │
│  │ - PUT /task/{id} │                  │                    │
│  │ - DELETE /task   │                  │                    │
│  └──────────────┬───┴────────────┬─────┘                    │
└─────────────────┼────────────────┼──────────────────────────┘
                  │                │
┌─────────────────▼────────────────▼──────────────────────────┐
│                 CAPA DE NEGOCIO (SERVICIOS)                 │
│  ┌──────────────────┬──────────────────┐                    │
│  │ TaskService      │ UserService      │                    │
│  │                  │                  │                    │
│  │ - findAll()      │ - register()     │                    │
│  │ - findById()     │                  │                    │
│  │ - findByAuthor() │                  │                    │
│  │ - save()         │                  │                    │
│  │ - edit()         │                  │                    │
│  │ - delete()       │                  │                    │
│  └──────────────┬───┴────────────┬─────┘                    │
└─────────────────┼────────────────┼──────────────────────────┘
                  │                │
┌─────────────────▼────────────────▼──────────────────────────┐
│              CAPA DE PERSISTENCIA (REPOSITORIES)            │
│  ┌──────────────────┬──────────────────┐                    │
│  │ TaskRepository   │ UserRepository   │                    │
│  │ extends          │ extends          │                    │
│  │ JpaRepository    │ JpaRepository    │                    │
│  │                  │                  │                    │
│  │ - findByAuthor() │ - (métodos de    │                    │
│  │                  │   JpaRepository) │                    │
│  └──────────────┬───┴────────────┬─────┘                    │
└─────────────────┼────────────────┼──────────────────────────┘
                  │                │
┌─────────────────▼────────────────▼──────────────────────────┐
│                    BASE DE DATOS (H2)                       │
│  ┌──────────────────────────────────────────┐               │
│  │ Tablas:                                  │               │
│  │ - task (Tareas)                          │               │
│  │ - user_entity (Usuarios)                 │               │
│  │ - hibernate_sequence (Generador IDs)     │               │
│  └──────────────────────────────────────────┘               │
└─────────────────────────────────────────────────────────────┘
```

## 2. Diagrama de Flujo - Crear una Tarea

```
┌─────────────────┐
│ Cliente REST    │
│ (POST /task/)   │
└────────┬────────┘
         │ JSON: {title, description, deadline}
         │ Auth: user:pass (Base64)
         │
┌────────▼────────────────────────────────┐
│ TaskController.create()                 │
│ - Valida autenticación                  │
│ - Recibe TaskUpdateRequestDto           │
│ - Obtiene usuario autenticado           │
└────────┬────────────────────────────────┘
         │
┌────────▼────────────────────────────────┐
│ TaskService.save()                      │
│ - Recibe DTO y usuario                  │
│ - Construye entidad Task                │
│ - Asigna fecha de creación (ahora)      │
│ - Asigna usuario como autor             │
└────────┬────────────────────────────────┘
         │
┌────────▼────────────────────────────────┐
│ TaskRepository.save()                   │
│ - Persiste en H2                        │
│ - Genera ID automáticamente             │
│ - Retorna Task guardada                 │
└────────┬────────────────────────────────┘
         │
┌────────▼────────────────────────────────┐
│ TaskController.create()                 │
│ - Convierte Task a TaskResponseDto      │
│ - Retorna 201 Created                   │
└────────┬────────────────────────────────┘
         │ JSON: TaskResponseDto completo
         │ + metadata (id, createdAt, etc)
         │
┌────────▼────────────┐
│ Cliente recibe      │
│ Tarea creada ✓      │
└─────────────────────┘
```

## 3. Diagrama de Flujo - Autenticación y Autorización

```
┌─────────────────────────────┐
│ Cliente envía request       │
│ con autenticación (user:pw) │
└────────┬────────────────────┘
         │
┌────────▼──────────────────────────────────┐
│ Spring Security Filter                    │
│ - Intercepta la solicitud                 │
│ - Descodifica Base64 (user:pw)            │
└────────┬───────────────────────────────────┘
         │
┌────────▼──────────────────────────────────┐
│ CustomUserDetailsService.loadUserByUsername()
│ - Busca usuario en UserRepository         │
│ - Retorna UserDetails                     │
└────────┬───────────────────────────────────┘
         │
┌────────▼──────────────────────────────────┐
│ PasswordEncoderConfig                     │
│ - Compara contraseña con la almacenada    │
│ - Valida con bcrypt                       │
└────────┬───────────────────────────────────┘
         │
    ¿Válido?
    │
    ├──→ NO → CustomAuthenticationEntryPoint
    │         (Retorna 401 Unauthorized)
    │
    └──→ SÍ → Crea Authentication objeto
              │
         ┌────▼─────────────────────────────┐
         │ @PreAuthorize en endpoint        │
         │ - Valida roles y permisos        │
         │ - Comprueba propiedad de recurso │
         │   (OwnerCheck)                   │
         └────┬──────────────────────────────┘
              │
          ¿Autorizado?
          │
          ├──→ NO → CustomAccessDeniedHandler
          │         (Retorna 403 Forbidden)
          │
          └──→ SÍ → Ejecuta controller
                    Procesa solicitud
```

## 4. Diagrama de Relaciones en Base de Datos

```
┌─────────────────────────────────────┐
│          USER_ENTITY                │
│  (Tabla de Usuarios)                │
├─────────────────────────────────────┤
│ ★ id (PK)              : BIGINT     │
│ - username             : VARCHAR    │
│ - email                : VARCHAR    │
│ - password             : VARCHAR    │
│ - is_admin             : BOOLEAN    │
└────────────────┬────────────────────┘
                 │ 1:N
        (Un usuario puede tener
         múltiples tareas)
                 │
┌────────────────▼────────────────────┐
│           TASK                       │
│  (Tabla de Tareas)                  │
├─────────────────────────────────────┤
│ ★ id (PK)              : BIGINT     │
│ - created_at           : TIMESTAMP  │
│ - title                : VARCHAR    │
│ - description          : CLOB       │
│ - deadline             : TIMESTAMP  │
│ ● author_id (FK)       : BIGINT     │
│   ↓ referencia a user_entity.id     │
└─────────────────────────────────────┘

Convenciones:
★ = Primary Key (Clave Primaria)
● = Foreign Key (Clave Foránea)
PK = Primary Key
FK = Foreign Key
```

## 5. Flujo de Datos - Secuencia de Componentes

### Creación de Tarea (Secuencia Temporal)

```
Tiempo   Cliente    Controller   Service    Repository   Database
  │
  │      POST /task/
  ├─────────────────→
  │           │ validar auth
  │           │ crear objeto
  │           │─────────────→
  │           │           │ mapear DTO a entidad
  │           │           │ asignar usuario actual
  │           │           │─────────────────────→
  │           │           │           │ INSERT en task
  │           │           │           │ NEXTVAL('seq')
  │           │           │           ├─────────────────┐
  │           │           │           │ Retorna Task    │
  │           │           │←──────────┤ (con ID)        │
  │           │           │           └─────────────────┘
  │           │←──────────┤
  │           │ convertir a DTO
  │←──────────┤
  │ 201 Created
  │ JSON con tarea
  ▼
```

## 6. Estructura de Clases - Relaciones

```
┌──────────────────────────────────────────────────────────┐
│              Task (Entidad JPA)                         │
│  ┌────────────────────────────────────────────────────┐ │
│  │ - id: Long                                         │ │
│  │ - createdAt: LocalDateTime                         │ │
│  │ - title: String                                    │ │
│  │ - description: String (@Lob)                       │ │
│  │ - deadline: LocalDateTime                          │ │
│  │ - author: User (@ManyToOne)                        │ │
│  │                                                    │ │
│  │ Methods:                                           │ │
│  │ + getId(), getTitle(), getDescription()           │ │
│  │ + equals(), hashCode(), toString()                │ │
│  └────────────────────────────────────────────────────┘ │
└──────────────┬──────────────────────────────────────────┘
               │ usa
               │
┌──────────────▼──────────────────────────────────────────┐
│        TaskResponseDto (Record)                         │
│  ┌────────────────────────────────────────────────────┐ │
│  │ - id: Long                                         │ │
│  │ - title: String                                    │ │
│  │ - description: String                              │ │
│  │ - createdAt: LocalDateTime                         │ │
│  │ - deadline: LocalDateTime                          │ │
│  │ - author: UserRegistrationResponseDto             │ │
│  │                                                    │ │
│  │ Static:                                            │ │
│  │ + toDto(Task): TaskResponseDto                     │ │
│  └────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│          User (Entidad JPA)                             │
│  ┌────────────────────────────────────────────────────┐ │
│  │ - id: Long                                         │ │
│  │ - username: String                                 │ │
│  │ - email: String                                    │ │
│  │ - password: String (cifrada)                       │ │
│  │ - isAdmin: boolean                                 │ │
│  │                                                    │ │
│  │ Implements: UserDetails                            │ │
│  │ Methods:                                           │ │
│  │ + getAuthorities(): Collection<GrantedAuthority>   │ │
│  └────────────────────────────────────────────────────┘ │
└──────────────┬──────────────────────────────────────────┘
               │ usa
               │
┌──────────────▼──────────────────────────────────────────┐
│    UserRegistrationResponseDto (Record)                │
│  ┌────────────────────────────────────────────────────┐ │
│  │ - id: Long                                         │ │
│  │ - username: String                                 │ │
│  │ - email: String                                    │ │
│  │                                                    │ │
│  │ Static:                                            │ │
│  │ + toDto(User): UserRegistrationResponseDto         │ │
│  └────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────┘
```

## 7. Flujo de Autorización - Validación de Propiedad

```
┌────────────────────────────────────────────┐
│ Usuario intenta acceder a /task/{id}       │
└─────────────┬──────────────────────────────┘
              │
┌─────────────▼──────────────────────────────┐
│ @PostAuthorize o @PreAuthorize             │
│ evaluates expression                       │
└─────────────┬──────────────────────────────┘
              │
              ├─→ PreAuthorize:
              │   Verifica ANTES de ejecutar
              │   @PreAuthorize("@ownerCheck.check(#id, authentication.principal.getId())")
              │
              └─→ OwnerCheck.check(taskId, userId)
                  │
                  ├─→ Busca task por ID
                  │
                  ├─→ ¿Existe?
                  │   │
                  │   ├─→ NO  → false (lanza TaskNotFoundException)
                  │   │
                  │   └─→ SÍ  → (continúa)
                  │       │
                  │       ├─→ Obtiene author de la task
                  │       │
                  │       ├─→ ¿author.id == userId?
                  │           │
                  │           ├─→ NO  → false (403 Forbidden)
                  │           │
                  │           └─→ SÍ  → true
                  │               │
                  │               └─→ Ejecuta operación
```

## 8. Diagrama de Estados - Ciclo de Vida de una Tarea

```
           ┌──────────────────┐
           │ Tarea Creada     │
           │ (Estado inicial) │
           └────────┬─────────┘
                    │
                    │ PUT /task/{id}
                    │ (Actualizar)
                    │
           ┌────────▼─────────┐
           │ Tarea            │
           │ Actualizada      │
           └────────┬─────────┘
                    │
         ┌──────────┴──────────┐
         │                     │
    PUT /task/{id}       DELETE /task/{id}
    (Actualizar          (Eliminar)
     de nuevo)           │
         │               │
    ┌────▼────┐      ┌───▼──────┐
    │ Tarea   │      │ Tarea    │
    │ Actual  │      │ Eliminada│
    │ Actual  │      │ (borrada │
    │ Actual  │      │ de BD)   │
    └────┬────┘      └─────────┘
         │
         │ (mientras exista)
         │
         └──→ (puede seguir editándose)
```

## 9. Ciclo de Procesamiento de Solicitud

```
1. RECEPCIÓN DE SOLICITUD
   ├─ HTTP GET/POST/PUT/DELETE + URL
   ├─ Headers (Authorization, Content-Type)
   └─ Body (JSON si aplica)

2. INTERCEPTACIÓN POR SPRING SECURITY
   ├─ Valida autenticación
   ├─ Extrae usuario y roles
   └─ Agrega contexto de seguridad

3. ENRUTAMIENTO A CONTROLLER
   ├─ DispatcherServlet mapea a endpoint
   ├─ Inyecta dependencias
   └─ Ejecuta método de controller

4. VALIDACIÓN EN CONTROLLER
   ├─ @PreAuthorize evalúa permisos
   ├─ Valida entrada (DTOs)
   └─ Obtiene usuario actual

5. DELEGACIÓN AL SERVICIO
   ├─ Convierte DTO a entidad
   ├─ Aplica lógica de negocio
   └─ Delega persistencia

6. EJECUCIÓN EN REPOSITORIO
   ├─ JPA genera SQL
   ├─ Ejecuta en base de datos
   └─ Retorna entidades populadas

7. CONVERSIÓN A DTO
   ├─ Entidades → DTOs
   ├─ Asigna códigos HTTP
   └─ Genera respuesta

8. SERIALIZACIÓN
   ├─ DTO → JSON
   ├─ Agrega headers
   └─ Retorna al cliente

9. FORMATO DE RESPUESTA
   ├─ HTTP Status (200, 201, 404, etc)
   ├─ Headers (Content-Type, etc)
   └─ Body (JSON)
```

## 10. Matriz de Permisos por Endpoint

```
┌─────────────────────┬──────────┬──────────────┬─────────────┐
│ Endpoint            │ Método   │ Autenticación│ Autorización│
├─────────────────────┼──────────┼──────────────┼─────────────┤
│ /task/              │ GET      │ Sí           │ Solo propias │
│ /task/{id}          │ GET      │ Sí           │ Solo propias │
│ /task/              │ POST     │ Sí           │ Crear propia │
│ /task/{id}          │ PUT      │ Sí           │ Solo propias │
│ /task/{id}          │ DELETE   │ Sí           │ Solo propias │
│ /auth/register      │ POST     │ No           │ Pública      │
└─────────────────────┴──────────┴──────────────┴─────────────┘
```

## 11. Mapeo de Excepciones a Códigos HTTP

```
Exception                          → HTTP Code
────────────────────────────────────────────────
TaskNotFoundException             → 404 Not Found
AuthenticationException           → 401 Unauthorized
AccessDeniedException             → 403 Forbidden
BadCredentialsException           → 401 Unauthorized
UsernameNotFoundException          → 401 Unauthorized
ValidationException               → 400 Bad Request
```

---

**Documento de Arquitectura - Versión 1.0**
