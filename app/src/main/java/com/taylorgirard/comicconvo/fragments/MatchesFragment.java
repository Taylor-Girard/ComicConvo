package com.taylorgirard.comicconvo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.IndividualMessageActivity;
import com.taylorgirard.comicconvo.adapters.MatchListAdapter;
import com.taylorgirard.comicconvo.adapters.UserListAdapter;
import com.taylorgirard.comicconvo.models.Comic;
import com.taylorgirard.comicconvo.tools.ListType;
import com.taylorgirard.comicconvo.tools.Match;

import java.util.ArrayList;
import java.util.List;


public class MatchesFragment extends Fragment {

    public static final int LIST_COLUMNS = 2;

    ImageButton ibMessage;
    ImageView ivMatchProfile;
    TextView tvAboutMatch;
    RecyclerView rvMatchLikes;
    RecyclerView rvMatchDislikes;
    List<Comic> likes;
    List<Comic> dislikes;

    public MatchesFragment(){
        //empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseUser match = currentUser.getParseUser("bestMatch");


//
//        try {
//            Match.findMatch(currentUser);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        ibMessage = view.findViewById(R.id.ibMessage);
        ivMatchProfile = view.findViewById(R.id.ivMatchProfile);
        tvAboutMatch = view.findViewById(R.id.tvAboutMatch);
        rvMatchLikes = view.findViewById(R.id.rvMatchLikes);
        rvMatchDislikes = view.findViewById(R.id.rvMatchDislikes);

        List<Comic> matchDislikes = null;
        try {
            matchDislikes = match.fetchIfNeeded().getList("Dislikes");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dislikes = new ArrayList<Comic>();
        try {
            dislikes.addAll(Comic.fromParseArray(matchDislikes));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final MatchListAdapter comicAdapterDislikes = new MatchListAdapter(getContext(), dislikes);
        rvMatchDislikes.setAdapter(comicAdapterDislikes);
        rvMatchDislikes.setLayoutManager(new GridLayoutManager(getContext(), LIST_COLUMNS));

        List<Comic> matchLikes= null;
        try {
            matchLikes = match.fetchIfNeeded().getList("Likes");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        likes = new ArrayList<Comic>();
        try {
            likes.addAll(Comic.fromParseArray(matchLikes));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final MatchListAdapter comicAdapterLikes = new MatchListAdapter(getContext(), likes);
        rvMatchLikes.setAdapter(comicAdapterLikes);
        rvMatchLikes.setLayoutManager(new GridLayoutManager(getContext(), LIST_COLUMNS));

        try {
            tvAboutMatch.setText(match.fetchIfNeeded().getString("aboutMe"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseFile matchPic = match.getParseFile("profilePic");
        if (matchPic != null) {
            Glide.with(getContext()).load(matchPic.getUrl()).transform(new CircleCrop()).into(ivMatchProfile);
        }

        ibMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), IndividualMessageActivity.class);
                startActivity(intent);
            }
        });
    }
}