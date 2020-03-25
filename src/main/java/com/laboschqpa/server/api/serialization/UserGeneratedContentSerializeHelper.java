package com.laboschqpa.server.api.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.laboschqpa.server.entity.usergeneratedcontent.UserGeneratedContent;

import java.io.IOException;
import java.util.Objects;

public class UserGeneratedContentSerializeHelper {
    public static void serializeUserGeneratedContentIntoJsonGenerator(UserGeneratedContent newsPost, JsonGenerator jgen) throws IOException {
        Objects.requireNonNull(newsPost.getId());
        Objects.requireNonNull(newsPost.getCreationTime());
        Objects.requireNonNull(newsPost.getEditTime());
        Objects.requireNonNull(newsPost.getCurrentOwnerUser());
        Objects.requireNonNull(newsPost.getOriginalOwnerUser());
        Objects.requireNonNull(newsPost.getOriginalEditorUser());

        jgen.writeNumberField("id", newsPost.getId());
        jgen.writeNumberField("creationTime", newsPost.getCreationTime().toEpochMilli());
        jgen.writeNumberField("editTime", newsPost.getEditTime().toEpochMilli());
        jgen.writeNumberField("currentOwnerUserId", newsPost.getCurrentOwnerUser().getId());
        jgen.writeNumberField("originalOwnerUserId", newsPost.getOriginalOwnerUser().getId());
        jgen.writeNumberField("currentEditorUserId", newsPost.getCurrentEditorUser().getId());
        jgen.writeNumberField("originalEditorUserId", newsPost.getOriginalEditorUser().getId());
    }
}