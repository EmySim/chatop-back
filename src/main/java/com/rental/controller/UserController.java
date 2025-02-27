package com.rental.controller;

import com.rental.dto.UserDTO;
import com.rental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

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
     * Récupère un utilisateur par ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
    })
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        logger.info("Requête pour récupérer un utilisateur par ID : " + id);
        try {
            UserDTO userDTO = userService.findUserById(id);
            return ResponseEntity.ok(userDTO);
        } catch (IllegalStateException e) {
            logger.warning("Utilisateur non trouvé : " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}