package com.tesis.yudith.showmethepast.configuration;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;

public class NitriteManager {

    public final static ObjectFilter ALL_FILTER = ObjectFilters.not(ObjectFilters.eq("_id", ""));

    private Nitrite db;

    public NitriteManager(String dbPath) {
        this.db = Nitrite.builder().compressed().filePath(dbPath).openOrCreate();
                    //.openOrCreate("user", "password");
    }

    public Nitrite getDb() {
        return db;
    }

}
