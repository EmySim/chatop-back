package com.rental.controller;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rental.dto.UserDTO;
import com.rental.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "Gestion des utilisateurs")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint pour récupérer les détails d'un utilisateur par ID.
     *
     * @param id L'ID de l'utilisateur passé dans l'URL.
     * @return Les détails de l'utilisateur en tant que UserDTO.
     */
    @Operation(summary = "Récupérer un utilisateur par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non autorisé"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        logger.info("Requête reçue pour récupérer un utilisateur avec l'ID : " + id);

        // Vérifier si l'ID est "null" ou vide -> On récupère l'utilisateur connecté
        if (id == null) {
            try {
                UserDTO authenticatedUser = userService.getAuthenticatedUser();
                id = authenticatedUser.getId();
                logger.info("ID récupéré : " + id);
            } catch (Exception e) {
                logger.severe("Impossible de récupérer l'utilisateur connecté : " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        if (id == null) {
            logger.warning("ID utilisateur invalide (vide) : " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        logger.info("ID utilisateur validé : " + id);

        // Récupération de l'utilisateur
        try {
            UserDTO userDTO = userService.findUserById(id);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            logger.severe("Erreur interne lors de la récupération de l'utilisateur ID : " + id + " | Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}