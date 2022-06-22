package com.taylorgirard.comicconvo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.models.Comic;

import java.util.List;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ViewHolder> {

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivCover = itemView.findViewById(R.id.ivCover);
        }

        public void bind(Comic comic){
            tvTitle.setText(comic.getTitle());
            String imageUrl;

            imageUrl = comic.getCoverPath();
            Glide.with(context).load(imageUrl).into(ivCover);
        }
    }
}
