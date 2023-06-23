package com.adt.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(basePackageClasses = {
		AuthservicesApplication.class,
        Jsr310JpaConverters.class
})
public class AuthservicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthservicesApplication.class, args);
	}

}