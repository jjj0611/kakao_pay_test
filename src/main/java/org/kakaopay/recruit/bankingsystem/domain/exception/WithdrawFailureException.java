package org.kakaopay.recruit.bankingsystem.domain.exception;

public class WithdrawFailureException extends RuntimeException {
    public WithdrawFailureException() {
    }

    public WithdrawFailureException(String message) {
        super(message);
    }

    public WithdrawFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public WithdrawFailureException(Throwable cause) {
        super(cause);
    }

    public WithdrawFailureException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
