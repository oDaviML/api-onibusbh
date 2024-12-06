package com.dmware.api_onibusbh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableScheduling
@CrossOrigin
@OpenAPIDefinition(info = @Info(title = "API Onibus BH", version = "1.0", description = "API para consulta de dados relacionados aos onibus de Belo Horizonte"))
public class ApiOnibusbhApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiOnibusbhApplication.class, args);
	}

}
