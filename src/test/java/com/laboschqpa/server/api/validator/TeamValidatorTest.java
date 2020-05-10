package com.laboschqpa.server.api.validator;

import com.laboschqpa.server.exceptions.FieldValidationFailedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeamValidatorTest {

    @Test
    void teamName() {
        TeamValidator teamValidator = new TeamValidator();
        teamValidator.setId(1L);
        teamValidator.setName("Valid Team Name 69-!áéíóöőúüűÁÉÍÓÖŐÚÜŰ");
        teamValidator.validateSelf();

        teamValidator = new TeamValidator();
        assertThrows(FieldValidationFailedException.class, teamValidator::validateSelf);

        teamValidator = new TeamValidator();
        teamValidator.setId(1L);
        assertThrows(FieldValidationFailedException.class, teamValidator::validateSelf);

        teamValidator = new TeamValidator();
        teamValidator.setId(1L);
        teamValidator.setName("");
        assertThrows(FieldValidationFailedException.class, teamValidator::validateSelf);

        teamValidator = new TeamValidator();
        teamValidator.setId(1L);
        teamValidator.setName("^team");
        assertThrows(FieldValidationFailedException.class, teamValidator::validateSelf);

        teamValidator = new TeamValidator();
        teamValidator.setId(1L);
        teamValidator.setName(":");
        assertThrows(FieldValidationFailedException.class, teamValidator::validateSelf);

        teamValidator = new TeamValidator();
        teamValidator.setId(1L);
        teamValidator.setName(")ÖÜ:ÉÁ/");
        assertThrows(FieldValidationFailedException.class, teamValidator::validateSelf);

        teamValidator = new TeamValidator();
        teamValidator.setId(1L);
        teamValidator.setName("<");
        assertThrows(FieldValidationFailedException.class, teamValidator::validateSelf);
    }
}