package br.com.jorgefigueredoo.agendamento_barbearia_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class AgendamentoBarbeariaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgendamentoBarbeariaApiApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void abrirSwagger(ApplicationReadyEvent event) {
		try {
			String port = event.getApplicationContext()
					.getEnvironment()
					.getProperty("server.port", "8080");

			// rota mais garantida no springdoc:
			String url = "http://localhost:" + port + "/swagger-ui/index.html";

			// tenta via Desktop
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(new URI(url));
				return;
			}

			// fallback Windows (se Desktop falhar)
			new ProcessBuilder("cmd", "/c", "start", url).start();

		} catch (Exception e) {
			// se falhar, só ignora (em servidor não tem navegador)
		}
	}
}
