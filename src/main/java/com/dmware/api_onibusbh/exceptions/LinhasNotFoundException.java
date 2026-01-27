package com.dmware.api_onibusbh.exceptions;

public class LinhasNotFoundException extends RuntimeException {
    public LinhasNotFoundException(String message) {
        super(message);
    }

    public LinhasNotFoundException() {
        super("Linhas não encontradas");
    }
}
