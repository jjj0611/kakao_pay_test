package org.kakaopay.recruit.bankingsystem.domain.exception;

public class WithdrawRuleViolationException extends IllegalArgumentException {
    public WithdrawRuleViolationException() {
    }

    public WithdrawRuleViolationException(String s) {
        super(s);
    }

    public WithdrawRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WithdrawRuleViolationException(Throwable cause) {
        super(cause);
    }
}
