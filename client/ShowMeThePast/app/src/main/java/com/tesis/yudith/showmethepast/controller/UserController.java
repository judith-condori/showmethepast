package com.tesis.yudith.showmethepast.controller;

import com.tesis.yudith.showmethepast.configuration.NitriteManager;
import com.tesis.yudith.showmethepast.dao.AppDaos;
import com.tesis.yudith.showmethepast.dao.CommonsDao;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;

public class UserController {
    AppDaos appDaos;

    public UserController(AppDaos appDaos) {
        this.appDaos = appDaos;
    }

    public void storeUserResult(UserInformation userInformation) {
        CommonsDao commonsDao = this.appDaos.getCommonsDao();
        UserInformation savedUser = commonsDao.findOne(userInformation.getId(), UserInformation.class);

        if (savedUser == null) {
            commonsDao.remove(NitriteManager.ALL_FILTER, UserInformation.class);
            commonsDao.insert(userInformation, UserInformation.class);
        } else {
            commonsDao.update(userInformation, UserInformation.class);
        }
    }

    public UserInformation getLastUser() {
        CommonsDao commonsDao = this.appDaos.getCommonsDao();
        return commonsDao.findOne(UserInformation.class);
    }

    public void removeAll() {
        CommonsDao commonsDao = this.appDaos.getCommonsDao();
        commonsDao.remove(NitriteManager.ALL_FILTER, UserInformation.class);
    }
}
