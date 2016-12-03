package com.example.evgeniy.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

public class MyCodeFragment extends Fragment{

    private boolean picture_present = false;

    public MyCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_code, container, false);

        FileInputStream in;
        Context context = view.getContext();
        View myCodeAlt = view.findViewById(R.id.myCodeAlt);
        try {
            File sd = context.getFilesDir();
            File myCode = new File(sd, getString(R.string.my_qr_code));
            in = new FileInputStream(myCode);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            ImageView tv1 = (ImageView) view.findViewById(R.id.barcodeView);
            tv1.setImageBitmap(bitmap);
            myCodeAlt.setVisibility(View.GONE);
        } catch(Exception e) {
            myCodeAlt.setVisibility(View.VISIBLE);
        }
        return view;
    }
}