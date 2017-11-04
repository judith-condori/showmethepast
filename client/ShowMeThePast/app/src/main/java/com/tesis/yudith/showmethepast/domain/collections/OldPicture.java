package com.tesis.yudith.showmethepast.domain.collections;

import com.tesis.yudith.showmethepast.domain.collections.annotations.ServerCollectionName;
import com.tesis.yudith.showmethepast.domain.collections.childs.ARPosition;
import com.tesis.yudith.showmethepast.domain.collections.childs.MultiLanguageString;

import java.io.Serializable;

@ServerCollectionName(collectionName = "oldPictures")
public class OldPicture extends MongoCollection implements Serializable {
    private MultiLanguageString name;
    private MultiLanguageString description;
    private String image;

    private ARPosition position;
    private String touristicPlace;
    private String owner;

    public OldPicture() {
        this.setName(new MultiLanguageString());
        this.setDescription(new MultiLanguageString());
        this.setPosition(new ARPosition());
    }

    public MultiLanguageString getName() {
        return name;
    }

    public void setName(MultiLanguageString name) {
        this.name = name;
    }

    public MultiLanguageString getDescription() {
        return description;
    }

    public void setDescription(MultiLanguageString description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ARPosition getPosition() {
        return position;
    }

    public void setPosition(ARPosition position) {
        this.position = position;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTouristicPlace() {
        return touristicPlace;
    }

    public void setTouristicPlace(String touristicPlace) {
        this.touristicPlace = touristicPlace;
    }
}
