package com.taylorgirard.comicconvo.tools;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.models.Comic;

import java.util.ArrayList;
import java.util.List;

public class Match {

    public static final String TAG = "matchUtil";

    public static void findMatch(ParseUser user) throws ParseException {

        //query list of all users
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        List<ParseUser> userList = query.find();

        //list of previous matches of the current user, separate out into ids
        List<ParseUser> previousMatches = user.getList("matchedWith");
        ArrayList<String> previousIds = new ArrayList<>();

        for (ParseUser i: previousMatches){
            previousIds.add(i.getObjectId());
        }

        //Initialize bestMatch as the first user for now
        ParseUser bestMatch = userList.get(0);

        //Set the highest Match score
        int highestMatch = 0;

        //go through all of the users
        for (int i = 0; i < userList.size(); i++){

            ParseUser potentialMatch = userList.get(i);

            //check if user has been matched to the current user before
            if (!previousIds.contains(potentialMatch.getObjectId()) && !(potentialMatch.getObjectId().equals(user.getObjectId()))){

                //will keep score of this potential Match's score
                int matchScore = 0;

                //get current user's like and dislike lists
                List<Comic> userLikes = user.getList("Likes");
                ArrayList<String> userLikesIds = new ArrayList<>();
                for (Comic j: userLikes){
                    userLikesIds.add(j.getObjectId());
                }
                List<Comic> userDislikes = user.getList("Dislikes");
                ArrayList<String> userDislikesIds = new ArrayList<>();
                for (Comic j: userDislikes){
                    userDislikesIds.add(j.getObjectId());
                }


                //get potential match's like and dislike lists to compare to
                List<Comic> matchLikes = potentialMatch.getList("Likes");
                ArrayList<String> matchLikesIds = new ArrayList<>();
                for (Comic j: matchLikes){
                    matchLikesIds.add(j.getObjectId());
                }
                List<Comic> matchDislikes = potentialMatch.getList("Dislikes");
                ArrayList<String> matchDislikesIds = new ArrayList<>();
                for (Comic j: matchDislikes){
                    matchDislikesIds.add(j.getObjectId());
                }

                //go through user's likes and compare to potential Match lists
                for (int j = 0; j < userLikesIds.size(); j++){
                    if (matchLikesIds.contains(userLikesIds.get(j))){
                        matchScore += 1;
                    } else if (matchDislikesIds.contains(userLikesIds.get(j))){
                        matchScore -= 1;
                    }
                }

                //go through user's dislikes list and add to matchScore
                for (int j = 0; j < userDislikes.size(); j++){
                    if(matchDislikesIds.contains(userDislikesIds.get(j))){
                        matchScore += 1;
                    } else if (matchLikesIds.contains(userDislikesIds.get(j))){
                        matchScore -= 1;
                    }
                }


                //if Match score is higher than the highest Match, replace highest Match user with this potential Match
                if (matchScore > highestMatch){
                    bestMatch = potentialMatch;
                    highestMatch = matchScore;
                }

            }

        }

        //set user's "bestMatch" column to be highest Match user
        //add Match to user's matched list
        user.put("bestMatch", bestMatch);
        user.addUnique("matchedWith", bestMatch);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d(TAG, "Successfully saved to user");
                } else{
                    Log.e(TAG, "Error saving to user", e);
                }
            }
        });

    }

}