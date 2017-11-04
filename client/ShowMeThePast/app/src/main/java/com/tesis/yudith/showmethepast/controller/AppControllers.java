package com.tesis.yudith.showmethepast.controller;

import com.tesis.yudith.showmethepast.dao.AppDaos;

public class AppControllers {
    private TouristicPlacesController touristicPlacesController;
    private UserController userController;
    private OldPictureController oldPictureController;

    public AppControllers(AppDaos daos) {
        this.touristicPlacesController = new TouristicPlacesController(daos, this);
        this.userController = new UserController(daos);
        this.oldPictureController = new OldPictureController(daos);
    }

    public TouristicPlacesController getTouristicPlacesController() {
        return this.touristicPlacesController;
    }

    public UserController getUserController() { return userController; }

    public OldPictureController getOldPictureController() { return this.oldPictureController; }

}
