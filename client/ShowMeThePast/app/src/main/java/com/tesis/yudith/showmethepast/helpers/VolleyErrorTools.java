package com.tesis.yudith.showmethepast.helpers;

import com.android.volley.VolleyError;

import java.net.HttpURLConnection;

public class VolleyErrorTools {

    public static boolean isHttpNotFound(VolleyError volleyError) {

        return VolleyErrorTools.isHttpStatus(volleyError, HttpURLConnection.HTTP_NOT_FOUND);
    }

    private static boolean isHttpStatus(VolleyError volleyError, int targetStatus) {
        if (volleyError == null) {
            return false;
        }

        if (volleyError.networkResponse == null) {
            return false;
        }

        return volleyError.networkResponse.statusCode == targetStatus;
    }
}
