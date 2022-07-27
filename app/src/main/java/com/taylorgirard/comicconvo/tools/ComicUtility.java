package com.taylorgirard.comicconvo.tools;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.models.Comic;

import java.util.List;

/**Utility file for all things related to adding comics to user lists*/

public class ComicUtility {

    public static final String TAG = "ComicUtility";

    public static void addComic(Comic comic, ParseUser user, ListType comicType){

            ParseQuery<Comic> query = new ParseQuery<Comic>("Comic");
            query.whereEqualTo("comicId", comic.getComicId());
            try {
                Comic existingComic = query.getFirst();
                user.addUnique(comicType.toString(), existingComic);
                int timesAdded = existingComic.getInt("timesAdded");
                timesAdded += 1;
                existingComic.put("timesAdded", timesAdded);
                existingComic.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error incrementing timesAdded", e);
                        } else {
                            Log.i(TAG, "Success incrementing timesAdded");
                        }
                    }
                });
            } catch (ParseException e) {
                user.addUnique(comicType.toString(), comic);
                comic.put("comicId", comic.getComicId());
                comic.put("Title", comic.getTitle());
                comic.put("coverPath", comic.getCoverPath());
                comic.put("timesAdded", 1);
                comic.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving comic", e);
                        } else {
                            Log.d(TAG, "Successfully saved comic");
                        }
                    }
                });
            }
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving to comic list", e);
                    } else {
                        Log.d(TAG, "Successfully saved to comic list");
                    }
                }
            });

    }


}
