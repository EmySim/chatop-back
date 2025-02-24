package com.rental.controller;

import com.rental.dto.UserDTO;
import com.rental.service.UserServiceDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final UserServiceDetail userServiceDetail;

    public UserController(UserServiceDetail userServiceDetail) {
        this.userServiceDetail = userServiceDetail;
    }

    @Operation(summary = "Récupérer les informations d'un utilisateur par ID via Bearer Token", description = "Retourne les informations d'un utilisateur spécifique via son ID avec un Token Bearer requis")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserDetails(
            @Parameter(description = "ID de l'utilisateur à récupérer") @PathVariable Long id) {
        logger.info("Récupération des détails pour l'utilisateur ID : " + id);
        UserDTO userDTO = userServiceDetail.getUserDetailsById(id);
        if (userDTO == null) {
            logger.warning("Utilisateur non trouvé pour ID : " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        logger.info("Utilisateur récupéré avec succès : " + id);
        return ResponseEntity.ok(userDTO);
    }
}