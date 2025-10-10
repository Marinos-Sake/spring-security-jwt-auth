package io.github.marinossake.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.marinossake.core.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInsertDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 16, message = "Username must be 5-16 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9._-]+$",
            message = "Only letters, digits, dot (.), underscore (_), and hyphen (-) are allowed"
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 5, max = 20, message = "Password must be 5-20 characters")
    @Pattern(regexp = "^\\S+$", message = "Password must not contain spaces")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // protects in case someone accidentally returns this DTO instead of a ReadOnlyDTO
    private String password;

    private UserRole role = UserRole.USER;

}
