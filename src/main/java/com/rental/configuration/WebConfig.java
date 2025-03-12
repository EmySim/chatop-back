package com.rental.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration pour gérer les ressources statiques, notamment les fichiers uploadés.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure les gestionnaires de ressources pour servir les fichiers statiques.
     * Ici, nous ajoutons un gestionnaire pour les fichiers uploadés.
     *
     * @param registry le registre des gestionnaires de ressources
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ajoute un gestionnaire de ressources pour les fichiers dans le répertoire 'uploads'
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}