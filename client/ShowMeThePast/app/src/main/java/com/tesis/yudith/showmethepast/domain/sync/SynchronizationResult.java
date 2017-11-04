package com.tesis.yudith.showmethepast.domain.sync;

import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;

import java.util.ArrayList;
import java.util.List;

public class SynchronizationResult {
    private List<MongoCollection> list;

    public SynchronizationResult() {
        this.list = new ArrayList<>();
    }

    public List<MongoCollection> getList() {
        return list;
    }

    public void setList(List<MongoCollection> list) {
        this.list = list;
    }
}
