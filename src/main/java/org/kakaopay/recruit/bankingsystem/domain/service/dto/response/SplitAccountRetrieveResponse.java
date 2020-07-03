package org.kakaopay.recruit.bankingsystem.domain.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SplitAccountRetrieveResponse {
    private LocalDateTime createdAt;
    private long amount;
    private long receivedAmount;
    private List<ReceivedHistory> histories;

    @Builder
    public SplitAccountRetrieveResponse(LocalDateTime createdAt, long amount, long receivedAmount,
        List<ReceivedHistory> histories) {
        this.createdAt = createdAt;
        this.amount = amount;
        this.receivedAmount = receivedAmount;
        this.histories = histories;
    }
}
