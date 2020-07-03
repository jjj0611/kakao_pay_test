package org.kakaopay.recruit.bankingsystem.domain.service;

import org.kakaopay.recruit.bankingsystem.domain.entity.Account;
import org.kakaopay.recruit.bankingsystem.domain.exception.AccountNotFoundException;
import org.kakaopay.recruit.bankingsystem.domain.reposiotry.AccountRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account create(AccountCreateRequest request) {
        Account account = Account.builder()
            .token(request.getToken())
            .ownerId(request.getOwnerId())
            .roomId(request.getRoomId())
            .createdAt(request.getCreatedAt())
            .withdrawExpiredAt(request.getWithdrawExpiredAt())
            .lookupExpiredAt(request.getLookupExpiredAt())
            .build();
        return accountRepository.save(account);
    }

    public Account findByToken(String token) {
        return accountRepository.findByToken(token)
            .orElseThrow(() -> new AccountNotFoundException());
    }
}
