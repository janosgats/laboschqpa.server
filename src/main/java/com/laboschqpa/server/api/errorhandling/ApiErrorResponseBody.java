package com.laboschqpa.server.api.errorhandling;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.laboschqpa.server.enums.ApiErrorResponseKeys;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrorResponseBody {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ApiErrorResponseKeys key;
    private String message;

    public ApiErrorResponseBody(String message) {
        this.message = message;
    }

    public ApiErrorResponseBody(ApiErrorResponseKeys key) {
        this.key = key;
    }

    @JsonValue
    public ObjectNode toJson() {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.put("message", message);
        if (key != null) {
            objectNode.put("apiError", key.toString());
            objectNode.put("apiErrorNumber", key.getErrorResponseNumber());
        }
        return objectNode;
    }
}
