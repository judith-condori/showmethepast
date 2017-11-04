package com.tesis.yudith.showmethepast.domain.position;

import com.google.android.gms.maps.model.LatLng;
import com.tesis.yudith.showmethepast.domain.collections.childs.MultiLanguageString;

public class PositionForEntity {
    private LatLng position;
    private Class<?> entityClass;
    private String id;
    private MultiLanguageString title;

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MultiLanguageString getTitle() {
        return title;
    }

    public void setTitle(MultiLanguageString title) {
        this.title = title;
    }
}
