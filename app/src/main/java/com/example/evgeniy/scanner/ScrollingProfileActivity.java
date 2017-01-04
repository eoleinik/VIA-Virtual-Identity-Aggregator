package com.example.evgeniy.scanner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;

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

        // Facebook button
        String facebook = person.getFacebook();
        LinearLayout facebookBlock = (LinearLayout)findViewById(R.id.facebook_block);
        if (facebook == null || facebook.isEmpty() || facebook.equals("null")) {
            facebookBlock.setVisibility(View.GONE);
        } else {
            TextView facebook_id = (TextView) findViewById(R.id.facebook_id);
            facebook_id.setText(String.format(getString(R.string.on_facebook), person.getFirstName()));
            facebookBlock.setVisibility(View.VISIBLE);
        }

        // Twitter button
        LinearLayout twitterBlock = (LinearLayout) findViewById(R.id.twitter_block);
        String twitter = person.getTwitter();
        if (twitter == null || twitter.isEmpty() || twitter.equals("null")) {
            twitterBlock.setVisibility(View.GONE);
        } else {
            TextView twitter_id = (TextView) findViewById(R.id.twitter_id);
            twitter_id.setText(String.format(getString(R.string.follow), "@"+twitter));
            twitterBlock.setVisibility(View.VISIBLE);
        }

        // deleting profile
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final ScrollingProfileActivity that = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Deleting profile...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                DBHandler.removeContact(that, person.getId());
            }
        });
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);

    }

    public void onFacebookClick(View view) {
        String fbId = person.getFacebook();
        if (fbId == null || fbId.equals("null") || fbId.isEmpty())
            return;

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        try {
            customTabsIntent.launchUrl(this, Uri.parse("https://www.facebook.com/app_scoped_user_id/" + fbId));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onTwitterClick(View view) {
        TwitterSession session = Twitter.getInstance().core.getSessionManager().getActiveSession();
        if (session != null) {
            final String twitterTag = "@" + person.getTwitter();
            TwitterFollow apiClient = new TwitterFollow(session);
            Call<User> call = apiClient.getFollowService().create(person.getTwitter(), null, true);
            call.enqueue(new Callback<User>() {
                @Override
                public void success(Result<User> result) {
                    Toast.makeText(getApplicationContext(), "Following " + twitterTag, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(TwitterException e) {
                    Toast.makeText(getApplicationContext(), "Error following " + twitterTag, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Twitter.getInstance().core.logIn(this, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(getApplicationContext(),
                            "Must login to twitter to follow.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

//    public void onSmsClick(View view) {
//        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//        sendIntent.setData(Uri.parse("sms:" + person.getPhone()));
//        startActivity(sendIntent);
//    }

    public void onEmailClick(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("*/*");
        intent.setData(Uri.parse("mailto:" + person.getEmail()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void onCallClick(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_DIAL);
        sendIntent.setData(Uri.parse("tel:" + person.getPhone()));
        startActivity(sendIntent);
    }
}
