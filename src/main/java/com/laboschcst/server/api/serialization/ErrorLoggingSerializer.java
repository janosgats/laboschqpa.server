package com.laboschcst.server.api.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.laboschcst.server.api.errorhandling.ApiErrorResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class ErrorLoggingSerializer<T> extends StdSerializer<T> {
    private static final Logger logger = LoggerFactory.getLogger(ErrorLoggingSerializer.class);

    protected ErrorLoggingSerializer(Class<T> t) {
        super(t);
    }

    @Override
    public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        try {
            wrappedSerialize(t, jsonGenerator, serializerProvider);
        } catch (Exception e) {
            logger.error("Exception while serializing with SafeSerializer.", e);
            throw new RuntimeException("Exception ReThrown in ErrorLoggingSerializer.", e);
        }
    }

    protected abstract void wrappedSerialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException;
}
