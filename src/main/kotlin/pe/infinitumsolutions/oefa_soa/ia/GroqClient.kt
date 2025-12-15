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

    /**
     * Método para Análisis de Datos (Dashboard)
     * Usa un System Prompt específico de analista ambiental.
     */
    fun analyze(prompt: String): Mono<String> {
        val systemRole = "You are an environmental analyst specialized in sustainability and ecological management."
        // Aumentamos el límite de caracteres porque el JSON de entrada puede ser grande
        return sendRequest(systemRole, prompt.take(4000))
    }

    /**
     * Método para Chat / Consultas (RAG Ligero)
     * Usa un System Prompt de asistente útil.
     */
    fun chat(prompt: String): Mono<String> {
        // En el chat, el Controller ya envía las instrucciones ("Eres un experto...")
        // dentro del prompt del usuario, así que el system role aquí es genérico de soporte.
        val systemRole = "You are a helpful and concise assistant."
        return sendRequest(systemRole, prompt)
    }

    /**
     * 🔧 Método privado genérico para evitar duplicar código WebClient
     */
    private fun sendRequest(systemContent: String, userContent: String): Mono<String> {
        val messages = listOf(
            ChatMessage("system", systemContent),
            ChatMessage("user", userContent)
        )

        val body = ChatRequest(
            model = model,
            messages = messages,
            temperature = 0.3, // Temperatura baja para respuestas factuales
            stream = false
        )

        // Log para depuración (Opcional: usar Logger en prod)
        // println("🛰️ Sending to Groq ($model)...")

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