package me.dev.onlinestoreapi.repository;

import me.dev.onlinestoreapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}