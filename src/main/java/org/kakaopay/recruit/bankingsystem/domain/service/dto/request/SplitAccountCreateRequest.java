package org.kakaopay.recruit.bankingsystem.domain.service.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SplitAccountCreateRequest {
    private Long userId;
    private String roomId;
    private long amount;
    private int withdrawLimit;
    private LocalDateTime requestAt = LocalDateTime.now();

    @Builder
    public SplitAccountCreateRequest(Long userId, String roomId, long amount,
        int withdrawLimit, LocalDateTime requestAt) {
        this.userId = userId;
        this.roomId = roomId;
        this.amount = amount;
        this.withdrawLimit = withdrawLimit;
        this.requestAt = requestAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
