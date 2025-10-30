package pe.infinitumsolutions.oefa_soa.ia


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pe.infinitumsolutions.oefa_soa.esb.EsbOrchestrator
import reactor.core.publisher.Mono
import java.time.Instant

@Tag(
    name = "Módulo de IA Ambiental",
    description = "Integra el modelo Groq para generar análisis y conclusiones sobre los datos orquestados por el ESB."
)
@RestController
@RequestMapping("/api/ia")
class IaController(
    private val orchestrator: EsbOrchestrator,
    private val groqClient: GroqClient
) {

    // ===========================================================
    // 🧩 Análisis general del estado ambiental
    // ===========================================================
    @Operation(
        summary = "Analiza el estado ambiental integral",
        description = "Usa el modelo de IA para generar un resumen analítico de los datos orquestados del ESB."
    )
    @GetMapping("/resumen")
    fun analizarResumen(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<Map<String, Any>> {
        val data = orchestrator.obtenerResumenAmbiental(limit)
        val prompt = """
            Eres un analista ambiental especializado en datos del OEFA.
            A partir del siguiente resumen de fiscalización, supervisión, evaluación ambiental y políticas, 
            genera un informe con:
            
            1. Principales hallazgos.
            2. Patrones ambientales observados.
            3. Riesgos emergentes.
            4. Recomendaciones de gestión ambiental.

            Datos:
            ${data.toString().take(4000)}
        """.trimIndent()

        val analisis = groqClient.analyze(prompt).block()

        return ResponseEntity.ok(
            mapOf(
                "tipo" to "Análisis Ambiental Integral",
                "modelo" to "Groq Llama3-70B",
                "timestamp" to Instant.now().toString(),
                "analisis" to analisis
            )
        ) as ResponseEntity<Map<String, Any>>
    }

    // ===========================================================
    // 💧 Análisis temático: Agua
    // ===========================================================
    @Operation(
        summary = "Genera un análisis de IA sobre el tema Agua",
        description = "Integra datos orquestados del ESB y produce un resumen técnico del estado ambiental del agua."
    )
    @GetMapping("/agua")
    fun analizarAgua(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<Map<String, Any>> {
        val data = orchestrator.obtenerResumenAgua(limit)
        val prompt = """
            Analiza los siguientes datos ambientales relacionados con el AGUA:
            Incluyen calidad del agua, denuncias, políticas y supervisión ambiental.
            
            Genera un informe con:
            - Estado general del agua.
            - Posibles causas de contaminación.
            - Áreas críticas o en riesgo.
            - Recomendaciones técnicas basadas en las políticas actuales.
            
            Datos:
            ${data.toString().take(4000)}
        """.trimIndent()

        val analisis = groqClient.analyze(prompt).block()

        return ResponseEntity.ok(
            mapOf(
                "tema" to "Agua",
                "modelo" to "Groq Llama3-70B",
                "analisis" to analisis
            )
        ) as ResponseEntity<Map<String, Any>>
    }

    // ===========================================================
    // 🌱 Análisis temático: Suelo
    // ===========================================================
    @Operation(
        summary = "Genera un análisis de IA sobre el tema Suelo",
        description = "Orquesta información del ESB para producir un análisis de IA enfocado en el suelo."
    )
    @GetMapping("/suelo")
    fun analizarSuelo(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<Map<String, Any>> {
        val data = orchestrator.obtenerResumenSuelo(limit)
        val prompt = """
            A continuación tienes datos ambientales del SUELO del Perú, incluyendo calidad, supervisión y políticas.
            Genera un resumen técnico con:
            - Principales hallazgos en calidad del suelo.
            - Riesgos y patrones de degradación.
            - Políticas o medidas preventivas aplicables.
            - Recomendaciones de mitigación ambiental.
            
            Datos:
            ${data.toString().take(4000)}
        """.trimIndent()

        val analisis = groqClient.analyze(prompt).block()

        return ResponseEntity.ok(
            mapOf(
                "tema" to "Suelo",
                "modelo" to "Groq Llama3-70B",
                "analisis" to analisis
            )
        ) as ResponseEntity<Map<String, Any>>
    }


    @GetMapping("/test")
    fun testGroq(): ResponseEntity<String> {
        val result = groqClient.analyze("Hello from OEFA test").block()
        return ResponseEntity.ok(result ?: "No response.")
    }

    // ===========================================================
// 🤖 Análisis automático temático (unificado)
// ===========================================================
    @Operation(
        summary = "Genera un análisis de IA para un tema ambiental específico",
        description = """
        Analiza automáticamente los datos orquestados por el ESB según el tema indicado.
        Temas disponibles: agua, suelo, aire, biota, ruido, hidrobiologia, sedimentos, resumen.
    """
    )
    @GetMapping("/analizar/{tema}")
    fun analizarPorTema(
        @PathVariable tema: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<Map<String, Any>> {
        val data = when (tema.lowercase()) {
            "agua" -> orchestrator.obtenerResumenAgua(limit)
            "suelo" -> orchestrator.obtenerResumenSuelo(limit)
            "aire" -> orchestrator.obtenerResumenAire(limit)
            "biota" -> orchestrator.obtenerResumenBiota(limit)
            "ruido"-> orchestrator.obtenerResumenRuido(limit)
            "hidrobiologia"-> orchestrator.obtenerResumenHidrobiologia(limit)
            "sedimentos"-> orchestrator.obtenerResumenSedimentos(limit)
            "resumen", "general" -> orchestrator.obtenerResumenAmbiental(limit)
            else -> return ResponseEntity.badRequest().body(
                mapOf(
                    "error" to "Tema no reconocido. Usa uno de los siguientes: agua, suelo, aire, biota, resumen"
                )
            )
        }

        val prompt = when (tema.lowercase()) {
            "agua" -> """
            Analiza los siguientes datos ambientales relacionados con el AGUA:
            Incluyen calidad del agua, denuncias, políticas y supervisión ambiental.
            
            Genera un informe con:
            - Estado general del agua.
            - Posibles causas de contaminación.
            - Áreas críticas o en riesgo.
            - Recomendaciones técnicas basadas en las políticas actuales.
            
            Datos:
            ${data.toString().take(4000)}
        """.trimIndent()

            "suelo" -> """
            A continuación tienes datos ambientales del SUELO del Perú, incluyendo calidad, supervisión y políticas.
            Genera un resumen técnico con:
            - Principales hallazgos en calidad del suelo.
            - Riesgos y patrones de degradación.
            - Políticas o medidas preventivas aplicables.
            - Recomendaciones de mitigación ambiental.
            
            Datos:
            ${data.toString().take(4000)}
        """.trimIndent()

            "aire" -> """
            Analiza los indicadores de calidad del AIRE proporcionados por el OEFA.
            Describe:
            - Estado actual de la calidad del aire.
            - Fuentes principales de contaminación.
            - Zonas con mayor riesgo para la salud.
            - Recomendaciones de monitoreo y control.
            
            Datos:
            ${data.toString().take(4000)}
        """.trimIndent()

            "biota" -> """
            Analiza los siguientes datos sobre la BIOTA (flora y fauna):
            Describe:
            - Estado de la biodiversidad.
            - Impactos observados en especies o ecosistemas.
            - Políticas o acciones recomendadas para restauración ecológica.
            
            Datos:
            ${data.toString().take(4000)}
        """.trimIndent()

            "ruido" -> """
            Analiza los siguientes datos ambientales relacionados con RUIDO y VIBRACIONES:
            Incluyen niveles de ruido, supervisión y políticas ambientales.
            Genera un informe con:
            - Estado general del ruido ambiental.
            - Posibles fuentes de contaminación acústica.
            - Áreas críticas o en riesgo.
            - Recomendaciones técnicas basadas en las políticas actuales.
            
            Datos:
            ${data.toString().take(4000)}
        """.trimIndent()

            "hidrobiologia" -> """
            Analiza los siguientes datos ambientales relacionados con HIDROBIOLOGÍA:
            Incluyen indicadores hidrobiológicos, denuncias, políticas y supervisión ambiental.
            Genera un informe con:
            - Estado general de los cuerpos de agua.
            - Posibles causas de alteración hidrobiológica.
            - Áreas críticas o en riesgo.
            - Recomendaciones técnicas basadas en las políticas actuales.
             Datos:
            ${data.toString().take(4000)}
        """.trimIndent()

            "sedimentos" -> """
            Analiza los siguientes datos ambientales relacionados con SEDIMENTOS:
            Incluyen calidad de sedimentos, supervisión y políticas ambientales.
            Genera un informe con:
            - Estado general de los sedimentos.
            - Posibles causas de contaminación.
            - Áreas críticas o en riesgo.
            - Recomendaciones técnicas basadas en las políticas actuales.
            Datos:
            ${data.toString().take(4000)}
        """.trimIndent()

            else -> """
            Eres un analista ambiental del OEFA.
            Analiza el siguiente resumen general de fiscalización, supervisión y evaluación ambiental.
            Genera un informe integral con hallazgos, riesgos, y recomendaciones de gestión ambiental.
            
            Datos:
            ${data.toString().take(4000)}
        """.trimIndent()
        }

        val analisis = groqClient.analyze(prompt).block()

        return ResponseEntity.ok(
            mapOf(
                "tema" to tema.capitalize(),
                "modelo" to "Groq Llama3-70B",
                "timestamp" to Instant.now().toString(),
                "analisis" to analisis
            )
        ) as ResponseEntity<Map<String, Any>>
    }
}