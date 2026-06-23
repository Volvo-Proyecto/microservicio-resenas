package cl.volvo.resenas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Value("${services.usuario.url}")
    private String usuarioServiceUrl;

    @Value("${services.juego.url}")
    private String juegoServiceUrl;

    @Bean
    public WebClient usuariowebClient() {
        return WebClient.builder().baseUrl(usuarioServiceUrl).build();
    }

    @Bean
    public WebClient juegowebClient() {
        return WebClient.builder().baseUrl(juegoServiceUrl).build();
    }
}