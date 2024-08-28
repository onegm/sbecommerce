package com.ecommerce.sbecommerce.security.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank
    @Size(min=3, max=20)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min=8, max=12)
    private String password;

    private Set<String> roles;
}
