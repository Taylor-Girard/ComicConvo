package com.taylorgirard.comicconvo.models;

import com.parse.ParseUser;

/**Model for a potential match that has a user and a score. For comparison during matching*/

public class PotentialMatch implements Comparable<PotentialMatch>{

    private ParseUser user;
    private double score;

    public PotentialMatch(ParseUser user, double score){
        this.user = user;
        this.score = score;
    }

    public double getScore(){return score;}

    public ParseUser getUser(){return user;}

    @Override
    public int compareTo(PotentialMatch o) {
        if (this.score < o.getScore()){
            return -1;
        } else if (this.score == o.getScore()){
            return 0;
        } else {
            return 1;
        }
    }
}
