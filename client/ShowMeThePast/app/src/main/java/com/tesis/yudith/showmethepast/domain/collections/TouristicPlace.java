package com.tesis.yudith.showmethepast.domain.collections;

import com.tesis.yudith.showmethepast.domain.collections.annotations.ServerCollectionName;
import com.tesis.yudith.showmethepast.domain.collections.childs.GPSPosition;
import com.tesis.yudith.showmethepast.domain.collections.childs.MultiLanguageString;

import java.util.ArrayList;
import java.util.List;

@ServerCollectionName(collectionName = "touristicPlaces")
public class TouristicPlace extends MongoCollection  {
    private MultiLanguageString name;
    private MultiLanguageString description;
    private GPSPosition position;
    private String image;
    private String owner;
    private List<MultiLanguageString> informationList;

    public TouristicPlace() {
        this.name = new MultiLanguageString();
        this.description = new MultiLanguageString();
        this.name = new MultiLanguageString();
        this.position = new GPSPosition();
        this.informationList = new ArrayList<>();
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

    public GPSPosition getPosition() {
        return position;
    }

    public void setPosition(GPSPosition position) {
        this.position = position;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<MultiLanguageString> getInformationList() {
        return informationList;
    }

    public void setInformationList(List<MultiLanguageString> informationList) {
        this.informationList = informationList;
    }
}
