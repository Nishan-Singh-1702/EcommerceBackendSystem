package com.Ecommerce.repository;

import com.Ecommerce.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(@NotBlank @Size(min = 3, max = 20, message = "Username cannot be less than 3 and greater than 20") String username);

    boolean existsByEmail(@NotBlank @Email @Size(max = 50, message = "Email cannot be greater than 50.") String email);
}
