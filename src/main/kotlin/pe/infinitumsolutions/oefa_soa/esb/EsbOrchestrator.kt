package pe.infinitumsolutions.oefa_soa.esb

import org.springframework.stereotype.Service
import pe.infinitumsolutions.oefa_soa.service.OefaService

@Service
class EsbOrchestrator(private val service: OefaService) {

    /**
     * Orquesta datos de Fiscalización, Supervisión y Evaluación Ambiental
     * para generar un resumen integral del estado ambiental nacional.
     */
    fun obtenerResumenAmbiental(limit: Int = 10): Map<String, Any?> {
        val fiscalizacion = mapOf(
            "denuncias" to service.safeFetch("DENUN-SINAD-61293", limit),
            "pedidosFiscalia" to service.safeFetch("PEDID-FISCA-2019-61940", limit),
            "resoluciones" to service.safeFetch("RESOL-CON-MULTA-FIRME", limit)
        )

        val supervision = mapOf(
            "informes" to service.safeFetch("INFOR-DE-LA-COORD-AGRIC", limit),
            "medidas" to service.safeFetch("MEDID-ADMIN-DE-LAS-DIREC", limit),
            "direccion" to service.safeFetch("INFOR-DE-LA-DIREC-28304", limit)
        )

        val evaluacion = mapOf(
            "agua" to service.indicadoresAgua(limit),
            "aire" to service.indicadoresAire(limit),
            "suelo" to service.indicadoresSuelo(limit),
            "biota" to service.indicadoresBiota(limit),
            "ruido" to service.indicadoresRuido(limit),
            "hidrobiologia" to service.indicadoresHidrobiologia(limit),
            "sedimento" to service.indicadoresSedimento(limit),
            "floraFauna" to service.indicadoresFloraFauna(limit)
        )

        val politicas = mapOf(
            "proyectosNormativos" to service.safeFetch("PROYE-NORMA-OEFA", limit),
            "actividadesAFA" to service.safeFetch("ACTIV-AFA-36113", limit)
        )

        return mapOf(
            "timestamp" to java.time.Instant.now().toString(),
            "resumen" to "Integración OEFA SOA",
            "fiscalizacion" to fiscalizacion,
            "supervision" to supervision,
            "evaluacion" to evaluacion,
            "politicas" to politicas
        )
    }

    /**
     * Ejemplo de orquestación temática: agua
     */
    fun obtenerResumenAgua(limit: Int = 10): Map<String, Any?> {
        val denuncias = service.safeFetch("DENUN-SINAD-61293", limit)
        val calidad = service.indicadoresAgua(limit)
        val politicas = service.safeFetch("PROYE-NORMA-OEFA", limit)

        return mapOf(
            "tema" to "Agua",
            "descripcion" to "Datos integrados sobre calidad del agua y gestión ambiental.",
            "indicadores" to calidad,
            "denunciasRelacionadas" to denuncias,
            "normativas" to politicas
        )
    }

    // ===========================================================
    // 🌱 SUELO
    // ===========================================================
    fun obtenerResumenSuelo(limit: Int = 10): Map<String, Any?> {
        val supervision = service.safeFetch("INFOR-DE-LA-COORD-AGRIC", limit)
        val calidad = service.indicadoresSuelo(limit)
        val politicas = service.safeFetch("PROYE-NORMA-EXTER", limit)

        return mapOf(
            "tema" to "Suelo",
            "descripcion" to "Información consolidada sobre monitoreo y regulación del suelo.",
            "indicadores" to calidad,
            "supervision" to supervision,
            "politicas" to politicas
        )
    }

    // ===========================================================
    // 🌫️ AIRE
    // ===========================================================
    fun obtenerResumenAire(limit: Int = 10): Map<String, Any?> {
        val calidad = service.indicadoresAire(limit)
        val supervision = service.safeFetch("INFOR-DE-LA-DIREC-28304", limit)
        val politicas = service.safeFetch("PROYE-NORMA-OEFA", limit)

        return mapOf(
            "tema" to "Aire",
            "descripcion" to "Indicadores y políticas sobre la calidad del aire y emisiones en el territorio nacional.",
            "indicadores" to calidad,
            "supervision" to supervision,
            "politicas" to politicas
        )
    }

    // ===========================================================
    // 🐠 BIOTA
    // ===========================================================
    fun obtenerResumenBiota(limit: Int = 10): Map<String, Any?> {
        val indicadores = service.indicadoresBiota(limit)
        val fiscalizacion = service.safeFetch("DENUN-SINAD-61293", limit)
        val politicas = service.safeFetch("PROYE-NORMA-OEFA", limit)

        return mapOf(
            "tema" to "Biota",
            "descripcion" to "Información sobre flora, fauna y ecosistemas afectados por actividades humanas.",
            "indicadores" to indicadores,
            "denunciasRelacionadas" to fiscalizacion,
            "politicas" to politicas
        )
    }
    //ruido
    fun obtenerResumenRuido(limit: Int = 10): Map<String, Any?> {
        val indicadores = service.indicadoresRuido(limit)
        val supervision = service.safeFetch("INFOR-DE-LA-COORD-AGRIC", limit)
        val politicas = service.safeFetch("PROYE-NORMA-OEFA", limit)

        return mapOf(
            "tema" to "Ruido y Vibraciones",
            "descripcion" to "Información sobre monitoreo ambiental de ruido y vibraciones en distintas zonas del país.",
            "indicadores" to indicadores,
            "supervision" to supervision,
            "politicas" to politicas
        )
    }

    // ===========================================================
    // 🧬 HIDROBIOLOGÍA
    // ===========================================================
    fun obtenerResumenHidrobiologia(limit: Int = 10): Map<String, Any?> {
        val indicadores = service.indicadoresHidrobiologia(limit)
        val denuncias = service.safeFetch("DENUN-SINAD-61293", limit)
        val politicas = service.safeFetch("PROYE-NORMA-EXTER", limit)

        return mapOf(
            "tema" to "Hidrobiología",
            "descripcion" to "Evaluaciones sobre cuerpos de agua, ecosistemas acuáticos y biodiversidad hidrobiológica.",
            "indicadores" to indicadores,
            "denunciasRelacionadas" to denuncias,
            "politicas" to politicas
        )
    }

    // ===========================================================
    // 🪨 SEDIMENTOS
    // ===========================================================
    fun obtenerResumenSedimentos(limit: Int = 10): Map<String, Any?> {
        val indicadores = service.indicadoresSedimento(limit)
        val supervision = service.safeFetch("INFOR-DE-LA-DIREC-28304", limit)
        val politicas = service.safeFetch("PROYE-NORMA-OEFA", limit)

        return mapOf(
            "tema" to "Sedimentos",
            "descripcion" to "Datos sobre la calidad de sedimentos, acumulación de contaminantes y monitoreo ambiental.",
            "indicadores" to indicadores,
            "supervision" to supervision,
            "politicas" to politicas
        )
    }

    // ===========================================================
    // Flora y Fauna
    // ===========================================================
    fun obtenerResumenFloraFauna(limit: Int = 10): Map<String, Any?> {
        val indicadores = service.indicadoresFloraFauna(limit)
        val fiscalizacion = service.safeFetch("DENUN-SINAD-61293", limit)
        val politicas = service.safeFetch("PROYE-NORMA-EXTER", limit)
        return mapOf(
            "tema" to "Flora y Fauna",
            "descripcion" to "Información sobre la biodiversidad y conservación de especies en el territorio nacional.",
            "indicadores" to indicadores,
            "denunciasRelacionadas" to fiscalizacion,
            "politicas" to politicas
        )
    }
}