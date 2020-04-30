package com.laboschqpa.server.config.userservice;

public enum UserAccountResolvingResult {
    ByBoth,
    OnlyByExternalAccountDetail,
    OnlyByEmail,
    ByNeither;
}
