package pe.infinitumsolutions.oefa_soa.service


import org.springframework.stereotype.Service
import pe.infinitumsolutions.oefa_soa.oefa.JunarClient
import pe.infinitumsolutions.oefa_soa.oefa.dto.DataTable

@Service
class OefaService(private val client: JunarClient) {

    // ========================================================
    // 🔹 FISCALIZACIÓN AMBIENTAL
    // ========================================================

    fun pedidosFiscalia(limit: Int = 20, offset: Int = 0): DataTable =
        client.fetchDataStream("PEDID-FISCA-2019-61940", limit, offset)

    fun denunciasSinada(limit: Int = 20, offset: Int = 0): DataTable =
        client.fetchDataStream("DENUN-SINAD-61293", limit, offset)

    fun resolucionesFinales(limit: Int = 20, offset: Int = 0): DataTable =
        client.fetchDataStream("RESOL-CON-MULTA-FIRME", limit, offset)

    fun expedientesResueltos(limit: Int = 20, offset: Int = 0): DataTable =
        client.fetchDataStream("EXPED-RESUE-15640", limit, offset)

    fun actosAdministrativos(limit: Int = 20, offset: Int = 0): DataTable =
        client.fetchDataStream("REGIS-ACTOS-ADMIN-96376", limit, offset)

    fun resolucionesDirectorales(limit: Int = 20, offset: Int = 0): DataTable =
        client.fetchDataStream("RESOL-DIREC-50309", limit, offset)

    fun resolucionesSubdirectorales(limit: Int = 20, offset: Int = 0): DataTable =
        client.fetchDataStream("RESOL-SUBDI", limit, offset)

    fun resolucionesConMulta(limit: Int = 20, offset: Int = 0): DataTable =
        client.fetchDataStream("DENUN-SINAD", limit, offset)


    // ========================================================
    // 🔹 SUPERVISIÓN AMBIENTAL
    // ========================================================

    fun informesSupervision(limit: Int = 20): DataTable =
        client.fetchDataStream("INFOR-DE-LA-COORD-AGRIC", limit)

    fun medidasAdministrativas(limit: Int = 20): DataTable =
        client.fetchDataStream("MEDID-ADMIN-DE-LAS-DIREC", limit)

    fun informesDireccionSupervision(limit: Int = 20): DataTable =
        client.fetchDataStream("INFOR-DE-LA-DIREC-28304", limit)


    // ========================================================
    // 🔹 EVALUACIÓN AMBIENTAL
    // ========================================================

    // --- AGUA ---
    fun indicadoresGenerico(codigos: Map<String, String>, limit: Int = 20): Map<String, Any?> {
        return codigos.mapValues { (_, codigo) ->
            val raw = safeFetch(codigo, limit)
            analizarDataset(dataTableToMap(raw))
        }
    }

    // --- AGUA ---
    fun indicadoresAgua(limit: Int = 20): Map<String, Any?> = indicadoresGenerico(
        mapOf(
            "EAT" to "EAC-COMPO-AMBIE-AGUA",
            "EAS" to "EAS-COMPO-AMBIE-AGUA",
            "ISIM" to "ISIM-COMPO-AMBIE-AGUA",
            "IPASH" to "IPASH-COMPO-AMBIE-AGUA"
        ), limit
    )

    // --- SUELO ---
    fun indicadoresSuelo(limit: Int = 20): Map<String, Any?> = indicadoresGenerico(
        mapOf(
            "EAT" to "EAC-COMPO-AMBIE-SUELO",
            "EAS" to "EAS-COMPO-AMBIE-SUELO-18111",
            "ISIM" to "ISIM-COMPO-AMBIE-SUELO",
            "IPASH" to "IPASH-COMPO-AMBIE-SUELO"
        ), limit
    )

    // --- AIRE ---
    fun indicadoresAire(limit: Int = 20): Map<String, Any?> = indicadoresGenerico(
        mapOf(
            "EAT" to "EAC-COMPO-AMBIE-AIRE"
        ), limit
    )

