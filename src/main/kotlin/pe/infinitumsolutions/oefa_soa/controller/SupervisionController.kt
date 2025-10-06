package pe.infinitumsolutions.oefa_soa.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pe.infinitumsolutions.oefa_soa.service.OefaService

@Tag(
    name = "Supervisión Ambiental",
    description = "Servicios del OEFA relacionados con la supervisión ambiental, medidas administrativas e informes."
)
@RestController
@RequestMapping("/api/supervision")
class SupervisionController(private val service: OefaService) {

    // ===========================================================
    // 📊 Informes de Supervisión (2018–2025)
    // ===========================================================
    @Operation(
        summary = "Obtener informes de supervisión",
        description = "Devuelve los informes de supervisión ambiental realizados por el OEFA entre 2018 y 2025."
    )
    @GetMapping("/informes")
    fun informesSupervision(
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.informesSupervision(limit))


    // ===========================================================
    // ⚖️ Medidas Administrativas de Supervisión (2016–2025)
    // ===========================================================
    @Operation(
        summary = "Obtener medidas administrativas",
        description = "Devuelve las medidas administrativas de supervisión adoptadas por el OEFA entre 2016 y 2025."
    )
    @GetMapping("/medidas")
    fun medidasAdministrativas(
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.medidasAdministrativas(limit))


    // ===========================================================
    // 🧾 Informes de la Dirección de Supervisión (2019–2025)
    // ===========================================================
    @Operation(
        summary = "Obtener informes de la Dirección de Supervisión",
        description = "Devuelve los informes técnicos elaborados por la Dirección de Supervisión del OEFA entre 2019 y 2025."
    )
    @GetMapping("/informes-direccion")
    fun informesDireccion(
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.informesDireccionSupervision(limit))
}