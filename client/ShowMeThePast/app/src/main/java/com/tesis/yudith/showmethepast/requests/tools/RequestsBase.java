package com.tesis.yudith.showmethepast.requests.tools;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;

public abstract class RequestsBase {
    private RequestQueue currentQueue;
    private String serverUrlBase;

    public RequestsBase(RequestQueue targetQueue, String serverUrlBase) {
        this.currentQueue = targetQueue;
        this.serverUrlBase = serverUrlBase;
    }

    public RequestQueue getRequestQueue() {
        return this.currentQueue;
    }

    public String getServerUrlBase() {
        return this.serverUrlBase;
    }

}
