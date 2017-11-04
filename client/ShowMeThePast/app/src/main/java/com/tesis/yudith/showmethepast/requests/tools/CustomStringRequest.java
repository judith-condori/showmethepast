package com.tesis.yudith.showmethepast.requests.tools;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;

import java.util.HashMap;
import java.util.Map;

public class CustomStringRequest extends StringRequest {
    private Map<String, String> customHeaders;

    public CustomStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.customHeaders = new HashMap<>();
        this.expandTimeout();

    }

    public CustomStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
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
