package com.fca.dentalrag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final ChatClient chatClient;

    public CitaController(ChatClient.Builder builder, VectorStore vectorStore) {
        String systemPrompt = """
                Eres el asistente virtual de recepción del consultorio de la Dra. Nancy Rivera.
                Responde las dudas sobre horarios disponibles, servicios y costos utilizando ÚNICAMENTE
                la información recuperada de la agenda adjunta.
                Si el usuario pregunta por un horario ocupado o fuera de servicio, indica que no está disponible
                y sugiere el bloque libre más cercano. Sé breve, claro y cordial.
                """;

        this.chatClient = builder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }

    @PostMapping("/consultar")
    public Map<String, String> consultarDisponibilidad(@RequestBody Map<String, String> payload) {
        String pregunta = payload.getOrDefault("pregunta", "¿Qué horarios tienes disponibles?");
        String respuesta = chatClient.prompt()
                .user(pregunta)
                .call()
                .content();

        return Map.of(
                "consultorio", "Dra. Nancy Rivera",
                "consulta", pregunta,
                "respuesta", respuesta
        );
    }
}