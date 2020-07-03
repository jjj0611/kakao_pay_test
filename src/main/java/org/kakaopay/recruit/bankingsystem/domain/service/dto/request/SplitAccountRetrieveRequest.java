package org.kakaopay.recruit.bankingsystem.domain.service.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SplitAccountRetrieveRequest {
    private String token;
    private Long userId;
    private LocalDateTime requestAt;

    @Builder
    public SplitAccountRetrieveRequest(String token, Long userId, LocalDateTime requestAt) {
        this.token = token;
        this.userId = userId;
        this.requestAt = requestAt;
    }
}
