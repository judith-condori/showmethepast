package com.tesis.yudith.showmethepast.requests;

import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tesis.yudith.showmethepast.domain.CommonEnumerators;
import com.tesis.yudith.showmethepast.domain.SearchUsersResult;
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

public class AdminRequests extends RequestsBase {

    private final String ADMIN_SEARCH_USERS_URL = "%s/api/admin/users/search";
    private final String ADMIN_EDIT_USERS_URL = "%s/api/admin/users/changerole";

    public AdminRequests(RequestQueue targetQueue, String serverUrlBase) {
        super(targetQueue, serverUrlBase);
    }

    public void searchUsers(final int requestIdentifier, final UserInformation userInformation, String hint, final IRequestListener<SearchUsersResult> listeners) {
        final ERequestType requestType = ERequestType.READ;

        String targetUrl = String.format(ADMIN_SEARCH_USERS_URL, this.getServerUrlBase());
        int method = Request.Method.POST;

        Map<String, String> values = new HashMap<>();
        values.put("hint", hint);

        RequestQueue queue = this.getRequestQueue();
        JSONObject jsonBody = new JSONObject(values);

        CustomJsonRequest request = new CustomJsonRequest(method, targetUrl, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SearchUsersResult searchUsersResult = null;
                try {
                    searchUsersResult = JsonTools.jsonToObject(response, SearchUsersResult.class);
                } catch(Exception error) {
                    listeners.OnError(requestType, requestIdentifier, null, error);
                    return;
                }
                listeners.OnComplete(requestType, requestIdentifier, searchUsersResult);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listeners.OnError(requestType, requestIdentifier, error, null);
            }
        });

        request.setTokenInformation(userInformation);

        queue.add(request);
    }

    public void editUserRole(final int requestIdentifier, final UserInformation userInformation, final String targetId, CommonEnumerators.EUserRole targetRole, final IRequestListener<Void> listeners) {
        final ERequestType requestType = ERequestType.UPDATE;

        String targetUrl = String.format(ADMIN_EDIT_USERS_URL, this.getServerUrlBase());
        int method = Request.Method.PUT;

        UserInformation targetUser = new UserInformation();
        targetUser.setId(targetId);
        targetUser.setRole(targetRole.getValue());
        targetUser.setSmtpToken(null);

        RequestQueue queue = this.getRequestQueue();
        JSONObject jsonBody = JsonTools.objectToJsonObject(targetUser);

        CustomJsonRequest request = new CustomJsonRequest(method, targetUrl, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listeners.OnComplete(requestType, requestIdentifier, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listeners.OnError(requestType, requestIdentifier, error, null);
            }
        });

        request.setTokenInformation(userInformation);

        queue.add(request);
    }
}
