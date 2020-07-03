package org.kakaopay.recruit.bankingsystem.domain.controller;

import java.net.URI;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;

import org.kakaopay.recruit.bankingsystem.domain.service.SplitAccountService;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.request.SplitAccountCreateRequest;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.request.SplitAccountWithdrawRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/split-account")
public class SplitAccountController {

    private final SplitAccountService splitAccountService;

    @PostMapping
    public ResponseEntity<String> create(HttpServletRequest httpRequest, @RequestBody
        SplitAccountCreateRequest splitAccountRequest) {
        Long userId = Long.valueOf(httpRequest.getHeader("X-USER-ID"));
        String roomId = httpRequest.getHeader("X-ROOM-ID");
        splitAccountRequest.setUserId(userId);
        splitAccountRequest.setRoomId(roomId);
        String token = splitAccountService.create(splitAccountRequest);

        return ResponseEntity
            .created(URI.create("/api/split-account/" + token))
            .body(token);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Long> withdraw(HttpServletRequest httpRequest, @PathVariable String token) {
        Long userId = Long.valueOf(httpRequest.getHeader("X-USER-ID"));
        String roomId = httpRequest.getHeader("X-ROOM-ID");

        SplitAccountWithdrawRequest splitAccountWithdrawRequest = SplitAccountWithdrawRequest.builder()
            .token(token)
            .userId(userId)
            .roomId(roomId)
            .requestAt(LocalDateTime.now())
            .build();

        long amount = splitAccountService.withdraw(splitAccountWithdrawRequest);

        return ResponseEntity.ok(amount);
    }

}
