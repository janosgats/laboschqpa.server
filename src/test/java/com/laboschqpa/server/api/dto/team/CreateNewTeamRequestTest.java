package com.laboschqpa.server.api.dto.team;

import com.laboschqpa.server.exceptions.apierrordescriptor.FieldValidationFailedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateNewTeamRequestTest {

    @Test
    void teamName() {
        CreateNewTeamRequest createNewTeamRequest = new CreateNewTeamRequest();
        createNewTeamRequest.setName("Valid Team Name 69-!áéíóöőúüűÁÉÍÓÖŐÚÜŰ .#Ł$§ +");
        createNewTeamRequest.validateSelf();

        createNewTeamRequest = new CreateNewTeamRequest();
        assertThrows(FieldValidationFailedException.class, createNewTeamRequest::validateSelf);

        createNewTeamRequest = new CreateNewTeamRequest();
        createNewTeamRequest.setName(null);
        assertThrows(FieldValidationFailedException.class, createNewTeamRequest::validateSelf);

        createNewTeamRequest = new CreateNewTeamRequest();
        createNewTeamRequest.setName("");
        assertThrows(FieldValidationFailedException.class, createNewTeamRequest::validateSelf);

        createNewTeamRequest = new CreateNewTeamRequest();
        createNewTeamRequest.setName("    ");
        assertThrows(FieldValidationFailedException.class, createNewTeamRequest::validateSelf);

        createNewTeamRequest = new CreateNewTeamRequest();
        createNewTeamRequest.setName("^team");
        assertThrows(FieldValidationFailedException.class, createNewTeamRequest::validateSelf);

        createNewTeamRequest = new CreateNewTeamRequest();
        createNewTeamRequest.setName(":");
        assertThrows(FieldValidationFailedException.class, createNewTeamRequest::validateSelf);

        createNewTeamRequest = new CreateNewTeamRequest();
        createNewTeamRequest.setName(")ÖÜ:ÉÁ/");
        assertThrows(FieldValidationFailedException.class, createNewTeamRequest::validateSelf);

        createNewTeamRequest = new CreateNewTeamRequest();
        createNewTeamRequest.setName("<");
        assertThrows(FieldValidationFailedException.class, createNewTeamRequest::validateSelf);
    }
}