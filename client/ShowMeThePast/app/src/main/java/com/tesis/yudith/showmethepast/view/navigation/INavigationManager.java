package com.tesis.yudith.showmethepast.view.navigation;

import android.support.v4.app.Fragment;

public interface INavigationManager {
    void replaceFragment(INavigationChild targetFragment);
    void pushFragment(INavigationChild targetFragment);
    void popFragment(boolean needsReload);
}
