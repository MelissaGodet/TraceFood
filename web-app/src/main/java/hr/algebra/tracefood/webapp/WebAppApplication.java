package hr.algebra.tracefood.webapp;

import io.prometheus.client.exporter.HTTPServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.IOException;

@EnableAspectJAutoProxy
@SpringBootApplication
public class WebAppApplication {

	public static void main(String[] args) throws IOException {
		HTTPServer server = new HTTPServer(12345);
		SpringApplication.run(WebAppApplication.class, args);
	}

}
