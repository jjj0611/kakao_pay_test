package org.kakaopay.recruit.bankingsystem.domain.service;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SplitAccountWithdrawRequest {
    private String token;
    private String roomId;
    private Long userId;
    private LocalDateTime requestAt;

    @Builder
    public SplitAccountWithdrawRequest(String token, String roomId, Long userId,
        LocalDateTime requestAt) {
        this.token = token;
        this.roomId = roomId;
        this.userId = userId;
        this.requestAt = requestAt;
    }
}
