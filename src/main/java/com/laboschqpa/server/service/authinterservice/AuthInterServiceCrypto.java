package com.laboschqpa.server.service.authinterservice;

public interface AuthInterServiceCrypto {
    boolean isHeaderValid(String authInterServiceHeader);

    String generateHeader();
}
