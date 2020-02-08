package com.laboschcst.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Configuration
public class SecretPropertyLoadConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecretPropertyLoadConfig.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(Environment env) {
        loadSecretProperties(env);
        return new PropertySourcesPlaceholderConfigurer();//Returning an empty bean
    }

    public static void loadSecretProperties(Environment env) {
        try {
            Path apiKeysFilePath = getApiKeysFilePath(env);
            logger.info("Path of api-keys properties: " + apiKeysFilePath.toAbsolutePath());
            Path dbconfigFilePath = getDbConfigFilePath(env);
            logger.info("Path of dbconfig properties: " + dbconfigFilePath.toAbsolutePath());

            setSystemPropertiesLoadedFromPropertiesFile(apiKeysFilePath);
            setSystemPropertiesLoadedFromPropertiesFile(dbconfigFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setSystemPropertiesLoadedFromPropertiesFile(Path pathToFile) throws IOException {
        Stream<String> lines = Files.lines(pathToFile);

        lines.forEach((String s) -> {
            String[] parts = s
                    .replace("\n", "")
                    .replace("\r", "")
                    .trim()
                    .split("=", 2);

            if (parts.length < 2)
                return;

            System.setProperty(parts[0].trim(), parts[1].trim());
        });

        lines.close();
    }

    private static Path getApiKeysFilePath(Environment env) {
        return FileSystems.getDefault().getPath(Objects.requireNonNull(env.getProperty("secret.api-keys.file.location")));
    }

    private static Path getDbConfigFilePath(Environment env) {
        String secretScopeToLoad = "k8s_dev";
        if (Arrays.asList(env.getActiveProfiles()).contains("local_bare"))
            secretScopeToLoad = "local_bare";
        else if (Arrays.asList(env.getActiveProfiles()).contains("prod"))
            secretScopeToLoad = "prod";

        return FileSystems.getDefault().getPath("dbconfig", "dbconfig-" + secretScopeToLoad + ".properties");
    }
}

