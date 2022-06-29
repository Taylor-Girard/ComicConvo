package com.taylorgirard.comicconvo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.IndividualMessageActivity;
import com.taylorgirard.comicconvo.adapters.MatchListAdapter;
import com.taylorgirard.comicconvo.models.Comic;
import com.taylorgirard.comicconvo.tools.ListType;
import com.taylorgirard.comicconvo.tools.Match;
import com.taylorgirard.comicconvo.tools.OnSwipeTouchListener;

import java.util.ArrayList;
import java.util.List;


public class MatchesFragment extends Fragment {

    public static final String TAG = "Matches Fragment";
    public static final int LIST_COLUMNS = 2;

    ConstraintLayout clMatchLayout;
    TextView tvMatchUsername;
    ImageButton ibMessage;
    ImageButton ibSkip;
    ImageView ivMatchProfile;
    TextView tvAboutMatch;
    RecyclerView rvMatchLikes;
    RecyclerView rvMatchDislikes;
    List<Comic> likes;
    List<Comic> dislikes;
    ParseUser currentUser = ParseUser.getCurrentUser();

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

        if (currentUser.getParseUser("bestMatch") == null){
            try {
                Match.findMatch(currentUser);
                currentUser.addUnique("matchedWith", currentUser.getParseUser("bestMatch"));
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Error saving to matchedWith list", e);
                        } else{
                            Log.i(TAG, "Success saving to matchedWith list");
                        }
                    }
                });
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        ParseUser match = currentUser.getParseUser("bestMatch");

        clMatchLayout = view.findViewById(R.id.clMatchLayout);
        tvMatchUsername = view.findViewById(R.id.tvMatchUsername);
        ibMessage = view.findViewById(R.id.ibMessage);
        ibSkip = view.findViewById(R.id.ibSkip);
        ivMatchProfile = view.findViewById(R.id.ivMatchProfile);
        tvAboutMatch = view.findViewById(R.id.tvAboutMatch);
        rvMatchLikes = view.findViewById(R.id.rvMatchLikes);
        rvMatchDislikes = view.findViewById(R.id.rvMatchDislikes);

        loadInfo(match);

        clMatchLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()){
            @Override
            public void onSwipeLeft() {
                skipMatch();
            }

            @Override
            public void onSwipeRight() {
                goToMessages();
            }
        });

        ibMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goToMessages();
            }
        });

        ibSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipMatch();
            }
        });
    }

    public void loadInfo(ParseUser match){

        List<Comic> matchDislikes = null;
        try {
            matchDislikes = match.fetchIfNeeded().getList(ListType.DISLIKES.toString());
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
            matchLikes = match.fetchIfNeeded().getList(ListType.LIKES.toString());
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

        tvMatchUsername.setText(match.getUsername());

        try {
            tvAboutMatch.setText(match.fetchIfNeeded().getString("aboutMe"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseFile matchPic = match.getParseFile("profilePic");
        if (matchPic != null) {
            Glide.with(getContext()).load(matchPic.getUrl()).transform(new CircleCrop()).into(ivMatchProfile);
        }

    }

    public void skipMatch(){
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_animation);
        clMatchLayout.startAnimation(animation);
        try {
            Match.findMatch(currentUser);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ParseUser newMatch = currentUser.getParseUser("bestMatch");
        currentUser.addUnique("matchedWith", newMatch);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d(TAG, "Successfully saved to match list");
                } else{
                    Log.e(TAG, "Error saving to match list", e);
                }
            }
        });
        loadInfo(newMatch);
    }

    public void goToMessages(){
        Intent intent = new Intent(getContext(), IndividualMessageActivity.class);
        startActivity(intent);
    }
}