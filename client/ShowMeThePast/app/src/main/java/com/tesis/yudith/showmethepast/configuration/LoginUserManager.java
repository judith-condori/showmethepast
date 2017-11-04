package com.tesis.yudith.showmethepast.configuration;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.domain.CommonConstants;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;

import java.util.ArrayList;
import java.util.List;

public class LoginUserManager {

    /*
        Static fields
     */
    private static LoginUserManager currentUserManager;

    public static LoginUserManager getCurrent() {
        if (currentUserManager == null) {
            currentUserManager = new LoginUserManager();
        }
        return currentUserManager;
    }

    List<ILoginChangeListener> listeners;

    /*
        Object implementation
     */

    private UserInformation userInformation;

    private LoginUserManager() {
        this.listeners = new ArrayList<>();
    }

    public void addLoginChangeListener(ILoginChangeListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    public void removeLoginChangeListener(ILoginChangeListener listener) {
        if (this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }

    public UserInformation getUserInformation() {
        return this.userInformation;
    }

    public void updateLogin(UserInformation userInformation) {
        this.userInformation = userInformation;
        for(ILoginChangeListener listener : this.listeners) {
            listener.loginChanged(false);
        }
    }

    public void logout() {
        this.userInformation = null;
        for(ILoginChangeListener listener : this.listeners) {
            listener.loginChanged(true);
        }
    }

    public GoogleSignInOptions getGoogleConfiguration(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestIdToken(context.getString(R.string.server_client_id))
                .requestEmail()
                .build();

        return gso;
    }

    public boolean isCurrentUserAnEditor() {
        if (this.getUserInformation() == null) {
            return false;
        }

        if ( this.isCurrentUserAnAdmin() || this.getUserInformation().getRole().equals(CommonConstants.USER_ROLE_EDITOR)) {
            return true;
        }

        return false;
    }

    public boolean isCurrentUserAnAdmin() {
        if (this.getUserInformation() == null) {
            return false;
        }

        if (this.getUserInformation().getRole().equals(CommonConstants.USER_ROLE_ADMIN)) {
            return true;
        }

        return false;
    }
}
