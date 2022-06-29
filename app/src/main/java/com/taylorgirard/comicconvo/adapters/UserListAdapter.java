package com.taylorgirard.comicconvo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.tools.ListType;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.models.Comic;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    public static final String TAG = "UserListAdapter";
    public static final String USER_LIKES_KEY = "Likes";
    public static final String USER_DISLIKES_KEY = "Dislikes";

    ParseUser user = ParseUser.getCurrentUser();
    Context context;
    List<Comic> comics;
    ListType type;

    public UserListAdapter(Context context, List<Comic> comics, ListType type){
        this.comics = comics;
        this.context = context;
        this.type = type;
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
        Button btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivCover = itemView.findViewById(R.id.ivCover);
            btnRemove = itemView.findViewById(R.id.btnRemove);

        }

        public void bind(Comic comic) {
            tvTitle.setText(comic.getTitle());
            String imageUrl = comic.getCoverPath();
            Glide.with(context).load(imageUrl).into(ivCover);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comics.remove(comic);
                    int timesAdded = 0;
                    try {
                        timesAdded = comic.fetchIfNeeded().getInt("timesAdded");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    timesAdded -= 1;
                    comic.put("timesAdded", timesAdded);
                    comic.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                Log.e(TAG, "Error decrementing timesAdded", e);
                            } else{
                                Log.i(TAG, "Success decrementing timesAdded");
                            }
                        }
                    });
                    notifyDataSetChanged();
                    ArrayList<Comic> remove = new ArrayList<Comic>();
                    remove.add(comic);
                    switch(type){
                        case LIKES:
                            user.removeAll(USER_LIKES_KEY, remove);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null){
                                        Log.d(TAG, "success removing item");
                                    } else{
                                        Log.e(TAG, "error removing item", e);
                                    }
                                }
                            });
                        case DISLIKES:
                            user.removeAll(USER_DISLIKES_KEY, remove);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null){
                                        Log.d(TAG, "success removing item");
                                    } else{
                                        Log.e(TAG, "error removing item", e);
                                    }
                                }
                            });
                    }
                }
            });
        }
    }
}

