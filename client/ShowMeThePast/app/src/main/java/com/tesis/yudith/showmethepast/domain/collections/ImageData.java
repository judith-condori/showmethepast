package com.tesis.yudith.showmethepast.domain.collections;


import com.tesis.yudith.showmethepast.domain.collections.annotations.ServerCollectionName;
import com.tesis.yudith.showmethepast.domain.collections.childs.MultiLanguageString;

@ServerCollectionName(collectionName = "images")
public class ImageData extends MongoCollection {
    private String data;
    private MultiLanguageString author;
    private MultiLanguageString description;
    private String owner;

    public ImageData() {
        this.setAuthor(new MultiLanguageString());
        this.setDescription(new MultiLanguageString());
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public MultiLanguageString getAuthor() {
        return author;
    }

    public void setAuthor(MultiLanguageString author) {
        this.author = author;
    }

    public MultiLanguageString getDescription() {
        return description;
    }

    public void setDescription(MultiLanguageString description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
