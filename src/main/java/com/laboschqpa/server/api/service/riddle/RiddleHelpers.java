package com.laboschqpa.server.api.service.riddle;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RiddleHelpers {
    private static final Map<String, String> stringReplacementMap = new HashMap<>();

    static {
        stringReplacementMap.put("á", "a");
        stringReplacementMap.put("é", "é");
        stringReplacementMap.put("í", "i");
        stringReplacementMap.put("ú", "u");
        stringReplacementMap.put("ü", "u");
        stringReplacementMap.put("ű", "u");
        stringReplacementMap.put("ó", "o");
        stringReplacementMap.put("ö", "o");
        stringReplacementMap.put("ő", "o");

        final String[] stringsToRemove = new String[]{" ", "\t", "/", "!", ".", "?", "-", "_", "(", ")", ":", "+"};
        for (String toRemove : stringsToRemove) {
            stringReplacementMap.put(toRemove, "");
        }
    }

    public static boolean areSolutionsSimilar(String a, String b) {
        return simplifyByStringReplacementMap(a).equals(simplifyByStringReplacementMap(b));
    }

    private static String simplifyByStringReplacementMap(final String original) {
        String processed = original.toLowerCase();
        for (String key : stringReplacementMap.keySet()) {
            processed = StringUtils.replace(processed, key, stringReplacementMap.get(key));
        }
        return processed;
    }
}
