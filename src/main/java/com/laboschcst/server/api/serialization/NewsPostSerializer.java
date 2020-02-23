package com.laboschcst.server.api.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.laboschcst.server.entity.usergeneratedcontent.NewsPost;

import java.io.IOException;

public class NewsPostSerializer extends ErrorLoggingSerializer<NewsPost> {

    public NewsPostSerializer() {
        this(null);
    }

    public NewsPostSerializer(Class<NewsPost> t) {
        super(t);
    }

    @Override
    public void wrappedSerialize(NewsPost newsPost, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        UserGeneratedContentSerializeHelper.serializeUserGeneratedContentIntoJsonGenerator(newsPost, jgen);
        jgen.writeStringField("content", newsPost.getContent());

        jgen.writeEndObject();
    }
}