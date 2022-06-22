package com.taylorgirard.comicconvo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.adapters.ComicAdapter;
import com.taylorgirard.comicconvo.models.Comic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ComicSearchActivity extends AppCompatActivity {

    public static final String TAG = "ComicSearchActivity";
    public static final String BASE_COMIC_URL = "https://gateway.marvel.com/v1/public/comics?ts=123&apikey=865d88b76772d9a6a5fadb1f268538d4&hash=70025aec317dbd24c106b1867b239b36";

    List<Comic> comics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_search);
        RecyclerView rvComicList = findViewById(R.id.rvComicList);
        comics = new ArrayList<>();

        final ComicAdapter comicAdapter = new ComicAdapter(this, comics);
        rvComicList.setAdapter(comicAdapter);
        rvComicList.setLayoutManager(new GridLayoutManager(this, 2));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(BASE_COMIC_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG,"onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray results = data.getJSONArray("results");
                    Log.i(TAG, "Results" + results.toString());
                    comics.addAll(Comic.fromJsonArray(results));
                    comicAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure " + statusCode);
            }
        });
    }
}