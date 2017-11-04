package com.tesis.yudith.showmethepast.domain.collections;

import com.google.gson.annotations.SerializedName;
import com.tesis.yudith.showmethepast.domain.collections.annotations.ServerCollectionName;

import org.dizitart.no2.objects.Id;

import java.util.Date;
import java.math.BigInteger;
import java.util.Random;

public class MongoCollection {
    @SerializedName("_id")
    @Id
    private String id;
    private Date createdAt;
    private Date updatedAt;

    public void generateRandomId() {
        String dictionary = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder newId = new StringBuilder();
        int size = 25;
        Random random = new Random(new Date().getTime());

        for (int i = 0; i < size; i++) {
            int randomPosition = (int)(random.nextDouble() * dictionary.length());
            char targetChar = dictionary.charAt(randomPosition);
            newId.append(targetChar);
        }

        this.id = newId.toString();
    }

    public static String getServerCollectionName(Class<?> target) {
        if (target != null) {
            ServerCollectionName targetAnnotation = target.getAnnotation(ServerCollectionName.class);
            if (targetAnnotation != null) {
                return targetAnnotation.collectionName();
            }
        }
        return "";
    }

    public void setCreateInformation(MongoCollection createInformation) {
        this.setCreatedAt(createInformation.getCreatedAt());
        this.setUpdatedAt(createInformation.getUpdatedAt());
        this.setId(createInformation.getId());
    }

    public void setUpdateInformation(MongoCollection createInformation) {
        this.setUpdatedAt(createInformation.getUpdatedAt());
    }

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
