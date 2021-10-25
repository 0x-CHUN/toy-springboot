package springboot.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractConfiguration implements Configuration {
    private static final Map<String, String> CONFIGURATION_CACHE = new ConcurrentHashMap<>();

    @Override
    public int getInt(String id) {
        String res = CONFIGURATION_CACHE.get(id);
        return Integer.parseInt(res);
    }

    @Override
    public String getString(String id) {
        return CONFIGURATION_CACHE.get(id);
    }

    @Override
    public Boolean getBoolean(String id) {
        String res = CONFIGURATION_CACHE.get(id);
        return Boolean.parseBoolean(res);
    }

    @Override
    public void put(String id, String content) {
        CONFIGURATION_CACHE.put(id, content);
    }

    @Override
    public void putAll(Map<String, String> maps) {
        CONFIGURATION_CACHE.putAll(maps);
    }
}
