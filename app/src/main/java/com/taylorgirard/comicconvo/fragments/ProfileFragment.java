package com.taylorgirard.comicconvo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.LoginActivity;

import java.io.File;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final int PICK_IMAGE = 1;

    ParseUser user = ParseUser.getCurrentUser();
    ImageView ivUserProfile;
    EditText etAboutMe;
    Button btnLogout;
    Button btnAboutMe;
    private File photoFile;
    public String photoFileName = "photo.jpg";

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

        ParseFile profilePic = user.getParseFile("profilePic");
        if (profilePic != null) {
            Glide.with(getContext()).load(profilePic.getUrl()).transform(new CircleCrop()).into(ivUserProfile);
        }

        String AboutMe = user.getString("aboutMe");
        etAboutMe.setText(AboutMe);

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

                photoFile = getPhotoFileUri(photoFileName);

                Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
                chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                startActivityForResult(chooserIntent, PICK_IMAGE);

                ParseFile profile = new ParseFile(photoFile);
                profile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            user.put("profilePic", profile);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null){
                                        Log.e(TAG, "Error while saving", e);
                                        Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                                    }
                                    Log.i(TAG, "Profile save was successful!");
                                };
                            });
                        } else{
                            Log.e(TAG, "Error while saving image", e);
                            Toast.makeText(getContext(), "Error while saving image!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if(resultCode == Activity.RESULT_OK){
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null){
                    Glide.with(getContext()).load(selectedImageUri).transform(new CircleCrop()).into(ivUserProfile);
                }
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(String.valueOf(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)));

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);

    }

}

