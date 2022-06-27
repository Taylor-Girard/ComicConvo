package com.taylorgirard.comicconvo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.ComicSearchActivity;
import com.taylorgirard.comicconvo.activities.LoginActivity;
import com.taylorgirard.comicconvo.tools.ListType;
import com.taylorgirard.comicconvo.adapters.UserListAdapter;
import com.taylorgirard.comicconvo.models.Comic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final int PICK_IMAGE = 1;

    ParseUser user = ParseUser.getCurrentUser();
    ImageView ivUserProfile;
    EditText etAboutMe;
    Button btnLogout;
    Button btnAboutMe;
    Button btnEditLists;
    RecyclerView rvLikes;
    RecyclerView rvDislikes;
    List<Comic> likes;
    List<Comic> dislikes;

    public ProfileFragment() {
        //Empty constructor
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
        ivUserProfile = view.findViewById(R.id.ivUserProfile);
        etAboutMe = view.findViewById(R.id.etAboutMe);
        btnAboutMe = view.findViewById(R.id.btnAboutMe);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditLists = view.findViewById(R.id.btnEditLists);
        rvLikes = view.findViewById(R.id.rvLikes);
        rvDislikes = view.findViewById(R.id.rvDislikes);

        //Set up list of likes
        List<Comic> userDislikes = user.getList("Dislikes");

        dislikes = new ArrayList<Comic>();
        try {
            dislikes.addAll(Comic.fromParseArray(userDislikes));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final UserListAdapter comicAdapterDislikes = new UserListAdapter(getContext(), dislikes, ListType.DISLIKES);
        rvDislikes.setAdapter(comicAdapterDislikes);
        rvDislikes.setLayoutManager(new GridLayoutManager(getContext(), 2));

        //Set up list of dislikes
        List<Comic> userLikes = user.getList("Likes");

        likes = new ArrayList<Comic>();
        try {
            likes.addAll(Comic.fromParseArray(userLikes));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final UserListAdapter comicAdapterLikes = new UserListAdapter(getContext(), likes, ListType.LIKES);
        rvLikes.setAdapter(comicAdapterLikes);
        rvLikes.setLayoutManager(new GridLayoutManager(getContext(), 2));

        ParseFile profilePic = user.getParseFile("profilePic");
        if (profilePic != null) {
            Glide.with(getContext()).load(profilePic.getUrl()).transform(new CircleCrop()).into(ivUserProfile);
        }

        String AboutMe = user.getString("aboutMe");
        etAboutMe.setText(AboutMe);

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

