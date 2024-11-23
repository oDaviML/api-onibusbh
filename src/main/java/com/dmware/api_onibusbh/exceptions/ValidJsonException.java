package com.dmware.api_onibusbh.exceptions;

public class ValidJsonException extends RuntimeException {
      public ValidJsonException(String message) {
            super(message);
      }

      public ValidJsonException() {
            super("Json inválido");
      }
}
