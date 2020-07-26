package cn.myperf4j.base.config;

/**
 * Created by LinShunkang on 2020/05/24
 */
public final class ConfigKey {

    private final String key;

    private final String legacyKey;

    private ConfigKey(String key, String legacyKey) {
        this.key = key;
        this.legacyKey = legacyKey;
    }

    public String key() {
        return key;
    }

    public String legacyKey() {
        return legacyKey;
    }

    public static ConfigKey of(String key, String legacyKey) {
        return new ConfigKey(key, legacyKey);
    }
}
