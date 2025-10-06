package pe.infinitumsolutions.oefa_soa.oefa.dto

data class JunarResponse(
    val title: String? = null,
    val description: String? = null,
    val result: ResultData? = null
)

data class ResultData(
    val fLength: Int? = null,
    val fType: String? = null,
    val fTimestamp: Long? = null,
    val fArray: List<JunarCell> = emptyList(),
    val fRows: Int = 0,
    val fCols: Int = 0
)

data class JunarCell(
    val fStr: String? = null,
    val fHeader: Boolean? = null,
    val fType: String? = null
)

/** Modelo canónico simplificado: tabla con headers + filas (mapa columna->valor). */
data class DataTable(
    val title: String? = null,
    val description: String? = null,
    val headers: List<String> = emptyList(),
    val rows: List<Map<String, String>> = emptyList()
)