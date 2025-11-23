package pe.infinitumsolutions.oefa_soa.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins(
                        "http://localhost:8080",      // Swagger local
                        "http://127.0.0.1:8080",
                        "http://localhost:5000",      // Flutter Web
                        "http://127.0.0.1:5000",
                        "http://localhost:3000",
                        "http://localhost:8081",
                        "http://10.0.2.2:8080",       // Emulador Android
                        "http://192.168.1.96:8080",   // Tu IP local
                        "http://localhost:4200"       // Angular u otros frontends
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
            }
        }
    }
}