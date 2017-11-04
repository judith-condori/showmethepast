package com.tesis.yudith.showmethepast.requests.tools;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomJsonRequest extends JsonObjectRequest {
    private Map<String, String> customHeaders;

    public CustomJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.customHeaders = new HashMap<>();

        this.expandTimeout();
    }

    private void expandTimeout() {
        this.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    public CustomJsonRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        this.customHeaders = new HashMap<>();
        this.expandTimeout();
    }

    public void setTokenInformation(UserInformation userInformation) {
        this.customHeaders.put("smtp-token", userInformation.getSmtpToken().getToken());
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> defaultHeaders = super.getHeaders();
        Map<String, String> mergedHeaders = new HashMap<>();

        mergedHeaders.putAll(defaultHeaders);
        mergedHeaders.putAll(customHeaders);

        return mergedHeaders;
    }
}
