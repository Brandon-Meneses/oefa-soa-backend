package pe.infinitumsolutions.oefa_soa.oefa

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import pe.infinitumsolutions.oefa_soa.oefa.dto.DataTable
import pe.infinitumsolutions.oefa_soa.oefa.dto.JunarResponse
import reactor.core.publisher.Mono

@Component
class JunarClient(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${oefa.base-url}") private val baseUrl: String,
    @Value("\${oefa.auth-key}") private val authKey: String
) {
    private val webClient = webClientBuilder.baseUrl(baseUrl).build()

    @Retryable(
        include = [Exception::class],
        maxAttemptsExpression = "\${retry.oefa.max-attempts}",
        backoff = Backoff(delayExpression = "\${retry.oefa.backoff-ms}")
    )
    @Cacheable(cacheNames = ["oefa"], key = "#guid + ':' + #limit + ':' + #offset")
    fun fetchDataStream(guid: String, limit: Int = 50, offset: Int = 0): DataTable {
        require(authKey.isNotBlank()) { "OEFA auth-key no configurado" }

        val uri = "/datastreams/$guid/data.json?auth_key=$authKey&limit=$limit&offset=$offset"

        val resp = webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(JunarResponse::class.java)
            .onErrorResume { Mono.error(RuntimeException("Error llamando a OEFA: ${it.message}", it)) }
            .block() ?: throw RuntimeException("Respuesta vacía de OEFA")

        return parseToTable(resp)
    }

    private fun parseToTable(r: JunarResponse): DataTable {
        val res = r.result ?: return DataTable(r.title, r.description, emptyList(), emptyList())
        val cols = res.fCols
        if (cols <= 0) return DataTable(r.title, r.description, emptyList(), emptyList())

        val headers = res.fArray
            .filter { it.fHeader == true }
            .map { it.fStr?.trim().orEmpty() }

        // Resto de celdas: filas en orden row-major
        val dataCells = res.fArray.filter { it.fHeader != true }.map { it.fStr ?: "" }
        val rows = mutableListOf<Map<String, String>>()

        if (headers.isEmpty() || dataCells.isEmpty()) {
            return DataTable(r.title, r.description, headers, emptyList())
        }

        dataCells.chunked(cols).forEach { chunk ->
            val row = headers.indices.associate { i ->
                val key = headers.getOrElse(i) { "COL_$i" }
                val value = chunk.getOrElse(i) { "" }.trim()
                key to value
            }
            rows += row
        }
        return DataTable(r.title, r.description, headers, rows)
    }
}