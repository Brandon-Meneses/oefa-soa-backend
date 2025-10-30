package pe.infinitumsolutions.oefa_soa.ia

import org.springframework.stereotype.Service
import pe.infinitumsolutions.oefa_soa.esb.EsbOrchestrator
import reactor.core.publisher.Mono

@Service
class IaService(
    private val groqClient: GroqClient,
    private val orchestrator: EsbOrchestrator
) {
    fun analizarResumenGeneral(limit: Int = 10): Mono<Map<String, Any>> {
        val resumen = orchestrator.obtenerResumenAmbiental(limit)
        val prompt = """
            You are an environmental analyst. 
            Analyze the following structured environmental data from Peru's OEFA:
            ${resumen.toString()}
            
            Provide a concise summary (max 3 paragraphs) in Spanish explaining:
            - Main environmental trends.
            - Areas of concern or improvement.
            - Possible causes or context.
        """.trimIndent()

        return groqClient.analyze(prompt).map { analysis ->
            mapOf("type" to "Resumen Ambiental", "analysis" to analysis)
        }
    }

    fun analizarPorTema(tema: String, limit: Int = 10): Mono<Map<String, Any>> {
        val data = when (tema.lowercase()) {
            "agua" -> orchestrator.obtenerResumenAgua(limit)
            "suelo" -> orchestrator.obtenerResumenSuelo(limit)
            else -> orchestrator.obtenerResumenAmbiental(limit)
        }

        val prompt = """
            Analiza los datos ambientales del tema '$tema' proporcionados por OEFA:
            ${data.toString()}
            
            Resume los hallazgos más relevantes en español, destacando patrones, riesgos y oportunidades de mejora.
        """.trimIndent()

        return groqClient.analyze(prompt).map { analysis ->
            mapOf("type" to "Análisis temático: $tema", "analysis" to analysis)
        }
    }
}