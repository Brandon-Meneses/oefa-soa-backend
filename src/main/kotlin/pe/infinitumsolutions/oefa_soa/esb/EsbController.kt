package pe.infinitumsolutions.oefa_soa.esb

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "ESB Orchestrator",
    description = "Simula el Enterprise Service Bus (ESB) orquestando múltiples servicios SOA del OEFA."
)
@RestController
@RequestMapping("/api/esb")
class EsbController(private val orchestrator: EsbOrchestrator) {

    // ===========================================================
    // 🌎 Resumen ambiental general
    // ===========================================================
    @Operation(
        summary = "Obtener resumen ambiental integral",
        description = "Integra datos de fiscalización, supervisión, evaluación y políticas ambientales."
    )
    @GetMapping("/resumen")
    fun resumen(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<Any> =
        ResponseEntity.ok(orchestrator.obtenerResumenAmbiental(limit))

    // ===========================================================
    // 💧 Resumen temático: agua
    // ===========================================================
    @Operation(
        summary = "Obtener resumen ambiental sobre agua",
        description = "Integra indicadores de calidad, denuncias y políticas relacionadas al agua."
    )
    @GetMapping("/agua")
    fun resumenAgua(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<Any> =
        ResponseEntity.ok(orchestrator.obtenerResumenAgua(limit))

    // ===========================================================
    // 🌱 Resumen temático: suelo
    // ===========================================================
    @Operation(
        summary = "Obtener resumen ambiental sobre suelo",
        description = "Combina datos de calidad de suelo, supervisión y normativas."
    )
    @GetMapping("/suelo")
    fun resumenSuelo(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<Any> =
        ResponseEntity.ok(orchestrator.obtenerResumenSuelo(limit))

    //aire
    @Operation(
        summary = "Obtener resumen ambiental sobre aire",
        description = "Combina datos de calidad del aire, supervisión y normativas."
    )
    @GetMapping("/aire")
    fun resumenAire(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<Any> =
        ResponseEntity.ok(orchestrator.obtenerResumenAire(limit))
    //biota
    @Operation(
        summary = "Obtener resumen ambiental sobre biota",
        description = "Combina datos de calidad de biota, supervisión y normativas."
    )
    @GetMapping("/biota")
    fun resumenBiota(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<Any> =
        ResponseEntity.ok(orchestrator.obtenerResumenBiota(limit))

    // ===========================================================
// 🔹 Endpoint genérico por tema
// ===========================================================
    @Operation(
        summary = "Obtener resumen ambiental por tema",
        description = "Retorna la información orquestada correspondiente a un tema específico (agua, aire, suelo, biota, ruido, hidrobiologia o sedimento)."
    )
    @GetMapping("/{tema}")
    fun resumenPorTema(
        @PathVariable tema: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<Any> {
        val data = when (tema.lowercase()) {
            "agua" -> orchestrator.obtenerResumenAgua(limit)
            "suelo" -> orchestrator.obtenerResumenSuelo(limit)
            "aire" -> orchestrator.obtenerResumenAire(limit)
            "biota" -> orchestrator.obtenerResumenBiota(limit)
            "ruido" -> orchestrator.obtenerResumenRuido(limit)
            "hidrobiologia" -> orchestrator.obtenerResumenHidrobiologia(limit)
            "sedimento", "sedimentos" -> orchestrator.obtenerResumenSedimentos(limit)
            "florafauna", "flora y fauna" -> orchestrator.obtenerResumenFloraFauna(limit)
            "resumen" -> orchestrator.obtenerResumenAmbiental(limit)
            else -> mapOf("error" to "Tema '$tema' no reconocido")
        }

        return ResponseEntity.ok(data)
    }
}