package com.taylorgirard.comicconvo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.adapters.MessageAdapter;
import com.taylorgirard.comicconvo.models.Message;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndividualMessageActivity extends AppCompatActivity {

    public static final String TAG = "IndividualMessageActivity";

    EditText etMessage;
    ImageButton ibSend;
    ImageButton ibMakePin;
    RecyclerView rvMessages;
    ArrayList<Message> messages;
    MessageAdapter adapter;
    ParseUser match;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_message);
        setupMessagePosting();
        loadMessages();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(IndividualMessageActivity.this, MainActivity.class);
        startActivity(intent);
    }

    void setupMessagePosting(){
        etMessage = findViewById(R.id.etMessage);
        ibSend = findViewById(R.id.ibSend);
        ibMakePin = findViewById(R.id.ibMakePin);
        rvMessages = findViewById(R.id.rvMessages);
        messages = new ArrayList<>();

        match = getIntent().getParcelableExtra("Match");
        user = ParseUser.getCurrentUser();

        adapter = new MessageAdapter(IndividualMessageActivity.this, match, messages);
        rvMessages.setAdapter(adapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(IndividualMessageActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvMessages.setLayoutManager(linearLayoutManager);

        ibMakePin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndividualMessageActivity.this, AddPinActivity.class);
                intent.putExtra("Match", match);
                startActivity(intent);
            }
        });

        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etMessage.getText().toString();
                Message message = new Message();
                message.put("Body", body);
                message.put("Receiver", match);
                message.put("Sender", ParseUser.getCurrentUser());

                String userId = user.getObjectId();
                String matchId = match.getObjectId();
                String pairId;

                if (userId.compareTo(matchId) < 0){
                    pairId = userId + matchId;
                } else {
                    pairId = matchId + userId;
                }

                message.put("PairID", pairId);

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Error saving message", e);
                        } else{
                            Log.d(TAG, "Successfully saved message");
                        }
                    }
                });
                etMessage.setText("");

                user.addUnique("Messaged", match);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Error saving user messaged list", e);
                        } else{
                            Log.d(TAG, "Successfully saved user messaged list");
                        }
                    }
                });

                HashMap<String, Object> params = new HashMap<>();
                params.put("user" , user.getObjectId());
                params.put("match", match.getObjectId());
                ParseCloud.callFunctionInBackground("saveMessaged", params, new FunctionCallback<Boolean>() {
                    @Override
                    public void done(Boolean object, ParseException e) {
                        if (e==null && object){
                            Log.i(TAG, "Successfully saved match messaged list");
                        } else{
                            Log.e(TAG, "Error saving match messaged list", e);
                        }
                    }
                });

                ParseQuery<ParseUser> query = new ParseQuery<ParseUser>(ParseUser.class);
                ParseUser checkMatch = null;
                try {
                    checkMatch = query.get(match.getObjectId());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (checkMatch != null && checkMatch.getBoolean("Notifications")){
                    HashMap<String,Object> map = new HashMap<String, Object>();
                    map.put("username", user.getUsername());
                    map.put("matchId", match.getObjectId());
                    map.put("message", body);
                    ParseCloud.callFunctionInBackground("pushsample", map, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if (e==null){
                                Log.i(TAG, "Successfully launched push function");
                            } else{
                                Log.e(TAG, "Error launching push function", e);
                            }
                        }
                    });
                }

            }
        });
    }

    void loadMessages(){

        String websocketUrl = "wss://comicconvo.b4a.io/";

        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI(websocketUrl));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        List<ParseQuery<Message>> queryList = new ArrayList<>();

        ParseQuery<Message> querySender= ParseQuery.getQuery(Message.class);
        querySender.whereEqualTo("Sender", user);
        querySender.whereEqualTo("Receiver", match);
        queryList.add(querySender);

        ParseQuery<Message> queryReceiver= ParseQuery.getQuery(Message.class);
        queryReceiver.whereEqualTo("Sender", match);
        queryReceiver.whereEqualTo("Receiver", user);
        queryList.add(queryReceiver);

        ParseQuery<Message> finalQuery = ParseQuery.or(queryList);

        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(finalQuery);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {
            messages.add(0, object);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    rvMessages.scrollToPosition(0);
                }
            });
        });


        // get the latest 50 messages, order will show up newest to oldest of this group
        finalQuery.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        finalQuery.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> newMessages, ParseException e) {
                if (e == null) {
                    messages.clear();
                    messages.addAll(newMessages);
                    adapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    rvMessages.scrollToPosition(0);
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }

        });
    }

}