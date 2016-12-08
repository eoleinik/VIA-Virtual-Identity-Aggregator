package com.example.evgeniy.scanner;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ScrollingProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_profile);
        Person person = this.getIntent().getExtras().getParcelable("person");

        ImageView picture = (ImageView) findViewById(R.id.contact_picture);
        picture.setImageResource(R.drawable.dicaprio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView phoneNumber = (TextView) findViewById(R.id.phone_number);
        phoneNumber.setText(person.getPhone());

        TextView emailAddress = (TextView) findViewById(R.id.email_address);
        emailAddress.setText(person.getEmail());

        setTitle(person.getFullName());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
