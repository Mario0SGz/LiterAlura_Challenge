package com.aluracursos.literalura;

import com.aluracursos.literalura.principal.Principal;
import com.aluracursos.literalura.repository.IAutoresRepository;
import com.aluracursos.literalura.repository.ILibrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private IAutoresRepository autoresRepository;
	@Autowired
	private ILibrosRepository librosRepository;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(librosRepository, autoresRepository);
		principal.muestraMenu();
	}
}
