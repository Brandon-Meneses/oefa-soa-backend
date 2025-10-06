package pe.infinitumsolutions.oefa_soa.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pe.infinitumsolutions.oefa_soa.service.OefaService

@Tag(
    name = "Fiscalización Ambiental",
    description = "Servicios del OEFA relacionados con denuncias, pedidos del Ministerio Público, resoluciones y actos administrativos."
)
@RestController
@RequestMapping("/api/fiscalizacion")
class FiscalizacionController(private val service: OefaService) {

    // ===========================================================
    // 🧾 PEDIDOS DE FISCALÍA (2019–2025)
    // ===========================================================
    @Operation(
        summary = "Obtener pedidos de fiscalía ambiental",
        description = "Devuelve los pedidos solicitados por las fiscalías ambientales y el Ministerio Público al OEFA (2019–2025)."
    )
    @GetMapping("/pedidos-fiscalia")
    fun pedidosFiscalia(
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.pedidosFiscalia(limit, offset))


    // ===========================================================
    // 📢 DENUNCIAS AMBIENTALES (SINADA)
    // ===========================================================
    @Operation(
        summary = "Obtener denuncias ambientales (SINADA)",
        description = "Devuelve las denuncias ambientales registradas en el Sistema Nacional de Denuncias Ambientales (SINADA)."
    )
    @GetMapping("/denuncias")
    fun denunciasSinada(
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.denunciasSinada(limit, offset))


    // ===========================================================
    // ⚖️ RESOLUCIONES CON MULTA FIRME
    // ===========================================================
    @Operation(
        summary = "Obtener resoluciones con multa firme",
        description = "Devuelve las resoluciones administrativas con multa firme emitidas por el OEFA."
    )
    @GetMapping("/resoluciones-multa")
    fun resolucionesMulta(
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.resolucionesConMulta(limit, offset))


    // ===========================================================
    // 🗂️ EXPEDIENTES RESUELTOS (TRIBUNAL DE FISCALIZACIÓN)
    // ===========================================================
    @Operation(
        summary = "Obtener expedientes resueltos",
        description = "Devuelve los expedientes ambientales resueltos por el Tribunal de Fiscalización Ambiental del OEFA."
    )
    @GetMapping("/expedientes")
    fun expedientesResueltos(
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.expedientesResueltos(limit, offset))


    // ===========================================================
    // 🧩 REGISTRO DE ACTOS ADMINISTRATIVOS
    // ===========================================================
    @Operation(
        summary = "Obtener actos administrativos",
        description = "Devuelve el registro de actos administrativos emitidos por el OEFA en materia ambiental."
    )
    @GetMapping("/actos-administrativos")
    fun actosAdministrativos(
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.actosAdministrativos(limit, offset))


    // ===========================================================
    // 🧷 RESOLUCIONES DIRECTORALES
    // ===========================================================
    @Operation(
        summary = "Obtener resoluciones directorales",
        description = "Devuelve las resoluciones directorales emitidas por el OEFA en el marco de la fiscalización ambiental."
    )
    @GetMapping("/resoluciones-directorales")
    fun resolucionesDirectorales(
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.resolucionesDirectorales(limit, offset))


    // ===========================================================
    // 🧷 RESOLUCIONES SUBDIRECTORALES
    // ===========================================================
    @Operation(
        summary = "Obtener resoluciones subdirectorales",
        description = "Devuelve las resoluciones subdirectorales emitidas en procesos de fiscalización ambiental."
    )
    @GetMapping("/resoluciones-subdirectorales")
    fun resolucionesSubdirectorales(
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.resolucionesSubdirectorales(limit, offset))


    // ===========================================================
    // 📑 RESOLUCIONES FINALES CON MULTA (HISTÓRICO)
    // ===========================================================
    @Operation(
        summary = "Obtener resoluciones finales (histórico)",
        description = "Devuelve resoluciones finales con multa del histórico de datos abiertos del OEFA."
    )
    @GetMapping("/resoluciones-finales")
    fun resolucionesFinales(
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.resolucionesFinales(limit, offset))
}