# Preguntas Frecuentes y Solución de Problemas

## Preguntas Frecuentes (FAQ)

### ¿Cómo ejecuto la aplicación por primera vez?

**Respuesta:**

1. Abre una terminal en el directorio del proyecto:
```bash
cd "d:\2º DAW\Desarrollo Web Entorno Servidor\Proyectos_Empresa\todo-rest\todo-rest"
```

2. Ejecuta Maven:
```bash
.\mvnw.cmd spring-boot:run
```

3. Espera a que veas el mensaje:
```
Started TodoRestApplication in X.XXX seconds
```

4. Abre http://localhost:8080/swagger-ui/index.html en tu navegador

---

### ¿Cuál es la contraseña del usuario de prueba?

**Respuesta:**

La predefinida es:
- **Usuario:** testuser
- **Contraseña:** password123

Puedes crear nuevos usuarios usando el endpoint `/auth/register`

---

### ¿Puedo usar datos persistentes entre reinicios?

**Respuesta:**

Por defecto, la base de datos H2 se crea y destruye con cada reinicio. Para persistencia real, necesitas:

1. Cambiar `spring.jpa.hibernate.ddl-auto=create-drop` a `update` en `application.properties`
2. O cambiar a una base de datos real como PostgreSQL o MySQL

---

### ¿Cómo agrego más usuarios de prueba?

**Respuesta:**

Opción 1 - Usar el endpoint:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "maria",
    "email": "maria@test.com",
    "password": "contraseña123"
  }'
```

Opción 2 - Editar `import.sql` y agregar líneas como:
```sql
INSERT INTO user_entity (id, email, username, password, is_admin) 
VALUES (NEXTVAL('user_entity_seq'), 'maria@test.com','maria','{noop}pass123',false);
```

---

### ¿Qué significa "403 Forbidden" en una tarea?

**Respuesta:**

Significa que intentaste acceder a una tarea que no te pertenece. Solo puedes:
- Ver tareas que creaste
- Editar tareas tuyas
- Eliminar tareas tuyas

Si quieres probar, crea dos usuarios diferentes.

---

### ¿Cómo accedo a la consola H2?

**Respuesta:**

1. Ve a: http://localhost:8080/h2-console
2. Verifica que la URL de conexión sea: `jdbc:h2:./db/database`
3. Usuario: `sa`
4. Contraseña: (dejar vacía)
5. Click en "Connect"

Desde ahí puedes ejecutar queries SQL directas.

---

### ¿Cómo cambio el puerto (actualmente 8080)?

**Respuesta:**

Edita `application.properties` y agrega:
```properties
server.port=9000
```

Luego reinicia. La API estará en http://localhost:9000

---

### ¿Cómo veo las queries SQL que genera la aplicación?

**Respuesta:**

Ya está habilitado por defecto. En la consola donde ejecutas `spring-boot:run` verás las queries SQL.

Si quieres formateadas mejor, agrega a `application.properties`:
```properties
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
```

---

### ¿Cómo hago deploy a producción?

**Respuesta:**

1. Cambiar `spring.jpa.hibernate.ddl-auto` de `create-drop` a `update`
2. Usar base de datos real (PostgreSQL, MySQL)
3. Cambiar de HTTP a HTTPS
4. Configurar JWT en lugar de Basic Auth
5. Desactivar H2 console
6. Compilar JAR: `mvn clean package`
7. Ejecutar: `java -jar target/todo-rest-0.0.1-SNAPSHOT.jar`

---

### ¿Cómo añado tus propias excepciones personalizadas?

**Respuesta:**

Ejemplo - Nueva excepción:

```java
public class InvalidTaskException extends RuntimeException {
    public InvalidTaskException(String message) {
        super(message);
    }
}
```

Luego creates un handler en `GlobalErrorController`:

```java
@ExceptionHandler(InvalidTaskException.class)
public ResponseEntity<ErrorResponse> handleInvalidTask(InvalidTaskException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("Tarea inválida", ex.getMessage()));
}
```

---

### ¿Puedo usar esta API desde una aplicación Angular/React?

**Respuesta:**

Sí. Necesitas:

1. Habilitar CORS en `WebConfig.java`:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/task/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}
```

2. Desde el cliente, envía credenciales:

```javascript
// Ejemplo con JavaScript
fetch('http://localhost:8080/task/', {
    method: 'GET',
    headers: {
        'Authorization': 'Basic ' + btoa('testuser:password123'),
        'Content-Type': 'application/json'
    },
    credentials: 'include'
})
```

---

### ¿Cómo valido mejor los datos de entrada?

**Respuesta:**

Usa anotaciones de validación. Ejemplo:

```java
public record TaskUpdateRequestDto(
    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    String title,
    
    @NotBlank(message = "La descripción no puede estar vacía")
    String description,
    
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser futura")
    LocalDateTime deadline
) {}
```

Luego en el controller:

```java
public ResponseEntity<TaskResponseDto> create(
        @Valid @RequestBody TaskUpdateRequestDto updateRequest,
        @AuthenticationPrincipal User author) {
    // ...
}
```

---

---

## Solución de Problemas

### Problema 1: "mvn no se reconoce como comando"

**Solución:**

Opción 1 - Usa el wrapper (recomendado):
```bash
.\mvnw.cmd clean compile
```

Opción 2 - Instala Maven globalmente:
- Descarga desde maven.apache.org
- Configura variables de entorno
- Reinicia la terminal

---

### Problema 2: "Puerto 8080 ya está en uso"

**Solución:**

Opción 1 - Cambia el puerto:

En `application.properties`:
```properties
server.port=8081
```

