package com.laboschcst.server.logging;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class LoggingHelper {
    static final String errorFormat = "%s: %s";

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stackTrace = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stackTrace));
        return stackTrace.toString();
    }

    public static String getExceptionInOneLine(Throwable exception) {
        Gson gson = new Gson();
        JsonObject jsonRoot = new JsonObject();
        jsonRoot.add("errors", unwindCausesToList(exception));

        return gson.toJson(jsonRoot);
    }

    /**
     * @param exception Throwable with possible causes
     * @return List of descriptor objects of the exception and causes
     */
    protected static JsonArray unwindCausesToList(Throwable exception) {
        JsonArray errors = new JsonArray();
        while (exception != null) {
            addExceptionToJsonList(exception, errors);
            exception = exception.getCause();
        }
        return errors;
    }

    protected static void addExceptionToJsonList(Throwable exception, JsonArray list) {
        String errorMessage = String.format(errorFormat, exception.getClass().getName(), exception.getMessage());
        JsonArray traceJson = new JsonArray();
        Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).forEach(traceJson::add);
        JsonObject descriptorObject = new JsonObject();
        descriptorObject.add("error", new JsonPrimitive(errorMessage));
        descriptorObject.add("trace", traceJson);
        list.add(descriptorObject);
    }
}
