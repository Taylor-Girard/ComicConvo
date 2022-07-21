package com.taylorgirard.comicconvo.tools;

import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.adapters.MatchListAdapter;
import com.taylorgirard.comicconvo.models.Comic;
import com.taylorgirard.comicconvo.models.PotentialMatch;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**Utility file for all things related to calculating matches or loading a match's information*/

public class Match {

    public static final String TAG = "matchUtil";
    public static final int MAX_MATCH_LIST = 5;

    public static List<Comic> loadMatchComics(ListType listType, ParseUser match){

        List<Comic> matchList = null;
        try {
            matchList = match.fetchIfNeeded().getList(listType.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<Comic> comicList = new ArrayList<Comic>();
        try {
            comicList.addAll(Comic.fromParseArray(matchList));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return comicList;

    }

    public static void findMatch(ParseUser user) throws ParseException {

        //query list of all users
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        String userGenre = user.getString("Genre");
        query.whereEqualTo("Genre", userGenre);
        List<ParseUser> userList = query.find();

        //list of previous matches of the current user
        List<ParseUser> previousMatches = user.getList("matchedWith");
        //separate out into ids
        ArrayList<String> previousIds = new ArrayList<>();

        for (ParseUser i: previousMatches){
            previousIds.add(i.getObjectId());
        }

        ArrayList<PotentialMatch> matchListSorted = new ArrayList<>();
        Date currentDay = new Date();

        //go through all of the users
        for (int i = 0; i < userList.size(); i++){

            ParseUser potentialMatch = userList.get(i);
            Date lastLogin = potentialMatch.getDate("lastLogin");
            Boolean inactive = false;
            if (lastLogin != null){
                Instant now = currentDay.toInstant();
                Instant login = lastLogin.toInstant();
                long daysNotLoggedIn = ChronoUnit.DAYS.between(now, login);
                if (daysNotLoggedIn > 30){
                    inactive = true;
                }
            }

            //check if user has been matched to the current user before
            if (!previousIds.contains(potentialMatch.getObjectId()) && !(potentialMatch.getObjectId().equals(user.getObjectId())) && !inactive){

                double minScore;

                if (i > 5){
                    //set the min score as the lowest value in the arraylist
                    minScore = matchListSorted.get(0).getScore();
                } else {
                    minScore = 0;
                }

                //will keep score of this potential Match's score
                double matchScore = 0;

                //get current user's like and dislike lists
                List<Comic> userLikes = user.getList(ListType.LIKES.toString());
                ArrayList<String> userLikesIds = new ArrayList<>();
                for (Comic j: userLikes){
                    userLikesIds.add(j.getObjectId());
                }
                List<Comic> userDislikes = user.getList(ListType.DISLIKES.toString());
                ArrayList<String> userDislikesIds = new ArrayList<>();
                for (Comic j: userDislikes){
                    userDislikesIds.add(j.getObjectId());
                }

                //get potential match's like and dislike lists to compare to
                List<Comic> matchLikes = potentialMatch.getList(ListType.LIKES.toString());
                ArrayList<String> matchLikesIds = new ArrayList<>();
                for (Comic j: matchLikes){
                    matchLikesIds.add(j.getObjectId());
                }
                List<Comic> matchDislikes = potentialMatch.getList(ListType.DISLIKES.toString());
                ArrayList<String> matchDislikesIds = new ArrayList<>();
                for (Comic j: matchDislikes){
                    matchDislikesIds.add(j.getObjectId());
                }

                //go through user's likes and compare to potential Match lists
                for (int j = 0; j < userLikesIds.size(); j++){
                    int popularity = userLikes.get(j).fetchIfNeeded().getInt("timesAdded");
                    double multiplier = (1.0 / popularity);
                    if (matchLikesIds.contains(userLikesIds.get(j))){
                        matchScore += 1 * multiplier;
                    } else if (matchDislikesIds.contains(userLikesIds.get(j))){
                        matchScore -= 1 * multiplier;
                    }
                }

                //go through user's dislikes list and add to matchScore
                for (int j = 0; j < userDislikes.size(); j++){
                    int popularity = userDislikes.get(j).fetchIfNeeded().getInt("timesAdded");
                    double multiplier = (1.0 / popularity);
                    if(matchDislikesIds.contains(userDislikesIds.get(j))){
                        matchScore += 1 * multiplier;
                    } else if (matchLikesIds.contains(userDislikesIds.get(j))){
                        matchScore -= 1 * multiplier;
                    }
                }

                //compare the visited pins of both the user and the potential match
                List<String> userPins = user.getList("favoritePins");
                List<String> matchPins = potentialMatch.getList("favoritePins");

                for (String p : userPins){
                    if (matchPins.contains(p)){
                        matchScore += 1;
                    }
                }


                //if Match score is higher than the threshold, replace lowest Match user with this potential Match
                if (matchListSorted.size() < MAX_MATCH_LIST || matchScore > minScore){

                    PotentialMatch match = new PotentialMatch(potentialMatch, matchScore);

                    //add the match to the list and sort
                    matchListSorted.add(match);
                    Collections.sort(matchListSorted);

                    //remove the lowest value if size is greater than 5
                    if (matchListSorted.size() > 5) {
                        matchListSorted.remove(0);
                    }

                }

            }

        }

        //create empty list
        ArrayList<ParseUser> matches = new ArrayList<>();

        //create matches list based on the sorted list
        for (PotentialMatch i : matchListSorted){
            matches.add(i.getUser());
        }

        //set user's match list to the list of matches made
        user.put("matchList", matches);
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