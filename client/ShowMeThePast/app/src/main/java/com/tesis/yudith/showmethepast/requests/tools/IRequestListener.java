package com.tesis.yudith.showmethepast.requests.tools;

import com.android.volley.VolleyError;

public interface IRequestListener<T> {
    void OnComplete(ERequestType requestType, int requestIdentifier, T result);
    void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error);
}
