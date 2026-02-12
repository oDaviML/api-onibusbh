package com.dmware.api_onibusbh.exceptions;

public class CoordenadasApiIntegrationException extends RuntimeException {
    private final String endpoint;
    private final String errorType;

    public CoordenadasApiIntegrationException(String message, String endpoint, String errorType, Throwable cause) {
        super(message, cause);
        this.endpoint = endpoint;
        this.errorType = errorType;
    }

    public CoordenadasApiIntegrationException(String endpoint, String errorType, Throwable cause) {
        super("Falha na integração com API externa de coordenadas");
        this.endpoint = endpoint;
        this.errorType = errorType;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getErrorType() {
        return errorType;
    }
}
