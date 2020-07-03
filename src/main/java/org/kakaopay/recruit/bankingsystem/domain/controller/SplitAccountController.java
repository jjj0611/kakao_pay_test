package org.kakaopay.recruit.bankingsystem.domain.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.kakaopay.recruit.bankingsystem.domain.service.SplitAccountService;
import org.kakaopay.recruit.bankingsystem.domain.service.dto.request.SplitAccountCreateRequest;
import org.springframework.http.ResponseEntity;
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
}
