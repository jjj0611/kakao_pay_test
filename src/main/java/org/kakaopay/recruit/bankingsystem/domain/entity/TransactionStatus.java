package org.kakaopay.recruit.bankingsystem.domain.entity;

import java.util.EnumSet;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    DEPOSIT_COMPLETED,
    WITHDRAW_STANDBY,
    WITHDRAW_COMPLETED,
    WITHDRAW_TIMEOUT_EXPIRED;

    private EnumSet<TransactionStatus> possibleNextStatus;

    static {
        DEPOSIT_COMPLETED.possibleNextStatus = EnumSet.noneOf(TransactionStatus.class);
        WITHDRAW_STANDBY.possibleNextStatus = EnumSet.of(WITHDRAW_COMPLETED, WITHDRAW_TIMEOUT_EXPIRED);
        DEPOSIT_COMPLETED.possibleNextStatus = EnumSet.noneOf(TransactionStatus.class);
        DEPOSIT_COMPLETED.possibleNextStatus = EnumSet.noneOf(TransactionStatus.class);
    }
}
