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
        if (raw.isNullOrBlank()) return null
        val clean = raw
            .replace("<", "")           // "<0.005" → "0.005"
            .replace(">", "")           // ">50" → "50"
            .replace(",", ".")          // "7,36" → "7.36"
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
        if (rows.isEmpty()) return emptyList()

        val keys = rows.first().keys

        // 1. Buscamos el nombre del parámetro (Especie, Taxón, etc.)
        val keyParametro = keys.firstOrNull {
            it.equals("Especie", ignoreCase = true) ||
                    it.equals("Nombre común", ignoreCase = true) ||
                    it.equals("Taxon", ignoreCase = true) ||
                    it.equals("Parámetro", ignoreCase = true) ||
                    it.equals("Parametro", ignoreCase = true) ||
                    it.equals("Componente ambiental", ignoreCase = true)
        } ?: "Especie" // Default para Flora y Fauna

        // 2. Buscamos una columna de valor numérico
        val keyValor = keys.firstOrNull {
            it.equals("Valor", ignoreCase = true) ||
                    it.equals("Abundancia", ignoreCase = true) ||
                    it.equals("Resultado", ignoreCase = true) ||
                    it.equals("Individuos", ignoreCase = true)
        }

        // 3. Fecha y Unidad
        val keyFecha = keys.firstOrNull { it.equals("Fecha", ignoreCase = true) || it.equals("FECHA", ignoreCase = false) } ?: "Fecha"
        val keyUnidad = keys.firstOrNull { it.contains("Unidad", ignoreCase = true) } ?: "Unidad de medida"

        return rows.map { r ->
            val nombreParam = r[keyParametro] ?: "Desconocido"

            // LÓGICA HÍBRIDA:
            // Si encontramos columna numérica, parseamos.
            // Si NO (ej: Flora y Fauna), asumimos que es un avistamiento = 1.0
            var valorNum: Double? = null
            var unidad = "Conteo"

            if (keyValor != null) {
                valorNum = parseValor(r[keyValor])
                unidad = r[keyUnidad] ?: ""
            }

            // Fallback para datasets cualitativos (Flora y Fauna)
            if (valorNum == null && keyValor == null) {
                valorNum = 1.0 // Cada fila cuenta como 1 avistamiento
                unidad = "Avistamientos"
            }

            if (valorNum != null) {
                mapOf(
                    "parametro" to nombreParam,
                    "valor" to valorNum,
                    "unidad" to unidad,
                    "fecha" to parseFecha(r[keyFecha])
                )
            } else {
                null
            }
        }.filterNotNull()
    }

    /** Limpieza de respuesta Groq */
    // En tu IaController.kt
    fun extraerJsonSeguro(raw: String): String {
        // Estrategia 1: Buscar etiquetas <JSON> (Lo que le pedimos en el prompt)
        val tagRegex = "(?i)<JSON>\\s*([\\s\\S]*?)\\s*</JSON>".toRegex()
        tagRegex.find(raw)?.let { return it.groupValues[1].trim() }

        // Estrategia 2: Buscar bloques Markdown ```json (Lo que la IA suele mandar por defecto)
        val mdRegex = "(?i)```(?:json)?\\s*([\\s\\S]*?)\\s*```".toRegex()
        mdRegex.find(raw)?.let { return it.groupValues[1].trim() }

        // Estrategia 3: Fuerza bruta (Buscar el primer '{' y el último '}')
        // Esto salva el día si la IA manda texto antes o después del JSON sin formato
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')

        if (start != -1 && end != -1 && end > start) {
            return raw.substring(start, end + 1)
        }

        // Si nada funciona, lanzamos el error con el RAW para depurar
        throw RuntimeException("No se pudo extraer JSON válido. Respuesta RAW: $raw")
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

        // ===========================================================
        // 1. Obtener DATA desde el ESB
        // ===========================================================
        val dataset = when (tema.lowercase()) {
            "agua" -> orchestrator.obtenerResumenAgua(limit)
            "suelo" -> orchestrator.obtenerResumenSuelo(limit)
            "aire" -> orchestrator.obtenerResumenAire(limit)
            "biota" -> orchestrator.obtenerResumenBiota(limit)
            "ruido" -> orchestrator.obtenerResumenRuido(limit)
            "hidrobiologia" -> orchestrator.obtenerResumenHidrobiologia(limit)
            "sedimentos" -> orchestrator.obtenerResumenSedimentos(limit)
            "flora y fauna", "florafauna" -> orchestrator.obtenerResumenFloraFauna(limit)
            "resumen", "general" -> orchestrator.obtenerResumenAmbiental(limit)
            else -> return ResponseEntity.badRequest().body(
                mapOf("error" to "Tema no reconocido: $tema")
            )
        }

        // Extraer tabla principal (indicadores)
        val tabla = (dataset["indicadores"] as? Map<*, *>)?.values?.firstOrNull()
        val rows = (tabla as? Map<*, *>)?.get("rows") as? List<Map<String, String>> ?: emptyList()

        // Normalización
        val datosCompactados = compactarDataset(rows)

        // ===========================================================
        // 2. PROMPT "CURADOR DE UI" (VERSIÓN PERMISIVA)
        // ===========================================================
        val prompt = """
        Actúa como un Experto en Visualización de Datos Ambientales.
        Tu misión es generar SIEMPRE una visualización gráfica, incluso si los valores son bajos o cero.

        CONTEXTO: Analizando "$tema".
        
        INPUT RAW: 
        ${jacksonObjectMapper().writeValueAsString(datosCompactados).take(6000)}

        ===============================================================
        REGLAS DE LÓGICA DE NEGOCIO (PERMISIVAS)
        ===============================================================
        
        1. REGLA DE "DATOS VÁLIDOS":
           - Conserva los valores 0 (cero). Un valor 0 significa "No detectado" y ES UN DATO VÁLIDO para graficar.
           - Conserva los valores negativos (ej: Potencial Redox, Índices de Suelo).
           - Solo ignora valores si son estrictamente 'null'.
           - Si la lista de entrada está VACÍA ([]), entonces define "tipo_grafico": "empty".

        2. REGLA DE SELECCIÓN DE GRÁFICO:
           - Prioridad 1 (Porcentajes): Si detectas parámetros que suman aprox 100% (ej: Arena/Arcilla/Limo), usa "pie".
           - Prioridad 2 (Tendencia): Si hay 3+ fechas DISTINTAS para un mismo parámetro, usa "line".
           - Prioridad 3 (Comparativa): Para todo lo demás (1 sola fecha, múltiples parámetros, o valores 0), usa "bar".

        3. REGLA DE "ESCALAS VISUALES" (Anti-Rotura):
           - Si hay valores gigantes (ej: 2000) mezclados con pequeños (ej: 5), NO LOS PONGAS EN EL MISMO GRÁFICO.
           - ESTRATEGIA: Selecciona para el gráfico los parámetros más relevantes que tengan escalas visualmente compatibles. 
           - Si un parámetro domina excesivamente (outlier), exclúyelo del array "chart_data" pero menciónalo en "descripcion".

        4. REGLA DE ETIQUETAS:
           - Si un nombre es muy largo (ej: "Hidrocarburos totales..."), genera una versión corta en el campo "x" (ej: "HTP") y pon el nombre real en "categoria".
           
        5. REGLA DE "CONTEO/FRECUENCIA" (Para Flora y Fauna):
           - Si los datos de entrada tienen muchos registros con valor 1.0 y misma unidad ("Avistamientos" o "Conteo"):
           - DEBES AGRUPARLOS. Suma los valores por "parámetro" (Especie/Familia).
           - Usa un gráfico "bar" o "pie" mostrando el TOP 5 de especies/familias más frecuentes.
           - En el KPI Principal, muestra el "Total de Avistamientos" (suma total) o la "Especie más común".

        ===============================================================
        ESTRUCTURA DE SALIDA JSON
        ===============================================================
        <JSON>
        {
          "tema": "$tema",
          "descripcion": "String (Resumen ejecutivo corto. Ej: 'Todos los parámetros están dentro de la norma' o 'Se detectó Aluminio elevado')",
          "dashboard": {
            "tipo_grafico": "line | bar | pie | empty",
            "razon_seleccion": "String",
            "analisis_grafico": "String (Interpretación breve y directa de los DATOS visualizados. Ej: 'Se observa un pico inusual en agosto' o 'El Aluminio representa el 80% de la muestra'). MÁXIMO 2 FRASES.",
            "kpi_principal": {
              "titulo": "String (Ej: Máx. Nivel Ruido)",
              "valor": "String (Ej: 0 dB)",
              "unidad": "String",
              "estado": "NORMAL | ALERTA | CRITICO"
            },

            // Para BAR/LINE/PIE: "x" es la etiqueta corta, "y" es el valor numérico.
            "chart_data": [
              { "x": "String", "y": number, "categoria": "String (Nombre completo)" }
            ],

            "detalles_extra": [
               { "clave": "String", "valor": "String" }
            ]
          }
        }
        </JSON>
        
        Responde ÚNICAMENTE con el JSON dentro de <JSON>.
    """.trimIndent()

        // ===========================================================
        // 3. Llamar IA
        // ===========================================================
        val raw = groqClient.analyze(prompt).block() ?: ""

        // ===========================================================
        // 4. Parseo seguro del JSON
        // ===========================================================
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

    data class ConsultaRequest(
        val pregunta: String,
        val contexto: String
    )

    @PostMapping("/consultar")
    fun consultarIA(@RequestBody request: ConsultaRequest): ResponseEntity<Map<String, String>> {
        val prompt = """
            Eres un asistente experto en medio ambiente de la OEFA.
            Estás chateando con un usuario sobre un reporte específico.
            
            CONTEXTO DEL REPORTE (DATOS REALES):
            "${request.contexto}"
            
            PREGUNTA DEL USUARIO:
            "${request.pregunta}"
            
            REGLAS:
            1. Responde basándote ÚNICAMENTE en el contexto proporcionado.
            2. Sé breve, amable y directo (máximo 3 oraciones).
            3. Si la pregunta no tiene que ver con los datos, indica amablemente que solo puedes responder sobre este reporte.
            
            Respuesta:
        """.trimIndent()

        val respuesta = groqClient.chat(prompt).block() ?: "No pude procesar tu consulta."

        // Limpieza básica por si la IA devuelve comillas extra
        val limpia = respuesta.replace("\"", "").trim()

        return ResponseEntity.ok(mapOf("respuesta" to limpia))
    }


}
