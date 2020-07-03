package org.kakaopay.recruit.bankingsystem.domain.exception;

public class TransactionStatusChangeValidationException extends RuntimeException {
    public TransactionStatusChangeValidationException() {
    }

    public TransactionStatusChangeValidationException(String message) {
        super(message);
    }

    public TransactionStatusChangeValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionStatusChangeValidationException(Throwable cause) {
        super(cause);
    }

    public TransactionStatusChangeValidationException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
