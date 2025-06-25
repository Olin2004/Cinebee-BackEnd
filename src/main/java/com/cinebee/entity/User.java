package com.cinebee.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.cinebee.common.UserStatus;
import com.cinebee.common.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;

import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Users", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email")
})
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String avatarUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "date_of_birth")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String fullName;
    @Column(unique = true)
    private String phoneNumber;
    private String oauthId;
    private String password;

    @Enumerated(EnumType.STRING)
    private Provider provider = Provider.LOCAL; // Enum: GOOGLE, FACEBOOK, LOCAL, ...

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role; // Enum Role

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus = UserStatus.ACTIVE; // Enum: ACTIVE, BANNED, ...

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String username;

    public enum Provider {
        LOCAL, GOOGLE, FACEBOOK
    }

}