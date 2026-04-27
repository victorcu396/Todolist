# Guía Rápida - API REST de Tareas

## Inicio Rápido (2 minutos)

### 1. Ejecutar la Aplicación
```bash
cd "d:\2º DAW\Desarrollo Web Entorno Servidor\Proyectos_Empresa\todo-rest\todo-rest"
.\mvnw.cmd spring-boot:run
```

### 2. Acceder a Swagger
- URL: http://localhost:8080/swagger-ui/index.html
- Usuario: testuser
- Contraseña: password123

### 3. Probar primeros requests
```bash
# Ver mis tareas
curl -u testuser:password123 http://localhost:8080/task/

# Crear tarea
curl -u testuser:password123 -X POST http://localhost:8080/task/ \
  -H "Content-Type:application/json" \
  -d '{"title":"Mi tarea","description":"Descripción","deadline":"2025-12-31T23:59:59"}'

# Editar tarea
curl -u testuser:password123 -X PUT http://localhost:8080/task/1 \
  -H "Content-Type:application/json" \
  -d '{"title":"Tarea actualizada","description":"Nueva desc","deadline":"2025-12-31T23:59:59"}'

# Eliminar tarea
curl -u testuser:password123 -X DELETE http://localhost:8080/task/1

# Registrar usuario
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type:application/json" \
  -d '{"username":"nuevo","email":"nuevo@test.com","password":"pass123"}'
```

## Qué Hace Esta API

✅ Crear tareas personales  
✅ Ver tus tareas  
✅ Editar tareas  
✅ Eliminar tareas  
✅ Registrar usuarios  
✅ Autenticación segura  
✅ Documentación interactiva (Swagger)

## Endpoints Principales

| Endpoint | Método | Descripción |
|----------|--------|------------|
| /task/ | GET | Obtener mis tareas |
| /task/{id} | GET | Obtener tarea específica |
| /task/ | POST | Crear nueva tarea |
| /task/{id} | PUT | Actualizar tarea |
| /task/{id} | DELETE | Eliminar tarea |
| /auth/register | POST | Registrar usuario |

## Estructura de Datos

### Crear/Actualizar Tarea
```json
{
  "title": "string",
  "description": "string",
  "deadline": "2025-12-31T23:59:59"
}
```

### Respuesta de Tarea
```json
{
  "id": 1,
  "title": "string",
  "description": "string",
  "createdAt": "2025-01-13T16:12:11.295172",
  "deadline": "2025-01-20T16:12:11.295172",
  "author": {
    "id": 1,
    "username": "testuser",
    "email": "testuser@example.com"
  }
}
```

### Registrar Usuario
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

## Características de Seguridad

🔒 Autenticación HTTP Basic  
🔒 Contraseñas cifradas  
🔒 Validación de propiedad (solo accedes a tus tareas)  
🔒 Autorización por roles

## Datos de Ejemplo

- **Usuario:** testuser
- **Contraseña:** password123
- **Tareas precargadas:** IDs 1-10
- **Base de datos:** H2 (en memoria, reinicia con la app)

## Requisitos

- Java 17+
- Maven 3.8+

## Ver Documentación Completa

Para documentación detallada, ver: `DOCUMENTACION_COMPLETA.md`

---

**API Lista para usar** ✅
