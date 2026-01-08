package com.sca.stratai;


import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class StratAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StratAiApplication.class, args);
    }

}


