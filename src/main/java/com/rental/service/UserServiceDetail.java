package com.rental.service;

import com.rental.dto.UserDTO;
import com.rental.entity.User;
import com.rental.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service pour récupérer les détails supplémentaires d'un utilisateur.
 */
@Service
public class UserServiceDetail {

    private static final Logger logger = Logger.getLogger(UserServiceDetail.class.getName());

    private final UserRepository userRepository;

    public UserServiceDetail(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Méthode pour obtenir les détails d'un utilisateur par ID.
     * Le mot de passe n'est pas inclus pour des raisons de sécurité.
     *
     * @param id L'ID de l'utilisateur à rechercher.
     * @return Un DTO représentant l'utilisateur, ou null si non trouvé.
     */
    public UserDTO getUserDetailsById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.warning("Utilisateur non trouvé pour l'id : " + id);
            return null;
        }
        User user = optionalUser.get();
        // Retourne un DTO sans inclure le mot de passe pour des raisons de sécurité
        return new UserDTO(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
