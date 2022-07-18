package com.taylorgirard.comicconvo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.ComicSearchActivity;
import com.taylorgirard.comicconvo.fragments.ProfileFragment;
import com.taylorgirard.comicconvo.models.Comic;
import com.taylorgirard.comicconvo.tools.ComicUtility;
import com.taylorgirard.comicconvo.tools.ListType;
import com.taylorgirard.comicconvo.tools.Match;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**Adapter that binds comic object to the comic recycler view for the search*/

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ViewHolder> {

    public static final String TAG = "ComicAdapter";
    public static final int MAX_COMICS = 20;

    Context context;
    List<Comic> comics;

    public ComicAdapter(Context context, List<Comic> comics){
        this.comics = comics;
        this.context = context;
    }

    @NonNull
    @Override
    public ComicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("ComicAdapter", "onCreateViewHolder");
        View comicView = LayoutInflater.from(context).inflate(R.layout.item_comic, parent, false);
        return new ViewHolder(comicView);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicAdapter.ViewHolder holder, int position) {
        Log.d("ComicAdapter", "onBindViewHolder" + position);
        Comic comic = comics.get(position);
        holder.bind(comic);
    }

    @Override
    public int getItemCount() {
        return comics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView ivCover;
        Button btnLike;
        Button btnDislike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivCover = itemView.findViewById(R.id.ivCover);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnDislike = itemView.findViewById(R.id.btnDislike);

        }

        public void bind(Comic comic) {
            tvTitle.setText(comic.getTitle());
            String imageUrl;

            imageUrl = comic.getCoverPath();
            Glide.with(context).load(imageUrl).into(ivCover);

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = ParseUser.getCurrentUser();

                    //check if user already has the max amount of comics in their list
                    List<Comic> userLikes = user.getList(ListType.LIKES.toString());
                    if (userLikes.size() >= MAX_COMICS) {
                        Toast.makeText(context, "You already have the max amount of likes!", Toast.LENGTH_SHORT).show();
                    } else {
                        ComicUtility.addComic(comic, user, ListType.LIKES);
                    }
                }
            });

            btnDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = ParseUser.getCurrentUser();

                    //check if user already has the max amount of comics in their list
                    List<Comic> userLikes = user.getList(ListType.DISLIKES.toString());
                    if (userLikes.size() >= MAX_COMICS) {
                        Toast.makeText(context, "You already have the max amount of dislikes!", Toast.LENGTH_SHORT).show();
                    } else {
                        ComicUtility.addComic(comic, user, ListType.DISLIKES);
                    }
                }
            });
        }
    }
}


