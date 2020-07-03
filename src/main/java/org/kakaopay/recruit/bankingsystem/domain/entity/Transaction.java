package org.kakaopay.recruit.bankingsystem.domain.entity;

import java.time.LocalDateTime;
import java.util.EnumSet;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.kakaopay.recruit.bankingsystem.domain.exception.TransactionStatusChangeValidationException;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 2. 계좌 생성 + 뿌리기 총액
 * 뿌리기 총액 요청시마다 계좌 테이블 Update
 * Trx 하나씩 생성한다.
 * Insert Account * 1 N 명에게 나눠줄거야
 * Insert Trx * N (받을 금액, 수령한 사람 = 공란)
 * (
 * Select Account * 1 (필수)
 * Select * where Account from Trx (필수)
 * Update Trx * N
 * ) * N 번 발생
 */

@Getter
@Entity
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @ManyToOne
    private Account account;
    private long amount;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    private Transaction(TransactionStatus status, Account account, long amount, Long userId,
        LocalDateTime createdAt) {
        this.status = status;
        this.account = account;
        this.amount = amount;
        this.userId = userId;
        this.createdAt = createdAt;
        this.modifiedAt = createdAt;
    }

    public static Transaction depositCompleted(Account account, long amount, Long userId,
        LocalDateTime createdAt) {
        return Transaction.builder()
            .status(TransactionStatus.DEPOSIT_COMPLETED)
            .account(account)
            .amount(amount)
            .userId(userId)
            .createdAt(createdAt)
            .build();
    }

    public static Transaction withdrawStandby(Account account, long amount, Long userId,
        LocalDateTime createdAt) {
        return Transaction.builder()
            .status(TransactionStatus.WITHDRAW_STANDBY)
            .account(account)
            .amount(amount)
            .userId(userId)
            .createdAt(createdAt)
            .build();
    }

    public static Transaction withdrawCompleted(Account account, long amount, Long userId,
        LocalDateTime createdAt) {
        return Transaction.builder()
            .status(TransactionStatus.WITHDRAW_COMPLETED)
            .account(account)
            .amount(amount)
            .userId(userId)
            .createdAt(createdAt)
            .build();
    }

    public void toNextStatus(TransactionStatus nextStatus, Long userId) {
        if(!status.getPossibleNextStatus().contains(nextStatus)) {
           throw new TransactionStatusChangeValidationException("유효하지 않은 트랜잭션 상태 변화입니다.");
        }
        this.status = nextStatus;
        this.userId = userId;
        this.modifiedAt = LocalDateTime.now();
    }
}
