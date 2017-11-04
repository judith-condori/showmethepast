package com.tesis.yudith.showmethepast.configuration;

import java.util.HashMap;
import java.util.Map;

public class ObjectsCache {
    private Map<String, Object> mapCache;

    public ObjectsCache() {
        this.mapCache = new HashMap<>();
    }

    public void clear() {
        this.mapCache.clear();
    }

    public boolean containsKey(String key) {
        return this.mapCache.containsKey(key);
    }

    public  Object get(String key) {
        if (this.mapCache.containsKey(key)) {
            return mapCache.get(key);
        }
        return null;
    }
}
