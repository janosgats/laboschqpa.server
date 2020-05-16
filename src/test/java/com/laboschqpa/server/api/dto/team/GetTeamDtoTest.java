package com.laboschqpa.server.api.dto.team;

import com.laboschqpa.server.exceptions.FieldValidationFailedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateNewTeamDtoTest {

    @Test
    void teamName() {
        CreateNewTeamDto createNewTeamDto = new CreateNewTeamDto();
        createNewTeamDto.setName("Valid Team Name 69-!áéíóöőúüűÁÉÍÓÖŐÚÜŰ");
        createNewTeamDto.validateSelf();

        createNewTeamDto = new CreateNewTeamDto();
        assertThrows(FieldValidationFailedException.class, createNewTeamDto::validateSelf);

        createNewTeamDto = new CreateNewTeamDto();
        assertThrows(FieldValidationFailedException.class, createNewTeamDto::validateSelf);

        createNewTeamDto = new CreateNewTeamDto();
        createNewTeamDto.setName("");
        assertThrows(FieldValidationFailedException.class, createNewTeamDto::validateSelf);

        createNewTeamDto = new CreateNewTeamDto();
        createNewTeamDto.setName("^team");
        assertThrows(FieldValidationFailedException.class, createNewTeamDto::validateSelf);

        createNewTeamDto = new CreateNewTeamDto();
        createNewTeamDto.setName(":");
        assertThrows(FieldValidationFailedException.class, createNewTeamDto::validateSelf);

        createNewTeamDto = new CreateNewTeamDto();
        createNewTeamDto.setName(")ÖÜ:ÉÁ/");
        assertThrows(FieldValidationFailedException.class, createNewTeamDto::validateSelf);

        createNewTeamDto = new CreateNewTeamDto();
        createNewTeamDto.setName("<");
        assertThrows(FieldValidationFailedException.class, createNewTeamDto::validateSelf);
    }
}