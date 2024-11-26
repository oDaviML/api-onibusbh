package com.dmware.api_onibusbh.exceptions;

public class DicionarioNotFoundException extends RuntimeException {
    public DicionarioNotFoundException(String message) {
        super(message);
    }

    public DicionarioNotFoundException() {
        super("Dicionário não econtrado");
    }
}
