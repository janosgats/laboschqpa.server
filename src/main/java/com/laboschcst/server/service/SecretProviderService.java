package com.laboschcst.server.service;

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
            String[] parts = s.replace("\n", "").replace("\r", "").trim().split("=", 2);

            if (parts.length < 2)
                return;

            keyValues.put(parts[0], parts[1]);
        });

        lines.close();
    }
}
