package me.dev.onlinestoreapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dev.onlinestoreapi.dto.UpdateUserDTO;
import me.dev.onlinestoreapi.dto.UserDTO;
import me.dev.onlinestoreapi.exception.PermissionDenyException;
import me.dev.onlinestoreapi.exception.ResourceNotFoundException;
import me.dev.onlinestoreapi.exception.TokenExpiredException;
import me.dev.onlinestoreapi.model.Role;
import me.dev.onlinestoreapi.model.User;
import me.dev.onlinestoreapi.repository.RoleRepository;
import me.dev.onlinestoreapi.repository.UserRepository;
import me.dev.onlinestoreapi.security.jwt.JwtUtil;
import me.dev.onlinestoreapi.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public User createUser(UserDTO userDTO) {
        String phoneNumber = userDTO.getPhoneNumber();
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            log.error("Phone number already exist");
            throw new DataIntegrityViolationException("Phone number already exist");
        }

        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if (role.getName().equalsIgnoreCase("ADMIN")) {
            throw new PermissionDenyException("You can't register an admin account!");
        }

        // Convert from UserDTO to User
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(phoneNumber)
                .address(userDTO.getAddress())
                .password(userDTO.getPassword())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();

        newUser.setRole(role);

        if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            String passwordEncoded = passwordEncoder.encode(password);
            newUser.setPassword(passwordEncoded);
        }
        log.info("User has save!");
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) {
        User updateUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if the phone number is being changed and if it already exists for another user
        String newPhoneNumber = updateUserDTO.getPhoneNumber();
        if (!updateUser.getPhoneNumber().equals(newPhoneNumber)
                && userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exist!");
        }

        // Update user information if value not null
        if (updateUserDTO.getFullName() != null) {
            updateUser.setFullName(updateUserDTO.getFullName());
        }
        if (newPhoneNumber != null) {
            updateUser.setPhoneNumber(newPhoneNumber);
        }
        if (updateUserDTO.getAddress() != null) {
            updateUser.setAddress(updateUserDTO.getAddress());
        }
        if (updateUserDTO.getDateOfBirth() != null) {
            updateUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }
        if (updateUserDTO.getFacebookAccountId() > 0) {
            updateUser.setFacebookAccountId(updateUserDTO.getFacebookAccountId());
        }
        if (updateUserDTO.getGoogleAccountId() > 0) {
            updateUser.setGoogleAccountId(updateUserDTO.getGoogleAccountId());
        }

        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
            if (!updateUserDTO.getPassword().equals(updateUserDTO.getRetypePassword())) {
                throw new ResourceNotFoundException("Password and retype password are not the same");
            }
            String newPassword = updateUserDTO.getPassword();
            String encodedPw = passwordEncoder.encode(newPassword);
            updateUser.setPassword(encodedPw);
        }

        return userRepository.save(updateUser);
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Invalid phone number or password");
        }
        User user = optionalUser.get();
        // Check password
        if (user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Password is wrong");
            }
        }

        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if (optionalRole.isEmpty() || !roleId.equals(user.getRole().getId())) {
            throw new ResourceNotFoundException("Role doesn't exist");
        }

        if (!optionalUser.get().isActive()) {
            throw new ResourceNotFoundException("User has been blocked!");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password, user.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);
        return jwtUtil.generateToken(user);
    }

    @Override
    public User getUserDetailsFromToken(String token) {
        if (jwtUtil.isTokenExpired(token)) {
            throw new TokenExpiredException("Token is expired!");
        }

        String phoneNumber = jwtUtil.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isPresent()) {
            return user.get();
        } else
            throw new ResourceNotFoundException("User not found");
    }
}
