package com.tesis.yudith.showmethepast.domain.collections.childs;


import java.util.Date;

public class SmtpToken {
    private String token;
    private boolean alive;
    private Date expirationDate;



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
