package com.example.board_sp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String userId;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private java.time.LocalDate joinedAt;
}