    // --- BIOTA ---
    fun indicadoresBiota(limit: Int = 20): Map<String, Any?> = indicadoresGenerico(
        mapOf(
            "EAT" to "EAC-BIOTA",
            "IPASH" to "IPASH-COMPO-AMBIE-BIOTA"
        ), limit
    )

    // --- RUIDO Y VIBRACIONES ---
    fun indicadoresRuido(limit: Int = 20): Map<String, Any?> = indicadoresGenerico(
        mapOf("EAT" to "EAC-RUIDO"), limit
    )

    // --- HIDROBIOLOGÍA ---
    fun indicadoresHidrobiologia(limit: Int = 20): Map<String, Any?> = indicadoresGenerico(
        mapOf(
            "EAT" to "EAC-COMPO-HIDRO",
            "EAS" to "EAS-COMPO-HIDRO",
            "ISIM" to "ISIM-HIDRO"
        ), limit
    )

    // --- SEDIMENTOS ---
    fun indicadoresSedimento(limit: Int = 20): Map<String, Any?> = indicadoresGenerico(
        mapOf(
            "EAT" to "EAT-SEDIM",
            "ISIM" to "ISIM-SEDIM",
            "IPASH" to "IPASH-SEDIM"
        ), limit
    )

    // --- FLORA Y FAUNA ---
    fun indicadoresFloraFauna(limit: Int = 20): Map<String, Any?> = indicadoresGenerico(
        mapOf("EAS" to "EAS-FLORA-Y-FAUNA"), limit
    )


    // ========================================================
    // 🔹 POLÍTICAS Y ESTRATEGIAS
    // ========================================================

    fun proyectosNormativos(limit: Int = 20): DataTable =
        client.fetchDataStream("PROYE-NORMA-OEFA", limit)

    fun proyectosNormativosRecientes(limit: Int = 20): DataTable =
        client.fetchDataStream("PROYE-NORMA-OEFA-64027", limit)

    fun proyectosExternos(limit: Int = 20): DataTable =
        client.fetchDataStream("PROYE-NORMA-EXTER", limit)

    fun proyectosExternosRecientes(limit: Int = 20): DataTable =
        client.fetchDataStream("PROYE-NORMA-EXTER-65772", limit)

    fun actividadesAFA(limit: Int = 20): DataTable =
        client.fetchDataStream("ACTIV-AFA-36113", limit)


    // ========================================================
    // 🔹 UTILIDAD DE SEGURIDAD Y RESILIENCIA
    // ========================================================

    /**
     * Envuelve una llamada segura al cliente OEFA.
     * Si un GUID no existe o falla la llamada, devuelve DataTable vacío en lugar de lanzar excepción.
     */
    fun safeFetch(guid: String, limit: Int = 20, offset: Int = 0): DataTable {
        return try {
            client.fetchDataStream(guid, limit, offset)
        } catch (ex: Exception) {
            println("⚠️ Error al obtener datos del guid $guid: ${ex.message}")
            DataTable(guid, "Error: ${ex.message}", emptyList(), emptyList())
        }
    }

    fun analizarDataset(data: Map<String, Any?>): Map<String, Any?> {
        val rows = (data["rows"] as? List<Map<String, String>>) ?: emptyList()

        // Detectar campos numéricos (aunque estén como String)
        val camposNumericos = mutableSetOf<String>()
        rows.forEach { row ->
            row.forEach { (key, value) ->
                if (value.toDoubleOrNull() != null) camposNumericos.add(key)
            }
        }

        val tipo = when {
            camposNumericos.isEmpty() -> "cualitativo"
            camposNumericos.size < (rows.firstOrNull()?.keys?.size ?: 1) / 2 -> "mixto"
            else -> "cuantitativo"
        }

        val graficable = tipo != "cualitativo"

        return data + mapOf(
            "tipo" to tipo,
            "graficable" to graficable,
            "camposNumericos" to camposNumericos.toList()
        )
    }

    private fun dataTableToMap(table: DataTable?): Map<String, Any?> {
        if (table == null) return emptyMap()

        return mapOf(
            "title" to table.title,
            "description" to table.description,
            "headers" to table.headers,
            "rows" to table.rows
        )
    }
}