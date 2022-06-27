package com.taylorgirard.comicconvo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.IndividualMessageActivity;
import com.taylorgirard.comicconvo.tools.Match;


public class MatchesFragment extends Fragment {

    ImageButton ibMessage;

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

        try {
            Match.findMatch(currentUser);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ibMessage = view.findViewById(R.id.ibMessage);

        ibMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), IndividualMessageActivity.class);
                startActivity(intent);
            }
        });
    }
}