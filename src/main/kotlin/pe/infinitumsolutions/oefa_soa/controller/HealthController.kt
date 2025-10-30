package pe.infinitumsolutions.oefa_soa.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api")
class HealthController {

    @GetMapping("/health")
    fun health() = mapOf(
        "status" to "UP",
        "timestamp" to Instant.now().toString()
    )

    @GetMapping("/info")
    fun info() = mapOf(
        "app" to "OEFA SOA Backend",
        "version" to "1.0.0",
        "author" to "Brandon Meneses",
        "description" to "Sistema SOA basado en servicios de datos abiertos del OEFA."
    )
}