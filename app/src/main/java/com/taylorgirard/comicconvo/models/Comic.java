package com.taylorgirard.comicconvo.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Comic {

    String coverPath;
    String title;

    public Comic(JSONObject jsonObject) throws JSONException {

        JSONObject images = jsonObject.getJSONObject("thumbnail");
        coverPath = images.getString("path") + "." + images.getString("extension");
        title = jsonObject.getString("title");

    }

    public static List<Comic> fromJsonArray(JSONArray comicJsonArray) throws JSONException {

        List<Comic> comics = new ArrayList<>();

        for (int i = 0; i < comicJsonArray.length(); i++){
            comics.add(new Comic(comicJsonArray.getJSONObject(i)));
        }

        return comics;

    }

    public String getCoverPath(){
        return coverPath;
    }

    public String getTitle(){
        return title;
    }



}
