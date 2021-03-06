package org.unicef.rapidreg.exception;

public class ObservableNullResponseException extends Exception {
    public ObservableNullResponseException() {
        super("Observable returned null");
    }

    public ObservableNullResponseException(String message) {
        super(message);
    }
}
