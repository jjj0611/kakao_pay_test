package org.kakaopay.recruit.bankingsystem.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kakaopay.recruit.bankingsystem.domain.entity.Account;
import org.kakaopay.recruit.bankingsystem.domain.entity.Transaction;
import org.kakaopay.recruit.bankingsystem.domain.entity.TransactionStatus;
import org.kakaopay.recruit.bankingsystem.domain.exception.RetrieveRuleViolationException;
import org.kakaopay.recruit.bankingsystem.domain.exception.WithdrawFailureException;
import org.kakaopay.recruit.bankingsystem.domain.exception.WithdrawRuleViolationException;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.request.AccountCreateRequest;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.request.SplitAccountCreateRequest;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.request.SplitAccountRetrieveRequest;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.request.SplitAccountWithdrawRequest;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.response.ReceivedHistory;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.response.SplitAccountRetrieveResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SplitAccountService {
    public static final int SPLIT_ACCOUNT_WITHDRAW_EXPIRED_MINUTES = 10;
    public static final int SPLIT_ACCOUNT_LOOKUP_EXPIRED_DAYS = 7;
    private final AccountService accountService;
    private final TransactionService transactionService;

    @Transactional
    public String create(SplitAccountCreateRequest request) {
        Long userId = request.getUserId();
        long amount = request.getAmount();
        int withdrawLimit = request.getWithdrawLimit();
        LocalDateTime requestAt = request.getRequestAt();

        AccountCreateRequest accountCreateRequest = AccountCreateRequest.builder()
            // TODO: token 생성기 만들기
            .token("abc")
            .ownerId(userId)
            .roomId(request.getRoomId())
            .createdAt(requestAt)
            .withdrawExpiredAt(requestAt.plusMinutes(SPLIT_ACCOUNT_WITHDRAW_EXPIRED_MINUTES))
            .lookupExpiredAt(requestAt.plusDays(SPLIT_ACCOUNT_LOOKUP_EXPIRED_DAYS))
            .build();

        Account account = accountService.create(accountCreateRequest);
        transactionService.deposit(account, amount, userId);
        // TODO: 분배 규칙 만들기
        for (int i = 0; i < withdrawLimit; i++) {
            transactionService.withdrawStandby(account, amount / withdrawLimit);
        }
        return "abc";
    }

    @Transactional
    public void withdraw(SplitAccountWithdrawRequest request) {
        Account account = accountService.findByToken(request.getToken());
        Long userId = request.getUserId();
        if (!account.getRoomId().equals(request.getRoomId())) {
            throw new WithdrawRuleViolationException("일치하지 않는 방 번호입니다.");
        }
        if (request.getRequestAt().isAfter(account.getWithdrawExpiredAt())) {
            throw new WithdrawRuleViolationException("뿌리기가 만료되었습니다.");
        }
        if (account.getOwnerId().equals(userId)) {
            throw new WithdrawRuleViolationException("자신이 생성한 뿌리기는 받을 수 없습니다.");
        }
        List<Transaction> transactions = transactionService.findByACcount(account);
        if (hasAnyTransactionAlready(request, transactions)) {
            throw new WithdrawRuleViolationException("이미 받은 뿌리기는 중복으로 받을 수 없습니다.");
        }
        Transaction withdrawStandby = transactions.stream()
            .filter(trx -> TransactionStatus.WITHDRAW_STANDBY == trx.getStatus())
            .findFirst()
            .orElseThrow(() -> new WithdrawFailureException("더 이상 남은 뿌리기가 없습니다."));

        withdrawStandby.toNextStatus(TransactionStatus.WITHDRAW_COMPLETED, userId);
    }

    private boolean hasAnyTransactionAlready(SplitAccountWithdrawRequest request,
        List<Transaction> transactions) {
        return transactions.stream()
            .anyMatch(transaction -> request.getUserId().equals(transaction.getUserId()));
    }

    @Transactional
    public SplitAccountRetrieveResponse retrieve(SplitAccountRetrieveRequest request) {
        Account account = accountService.findByToken(request.getToken());
        if (!request.getUserId().equals(account.getOwnerId())) {
            throw new RetrieveRuleViolationException("자신이 생성한 뿌리기만 조회할 수 있습니다.");
        }
        if (request.getRequestAt().isAfter(account.getLookupExpiredAt())) {
            throw new RetrieveRuleViolationException("뿌리기 조회가 만료되었습니다.");
        }
        List<Transaction> transactions = transactionService.findByACcount(account);
        Map<TransactionStatus, List<Transaction>> transactionStatusMap = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getStatus));
        Transaction depositTransaction = transactionStatusMap.get(
            TransactionStatus.DEPOSIT_COMPLETED).get(0);
        List<Transaction> withdrawCompletedTransactions = transactionStatusMap.get(
            TransactionStatus.WITHDRAW_COMPLETED);
        return SplitAccountRetrieveResponse.builder()
            .createdAt(account.getCreatedAt())
            .amount(depositTransaction.getAmount())
            .receivedAmount(
                withdrawCompletedTransactions.stream().mapToLong(Transaction::getAmount).sum())
            .histories(withdrawCompletedTransactions.stream()
                .map(ReceivedHistory::of)
                .collect(Collectors.toList()))
            .build();
    }
}
