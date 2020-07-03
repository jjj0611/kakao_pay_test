package org.kakaopay.recruit.bankingsystem.domain.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, length = 3)
    private String token;
    private Long ownerId;
    private String roomId;
    private LocalDateTime createdAt;
    private LocalDateTime withdrawExpiredAt;
    private LocalDateTime lookupExpiredAt;

    @Builder
    public Account(String token, Long ownerId, String roomId, LocalDateTime createdAt,
        LocalDateTime withdrawExpiredAt, LocalDateTime lookupExpiredAt) {
        this.token = token;
        this.ownerId = ownerId;
        this.roomId = roomId;
        this.createdAt = createdAt;
        this.withdrawExpiredAt = withdrawExpiredAt;
        this.lookupExpiredAt = lookupExpiredAt;
    }
}
