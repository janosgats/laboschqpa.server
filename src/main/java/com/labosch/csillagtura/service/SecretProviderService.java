package com.labosch.csillagtura.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.stream.Stream;

@Service
public class SecretProviderService {

    @Value("${secret.api-keys.file.location}")
    private String secretApiKeysFilelocation;

    private Hashtable<String, String> keyValues = new Hashtable<>();//Hashtable is threadsafe

    public String getSecret(String key) {
        return keyValues.get(key);
    }

    @PostConstruct
    private void loadSecretsFromFiles() throws IOException {

        Path path = Path.of(secretApiKeysFilelocation);
        Stream<String> lines = Files.lines(path);

        lines.forEach((String s) -> {
            String[] parts = s.replace("\n", "").replace("\r", "").trim().split("=");
            if (parts.length < 2)
                return;

            String key = parts[0];
            StringBuilder value = new StringBuilder();

            for (int i = 1; i < parts.length; ++i)
                value.append(parts[i]);

            keyValues.put(key, value.toString());
        });

        lines.close();
    }
}
