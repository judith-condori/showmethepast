package com.tesis.yudith.showmethepast.requests;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;
import com.tesis.yudith.showmethepast.helpers.JsonTools;
import com.tesis.yudith.showmethepast.requests.tools.CustomJsonRequest;
import com.tesis.yudith.showmethepast.requests.tools.CustomStringRequest;
import com.tesis.yudith.showmethepast.requests.tools.ERequestType;
import com.tesis.yudith.showmethepast.requests.tools.IRequestListener;
import com.tesis.yudith.showmethepast.requests.tools.RequestsBase;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserRequests extends RequestsBase {

    private final String USER_LOGIN_URL = "%s/api/security/login";
    private final String USER_LOGOUT_URL = "%s/api/security/logout";
    private final String USER_STATUS_URL = "%s/api/security/status";

    public UserRequests(RequestQueue targetQueue, String serverUrlBase) {
        super(targetQueue, serverUrlBase);
    }

    public void loginUser(String googleToken, final IRequestListener<UserInformation> listeners) {
        final ERequestType requestType = ERequestType.READ;
        final int requestIdentifier = 0;

        String targetUrl = String.format(USER_LOGIN_URL, this.getServerUrlBase());
        int method = Request.Method.POST;

        Map<String, String> values = new HashMap<>();
        values.put("token", googleToken);

        RequestQueue queue = this.getRequestQueue();
        JSONObject jsonBody = new JSONObject(values);


        JsonObjectRequest request = new JsonObjectRequest(method, targetUrl, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserInformation userInformation = null;
                try {
                    userInformation = JsonTools.jsonToObject(response.getJSONObject("userInformation"), UserInformation.class);
                } catch(Exception error) {
                    listeners.OnError(requestType, requestIdentifier, null, error);
                    return;
                }
                listeners.OnComplete(requestType, requestIdentifier, userInformation);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listeners.OnError(requestType, requestIdentifier, error, null);
            }
        });

        queue.add(request);
    }

    public void logout(UserInformation currentUser, final IRequestListener<Void> listeners) {
        final ERequestType requestType = ERequestType.READ;
        final int requestIdentifier = 0;

        String targetUrl = String.format(USER_LOGOUT_URL, this.getServerUrlBase());
        int method = Request.Method.POST;

        RequestQueue queue = this.getRequestQueue();


        CustomStringRequest request = new CustomStringRequest(method, targetUrl, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                listeners.OnComplete(requestType, requestIdentifier, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listeners.OnError(requestType, requestIdentifier, error, null);
            }
        });

        request.setTokenInformation(currentUser);

        queue.add(request);
    }

    public void getStatus(UserInformation currentUser, final IRequestListener<UserInformation> listeners) {
        final ERequestType requestType = ERequestType.READ;
        final int requestIdentifier = 0;
        String targetUrl = String.format(USER_STATUS_URL, this.getServerUrlBase());
        int method = Request.Method.GET;

        RequestQueue queue = this.getRequestQueue();

        CustomJsonRequest request = new CustomJsonRequest(method, targetUrl, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserInformation userInformation = null;
                try {
                    userInformation = JsonTools.jsonToObject(response.getJSONObject("userInformation"), UserInformation.class);
                } catch(Exception error) {
                    listeners.OnError(requestType, requestIdentifier, null, error);
                    return;
                }
                listeners.OnComplete(requestType, requestIdentifier, userInformation);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listeners.OnError(requestType, requestIdentifier, error, null);
            }
        });

        request.setTokenInformation(currentUser);

        queue.add(request);
    }
}
