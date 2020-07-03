package org.kakaopay.recruit.bankingsystem.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.kakaopay.recruit.bankingsystem.domain.exception.TransactionStatusChangeValidationException;

class TransactionTest {

    @Test
    void exception() {
        Transaction transaction = Transaction.depositCompleted(null, 100000, null,
            LocalDateTime.now());
        assertThatThrownBy(() -> transaction.toNextStatus(TransactionStatus.WITHDRAW_COMPLETED, 3L))
            .isInstanceOf(TransactionStatusChangeValidationException.class);
    }

    @Test
    void success() {
        Transaction transaction = Transaction.withdrawStandby(null, 100000, null,
            LocalDateTime.now());
        transaction.toNextStatus(TransactionStatus.WITHDRAW_COMPLETED, 3L);
        assertThat(transaction.getUserId()).isEqualTo(3L);
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.WITHDRAW_COMPLETED);
    }
}