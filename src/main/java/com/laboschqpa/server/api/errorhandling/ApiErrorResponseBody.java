package com.laboschqpa.server.api.errorhandling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laboschqpa.server.exceptions.apierrordescriptor.ApiErrorDescriptorException;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrorResponseBody {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Integer apiErrorCode;
    private String apiErrorName;
    private String apiErrorCategory;

    private String message;
    private JsonNode payload;
    private Boolean payloadSerializationError = false;

    public ApiErrorResponseBody(String message) {
        this(null, message);
    }

    public ApiErrorResponseBody(ApiErrorDescriptor apiErrorDescriptor) {
        this(apiErrorDescriptor, null);
    }

    public ApiErrorResponseBody(ApiErrorDescriptor apiErrorDescriptor, String message) {
        this(apiErrorDescriptor, message, null);
    }

    public ApiErrorResponseBody(ApiErrorDescriptorException exception) {
        this(exception.getApiErrorDescriptor(), exception.getMessage(), exception.getPayload());
    }

    public ApiErrorResponseBody(ApiErrorDescriptor apiErrorDescriptor, String message, Object payload) {
        if (apiErrorDescriptor != null) {
            this.apiErrorCategory = apiErrorDescriptor.getApiErrorCategory();
            this.apiErrorCode = apiErrorDescriptor.getApiErrorCode();
            this.apiErrorName = apiErrorDescriptor.getApiErrorName();
        }

        this.message = message;

        if (payload != null) {
            try {
                this.payload = objectMapper.valueToTree(payload);
            } catch (Exception e) {
                payloadSerializationError = true;
            }
        }
    }
}
