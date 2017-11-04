package com.tesis.yudith.showmethepast.requests.tools;

import com.android.volley.VolleyError;

public interface IRequestSyncListener<T> {
    void OnSyncInformationComplete(Class<?> type, int requestIdentifier, T result);
    void OnSyncInformationError(Class<?> type, int requestIdentifier, VolleyError volleyError, Exception error);
}
