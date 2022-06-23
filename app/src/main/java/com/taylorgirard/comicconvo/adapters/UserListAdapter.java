package com.taylorgirard.comicconvo.adapters;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.ComicSearchActivity;
import com.taylorgirard.comicconvo.models.Comic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    public static final String TAG = "ComicAdapter";

    Context context;
    List<Comic> comics;

    public UserListAdapter(Context context, List<Comic> comics){
        this.comics = comics;
        this.context = context;
    }

    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("ComicAdapter", "onCreateViewHolder");
        View comicView = LayoutInflater.from(context).inflate(R.layout.item_user_list, parent, false);
        return new ViewHolder(comicView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("UserListAdapter", "onBindViewHolder" + position);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivCover = itemView.findViewById(R.id.ivCover);

        }

        public void bind(Comic comic) {
            tvTitle.setText(comic.getTitle());
            String imageUrl = comic.getCoverPath();
            Glide.with(context).load(imageUrl).into(ivCover);
        }
    }
}

