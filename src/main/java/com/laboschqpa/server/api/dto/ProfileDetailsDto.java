package com.laboschqpa.server.api.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProfileDetailsDto {
    private final Long userAccId;
    private final String firstName;
    private final String lastName;
    private final String nickName;
}
