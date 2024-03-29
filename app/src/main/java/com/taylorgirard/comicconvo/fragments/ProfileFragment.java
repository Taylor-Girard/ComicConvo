package com.taylorgirard.comicconvo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.ComicSearchActivity;
import com.taylorgirard.comicconvo.activities.LoginActivity;
import com.taylorgirard.comicconvo.tools.ListType;
import com.taylorgirard.comicconvo.adapters.UserListAdapter;
import com.taylorgirard.comicconvo.models.Comic;
import com.taylorgirard.comicconvo.tools.Match;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**Fragment where user can see their profile and change different attributes about themselves*/

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final int PICK_IMAGE = 1;

    ParseUser user = ParseUser.getCurrentUser();
    TextView tvUsername;
    ImageView ivUserProfile;
    EditText etAboutMe;
    ImageButton btnLogout;
    ImageButton btnAboutMe;
    ImageButton btnEditLists;
    Spinner spGenreList;
    RecyclerView rvLikes;
    RecyclerView rvDislikes;
    List<Comic> likes;
    List<Comic> dislikes;
    String genre;
    UserListAdapter comicAdapterDislikes;
    UserListAdapter comicAdapterLikes;

    public ProfileFragment() {
        //Empty constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInformation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivUserProfile = view.findViewById(R.id.ivUserProfile);
        etAboutMe = view.findViewById(R.id.etAboutMe);
        btnAboutMe = view.findViewById(R.id.btnAboutMe);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditLists = view.findViewById(R.id.btnEditLists);
        rvLikes = view.findViewById(R.id.rvMatchLikes);
        rvDislikes = view.findViewById(R.id.rvMatchDislikes);
        spGenreList = view.findViewById(R.id.spGenreList);

        loadInformation();

        btnEditLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ComicSearchActivity.class);
                startActivity(intent);
            }
        });

        btnAboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etAboutMe.getText().toString();
                user.put("aboutMe", description);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Error while saving about me", e);
                            Toast.makeText(getContext(), "Error while saving about me!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOutInBackground();
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                ArrayList<String> channel = new ArrayList<>();
                installation.put("channels", channel);
                installation.saveInBackground();
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent i = new Intent(getContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
                startActivity(i);
            }
        });

        ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if(resultCode == Activity.RESULT_OK){
                Uri photoUri = data.getData();
                if(photoUri != null){
                    Glide.with(getContext()).load(photoUri).transform(new CircleCrop()).into(ivUserProfile);
                }
                // Load the image located at photoUri into selectedImage
                Bitmap selectedImage = loadFromUri(photoUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ParseFile profilePic = new ParseFile(byteArray);
                user.put("profilePic", profilePic);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                                        Log.e(TAG, "Error while saving", e);
                                        Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                                    }
                                    Log.i(TAG, "Profile save was successful!");
                    }
                });
            }
        }
    }

    public void loadInformation(){
        genre = user.getString("Genre");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.genre_list, android.R.layout.simple_spinner_dropdown_item);
        spGenreList.setAdapter(adapter);
        if(genre.equals("Adventure")){
            spGenreList.setSelection(0);
        } else if (genre.equals("Comedy")){
            spGenreList.setSelection(1);
        } else{
            spGenreList.setSelection(2);
        }
        spGenreList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genre = parent.getItemAtPosition(position).toString();
                user.put("Genre", genre);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            Log.i(TAG, "Successfully saved genre tag");
                        } else{
                            Log.e(TAG, "Error saving to genre tag", e);
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set up list of likes
        List<Comic> userDislikes = user.getList(ListType.DISLIKES.toString());

        dislikes = new ArrayList<Comic>();
        try {
            dislikes.addAll(Comic.fromParseArray(userDislikes));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        comicAdapterDislikes = new UserListAdapter(getContext(), dislikes, ListType.DISLIKES);
        rvDislikes.setAdapter(comicAdapterDislikes);
        LinearLayoutManager layoutManagerDislikes = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDislikes.setLayoutManager(layoutManagerDislikes);
        DividerItemDecoration dividerItemDecorationDislikes = new DividerItemDecoration(rvDislikes.getContext(), layoutManagerDislikes.getOrientation());
        rvDislikes.addItemDecoration(dividerItemDecorationDislikes);

        //Set up list of dislikes
        List<Comic> userLikes = user.getList(ListType.LIKES.toString());

        likes = new ArrayList<Comic>();
        try {
            likes.addAll(Comic.fromParseArray(userLikes));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        comicAdapterLikes = new UserListAdapter(getContext(), likes, ListType.LIKES);
        rvLikes.setAdapter(comicAdapterLikes);
        LinearLayoutManager layoutManagerLikes = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvLikes.setLayoutManager(layoutManagerLikes);
        DividerItemDecoration dividerItemDecorationLikes = new DividerItemDecoration(rvLikes.getContext(), layoutManagerLikes.getOrientation());
        rvLikes.addItemDecoration(dividerItemDecorationLikes);

        tvUsername.setText(user.getUsername());

        ParseFile profilePic = user.getParseFile("profilePic");
        if (profilePic != null) {
            Glide.with(getContext()).load(profilePic.getUrl()).transform(new CircleCrop()).into(ivUserProfile);
        }

        String AboutMe = user.getString("aboutMe");
        etAboutMe.setText(AboutMe);

    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}

