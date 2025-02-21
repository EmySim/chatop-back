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

    public UserDTO getUserDetailsById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.warning("Utilisateur non trouvé pour l'id : " + id);
            return null;
        }
        User user = optionalUser.get();
        return new UserDTO(user.getEmail(), user.getName(), user.getPassword());
    }
}
