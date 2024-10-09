package com.dmware.api_onibusbh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiOnibusbhApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiOnibusbhApplication.class, args);
	}

}
