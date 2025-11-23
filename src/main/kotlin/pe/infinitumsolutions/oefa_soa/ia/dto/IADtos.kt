package pe.infinitumsolutions.oefa_soa.ia.dto

data class GraficoIA(
    val id: String,
    val titulo: String,
    val indicador: String,       // EAT / EAS / ISIM / IPASH, etc.
    val parametro: String,       // Ej: "Especie" o "Punto de monitoreo"
    val unidad: String?,         // Ej: "Individuos/muestra"
    val justificacion: String    // Por qué este gráfico es relevante
)

data class AnalisisIA(
    val resumenGeneral: String,
    val hallazgosClave: List<String>,
    val riesgosPotenciales: List<String>,
    val recomendaciones: List<String>,
    val graficosSugeridos: List<GraficoIA>
)

data class DashboardIAResponse(
    val tema: String,
    val analisis: AnalisisIA
)