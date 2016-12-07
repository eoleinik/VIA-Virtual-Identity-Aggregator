package com.example.evgeniy.scanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ContactDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        String name = this.getIntent().getExtras().getString("name");
        TextView textView = (TextView) findViewById(R.id.name_text_view);
        textView.setText(name);
    }
}
