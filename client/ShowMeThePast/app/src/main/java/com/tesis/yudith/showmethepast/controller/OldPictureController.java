package com.tesis.yudith.showmethepast.controller;

import com.tesis.yudith.showmethepast.dao.AppDaos;
import com.tesis.yudith.showmethepast.dao.CommonsDao;
import com.tesis.yudith.showmethepast.domain.collections.ImageData;
import com.tesis.yudith.showmethepast.domain.collections.OldPicture;

public class OldPictureController {
    AppDaos appDaos;

    public OldPictureController(AppDaos appDaos) {
        this.appDaos = appDaos;
    }

    public void delete(OldPicture target) {
        CommonsDao commonsDao = appDaos.getCommonsDao();
        commonsDao.remove(target.getImage(), ImageData.class);
        commonsDao.remove(target.getId(), OldPicture.class);
    }
}
