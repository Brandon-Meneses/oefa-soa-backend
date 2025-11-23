package pe.infinitumsolutions.oefa_soa.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pe.infinitumsolutions.oefa_soa.service.OefaService

@Tag(
    name = "Evaluación Ambiental Detallada",
    description = "Servicios atómicos del OEFA (EAT, EAS, ISIM, IPASH) por componente ambiental."
)
@RestController
@RequestMapping("/api/evaluacion")
class EvaluacionDetalladaController(private val service: OefaService) {

    // ===========================================================
    // 🌊 EAT (Evaluación Ambiental Temprana)
    // ===========================================================
    @GetMapping("/eat/agua") fun eatAgua(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAC-COMPO-AMBIE-AGUA", limit))
    @GetMapping("/eat/aire") fun eatAire(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAC-COMPO-AMBIE-AIRE", limit))
    @GetMapping("/eat/suelo") fun eatSuelo(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAC-COMPO-AMBIE-SUELO", limit))
    @GetMapping("/eat/biota") fun eatBiota(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAC-BIOTA", limit))
    @GetMapping("/eat/ruido") fun eatRuido(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAC-RUIDO", limit))
    @GetMapping("/eat/hidrobiologia") fun eatHidro(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAC-COMPO-HIDRO", limit))
    @GetMapping("/eat/sedimentos") fun eatSedimentos(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAT-SEDIM", limit))

    // ===========================================================
    // 🌱 EAS (Evaluación Ambiental de Seguimiento)
    // ===========================================================
    @GetMapping("/eas/agua") fun easAgua(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAS-COMPO-AMBIE-AGUA", limit))
    @GetMapping("/eas/suelo") fun easSuelo(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAS-COMPO-AMBIE-SUELO-18111", limit))
    @GetMapping("/eas/hidrobiologia") fun easHidro(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAS-COMPO-HIDRO", limit))
    @GetMapping("/eas/flora-fauna") fun easFlora(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("EAS-FLORA-Y-FAUNA", limit))

    // ===========================================================
    // 🧬 ISIM (Impacto Significativo)
    // ===========================================================
    @GetMapping("/isim/agua") fun isimAgua(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("ISIM-COMPO-AMBIE-AGUA", limit))
    @GetMapping("/isim/suelo") fun isimSuelo(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("ISIM-COMPO-AMBIE-SUELO", limit))
    @GetMapping("/isim/sedimento") fun isimSedimento(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("ISIM-SEDIM", limit))
    @GetMapping("/isim/hidrobiologia") fun isimHidro(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("ISIM-HIDRO", limit))

    // ===========================================================
    // 🪨 IPASH (Pasivos Ambientales)
    // ===========================================================
    @GetMapping("/ipash/agua") fun ipashAgua(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("IPASH-COMPO-AMBIE-AGUA", limit))
    @GetMapping("/ipash/suelo") fun ipashSuelo(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("IPASH-COMPO-AMBIE-SUELO", limit))
    @GetMapping("/ipash/sedimento") fun ipashSedimento(@RequestParam(defaultValue = "20") limit: Int) =
        ResponseEntity.ok(service.safeFetch("IPASH-SEDIM", limit))
//    @GetMapping("/ipash/biota") fun ipashBiota(@RequestParam(defaultValue = "20") limit: Int) =
//        ResponseEntity.ok(service.safeFetch("IPASH-COMPO-AMBIE-BIOTA", limit))
}