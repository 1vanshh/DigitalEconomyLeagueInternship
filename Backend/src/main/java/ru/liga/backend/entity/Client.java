package ru.liga.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "client_account")
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientStatus status;

    @Column(name = "create_dttm", nullable = false)
    private OffsetDateTime createDttm;

    @Column(name = "modify_dttm", nullable = false)
    private OffsetDateTime modifyDttm;
}

