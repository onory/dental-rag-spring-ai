package com.fca.dentalrag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@SpringBootApplication
public class DentalRagApplication {

    @Value("classpath:/agenda_dental.txt")
    private Resource agendaResource;

    public static void main(String[] args) {
        SpringApplication.run(DentalRagApplication.class, args);
    }

    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        TextReader textReader = new TextReader(agendaResource);
        TokenTextSplitter splitter = new TokenTextSplitter();
        vectorStore.add(splitter.apply(textReader.get()));
        System.out.println(">>> [RAG] Agenda de la Dra. Nancy Rivera vectorizada e indexada en memoria.");
        return vectorStore;
    }
}