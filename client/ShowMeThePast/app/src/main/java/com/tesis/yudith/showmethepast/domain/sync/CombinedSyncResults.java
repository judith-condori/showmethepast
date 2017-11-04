package com.tesis.yudith.showmethepast.domain.sync;

import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;

import java.util.ArrayList;
import java.util.List;

public class CombinedSyncResults {
    private List<MongoCollection> creations;
    private List<MongoCollection> editions;
    private List<MongoCollection> deletions;

    public CombinedSyncResults() {
        this.creations = new ArrayList<>();
        this.editions = new ArrayList<>();
        this.deletions = new ArrayList<>();
    }

    public List<MongoCollection> getCreations() {
        return creations;
    }

    public void setCreations(List<MongoCollection> creations) {
        this.creations = creations;
    }

    public List<MongoCollection> getEditions() {
        return editions;
    }

    public void setEditions(List<MongoCollection> editions) {
        this.editions = editions;
    }

    public List<MongoCollection> getDeletions() {
        return deletions;
    }

    public void setDeletions(List<MongoCollection> deletions) {
        this.deletions = deletions;
    }
}
