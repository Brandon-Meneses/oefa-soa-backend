package pe.infinitumsolutions.oefa_soa.ia


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pe.infinitumsolutions.oefa_soa.esb.EsbOrchestrator
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    // 🔧 Utilidades internas
    // ===========================================================

    /** Normaliza valores numéricos */
    private fun parseValor(raw: String?): Double? {
        if (raw == null) return null

        val clean = raw
            .replace("<", "")           // "<0.005" → "0.005"
            .replace(",", ".")          // "7,36"   → "7.36"
            .trim()

        return clean.toDoubleOrNull()
    }

    /** Normaliza fechas */
    private fun parseFecha(raw: String?): String? {
        if (raw == null) return null

        return try {
            LocalDate.parse(raw, DateTimeFormatter.ofPattern("M/d/yy"))
                .format(DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (ex: Exception) {
            null
        }
    }

    /** Extrae solo lo necesario para la IA */
    private fun compactarDataset(rows: List<Map<String, String>>): List<Map<String, Any?>> {
        return rows.map { r ->
            mapOf(
                "parametro" to r["Parámetro"],
                "valor" to parseValor(r["Valor"]),
                "unidad" to r["Unidad de medida"],
                "fecha" to parseFecha(r["Fecha"])
            )
        }.filter { it["parametro"] != null }
    }

    /** Limpieza de respuesta Groq */
    fun extraerJsonSeguro(raw: String): String {
        val clean = raw
            .replace("```json", "")
            .replace("```", "")
            .replace("“", "\"")
            .replace("”", "\"")

        val start = clean.indexOf("<JSON>")
        val end = clean.indexOf("</JSON>")

        if (start == -1 || end == -1) {
            throw RuntimeException("La IA no devolvió JSON válido.\nRAW:\n$raw")
        }

        return clean.substring(start + 6, end).trim()
    }

    // ===========================================================
    // 🔍 ANÁLISIS TEMÁTICO UNIFICADO
    // ===========================================================
    @Operation(
        summary = "Genera un análisis IA según el tema ambiental",
        description = "El ESB compila la data del OEFA y la IA genera un dashboard analítico estructurado."
    )
    @GetMapping("/analizar/{tema}")
    fun analizarPorTema(
        @PathVariable tema: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<Map<String, Any>> {

        // ------- Obtener data del ESB -------
        val dataset = when (tema.lowercase()) {
            "agua" -> orchestrator.obtenerResumenAgua(limit)
            "suelo" -> orchestrator.obtenerResumenSuelo(limit)
            "aire" -> orchestrator.obtenerResumenAire(limit)
            "biota" -> orchestrator.obtenerResumenBiota(limit)
            "ruido" -> orchestrator.obtenerResumenRuido(limit)
            "hidrobiologia" -> orchestrator.obtenerResumenHidrobiologia(limit)
            "sedimentos" -> orchestrator.obtenerResumenSedimentos(limit)
            "resumen", "general" -> orchestrator.obtenerResumenAmbiental(limit)
            else -> return ResponseEntity.badRequest().body(
                mapOf("error" to "Tema no reconocido: $tema")
            )
        }

        // Extraer primera tabla numérica disponible
        val tabla = (dataset["indicadores"] as? Map<*, *>)?.values?.firstOrNull()
        val rows = (tabla as? Map<*, *>)?.get("rows") as? List<Map<String, String>> ?: emptyList()

        val datosCompactados = compactarDataset(rows)

        // ===========================================================
        // 🧠 PROMPT INTELIGENTE Y ROBUSTO – SIN CAMBIAR BACKEND
        // ===========================================================
        val prompt = """
Eres un motor de analítica ambiental especializado en datos del OEFA.

Vas a analizar datos del tema: "$tema".

Los datos han sido normalizados y contienen:
- parámetro
- valor (numérico)
- unidad
- fecha normalizada

DATA PROCESADA:
${jacksonObjectMapper().writeValueAsString(datosCompactados).take(6000)}

Debes devolver EXCLUSIVAMENTE un JSON válido con este formato:

<JSON>
{
  "tema": "$tema",
  "descripcion": "string",
  "dashboard": {
    "metricas_clave": {
      "nombre_parametro": "string",
      "promedio": number | null,
      "min": number | null,
      "max": number | null
    },
    "series_temporales": [
      {
        "nombre": "string",
        "unidad": "string",
        "data": [
          {"fecha": "YYYY-MM-DD", "valor": number}
        ]
      }
    ],
    "top_parametros": [
      {"nombre": "string", "valor": number}
    ],
    "comparaciones": [
      {"parametro": "string", "unidad": "string", "min": number, "max": number, "promedio": number}
    ]
  }
}
</JSON>

REGLAS:
- Responde SOLO dentro de <JSON></JSON>.
- No inventes datos.
- Usa únicamente los valores numéricos provistos.
- SI una serie temporal tiene un solo dato, genera puntos sintéticos agregando 1 o 2 fechas posteriores,
  copiando exactamente el mismo valor. NO cambies el valor real.
- Genera máximo 3 puntos por serie sintética.
- Mantén la fecha original como primer punto.
- Formato de fecha: YYYY-MM-DD.
""".trimIndent()

        // ------- Llamar IA -------
        val raw = groqClient.analyze(prompt).block() ?: ""

        return try {
            val jsonText = extraerJsonSeguro(raw)
            val parsed = jacksonObjectMapper().readValue<Map<String, Any>>(jsonText)
            ResponseEntity.ok(parsed)
        } catch (ex: Exception) {
            ResponseEntity.ok(
                mapOf(
                    "error" to "Respuesta IA inválida",
                    "raw" to raw,
                    "exception" to ex.message
                )
            )
        } as ResponseEntity<Map<String, Any>>
    }
}
