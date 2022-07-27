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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

/**Fragment that calculates new matches and displays their information*/

public class MatchesFragment extends Fragment {

    public static final String TAG = "Matches Fragment";
    public static final int LIST_COLUMNS = 2;

    ScrollView clMatchLayout;
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
    ParseUser match;

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

        List<ParseUser> matchList = currentUser.getList("matchList");
        if (matchList.size() <= 0) {
            try {
                Match.findMatch(currentUser);
                matchList = currentUser.getList("matchList");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (matchList.size() > 0) {
            match = matchList.get(matchList.size() - 1);
            currentUser.addUnique("matchedWith", match);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error saving to matchedWith list", e);
                    } else {
                        Log.i(TAG, "Success saving to matchedWith list");
                    }
                }
            });
        } else {
            match = currentUser;
            Toast.makeText(getContext(), "No more matches are available!", Toast.LENGTH_SHORT).show();
        }

        clMatchLayout = view.findViewById(R.id.clMatchLayout);
        tvMatchUsername = view.findViewById(R.id.tvMatchUsername);
        ibMessage = view.findViewById(R.id.ibMessage);
        ibSkip = view.findViewById(R.id.ibSkip);
        ivMatchProfile = view.findViewById(R.id.ivMatchProfile);
        tvAboutMatch = view.findViewById(R.id.tvAboutMatch);
        rvMatchLikes = view.findViewById(R.id.rvMatchLikes);
        rvMatchDislikes = view.findViewById(R.id.rvMatchDislikes);

        loadInfo();

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

    public void loadInfo(){

        dislikes = Match.loadMatchComics(ListType.DISLIKES, match);

        final MatchListAdapter comicAdapterDislikes = new MatchListAdapter(getContext(), dislikes);
        rvMatchDislikes.setAdapter(comicAdapterDislikes);
        LinearLayoutManager layoutManagerDislikes = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvMatchDislikes.setLayoutManager(layoutManagerDislikes);
        DividerItemDecoration dividerItemDecorationDislikes = new DividerItemDecoration(rvMatchDislikes.getContext(), layoutManagerDislikes.getOrientation());
        rvMatchDislikes.addItemDecoration(dividerItemDecorationDislikes);

        likes = Match.loadMatchComics(ListType.LIKES, match);

        final MatchListAdapter comicAdapterLikes = new MatchListAdapter(getContext(), likes);
        rvMatchLikes.setAdapter(comicAdapterLikes);
        LinearLayoutManager layoutManagerLikes = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvMatchLikes.setLayoutManager(layoutManagerLikes);
        DividerItemDecoration dividerItemDecorationLikes = new DividerItemDecoration(rvMatchLikes.getContext(), layoutManagerLikes.getOrientation());
        rvMatchLikes.addItemDecoration(dividerItemDecorationLikes);

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
        List<ParseUser> matchList = currentUser.getList("matchList");
        if (matchList.size() > 0){
            matchList.remove(matchList.size() - 1);
        }
        if (matchList.size() <= 0) {
            try {
                Match.findMatch(currentUser);
                matchList =  currentUser.getList("matchList");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (matchList.size() > 0) {
            match = matchList.get(matchList.size() - 1);
            currentUser.addUnique("matchedWith", match);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error saving to matchedWith list", e);
                    } else {
                        Log.i(TAG, "Success saving to matchedWith list");
                    }
                }
            });
        } else {
            match = currentUser;
            Toast.makeText(getContext(), "No more matches are available!", Toast.LENGTH_SHORT).show();
        }
        loadInfo();
    }

    public void goToMessages(){
        Intent intent = new Intent(getContext(), IndividualMessageActivity.class);
        intent.putExtra("Match", match);
        startActivity(intent);
    }
}