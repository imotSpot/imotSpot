package com.imotspot.config;

import com.imotspot.enumerations.ConfigKey;
import com.imotspot.logging.Logger;
import com.imotspot.logging.LoggerFactory;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration {

    private final static Logger logger = LoggerFactory.getLogger(Configuration.class);
    private final static File CONFIG_FILE = new File("config.yml");
    public final static File CONFIG_ACCOUNT_SPECIFIC_FILE = new File("config_specific.yml");

    private final Map<String, Object> config;
    private final File file;

    public static final Configuration configuration = new Configuration();

    public static Configuration getDefaultConfig() {
        return configuration;
    }

    private Configuration() {
        this(CONFIG_FILE);
    }

    public Configuration(File file) {
        this.file = file;

        Map<String, Object> configTemp;
        try {
            configTemp = Yaml.loadType(file, LinkedHashMap.class);
        } catch (Exception e) {
            configTemp = new HashMap<>();
            if (file.equals(CONFIG_FILE)) {
                for (ConfigKey configKey : ConfigKey.values()) {
                    configTemp.put(configKey.name(), configKey.value());
                }
                logger.error("can't load config {}", e.getMessage());

                try {
                    logger.info("Creating default config");
                    Yaml.dump(configTemp, file);
                } catch (FileNotFoundException ee) {
                    logger.warn("Error saving config", ee);
                }
            }
        }
        config = configTemp;
    }

    public void addConfig(ConfigKey key, Object value) {
        addConfig(key, value, false);
    }

    public void addConfig(ConfigKey key, Object value, boolean persist) {
        config.put(key.name(), value);

        if (persist) {
            saveConfig();
        }
    }

    public Object getConfig(ConfigKey key) {
        Object value = config.get(key.name());
        if (value == null) {
            return key.value();
        }
        return value;
    }

    public String getConfigAsString(ConfigKey key) {
        return (String) getConfig(key);
    }

    public Integer getConfigAsInteger(ConfigKey key) {
        Object value = getConfig(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return Integer.valueOf(value.toString());
    }

    public Double getConfigAsDouble(ConfigKey key) {
        Object value = getConfig(key);
        if (value instanceof Double) {
            return (Double) value;
        }
        return Double.valueOf(value.toString());
    }

    public Boolean getConfigAsBoolean(ConfigKey key) {
        Object value = getConfig(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.valueOf(value.toString());
    }

    public Map<String, Object> asMap() {
        return new HashMap<>(config);
    }

    public void saveConfig() {
        try {
            Yaml.dump(config, file);
        } catch (FileNotFoundException e) {
            logger.warn("Error saving config", e);
        }
    }
}
