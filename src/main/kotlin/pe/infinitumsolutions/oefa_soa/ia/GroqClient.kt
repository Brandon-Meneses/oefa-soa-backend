package pe.infinitumsolutions.oefa_soa.ia


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ChatMessage(val role: String, val content: String)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.3,
    val stream: Boolean = false
)

@Component
class GroqClient(
    @Value("\${groq.api.url}") private val apiUrl: String,
    @Value("\${groq.api.key}") private val apiKey: String,
    @Value("\${groq.model}") private val model: String
) {

    private val webClient = WebClient.builder()
        .baseUrl(apiUrl)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun analyze(prompt: String): Mono<String> {
        val messages = listOf(
            ChatMessage("system", "You are an environmental analyst specialized in sustainability and ecological management."),
            ChatMessage("user", prompt.take(4000))
        )

        val body = ChatRequest(
            model = model,
            messages = messages,
            temperature = 0.3,
            stream = false
        )

        println("🛰️ Sending to Groq:\n${body}")

        return webClient.post()
            .uri("/chat/completions")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map::class.java)
            .map { resp ->
                val choices = resp["choices"] as? List<*>
                val message = (choices?.firstOrNull() as? Map<*, *>)?.get("message") as? Map<*, *>
                message?.get("content")?.toString() ?: "⚠️ No content returned from Groq"
            }
            .onErrorResume { e ->
                e.printStackTrace()
                Mono.just("⚠️ Error calling Groq API: ${e.message}")
            }
    }
}
