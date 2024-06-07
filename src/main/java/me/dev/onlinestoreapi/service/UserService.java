package me.dev.onlinestoreapi.service;

import me.dev.onlinestoreapi.dto.UpdateUserDTO;
import me.dev.onlinestoreapi.dto.UserDTO;
import me.dev.onlinestoreapi.model.User;

public interface UserService {
    User createUser(UserDTO userDTO);

    User updateUser(Long userId,UpdateUserDTO updateUserDTO);

    // Return token key
    String login(String phoneNumber, String password, Long roleId);

    User getUserDetailsFromToken(String token);
}
