package org.kakaopay.recruit.bankingsystem.domain.exception;

public class RetrieveRuleViolationException extends IllegalArgumentException {
    public RetrieveRuleViolationException() {
    }

    public RetrieveRuleViolationException(String s) {
        super(s);
    }

    public RetrieveRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrieveRuleViolationException(Throwable cause) {
        super(cause);
    }
}
