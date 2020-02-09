package com.laboschcst.server.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class ApiSupplierExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ApiSupplierExecutor.class);

    public JsonObject executeAndGetJsonObjectOrCatch(Supplier<JsonObject> jsonObjectSupplier) {
        JsonObject outJsonObject = new JsonObject();

        try {
            JsonObject resultJsonObject = jsonObjectSupplier.get();

            outJsonObject.add("result", resultJsonObject);
            outJsonObject.add("success", new JsonPrimitive(true));
        } catch (Exception e) {
            logger.error("Exception caught in executeAndGetJsonObject()!", e);
            outJsonObject.add("success", new JsonPrimitive(false));
        }

        return outJsonObject;
    }
}
