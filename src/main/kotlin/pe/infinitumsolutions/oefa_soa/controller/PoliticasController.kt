package pe.infinitumsolutions.oefa_soa.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pe.infinitumsolutions.oefa_soa.service.OefaService

@Tag(
    name = "Políticas y Estrategias Ambientales",
    description = "Servicios del OEFA sobre proyectos normativos, regulaciones ambientales y actividades de asistencia técnica (AFA)."
)
@RestController
@RequestMapping("/api/politicas")
class PoliticasController(private val service: OefaService) {

    // ===========================================================
    // ⚖️ PROYECTOS NORMATIVOS OEFA (GENERAL)
    // ===========================================================
    @Operation(
        summary = "Obtener proyectos normativos del OEFA",
        description = "Devuelve los proyectos normativos desarrollados por el OEFA relacionados con la gestión y fiscalización ambiental."
    )
    @GetMapping("/proyectos-normativos")
    fun proyectosNormativos(
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.proyectosNormativos(limit))


    // ===========================================================
    // ⚖️ PROYECTOS NORMATIVOS OEFA (2018–2025)
    // ===========================================================
    @Operation(
        summary = "Obtener proyectos normativos recientes del OEFA",
        description = "Devuelve los proyectos normativos elaborados por el OEFA entre 2018 y 2025."
    )
    @GetMapping("/proyectos-normativos-recientes")
    fun proyectosNormativosRecientes(
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.proyectosNormativosRecientes(limit))


    // ===========================================================
    // ⚖️ PROYECTOS NORMATIVOS EXTERNOS (GENERAL)
    // ===========================================================
    @Operation(
        summary = "Obtener proyectos normativos externos",
        description = "Devuelve los proyectos normativos externos vinculados al OEFA, propuestos por otras entidades o ministerios."
    )
    @GetMapping("/proyectos-externos")
    fun proyectosExternos(
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.proyectosExternos(limit))


    // ===========================================================
    // ⚖️ PROYECTOS NORMATIVOS EXTERNOS (2018–2025)
    // ===========================================================
    @Operation(
        summary = "Obtener proyectos normativos externos recientes",
        description = "Devuelve los proyectos normativos externos más recientes relacionados con la política ambiental peruana (2018–2025)."
    )
    @GetMapping("/proyectos-externos-recientes")
    fun proyectosExternosRecientes(
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.proyectosExternosRecientes(limit))


    // ===========================================================
    // 🧩 ACTIVIDADES DE ASISTENCIA TÉCNICA (AFA)
    // ===========================================================
    @Operation(
        summary = "Obtener actividades de asistencia técnica (AFA)",
        description = "Devuelve las actividades de Asistencia Técnica Ambiental (AFA) promovidas por el OEFA entre 2018 y 2024."
    )
    @GetMapping("/actividades-afa")
    fun actividadesAFA(
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.actividadesAFA(limit))
}