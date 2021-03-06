package com.taylorgirard.comicconvo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.IndividualMessageActivity;
import com.taylorgirard.comicconvo.adapters.MessageListAdapter;
import com.taylorgirard.comicconvo.models.Message;

import java.util.ArrayList;
import java.util.List;

/**Fragment where users can see their previous conversations and then go to those individual message pages*/

public class MessagesFragment extends Fragment {

    List<Message> recents = new ArrayList<>();
    ParseUser user = ParseUser.getCurrentUser();
    MessageListAdapter adapter;
    RecyclerView rvMessageList;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        makeMessageList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMessageList = view.findViewById(R.id.rvMessageList);

        adapter = new MessageListAdapter(getContext(), recents);
        rvMessageList.setAdapter(adapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvMessageList.setLayoutManager(linearLayoutManager);

        makeMessageList();
    }

    public void makeMessageList(){

        recents.clear();

        List<ParseUser> messaged = user.getList("Messaged");
        for (ParseUser match: messaged){

            String userId = user.getObjectId();
            String matchId = match.getObjectId();
            String pairId;

            if (userId.compareTo(matchId) < 0){
                pairId = userId + matchId;
            } else {
                pairId = matchId + userId;
            }

            ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
            query.orderByDescending("createdAt");
            query.whereEqualTo("PairID", pairId);

            try {
                Message message = query.getFirst();
                recents.add(message);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        adapter.notifyDataSetChanged();

    }
}