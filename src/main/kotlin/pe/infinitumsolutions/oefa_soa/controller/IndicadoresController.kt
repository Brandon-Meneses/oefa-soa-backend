package pe.infinitumsolutions.oefa_soa.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pe.infinitumsolutions.oefa_soa.service.OefaService

@Tag(
    name = "Evaluación Ambiental",
    description = "Servicios del OEFA para indicadores ambientales técnicos (EAT, EAS, ISIM, IPASH) por componente: agua, aire, suelo, biota, ruido, hidrobiología y sedimento."
)
@RestController
@RequestMapping("/api/indicadores")
class IndicadoresController(private val service: OefaService) {

    // --- EAT ---
    @GetMapping("/eat/agua") fun eatAgua(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAC-COMPO-AMBIE-AGUA", limit)
    @GetMapping("/eat/aire") fun eatAire(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAC-COMPO-AMBIE-AIRE", limit)
    @GetMapping("/eat/suelo") fun eatSuelo(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAC-COMPO-AMBIE-SUELO", limit)
    @GetMapping("/eat/biota") fun eatBiota(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAC-BIOTA", limit)
    @GetMapping("/eat/ruido") fun eatRuido(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAC-RUIDO", limit)
    @GetMapping("/eat/hidrobiologia") fun eatHidrobiologia(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAC-COMPO-HIDRO", limit)
    @GetMapping("/eat/sedimento") fun eatSedimento(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAT-SEDIM", limit)

    // --- EAS ---
    @GetMapping("/eas/agua") fun easAgua(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAS-COMPO-AMBIE-AGUA", limit)
    @GetMapping("/eas/suelo") fun easSuelo(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAS-COMPO-AMBIE-SUELO-18111", limit)
    @GetMapping("/eas/hidrobiologia") fun easHidro(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAS-COMPO-HIDRO", limit)
    @GetMapping("/eas/flora-fauna") fun easFloraFauna(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("EAS-FLORA-Y-FAUNA", limit)

    // --- ISIM ---
    @GetMapping("/isim/agua") fun isimAgua(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("ISIM-COMPO-AMBIE-AGUA", limit)
    @GetMapping("/isim/suelo") fun isimSuelo(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("ISIM-COMPO-AMBIE-SUELO", limit)
    @GetMapping("/isim/sedimento") fun isimSedimento(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("ISIM-SEDIM", limit)
    @GetMapping("/isim/hidrobiologia") fun isimHidro(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("ISIM-HIDRO", limit)

    // --- IPASH ---
    @GetMapping("/ipash/agua") fun ipashAgua(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("IPASH-COMPO-AMBIE-AGUA", limit)
    @GetMapping("/ipash/suelo") fun ipashSuelo(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("IPASH-COMPO-AMBIE-SUELO", limit)
    //@GetMapping("/ipash/biota") fun ipashBiota(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("IPASH-COMPO-AMBIE-BIOTA", limit)
    @GetMapping("/ipash/sedimento") fun ipashSedimento(@RequestParam(defaultValue = "20") limit: Int) = service.safeFetch("IPASH-SEDIM", limit)
}