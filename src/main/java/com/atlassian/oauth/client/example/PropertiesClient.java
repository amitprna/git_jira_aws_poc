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
            .put(PRIVATE_KEY, "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANbkITpCxJJCeFxYMnZ8OrjPRdGpcmkLpxw79IxR6dvfGCKPM6rOLK2DneyZXRY4NZz/BPRPeOqyHf20EjTWXuiaD47cAuFQCqKoOpbTJIk/vJ8YSDwSaMe6vKkUTQDjzY8N9ZyM+QeU/gZ6HSnam7PyGwiBVQyDfP6r9IdtsRR9AgMBAAECgYBuE7AdqwQMs/X5v8ghv6NI9gwayUTtIGX65Y9wrakw0wgG8/oQfcqPG4OzJiBz+FkmUdAU4fzbpGAOjMZiwIkehqH0E1uKipWj3xwVsFzWpT5i0wltKaGQCMY/vnVHMPKYWFWpC8hN+TdqU6kz9Nh7rtGS4uA98+xq58GlaSSL4QJBAPuT0lngRumWvuCtHeQ8fUzlYF5nfZR5GetEtF+/t5GuzQ9OWuRrSUVVs8yxAWhyI5G9Ggpgt4JlHSvtfxaB3tUCQQDaqzVH2vvmifxAhqQwpJkRX6LyTj8iyBTkH86pyr54uvdxfNb+z+e8u0awefC/e56M2GLX9YXgK62k/gh21MMJAkEAtL7eeR4ONJLOboNSH4FqEI4Xr5uw+LQM8B/7a3NFQtCQmTeVS9jc2oiZVi2xskWW0oVHKT+VeISG8eF+1j/WRQJBAKpJ3FNaAQO2/3/Mmr7a3+08uivSra5LfKFu98UYg56Hk72Ih/GmPQSoH1O9krO79Gwg81DU/m3l9fNgdXz9EekCQQDPss+cTl/RtQPB1gKOgZEMs49nWAvaSSvuq/xHbNT9GNBanSgZ6UeWQLDlvZSNpHisLTuEhuWsz40sB7s/RXNT")
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
