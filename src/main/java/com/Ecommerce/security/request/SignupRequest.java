package com.Ecommerce.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20, message = "Username cannot be less than 3 and greater than 20")
    private String username;

    @NotBlank
    @Email
    @Size(max = 50, message = "Email cannot be greater than 50.")
    private String email;


    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 50, message = "Password cannot be less than 6 and greater then 50")
    private String password;

}
