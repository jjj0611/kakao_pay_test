package org.kakaopay.recruit.bankingsystem.domain.reposiotry;

import java.util.Optional;

import org.kakaopay.recruit.bankingsystem.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByToken(String token);
}
