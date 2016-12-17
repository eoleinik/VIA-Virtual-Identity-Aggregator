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
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MyProfileFragment extends Fragment{


    public MyProfileFragment() {
        // Required empty public constructor
    }

    private void loadProfile(View v) {
        Person person = PersonContract.getProfile(getActivity().getApplicationContext());
        if (person == null)
            return;

        ((TextView) v.findViewById(R.id.email_address)).setText(person.getEmail());
        ((TextView) v.findViewById(R.id.phone_number)).setText(person.getPhone());
        ((TextView) v.findViewById(R.id.address)).setText(person.getAddress());

        String imageId = person.getPicture();
        if (imageId != null) {
            try {
                File sd = getContext().getFilesDir();
                File myImage = new File(sd, imageId);
                FileInputStream in = new FileInputStream(myImage);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                ImageView imageView = (ImageView) v.findViewById(R.id.my_picture);
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

        if (PersonContract.getProfile(v.getContext()) == null) {
            Intent detailIntent = new Intent(v.getContext(), ProfileEditActivity.class);
            startActivity(detailIntent);
            return v;
        } else {
            // Inflate the layout for this fragment
            loadProfile(v);
            return v;
        }
    }
}