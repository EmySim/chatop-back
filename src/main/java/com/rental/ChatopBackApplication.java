package com.rental;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.rental.repository")
@EnableJpaAuditing
public class ChatopBackApplication {

    /**
     * Point d'entrée principal pour démarrer l'application Spring Boot.
     */
    public static void main(String[] args) {
        SpringApplication.run(ChatopBackApplication.class, args);
    }

    // ==========================================================
    // Configuration des variables d'environnement (recommandé)
    // ==========================================================
    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Value("${DATABASE_USERNAME}")
    private String databaseUsername;

    @Value("${DATABASE_PASSWORD}")
    private String databasePassword;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_EXPIRATION}")
    private long jwtExpiration;

    @Value("${AWS_ACCESS_KEY_ID}")
    private String awsAccessKeyId;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String awsSecretAccessKey;

    @Value("${AWS_REGION}")
    private String awsRegion;

    @Value("${AWS_BUCKET_NAME}")
    private String awsBucketName;

    /**
     * Bean pour l'exécution au démarrage de l'application.
     * Vérifie que les variables d'environnement essentielles sont présentes
     * et correctement initialisées avant que l'application ne démarre pleinement.
     *
     * @return CommandLineRunner, une fonction qui s'exécute après le démarrage du contexte Spring.
     */
    @Bean
    public CommandLineRunner diagnosticRunner() {
        return args -> {
            // Vérification des variables de configuration essentielles
            try {
                printEnvVariable("DATABASE_URL", databaseUrl);
                printEnvVariable("DATABASE_USERNAME", databaseUsername);
                printEnvVariable("DATABASE_PASSWORD", obfuscate(databasePassword));
                printEnvVariable("JWT_SECRET", obfuscate(jwtSecret));
                printEnvVariable("JWT_EXPIRATION", jwtExpiration + " ms");
                printEnvVariable("AWS_ACCESS_KEY_ID", obfuscate(awsAccessKeyId));
                printEnvVariable("AWS_SECRET_ACCESS_KEY", obfuscate(awsSecretAccessKey));
                printEnvVariable("AWS_REGION", awsRegion);
                printEnvVariable("AWS_BUCKET_NAME", awsBucketName);
            } catch (Exception e) {
                // Affiche une erreur descriptive si une variable manque ou est mal configurée
                System.err.println("Erreur de configuration : " + e.getMessage());
                System.exit(1); // Arrête l'application
            }
        };
    }

    /**
     * Bean manuel pour la configuration du DataSource.
     * NOTE : Ce bean n'est nécessaire que pour les cas où les propriétés
     * ne sont pas suffisantes. Dans des scénarios normaux, il est préférable
     * de laisser Spring Boot configurer automatiquement le DataSource.
     *
     * @return DataSource configuré.
     */
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(databaseUrl)
                .username(databaseUsername)
                .password(databasePassword)
                .build();
    }

    /**
     * Permet d'afficher une variable avec son nom.
     *
     * @param name  Nom de la variable d'environnement.
     * @param value Valeur actuelle (affichée en clair ou masquée dans le cas de mots de passe/secrets).
     */
    private void printEnvVariable(String name, String value) {
        System.out.println(name + " : " + value);
    }

    /**
     * Masque une chaîne de caractères sensible pour éviter qu'elle ne soit affichée en clair.
     *
     * @param value La valeur à masquer.
     * @return Une version masquée de la valeur (ex. : "******").
     */
    private String obfuscate(String value) {
        if (value == null || value.isEmpty()) {
            return "NON FOURNIE";
        }
        return "*".repeat(value.length());
    }
}