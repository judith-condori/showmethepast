package com.tesis.yudith.showmethepast.requests.tools;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SharedRequestQueue {
    private static RequestQueue currentRequestQueue;

    public static synchronized RequestQueue getCurrent(Context context) {
        if (currentRequestQueue == null) {
            currentRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return currentRequestQueue;
    }
}
