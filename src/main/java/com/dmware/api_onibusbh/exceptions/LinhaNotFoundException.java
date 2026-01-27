package com.dmware.api_onibusbh.exceptions;

public class LinhaNotFoundException extends RuntimeException {
    public LinhaNotFoundException(String message) {
        super(message);
    }

    public LinhaNotFoundException() {
        super("Linha não encontrada");
    }
}
