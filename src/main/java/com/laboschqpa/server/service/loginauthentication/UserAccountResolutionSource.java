package com.laboschqpa.server.service.loginauthentication;

public enum UserAccountResolutionSource {
    ByBoth,
    OnlyByExternalAccountDetail,
    OnlyByEmail,
    ByNeither;
}
