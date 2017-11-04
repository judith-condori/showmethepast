package com.tesis.yudith.showmethepast.domain.sync;

import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;

import java.util.ArrayList;
import java.util.List;

public class ProcessedSyncResult {
    private List<MongoCollection> modifications;
    private List<MongoCollection> deletions;
    private int currentModification;
    private int currentDeletion;

    public ProcessedSyncResult() {
        this.modifications = new ArrayList<>();
        this.deletions = new ArrayList<>();
        this.currentDeletion = 0;
        this.currentModification = 0;
    }

    public List<MongoCollection> getModifications() {
        return modifications;
    }

    public void setModifications(List<MongoCollection> modifications) {
        this.modifications = modifications;
    }

    public List<MongoCollection> getDeletions() {
        return deletions;
    }

    public void setDeletions(List<MongoCollection> deletions) {
        this.deletions = deletions;
    }

    public int getCurrentModification() {
        return currentModification;
    }

    public void setCurrentModification(int currentModification) {
        this.currentModification = currentModification;
    }

    public int getCurrentDeletion() {
        return currentDeletion;
    }

    public void setCurrentDeletion(int currentDeletion) {
        this.currentDeletion = currentDeletion;
    }
}
