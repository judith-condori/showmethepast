package com.tesis.yudith.showmethepast.domain.collections.childs;

import java.io.Serializable;

public class MultiLanguageString implements Serializable {
    private String english;
    private String spanish;

    public MultiLanguageString() {

    }

    public MultiLanguageString(String english, String spanish){
        this.english = english;
        this.spanish = spanish;
    }

    public boolean isInvalid() {
        if (this.english == null || this.english.length() == 0) {
            return true;
        }
        if (this.spanish == null || this.spanish.length() == 0) {
            return true;
        }
        return false;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getSpanish() {
        return spanish;
    }

    public void setSpanish(String spanish) {
        this.spanish = spanish;
    }
}
