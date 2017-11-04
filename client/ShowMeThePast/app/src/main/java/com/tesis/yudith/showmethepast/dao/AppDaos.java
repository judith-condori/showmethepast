package com.tesis.yudith.showmethepast.dao;

import com.tesis.yudith.showmethepast.configuration.NitriteManager;

public class AppDaos {
    private NitriteManager nitriteManager;
    private CommonsDao commonsDao;

    public AppDaos(NitriteManager nitriteManager) {
        this.setNitriteManager(nitriteManager);
        this.setCommonsDao(new CommonsDao(nitriteManager));
    }

    public NitriteManager getNitriteManager() {
        return nitriteManager;
    }

    public void setNitriteManager(NitriteManager nitriteManager) {
        this.nitriteManager = nitriteManager;
    }

    public CommonsDao getCommonsDao() {
        return commonsDao;
    }

    public void setCommonsDao(CommonsDao commonsDao) {
        this.commonsDao = commonsDao;
    }
}
