package org.kakaopay.recruit.bankingsystem.domain.reposiotry;

import java.util.List;

import org.kakaopay.recruit.bankingsystem.domain.entity.Account;
import org.kakaopay.recruit.bankingsystem.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount(Account account);
}
