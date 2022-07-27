package com.taylorgirard.comicconvo.models;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**Parse class for a comic to get the comic's id, cover path, and title*/

@ParseClassName("Comic")
public class Comic extends ParseObject implements Serializable {

    String coverPath;
    String title;
    String basicPath;
    int comicId;

    public Comic(){}

    public Comic(JSONObject jsonObject) throws JSONException {

        JSONObject images = jsonObject.getJSONObject("thumbnail");

        basicPath = images.getString("path");
        coverPath = images.getString("path") + "." + images.getString("extension");
        title = jsonObject.getString("title");
        comicId = jsonObject.getInt("id");

    }

    public static List<Comic> fromJsonArray(JSONArray comicJsonArray) throws JSONException {

        List<Comic> comics = new ArrayList<>();

        for (int i = 0; i < comicJsonArray.length(); i++){
            comics.add(new Comic(comicJsonArray.getJSONObject(i)));
        }

        return comics;

    }

    public static List<Comic> fromParseArray(List<Comic> parseComics) throws ParseException {

        List<Comic> comics = new ArrayList<>();

        for (int i = 0; i < parseComics.size(); i++){
            Comic currentComic = parseComics.get(i);
            String id = currentComic.getObjectId();
            ParseQuery<Comic> query = new ParseQuery<Comic>("Comic");
            ParseObject object = query.get(id);
            Log.d(">>", ">>" + object);
            currentComic.setTitle(object.getString("Title"));
            currentComic.setCoverPath(object.getString("coverPath"));
            comics.add(currentComic);
        }

        return comics;

    }

    public String getCoverPath(){
        return coverPath;
    }

    public void setCoverPath(String coverPath){
        this.coverPath = coverPath;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public int getComicId(){
        return comicId;
    }



}
