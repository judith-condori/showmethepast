package com.tesis.yudith.showmethepast.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.Date;

public class JsonTools {
    private static Gson gsonBuilder;

    public static Gson getGSONBuilder() {
        if (gsonBuilder == null) {
            gsonBuilder = new GsonBuilder()
                                .registerTypeAdapter(Date.class, new ISODateAdapter())
                                .create();
        }
        return gsonBuilder;
    }

    public static <T> T jsonToObject(String jsonString, Class<T> type) {
        Gson gson = getGSONBuilder();
        return gson.fromJson(jsonString, type);
    }

    public static <T> T jsonToObject(JSONObject jsonObject, Class<T> type) {
        return jsonToObject(jsonObject.toString(), type);
    }

    public static String objectToJson(Object object) {
        Gson gson = getGSONBuilder();
        return gson.toJson(object);
    }

    public static <T> T cloneByJson(T object, Class<T> type) {
        Gson gson = getGSONBuilder();
        return gson.fromJson(gson.toJson(object), type);
    }


    public static JSONObject objectToJsonObject(Object object) {
        String json = getGSONBuilder().toJson(object);
        JSONObject result = null;

        try {
            result = new JSONObject(json);
        } catch (Exception error) {

        }

        return result;
    }

}