Opción 2 - Mata el proceso usando el puerto:

En PowerShell (Windows):
```powershell
Get-Process | Where-Object {$_.Handles -like "*8080*"} | Stop-Process
# O más directo:
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

### Problema 3: "Contraseña incorrecta, no puedo iniciar sesión"

**Solución:**

1. Verifica el usuario y contraseña sean exactos:
   - Usuario: **testuser** (no testuser, no testuser)
   - Contraseña: **password123** (números, no texto)

2. Si registraste un usuario nuevo, verifica el nombre:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"nuevo","email":"nuevo@test.com","password":"pass123"}'
```

3. La contraseña es case-sensitive (mayúsculas/minúsculas importan)

---

### Problema 4: "404 Not Found - No encuentro mi tarea"

**Posibles causas y soluciones:**

**Causa 1:** La tarea no existe
```bash
# Verifica que el ID existe
curl -u testuser:password123 http://localhost:8080/task/
# Verifica en la lista
```

**Causa 2:** La tarea pertenece a otro usuario
- Solo ves tus propias tareas
- El usuario "testuser" no ve tareas del usuario "maria"

**Causa 3:** La base de datos se reinició
- Las tareas de H2 se pierden al cerrar la app
- Los datos de ejemplo se cargan de nuevo

---

### Problema 5: "403 Forbidden - No tengo permisos"

**Solución:**

Verificas tres cosas:

1. ¿Estás autenticado?
```bash
curl -u testuser:password123 http://localhost:8080/task/1
```

2. ¿Eres el propietario de la tarea?
   - Verifica con GET /task/ para ver tus tareas
   - Mira el campo "author" en la respuesta

3. ¿Tienes el rol correcto?
   - Los nuevos usuarios tienen rol USER (suficiente)
   - Solo ADMIN tiene acceso a todo (futuro)

---

### Problema 6: "CORS error desde JavaScript/React"

**Solución:**

Si ves un error como:
```
Access to XMLHttpRequest blocked by CORS policy
```

Significa:

1. La aplicación frontend está en diferente puerto/dominio
2. Necesitas habilitar CORS en Spring

**Solución - Edita WebConfig.java:**

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
```

(En producción, especifica exactly tus dominios permitidos)

---

### Problema 7: "ERROR: Table not found"

**Solución:**

Si ves este error:
```
Table TASK not found
```

Significa:
1. La base de datos no se inicializó correctamente
2. La aplicación no ejecutó las anotaciones @Entity

**Soluciona:**

1. Borra el folder `db/`:
```bash
Remove-Item -Recurse -Force db/
```

2. Reinicia la aplicación:
```bash
.\mvnw.cmd spring-boot:run
```

3. La base de datos se recreará con los datos de `import.sql`

---

### Problema 8: "Conectar a base de datos PostgreSQL / MySQL"

**Solución:**

1. Instala la base de datos

2. Cambia `pom.xml` - reemplaza la dependencia H2:

**Para PostgreSQL:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Para MySQL:**
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
    <scope>runtime</scope>
</dependency>
```

3. Actualiza `application.properties`:

**Para PostgreSQL:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/todo_db
spring.datasource.username=postgres
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

**Para MySQL:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/todo_db
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

4. Reinicia la aplicación

---

### Problema 9: "¿Cómo resetear los datos iniciales?"

**Solución:**

Los datos se resetean automáticamente si usas H2 porque está configurado con `ddl-auto=create-drop`.

**Opción manual:**

1. Detén la aplicación
2. Borra el folder `db/`:
```bash
Remove-Item -Recurse -Force db/
```
3. Reinicia - los datos de `import.sql` se cargarán de nuevo

---

### Problema 10: "Aplicación lenta o baja performance"

**Soluciones:**

1. **Desactiva logs detallados:**
   En `application.properties` comenta:
   ```properties
   #spring.jpa.show-sql=true
   ```

2. **Agrega índices en base de datos:**
   En Task:
   ```java
   @Entity
   public class Task {
       @Id
       @GeneratedValue
       private Long id;
       
       @Index(name = "idx_author")
       @ManyToOne
       private User author;
       
       // ...
   }
   ```

3. **Implementa paginación:**
   ```java
   @GetMapping
   public Page<TaskResponseDto> getAllTasks(
       @AuthenticationPrincipal User user,
       @PageableDefault(size = 20) Pageable pageable) {
       return taskService.findByAuthor(user, pageable)
               .map(TaskResponseDto::toDto);
   }
   ```

4. **Usa caché:**
   ```java
   @Cacheable("tasks")
   public List<Task> findByAuthor(User author) {
       // ...
   }
   ```

---

## Checklist de Verificación

Antes de reportar un bug, verifica:

- [ ] ¿Está la aplicación funcionando? (sin errores en consola)
- [ ] ¿Es accesible http://localhost:8080/swagger-ui/index.html?
- [ ] ¿Son correctas las credenciales? (testuser/password123)
- [ ] ¿Existe el recurso que buscas? (verifica con GET)
- [ ] ¿Eres el propietario del recurso?
- [ ] ¿Enviaste el Content-Type correcto? (application/json)
- [ ] ¿Está bien formateado el JSON?
- [ ] ¿Hay datos de ejemplo cargados? (revisa /task/)

---

## Contacto y Soporte

Si después de todo aún tienes problemas:

1. Revisa los logs completos en la consola
2. Verifica en la consola H2 si los datos están ahí
3. Intenta desde Swagger UI en lugar de curl/Postman
4. Contacta: admin@taskapi.example.com

---

**Guía de Solución de Problemas - Versión 1.0**
