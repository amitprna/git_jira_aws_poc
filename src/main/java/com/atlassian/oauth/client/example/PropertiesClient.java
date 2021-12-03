package com.atlassian.oauth.client.example;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertiesClient {
    public static final String CONSUMER_KEY = "consumer_key";
    public static final String PRIVATE_KEY = "private_key";
    public static final String REQUEST_TOKEN = "request_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String SECRET = "secret";
    public static final String JIRA_HOME = "jira_home";


    private final static Map<String, String> DEFAULT_PROPERTY_VALUES = ImmutableMap.<String, String>builder()
            .put(JIRA_HOME, "https://amittelia.atlassian.net/jira")
            .put(CONSUMER_KEY, "OauthKey")
            .put(PRIVATE_KEY, "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOFlGy9LBozZX5BjuqYBux3GxiSMaHmEbRMT2WG+8/eAI9GEUydB9YK5dzcg0rwdcZwgu7hAbT36uA7zZN+INKPVc6Be7pVEZhNuYuJJs493qHnfw0+dYlIua2ZYsr2kd7GVylj0yK45omgywt2b5J1Vs58hMFgndjdp/iiPwtkpAgMBAAECgYEAghL0LkXQ4OXtdYloRLdJAfUdkigJft9Chzj0QamOZ63mfdoslsJE6g0YVJM3qmIfl2d4jet4X8VaBR7Hfwy+uxwgmC02Js0bgNlli8UKcHuz1mgj3oZn8HVSdzVqOil6VYaZSBTgUfWiuqnfGsFcMayfO/PFChB2wEwmoEPOB1ECQQD9rqA9nM8sb5qYeiCuwrLL0GjddCgnnlM0hf7uevkDiicQ9gT9OVko/UjoKrstdNUJLC1qdbIzDdSof1nYauB9AkEA43RQsxofCijHCQ0QBhMpkCMvXe3GVW2ifkeFE4ce57Y9lgJ+LP1PySRtKVkNcDzLGjl6ZeWjN3yxjR6UmNQHHQJBAIfG4GM5u7UX4tG7hCSldtcZrBbjZd6WoggZs/HmhcM8PV6Znt/9wtfqgQwqWtagkql/KKuzZPZob6rjxq3Lvm0CQQCRhRQwUhDzxfz5DHNo/4+C2Q/v7zUJ6NeiJQ293Z/nLRAfzj59eB84l+aDIqdXXH2wrH9je9S9pjhOCfeWA2j9AkBMfwnwtTTXJCDgtI3DZkRQsaLxE3ZUq7QsDw5XdtM1BMTho+VbXViEUNDP0Nl6Qg5JhptScDbpeMI4C55/lFIV")
            .build();

    private final String fileUrl;
    private final String propFileName = "config.properties";

    public PropertiesClient() throws Exception {
        fileUrl = "./" + propFileName;
    }

    public Map<String, String> getPropertiesOrDefaults() {
        try {
            Map<String, String> map = toMap(tryGetProperties());
            map.putAll(Maps.difference(map, DEFAULT_PROPERTY_VALUES).entriesOnlyOnRight());
            return map;
        } catch (FileNotFoundException e) {
            tryCreateDefaultFile();
            return new HashMap<>(DEFAULT_PROPERTY_VALUES);
        } catch (IOException e) {
            return new HashMap<>(DEFAULT_PROPERTY_VALUES);
        }
    }

    private Map<String, String> toMap(Properties properties) {
        return properties.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(o -> o.getKey().toString(), t -> t.getValue().toString()));
    }

    private Properties toProperties(Map<String, String> propertiesMap) {
        Properties properties = new Properties();
        propertiesMap.entrySet()
                .stream()
                .forEach(entry -> properties.put(entry.getKey(), entry.getValue()));
        return properties;
    }

    private Properties tryGetProperties() throws IOException {
        InputStream inputStream = new FileInputStream(new File(fileUrl));
        Properties prop = new Properties();
        prop.load(inputStream);
        return prop;
    }

    public void savePropertiesToFile(Map<String, String> properties) {
        OutputStream outputStream = null;
        File file = new File(fileUrl);

        try {
            outputStream = new FileOutputStream(file);
            Properties p = toProperties(properties);
            p.store(outputStream, null);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            closeQuietly(outputStream);
        }
    }

    public void tryCreateDefaultFile() {
        System.out.println("Creating default properties file: " + propFileName);
        tryCreateFile().ifPresent(file -> savePropertiesToFile(DEFAULT_PROPERTY_VALUES));
    }

    private Optional<File> tryCreateFile() {
        try {
            File file = new File(fileUrl);
            file.createNewFile();
            return Optional.of(file);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // ignored
        }
    }
}
