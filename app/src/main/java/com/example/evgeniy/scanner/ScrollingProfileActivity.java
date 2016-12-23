package com.example.evgeniy.scanner;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScrollingProfileActivity extends AppCompatActivity {
    private Person person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_profile);
        person = this.getIntent().getExtras().getParcelable("person");

        ImageView picture = (ImageView) findViewById(R.id.contact_picture);
        picture.setImageBitmap(person.getBitmap(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(person.getFullName());

        // Phone
        String phone = person.getPhone();
        LinearLayout phoneBlock = (LinearLayout) findViewById(R.id.phone_block);
        if (phone != null && !phone.equals("")) {
            TextView phoneView = (TextView) findViewById(R.id.phone_number);
            phoneView.setText(phone);
            phoneBlock.setVisibility(View.VISIBLE);
        } else {
            phoneBlock.setVisibility(View.GONE);
        }

        // Email
        String email = person.getEmail();
        LinearLayout emailBlock = (LinearLayout) findViewById(R.id.email_block);
        if (email != null && !email.equals("")) {
            TextView emailView = (TextView) findViewById(R.id.email_address);
            emailView.setText(email);
            emailBlock.setVisibility(View.VISIBLE);
        } else {
            emailBlock.setVisibility(View.GONE);
        }

        // Address
        String address = person.getAddress();
        LinearLayout addressBlock = (LinearLayout) findViewById(R.id.address_block);
        if (address != null && !address.equals("")) {
            TextView addressView = (TextView) findViewById(R.id.address);
            addressView.setText(address);
            addressBlock.setVisibility(View.VISIBLE);
        } else {
            addressBlock.setVisibility(View.GONE);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void onRemoveClick(View view) {
        DBHandler.removeContact(this, person.getId());
        // After contact removed (async), will go back to main activity
    }
}
