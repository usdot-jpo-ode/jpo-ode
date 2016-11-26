package us.dot.its.jpo.ode;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(OdeProperties.class)
public class OdeSvcsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OdeSvcsApplication.class, args);
	}

	@Bean
	CommandLineRunner init(OdeProperties odeProperties) {
		return (args) -> {
			odeProperties.init();
		};
	}
}
