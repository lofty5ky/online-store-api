package me.dev.onlinestoreapi.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "social_accounts")
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "provider", length = 20, nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
