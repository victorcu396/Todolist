package com.azx23034.todo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info =
@Info(description = "Una API REST para gestionar tareas personales",
		version = "1.0",
		contact = @Contact(email = "admin@taskapi.example.com", name = "Task API Team"),
		license = @License(name = "MIT"),
		title = "API REST de Gestión de Tareas"
)
)
@SpringBootApplication
public class ToDolistApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToDolistApplication.class, args);
	}

}
