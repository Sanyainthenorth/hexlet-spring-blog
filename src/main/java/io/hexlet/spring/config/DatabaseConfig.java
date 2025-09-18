package io.hexlet.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DatabaseConfig {

    // Этот бин будет создан только для development профиля
    @Bean
    @Profile("development")
    public String developmentDatabaseInfo() {
        System.out.println("Development database: H2 in file");
        return "H2 File Database";
    }

    // Этот бин будет создан только для production профиля
    @Bean
    @Profile("production")
    public String productionDatabaseInfo() {
        System.out.println("Production database: PostgreSQL");
        return "PostgreSQL Database";
    }

    // Этот бин будет создан только для test профиля
    @Bean
    @Profile("test")
    public String testDatabaseInfo() {
        System.out.println("Test database: H2 in memory");
        return "H2 In-Memory Database";
    }
}
