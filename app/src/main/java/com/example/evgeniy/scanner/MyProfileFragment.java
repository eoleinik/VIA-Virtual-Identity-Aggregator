package com.example.evgeniy.scanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MyProfileFragment extends Fragment{
    private View view;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    void loadProfile() {
        Person person = PersonContract.getProfile(view.getContext());
        if (person == null) {
            return;
        }

        ((TextView) view.findViewById(R.id.email_address)).setText(person.getEmail());
        ((TextView) view.findViewById(R.id.phone_number)).setText(person.getPhone());
        ((TextView) view.findViewById(R.id.my_name)).setText(person.getFullName());

        // facebook account
        String facebook = person.getFacebook();
        LinearLayout facebookBlock = (LinearLayout)view.findViewById(R.id.facebook_block);
        if (facebook == null || facebook.isEmpty() || facebook.equals("null")) {
            facebookBlock.setVisibility(View.GONE);
        } else {
            TextView facebook_id = (TextView) view.findViewById(R.id.facebook_id);
            facebook_id.setText(String.format(getString(R.string.on_facebook), person.getFirstName()));
            facebookBlock.setVisibility(View.VISIBLE);
        }

        // twitter account
        String twitter_handle = person.getTwitter();
        LinearLayout twitterBlock = (LinearLayout)view.findViewById(R.id.twitter_block);
        if (twitter_handle == null || twitter_handle.isEmpty() || twitter_handle.equals("null")) {
            twitterBlock.setVisibility(View.GONE);
        } else {
            TextView twitter_id = (TextView) view.findViewById(R.id.twitter_id);
            twitter_id.setText("@"+twitter_handle);
            twitterBlock.setVisibility(View.VISIBLE);
        }

        String imageId = person.getPicture();
        if (imageId != null) {
            try {
                File sd = getContext().getFilesDir();
                File myImage = new File(sd, imageId);
                FileInputStream in = new FileInputStream(myImage);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                ImageView imageView = (ImageView) view.findViewById(R.id.my_picture);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Couldn't load my profile image");
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);

        view = v;

        if (PersonContract.getProfile(v.getContext()) == null) {
            Intent detailIntent = new Intent(v.getContext(), ProfileEditActivity.class);
            startActivity(detailIntent);
            return v;
        } else {
            // Inflate the layout for this fragment
            loadProfile();
            return v;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }
}