package com.tesis.yudith.showmethepast.controller;

import com.tesis.yudith.showmethepast.configuration.LanguageManager;
import com.tesis.yudith.showmethepast.configuration.NitriteManager;
import com.tesis.yudith.showmethepast.dao.AppDaos;
import com.tesis.yudith.showmethepast.dao.CommonsDao;
import com.tesis.yudith.showmethepast.domain.collections.ImageData;
import com.tesis.yudith.showmethepast.domain.collections.OldPicture;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;

import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.List;

public class TouristicPlacesController {

    private AppDaos appDaos;
    private AppControllers appControllers;

    public TouristicPlacesController(AppDaos appDaos, AppControllers appControllers) {
        this.appDaos = appDaos;
        this.appControllers = appControllers;
    }

    public List<TouristicPlace> filterTouristicPlaces(String hint) {
        ObjectFilter targetFilter = NitriteManager.ALL_FILTER;

        if (hint != null && hint.length() > 0) {
            targetFilter = ObjectFilters.regex( LanguageManager.adaptFieldName("name"), String.format("(?i).*%s.*", hint));
        }

        return this.appDaos.getCommonsDao().find(targetFilter, TouristicPlace.class).toList();
    }

    public void delete(TouristicPlace target) {
        CommonsDao commonsDao = appDaos.getCommonsDao();

        List<OldPicture> oldPictures = commonsDao.find(ObjectFilters.eq("touristicPlace", target.getId()), OldPicture.class).toList();

        for (OldPicture oldPicture : oldPictures) {
            this.appControllers.getOldPictureController().delete(oldPicture);
        }

        commonsDao.remove(target.getImage(), ImageData.class);
        commonsDao.remove(target.getId(), TouristicPlace.class);
    }
}
