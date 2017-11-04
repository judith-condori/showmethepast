package com.tesis.yudith.showmethepast.domain.collections;
import com.tesis.yudith.showmethepast.domain.collections.annotations.ServerCollectionName;
import com.tesis.yudith.showmethepast.domain.collections.childs.SmtpToken;

@ServerCollectionName(collectionName = "users")
public class UserInformation extends MongoCollection {
    private String userId;
    private String name;
    private String email;
    private String picture;
    private String role;
    private SmtpToken smtpToken;

    public UserInformation() {
        this.smtpToken = new SmtpToken();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public SmtpToken getSmtpToken() {
        return smtpToken;
    }

    public void setSmtpToken(SmtpToken smtpToken) {
        this.smtpToken = smtpToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
