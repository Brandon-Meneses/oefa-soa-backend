package pe.infinitumsolutions.oefa_soa.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pe.infinitumsolutions.oefa_soa.service.OefaService

@Tag(name = "ESB", description = "Servicios combinados de fiscalización, supervisión e indicadores ambientales")
@RestController
@RequestMapping("/api/esb")
class EsbController(private val service: OefaService) {

    @Operation(summary = "Resumen de datos ambientales", description = "Proporciona un resumen combinado de denuncias, pedidos de fiscalía, indicadores de agua, informes de supervisión y medidas administrativas.")
    @GetMapping("/resumen")
    fun resumen() = mapOf(
        "fiscalizacion" to mapOf(
            "denuncias" to service.denunciasSinada(),
            "pedidosFiscalia" to service.pedidosFiscalia()
        ),
        "evaluacionAgua" to service.indicadoresAgua(),
        "supervision" to mapOf(
            "informes" to service.informesSupervision(),
            "medidas" to service.medidasAdministrativas()
        )
    )
}