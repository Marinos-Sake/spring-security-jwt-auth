package io.github.marinossake.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 16, message = "Username must be 5-16 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 5, max = 20, message = "Password must be 5-20 characters")
    private String password;
}
