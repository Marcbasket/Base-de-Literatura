package com.challenge.baseliteratura;

import com.challenge.baseliteratura.principal.Principal;
import com.challenge.baseliteratura.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BaseliteraturaApplication implements CommandLineRunner {

	@Autowired
	private AutorRepository repository;
	public static void main(String[] args) {
		SpringApplication.run(BaseliteraturaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repository);
		principal.muestraElMenu();
	}
}