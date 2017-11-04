package com.tesis.yudith.showmethepast.view.navigation;

import android.content.res.Resources;

public interface INavigationChild {
    String getNavigationTitle(Resources resources);
    String getFragmentTag();
    void onChildrenClosed(INavigationChild origin, boolean needReload);
}
