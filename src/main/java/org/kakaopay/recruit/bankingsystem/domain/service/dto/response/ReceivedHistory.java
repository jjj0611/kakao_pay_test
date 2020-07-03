package org.kakaopay.recruit.bankingsystem.domain.service.dto.response;

import org.kakaopay.recruit.bankingsystem.domain.entity.Transaction;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class ReceivedHistory {
    private long amount;
    private Long userId;

    @Builder
    public ReceivedHistory(long amount, Long userId) {
        this.amount = amount;
        this.userId = userId;
    }

    public static ReceivedHistory of(Transaction transaction) {
        return ReceivedHistory.builder()
            .amount(transaction.getAmount())
            .userId(transaction.getUserId())
            .build();
    }
}
