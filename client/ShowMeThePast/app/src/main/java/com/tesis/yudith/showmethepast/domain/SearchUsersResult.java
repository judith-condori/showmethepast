package com.tesis.yudith.showmethepast.domain;

import com.tesis.yudith.showmethepast.domain.collections.UserInformation;

import java.util.ArrayList;
import java.util.List;

public class SearchUsersResult {
    private List<UserInformation> result;

    public SearchUsersResult() {
        this.result = new ArrayList<>();
    }

    public List<UserInformation> getResult() {
        return result;
    }

    public void setResult(List<UserInformation> result) {
        this.result = result;
    }
}
