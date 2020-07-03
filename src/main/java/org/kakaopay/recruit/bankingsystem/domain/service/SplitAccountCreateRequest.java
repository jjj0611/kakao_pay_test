package org.kakaopay.recruit.bankingsystem.domain.service;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SplitAccountCreateRequest {
    private Long userId;
    private String rommId;
    private long amount;
    private int withdrawLimit;
    private LocalDateTime requestAt;

    @Builder
    public SplitAccountCreateRequest(Long userId, String rommId, long amount,
        int withdrawLimit, LocalDateTime requestAt) {
        this.userId = userId;
        this.rommId = rommId;
        this.amount = amount;
        this.withdrawLimit = withdrawLimit;
        this.requestAt = requestAt;
    }
}
