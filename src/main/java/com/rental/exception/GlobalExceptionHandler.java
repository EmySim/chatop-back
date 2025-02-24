package com.rental.exception;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Gestion globale des exceptions pour l'application.
 *
 * Cette classe intercepte les erreurs courantes, fournit des réponses adaptées, et génère des logs utilisables pour le diagnostic.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    /**
     * Gère les erreurs de validation levées par Hibernate Validator (DTO).
     *
     * @param ex Instance de MethodArgumentNotValidException contenant les erreurs détectées.
     * @return Une réponse HTTP 400 (BAD REQUEST) avec les détails des erreurs pour chaque champ invalide.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Erreur de validation des champs. Les détails sont fournis dans la réponse.")
    })
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Boucle à travers chaque champ ayant une erreur de validation
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField(); // Nom du champ ayant l'erreur
            String errorMessage = error.getDefaultMessage(); // Message personnalisé défini dans l'annotation

            // Log pour chaque champ analysé
            logger.warning("Validation échouée - Champ : " + fieldName +
                    " | Message brut : " + errorMessage);

            // Mapping direct des messages personnalisés
            if ("email".equals(fieldName)) {
                if ("NotBlank".equalsIgnoreCase(error.getCode())) {
                    errorMessage = "ᕦ(òᴥó)ᕥL'email est obligatoire.";
                } else if ("Email".equalsIgnoreCase(error.getCode())) {
                    errorMessage = "Le format de l'email est invalide.";
                }
            } else if ("password".equals(fieldName)) {
                if ("NotBlank".equalsIgnoreCase(error.getCode())) {
                    errorMessage = "(๑•́ ヮ •̀๑)Le mot de passe est obligatoire.";
                } else if ("Size".equalsIgnoreCase(error.getCode())) {
                    errorMessage = "Le mot de passe doit contenir au moins 6 caractères.";
                }
            } else if ("name".equals(fieldName)) {
                errorMessage = "Le nom est obligatoire.";
            }

            // Ajouter au résultat final
            errors.put(fieldName, errorMessage);
        }

        // Logger global pour suivre le diagnostic général
        logger.warning("Erreurs détectées : " + errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Gère les erreurs d'authentification (utilisateur introuvable ou erreur liée à l'accès).
     *
     * @param ex Instance d'une exception liée à l'authentification.
     * @return Une réponse HTTP 401 (UNAUTHORIZED) contenant un message d'erreur.
     */
    @ExceptionHandler({UsernameNotFoundException.class, IllegalArgumentException.class})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Erreur d'authentification ou accès non autorisé.")
    })
    public ResponseEntity<Map<String, String>> handleAuthExceptions(Exception ex) {
        logger.warning("Erreur d'authentification détectée : " + ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Gère toutes les autres exceptions imprévues.
     *
     * @param ex Toute instance d'exception non traitée spécifiquement.
     * @return Une réponse HTTP 500 (INTERNAL SERVER ERROR) contenant un message générique.
     */
    @ExceptionHandler(Exception.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur.")
    })
    public ResponseEntity<Map<String, String>> handleGlobalExceptions(Exception ex) {
        logger.severe("Erreur générale détectée : " + ex.getMessage());
        ex.printStackTrace();

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Une erreur interne est survenue. Veuillez contacter l'administrateur.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
