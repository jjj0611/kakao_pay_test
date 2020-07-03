package org.kakaopay.recruit.bankingsystem.domain.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kakaopay.recruit.bankingsystem.domain.entity.Account;
import org.kakaopay.recruit.bankingsystem.domain.entity.Transaction;
import org.kakaopay.recruit.bankingsystem.domain.entity.TransactionStatus;
import org.kakaopay.recruit.bankingsystem.domain.exception.RetrieveRuleViolationException;
import org.kakaopay.recruit.bankingsystem.domain.exception.WithdrawFailureException;
import org.kakaopay.recruit.bankingsystem.domain.exception.WithdrawRuleViolationException;
import org.kakaopay.recruit.bankingsystem.domain.reposiotry.AccountRepository;
import org.kakaopay.recruit.bankingsystem.domain.reposiotry.TransactionRepository;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.request.SplitAccountCreateRequest;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.request.SplitAccountRetrieveRequest;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.request.SplitAccountWithdrawRequest;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.response.ReceivedHistory;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.response.SplitAccountRetrieveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SplitAccountServiceTest {

    @Autowired
    private SplitAccountService splitAccountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("뿌리기 생성하기")
    void create() {
        LocalDateTime now = LocalDateTime.now();
        SplitAccountCreateRequest splitAccountCreateRequest = SplitAccountCreateRequest.builder()
            .userId(1L)
            .rommId("room")
            .amount(100000)
            .withdrawLimit(5)
            .requestAt(now)
            .build();

        splitAccountService.create(splitAccountCreateRequest);
        List<Account> accounts = accountRepository.findAll();
        List<Transaction> transactions = transactionRepository.findAll();
        Map<TransactionStatus, List<Transaction>> transactionStatusMap = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getStatus));

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getOwnerId()).isEqualTo(1L);
        assertThat(accounts.get(0).getWithdrawExpiredAt()).isEqualTo(now.plusMinutes(10));
        assertThat(accounts.get(0).getLookupExpiredAt()).isEqualTo(now.plusDays(7));
        assertThat(transactionStatusMap.get(TransactionStatus.DEPOSIT_COMPLETED)).hasSize(1);
        assertThat(transactionStatusMap.get(TransactionStatus.WITHDRAW_STANDBY)).hasSize(
            splitAccountCreateRequest.getWithdrawLimit());
    }

    @Test
    @DisplayName("받기")
    void withdraw() {
        LocalDateTime now = LocalDateTime.now();
        SplitAccountCreateRequest splitAccountCreateRequest = SplitAccountCreateRequest.builder()
            .userId(1L)
            .rommId("room")
            .amount(100000)
            .withdrawLimit(5)
            .requestAt(now)
            .build();

        splitAccountService.create(splitAccountCreateRequest);

        SplitAccountWithdrawRequest splitAccountWithdrawRequest = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("room")
            .userId(2L)
            .requestAt(now.plusMinutes(1))
            .build();

        splitAccountService.withdraw(splitAccountWithdrawRequest);

        List<Transaction> transactions = transactionRepository.findAll();
        Map<TransactionStatus, List<Transaction>> transactionStatusMap = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getStatus));

        assertThat(transactionStatusMap.get(TransactionStatus.WITHDRAW_STANDBY)).hasSize(splitAccountCreateRequest.getWithdrawLimit() - 1);
        List<Transaction> completed = transactionStatusMap.get(TransactionStatus.WITHDRAW_COMPLETED);
        assertThat(completed).hasSize(1);
        assertThat(completed.get(0).getAmount()).isEqualTo(20000);
        assertThat(completed.get(0).getUserId()).isEqualTo(2);
    }

    @Test
    @DisplayName("뿌리기를 생성한 사람이 받기를 시도한다.")
    void withdrawError1() {
        LocalDateTime now = LocalDateTime.now();
        SplitAccountCreateRequest splitAccountCreateRequest = SplitAccountCreateRequest.builder()
            .userId(1L)
            .rommId("room")
            .amount(100000)
            .withdrawLimit(5)
            .requestAt(now)
            .build();

        splitAccountService.create(splitAccountCreateRequest);

        SplitAccountWithdrawRequest splitAccountWithdrawRequest = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("room")
            .userId(1L)
            .requestAt(now.plusMinutes(1))
            .build();

        assertThatThrownBy(() -> splitAccountService.withdraw(splitAccountWithdrawRequest))
            .isInstanceOf(WithdrawRuleViolationException.class)
            .hasMessageContaining("자신이 생성한 뿌리기는 받을 수 없습니다.");
    }

    @Test
    @DisplayName("다른 방에 있는 사람이 받기를 시도한다.")
    void withdrawError2() {
        LocalDateTime now = LocalDateTime.now();
        SplitAccountCreateRequest splitAccountCreateRequest = SplitAccountCreateRequest.builder()
            .userId(1L)
            .rommId("room")
            .amount(100000)
            .withdrawLimit(5)
            .requestAt(now)
            .build();

        splitAccountService.create(splitAccountCreateRequest);

        SplitAccountWithdrawRequest splitAccountWithdrawRequest = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("rom")
            .userId(2L)
            .requestAt(now.plusMinutes(1))
            .build();

        assertThatThrownBy(() -> splitAccountService.withdraw(splitAccountWithdrawRequest))
            .isInstanceOf(WithdrawRuleViolationException.class)
            .hasMessageContaining("일치하지 않는 방 번호입니다.");
    }

    @Test
    @DisplayName("뿌리기가 만료된 이후에 받기를 시도한다.")
    void withdrawError3() {
        LocalDateTime now = LocalDateTime.now();
        SplitAccountCreateRequest splitAccountCreateRequest = SplitAccountCreateRequest.builder()
            .userId(1L)
            .rommId("room")
            .amount(100000)
            .withdrawLimit(5)
            .requestAt(now)
            .build();

        splitAccountService.create(splitAccountCreateRequest);

        SplitAccountWithdrawRequest splitAccountWithdrawRequest = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("room")
            .userId(2L)
            .requestAt(now.plusMinutes(11))
            .build();

        assertThatThrownBy(() -> splitAccountService.withdraw(splitAccountWithdrawRequest))
            .isInstanceOf(WithdrawRuleViolationException.class)
            .hasMessageContaining("뿌리기가 만료되었습니다.");
    }

    @Test
    @DisplayName("중복 받기를 시도한다.")
    void withdrawError4() {
        LocalDateTime now = LocalDateTime.now();
        SplitAccountCreateRequest splitAccountCreateRequest = SplitAccountCreateRequest.builder()
            .userId(1L)
            .rommId("room")
            .amount(100000)
            .withdrawLimit(5)
            .requestAt(now)
            .build();

        splitAccountService.create(splitAccountCreateRequest);

        SplitAccountWithdrawRequest splitAccountWithdrawRequest = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("room")
            .userId(2L)
            .requestAt(now.plusMinutes(1))
            .build();

        splitAccountService.withdraw(splitAccountWithdrawRequest);

        assertThatThrownBy(() -> splitAccountService.withdraw(splitAccountWithdrawRequest))
            .isInstanceOf(WithdrawRuleViolationException.class)
            .hasMessageContaining("이미 받은 뿌리기는 중복으로 받을 수 없습니다.");
    }


    @Test
    @DisplayName("모두 소진된 뿌리기를 받기를 시도한다.")
    void withdrawError5() {
        LocalDateTime now = LocalDateTime.now();
        SplitAccountCreateRequest splitAccountCreateRequest = SplitAccountCreateRequest.builder()
            .userId(1L)
            .rommId("room")
            .amount(99999)
            .withdrawLimit(3)
            .requestAt(now)
            .build();

        splitAccountService.create(splitAccountCreateRequest);

        SplitAccountWithdrawRequest splitAccountWithdrawRequest1 = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("room")
            .userId(2L)
            .requestAt(now.plusMinutes(1))
            .build();
        SplitAccountWithdrawRequest splitAccountWithdrawRequest2 = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("room")
            .userId(3L)
            .requestAt(now.plusMinutes(1))
            .build();
        SplitAccountWithdrawRequest splitAccountWithdrawRequest3 = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("room")
            .userId(4L)
            .requestAt(now.plusMinutes(1))
            .build();
        SplitAccountWithdrawRequest splitAccountWithdrawRequest4 = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("room")
            .userId(5L)
            .requestAt(now.plusMinutes(1))
            .build();

        splitAccountService.withdraw(splitAccountWithdrawRequest1);
        splitAccountService.withdraw(splitAccountWithdrawRequest2);
        splitAccountService.withdraw(splitAccountWithdrawRequest3);

        assertThatThrownBy(() -> splitAccountService.withdraw(splitAccountWithdrawRequest4))
            .isInstanceOf(WithdrawFailureException.class)
            .hasMessageContaining("더 이상 남은 뿌리기가 없습니다.");
    }

    @Test
    @DisplayName("모두 소진된 뿌리기를 받기를 시도한다.")
    void retrieve() {
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        int amount = 99999;
        SplitAccountCreateRequest splitAccountCreateRequest = SplitAccountCreateRequest.builder()
            .userId(ownerId)
            .rommId("room")
            .amount(amount)
            .withdrawLimit(3)
            .requestAt(now)
            .build();
        splitAccountService.create(splitAccountCreateRequest);

        SplitAccountWithdrawRequest splitAccountWithdrawRequest1 = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("room")
            .userId(2L)
            .requestAt(now.plusMinutes(1))
            .build();
        SplitAccountWithdrawRequest splitAccountWithdrawRequest2 = SplitAccountWithdrawRequest.builder()
            .token("abc")
            .roomId("room")
            .userId(3L)
            .requestAt(now.plusMinutes(1))
            .build();

        splitAccountService.withdraw(splitAccountWithdrawRequest1);
        splitAccountService.withdraw(splitAccountWithdrawRequest2);
        ReceivedHistory receivedHistory1 = ReceivedHistory.builder()
            .userId(2L)
            .amount(33333)
            .build();
        ReceivedHistory receivedHistory2 = ReceivedHistory.builder()
            .userId(3L)
            .amount(33333)
            .build();

        SplitAccountRetrieveRequest splitAccountRetrieveRequest = SplitAccountRetrieveRequest.builder()
            .token("abc")
            .userId(ownerId)
            .requestAt(now.plusDays(1))
            .build();

        SplitAccountRetrieveResponse splitAccountRetrieveResponse = splitAccountService.retrieve(splitAccountRetrieveRequest);

        assertThat(splitAccountRetrieveResponse.getCreatedAt()).isEqualTo(now);
        assertThat(splitAccountRetrieveResponse.getAmount()).isEqualTo(amount);
        assertThat(splitAccountRetrieveResponse.getReceivedAmount()).isEqualTo(66666);
        List<ReceivedHistory> histories = splitAccountRetrieveResponse.getHistories();
        assertThat(histories).hasSize(2);
        assertThat(histories).contains(receivedHistory1);
        assertThat(histories).contains(receivedHistory2);
    }

    @Test
    @DisplayName("뿌리기를 새성하지않은 유저가 조회 요청")
    void retrieveError1() {
        LocalDateTime now = LocalDateTime.now();
        SplitAccountCreateRequest splitAccountCreateRequest = SplitAccountCreateRequest.builder()
            .userId(1L)
            .rommId("room")
            .amount(99999)
            .withdrawLimit(3)
            .requestAt(now)
            .build();
        splitAccountService.create(splitAccountCreateRequest);

        SplitAccountRetrieveRequest splitAccountRetrieveRequest = SplitAccountRetrieveRequest.builder()
            .token("abc")
            .userId(2L)
            .requestAt(now.plusDays(1))
            .build();

        assertThatThrownBy(() -> splitAccountService.retrieve(splitAccountRetrieveRequest))
            .isInstanceOf(RetrieveRuleViolationException.class)
            .hasMessageContaining("자신이 생성한 뿌리기만 조회할 수 있습니다.");
    }

    @Test
    @DisplayName("뿌리기 조회 만료 후 조회 요청")
    void retrieveError2() {
        LocalDateTime now = LocalDateTime.now();
        SplitAccountCreateRequest splitAccountCreateRequest = SplitAccountCreateRequest.builder()
            .userId(1L)
            .rommId("room")
            .amount(99999)
            .withdrawLimit(3)
            .requestAt(now)
            .build();
        splitAccountService.create(splitAccountCreateRequest);

        SplitAccountRetrieveRequest splitAccountRetrieveRequest = SplitAccountRetrieveRequest.builder()
            .token("abc")
            .userId(1L)
            .requestAt(now.plusDays(8))
            .build();

        assertThatThrownBy(() -> splitAccountService.retrieve(splitAccountRetrieveRequest))
            .isInstanceOf(RetrieveRuleViolationException.class)
            .hasMessageContaining("뿌리기 조회가 만료되었습니다.");
    }
}