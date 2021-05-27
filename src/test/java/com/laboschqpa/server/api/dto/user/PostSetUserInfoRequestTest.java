package com.laboschqpa.server.api.dto.user;

import com.laboschqpa.server.exceptions.apierrordescriptor.FieldValidationFailedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PostSetUserInfoRequestTest {
    @Test
    void nickName() {
        PostSetUserInfoRequest postSetUserInfoRequest;
        postSetUserInfoRequest = createValidWithNickName("Valid Nick Name 69-!áéíóöőúüűÁÉÍÓÖŐÚÜŰ,()");
        postSetUserInfoRequest.validateSelf();

        postSetUserInfoRequest = createValidWithNickName("^nickname");
        assertThrows(FieldValidationFailedException.class, postSetUserInfoRequest::validateSelf);

        postSetUserInfoRequest = createValidWithNickName("#");
        assertThrows(FieldValidationFailedException.class, postSetUserInfoRequest::validateSelf);

        postSetUserInfoRequest = createValidWithNickName(")ÖÜ#ÉÁ/");
        assertThrows(FieldValidationFailedException.class, postSetUserInfoRequest::validateSelf);

        postSetUserInfoRequest = createValidWithNickName("<");
        assertThrows(FieldValidationFailedException.class, postSetUserInfoRequest::validateSelf);
    }

    private PostSetUserInfoRequest createValidWithNickName(String nickName) {
        PostSetUserInfoRequest created = new PostSetUserInfoRequest();
        created.setNickName(nickName);
        created.setUserId(1L);
        created.setFirstName("First");
        created.setLastName("Last");

        return created;
    }
}