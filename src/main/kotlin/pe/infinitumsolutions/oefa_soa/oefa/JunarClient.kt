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
    fun fetchDataStream(guid: String, limit: Int = 200, offset: Int = 0): DataTable {
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
        val rowsCount = res.fRows
        val array = res.fArray

        if (cols <= 0 || rowsCount <= 0 || array.isEmpty()) {
            return DataTable(r.title, r.description, emptyList(), emptyList())
        }

        // ✔ CORRECCIÓN: Los headers son siempre los primeros `cols` elementos
        val headers = array.take(cols).map { it.fStr?.trim().orEmpty() }

        // ✔ El resto de celdas son los registros
        val rawCells = array.drop(cols).map { it.fStr?.trim().orEmpty() }

        // ✔ Reconstrucción real de filas
        val rows = rawCells.chunked(cols).take(rowsCount).map { chunk ->
            headers.indices.associate { i ->
                headers[i] to chunk.getOrElse(i) { "" }
            }
        }

        return DataTable(
            title = r.title,
            description = r.description,
            headers = headers,
            rows = rows
        )
    }
}