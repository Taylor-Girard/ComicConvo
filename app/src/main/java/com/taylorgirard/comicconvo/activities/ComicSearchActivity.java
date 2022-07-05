package com.taylorgirard.comicconvo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.adapters.ComicAdapter;
import com.taylorgirard.comicconvo.fragments.ProfileFragment;
import com.taylorgirard.comicconvo.models.Comic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Request;

public class ComicSearchActivity extends AppCompatActivity {

    public static final String TAG = "ComicSearchActivity";
    public static final int GRID_COLUMNS = 2;
    public static final String BASE_COMIC_URL = "https://gateway.marvel.com/v1/public/comics?ts=123&apikey=865d88b76772d9a6a5fadb1f268538d4&hash=70025aec317dbd24c106b1867b239b36";

    List<Comic> comics;
    SearchView svComicSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_search);
        RecyclerView rvComicList = findViewById(R.id.rvComicList);
        svComicSearch = findViewById(R.id.svComicSearch);
        comics = new ArrayList<>();

        final ComicAdapter comicAdapter = new ComicAdapter(this, comics);
        rvComicList.setAdapter(comicAdapter);
        rvComicList.setLayoutManager(new GridLayoutManager(this, GRID_COLUMNS));

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

        svComicSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(ComicSearchActivity.this, query, Toast.LENGTH_SHORT).show();
                RequestParams params = new RequestParams();
                params.put("titleStartsWith", query);
                client.get(BASE_COMIC_URL, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d(TAG,"onSuccess");
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray results = data.getJSONArray("results");
                            Log.i(TAG, "Results" + results.toString());
                            comics.clear();
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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}