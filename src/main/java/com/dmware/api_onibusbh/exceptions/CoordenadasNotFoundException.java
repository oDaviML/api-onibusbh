package com.dmware.api_onibusbh.exceptions;

public class CoordenadasNotFoundException extends RuntimeException {
      public CoordenadasNotFoundException(String message) {
            super(message);
      }

      public CoordenadasNotFoundException() {
            super("Coordenadas não encontradas");
      }

}
