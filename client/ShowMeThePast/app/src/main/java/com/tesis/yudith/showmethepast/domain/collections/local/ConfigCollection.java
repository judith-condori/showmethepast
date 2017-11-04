package com.tesis.yudith.showmethepast.domain.collections.local;

import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;

public class ConfigCollection extends MongoCollection {
    private long lastSynchronization;

    public long getLastSynchronization() {
        return lastSynchronization;
    }

    public void setLastSynchronization(long lastSynchronization) {
        this.lastSynchronization = lastSynchronization;
    }
}
