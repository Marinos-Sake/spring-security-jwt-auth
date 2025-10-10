package com.jwt.safe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jwt.safe.core.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@ToString(exclude = {"password", "attachments"}) // avoids printing sensitive (password) or heavy (attachments) fields in logs/debug
@EqualsAndHashCode(of = "id", callSuper = false) // equality & hashCode based only on 'id' (stable identity for JPA entities)
@Table(name = "users")
public class User extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // password can be written (input) but never serialized in API responses
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserRole role;

    @Column(name = "is_active", nullable = false)
    @ColumnDefault("true") //Ensures DB default value = true if not explicitly set during insert
    private Boolean isActive = true;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false, length = 36)
    private String publicId;

    @PrePersist
    private void ensurePublicId() {
        if (publicId == null || publicId.isBlank()) {
            publicId = UUID.randomUUID().toString();
        }
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore  // prevents attachments from being serialized in JSON responses (avoid heavy data & recursion)
    private List<Attachment> attachments = new ArrayList<>();
}
