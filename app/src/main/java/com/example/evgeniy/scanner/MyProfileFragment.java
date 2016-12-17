package com.example.evgeniy.scanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

        v.findViewById(R.id.linerLayoutView).setVisibility(View.VISIBLE);
        v.findViewById(R.id.linerLayoutEdit).setVisibility(View.INVISIBLE);

        v.findViewById(R.id.buttonEdit).setVisibility(View.VISIBLE);
        v.findViewById(R.id.buttonSave).setVisibility(View.INVISIBLE);

        ((TextView) v.findViewById(R.id.textViewFirstName)).setText(person.getFirstName());
        ((TextView) v.findViewById(R.id.textViewLastName)).setText(person.getLastName());
        ((TextView) v.findViewById(R.id.textViewEmail)).setText(person.getEmail());
        ((TextView) v.findViewById(R.id.textViewPhone)).setText(person.getPhone());

        ProgressBar spinner = (ProgressBar)v.findViewById(R.id.uploadSpinner);
        spinner.setVisibility(View.GONE);

        String imageId = person.getPicture();

        if (imageId != null) {
            try {
                File sd = getContext().getFilesDir();
                File myImage = new File(sd, imageId);
                FileInputStream in = new FileInputStream(myImage);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                ImageView imageView = (ImageView)v.findViewById(R.id.imageView);
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        loadProfile(v);
        return v;
    }
}