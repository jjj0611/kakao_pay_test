package org.kakaopay.recruit.bankingsystem.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.kakaopay.recruit.bankingsystem.domain.entity.Account;
import org.kakaopay.recruit.bankingsystem.domain.entity.Transaction;
import org.kakaopay.recruit.bankingsystem.domain.reposiotry.TransactionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public List<Transaction> findByACcount(Account account) {
        return transactionRepository.findByAccount(account);
    }

    public Transaction deposit(Account account, long amount, Long userId) {
        return transactionRepository.save(
            Transaction.depositCompleted(account, amount, userId, LocalDateTime
                .now()));
    }

    public Transaction withdrawStandby(Account account, long amount) {
        return transactionRepository.save(
            Transaction.withdrawStandby(account, amount, null, LocalDateTime
                .now()));
    }
}
