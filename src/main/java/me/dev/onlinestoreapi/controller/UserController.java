package me.dev.onlinestoreapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dev.onlinestoreapi.dto.UpdateUserDTO;
import me.dev.onlinestoreapi.dto.UserDTO;
import me.dev.onlinestoreapi.dto.UserLoginDTO;
import me.dev.onlinestoreapi.model.User;
import me.dev.onlinestoreapi.response.LoginResponse;
import me.dev.onlinestoreapi.response.RegisterResponse;
import me.dev.onlinestoreapi.response.UserResponse;
import me.dev.onlinestoreapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody UserDTO userDTO, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                return ResponseEntity.badRequest().body(RegisterResponse.builder()
                        .message(errorMessages.toString())
                        .build());
            }

            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(RegisterResponse.builder()
                        .message("Password does not match!")
                        .build());
            }

            User user = userService.createUser(userDTO);
            return ResponseEntity.ok(RegisterResponse.builder()
                    .message("User registration successfully")
                    .user(user)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RegisterResponse.builder()
                    .message(String.format("Register failed: %s", e.getMessage()))
                    .build());
        }
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updateUserDTO,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            // Ensure that the user making the request matches the user being updated
            if (!Objects.equals(user.getId(), userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updatedUser = userService.updateUser(userId, updateUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            // Check login information and generate tokens
            String token = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword(),
                    userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId());

            // Return the token in response
            return ResponseEntity.ok(LoginResponse.builder()
                    .message("Login successfully!")
                    .token(token)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message(String.format("Login failed: %s", e.getMessage()))
                    .build());
        }
    }

    @PostMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
