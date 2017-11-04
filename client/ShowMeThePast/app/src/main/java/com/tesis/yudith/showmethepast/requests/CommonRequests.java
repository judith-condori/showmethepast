package com.tesis.yudith.showmethepast.requests;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tesis.yudith.showmethepast.domain.collections.ImageData;
import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;
import com.tesis.yudith.showmethepast.domain.collections.OldPicture;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;
import com.tesis.yudith.showmethepast.domain.sync.CombinedSyncResults;
import com.tesis.yudith.showmethepast.domain.sync.SyncRequestInformation;
import com.tesis.yudith.showmethepast.domain.sync.SynchronizationResult;
import com.tesis.yudith.showmethepast.helpers.JsonTools;
import com.tesis.yudith.showmethepast.requests.tools.CustomJsonRequest;
import com.tesis.yudith.showmethepast.requests.tools.CustomStringRequest;
import com.tesis.yudith.showmethepast.requests.tools.ERequestType;
import com.tesis.yudith.showmethepast.requests.tools.ESyncType;
import com.tesis.yudith.showmethepast.requests.tools.IRequestListener;
import com.tesis.yudith.showmethepast.requests.tools.IRequestSyncListener;
import com.tesis.yudith.showmethepast.requests.tools.RequestsBase;

import org.json.JSONObject;

import java.util.Date;

public class CommonRequests extends RequestsBase {

    private final String COMMONS_CREATE_URL = "%s/api/collections/%s";
    private final String COMMONS_READ_URL = "%s/api/collections/%s/%s";
    private final String COMMONS_UPDATE_URL = "%s/api/collections/%s/%s";
    private final String COMMONS_DELETE_URL = "%s/api/collections/%s/%s";

    private final String COMMONS_GET_CHANGES_URL = "%s/api/collections/%s/changes";

    public CommonRequests(RequestQueue targetQueue, String serverUrlBase) {
        super(targetQueue, serverUrlBase);
    }

    private <T extends MongoCollection> String getCollectionName(T target) {
        if (target instanceof ImageData) {
            return "images";
        } else if (target instanceof TouristicPlace) {
            return "touristicPlaces";
        } else if (target instanceof  OldPicture) {
            return "oldPictures";
        }
        return "";
    }

    private <T extends MongoCollection> String getCollectionName(Class<T> target) {
        if (target == ImageData.class) {
            return "images";
        } else if (target == TouristicPlace.class) {
            return "touristicPlaces";
        } else if (target == OldPicture.class) {
            return "oldPictures";
        }
        return "";
    }

    public <T extends MongoCollection> void get(final int requestIdentifier, UserInformation currentUser, String documentId, final Class<T> targetClass, final IRequestListener<MongoCollection> listeners) {
        final ERequestType requestType = ERequestType.READ;
        int method = Request.Method.GET;

        RequestQueue queue = this.getRequestQueue();
        String parsedCollection = this.getCollectionName(targetClass);

        String targetUrl = String.format(COMMONS_READ_URL, this.getServerUrlBase(), parsedCollection, documentId);

        JSONObject jsonBody = new JSONObject();

        CustomJsonRequest request = new CustomJsonRequest(method, targetUrl, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MongoCollection creationResponse = null;
                try {
                    creationResponse = JsonTools.jsonToObject(response, targetClass);
                } catch(Exception error) {
                    listeners.OnError(requestType, requestIdentifier, null, error);
                    return;
                }
                listeners.OnComplete(requestType, requestIdentifier, creationResponse);
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

    public <T extends MongoCollection> void create(final int requestIdentifier, UserInformation currentUser, T target, final IRequestListener<MongoCollection> listeners) {
        final ERequestType requestType = ERequestType.CREATE;
        int method = Request.Method.POST;
        RequestQueue queue = this.getRequestQueue();
        String parsedCollection = this.getCollectionName(target);

        String targetUrl = String.format(COMMONS_CREATE_URL, this.getServerUrlBase(), parsedCollection);

        JSONObject jsonBody = JsonTools.objectToJsonObject(target);

        CustomJsonRequest request = new CustomJsonRequest(method, targetUrl, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            MongoCollection creationResponse = null;
            try {
                creationResponse = JsonTools.jsonToObject(response, MongoCollection.class);
            } catch(Exception error) {
                listeners.OnError(requestType, requestIdentifier, null, error);
                return;
            }
            listeners.OnComplete(requestType, requestIdentifier, creationResponse);
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

    public <T extends MongoCollection> void update(final int requestIdentifier, UserInformation currentUser, T target, final IRequestListener<MongoCollection> listeners) {
        final ERequestType requestType = ERequestType.UPDATE;
        int method = Request.Method.PUT;
        RequestQueue queue = this.getRequestQueue();
        String parsedCollection = this.getCollectionName(target);

        String targetUrl = String.format(COMMONS_UPDATE_URL, this.getServerUrlBase(), parsedCollection, target.getId());

        JSONObject jsonBody = JsonTools.objectToJsonObject(target);

        CustomJsonRequest request = new CustomJsonRequest(method, targetUrl, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MongoCollection updateResponse = null;
                try {
                    updateResponse = JsonTools.jsonToObject(response, MongoCollection.class);
                } catch(Exception error) {
                    listeners.OnError(requestType, requestIdentifier, null, error);
                    return;
                }
                listeners.OnComplete(requestType, requestIdentifier, updateResponse);
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

    public <T extends MongoCollection> void delete(final int requestIdentifier, UserInformation currentUser, T target, final IRequestListener<MongoCollection> listeners) {
        final ERequestType requestType = ERequestType.DELETE;
        int method = Request.Method.DELETE;
        RequestQueue queue = this.getRequestQueue();
        String parsedCollection = this.getCollectionName(target);

        String targetUrl = String.format(COMMONS_DELETE_URL, this.getServerUrlBase(), parsedCollection, target.getId());

        CustomStringRequest request = new CustomStringRequest(method, targetUrl, new Response.Listener<String>() {
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

    public void processSyncRequest(final int requestIdentifier, UserInformation currentUser, final Class<?> type, Date startDate, final IRequestSyncListener<CombinedSyncResults> listeners) {
        final ERequestType requestType = ERequestType.READ;

        int method = Request.Method.POST;
        RequestQueue queue = this.getRequestQueue();
        String parsedCollection = "";

        if (type == ImageData.class) {
            parsedCollection = "images";
        } else if (type == TouristicPlace.class) {
            parsedCollection = "touristicPlaces";
        } else if (type == OldPicture.class) {
            parsedCollection = "oldPictures";
        }


        String targetUrl = String.format(COMMONS_GET_CHANGES_URL, this.getServerUrlBase(), parsedCollection);

        SyncRequestInformation syncInformation = new SyncRequestInformation();
        syncInformation.setStartDate(startDate);

        JSONObject jsonBody = JsonTools.objectToJsonObject(syncInformation);

        CustomJsonRequest request = new CustomJsonRequest(method, targetUrl, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                CombinedSyncResults syncResults = null;
                try {
                    syncResults = JsonTools.jsonToObject(response, CombinedSyncResults.class);
                } catch(Exception error) {
                    listeners.OnSyncInformationError(type, requestIdentifier, null, error);
                    return;
                }
                listeners.OnSyncInformationComplete(type, requestIdentifier, syncResults);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listeners.OnSyncInformationError(type, requestIdentifier, error, null);
            }
        });

        request.setTokenInformation(currentUser);
        queue.add(request);
    }
}

