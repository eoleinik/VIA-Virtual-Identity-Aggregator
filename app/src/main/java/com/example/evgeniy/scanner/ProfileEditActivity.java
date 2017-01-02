package com.example.evgeniy.scanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileEditActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_FROM_GALLERY = 1;
    private Uri myImageUri = null;
    private CallbackManager callbackManager;

    private EditText editTextEmail;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPhone;

    private ProgressBar uploadSpinner;
    private ImageView imagePreview;

    private TwitterLoginButton twitterLoginButton;


    private ProfileTracker profileTracker = new ProfileTracker() {
        @Override
        protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
            if (newProfile == null)
                return;

            if (editTextLastName.getText().length() == 0 &&
                    editTextFirstName.getText().length() == 0) {
                editTextLastName.setText(newProfile.getLastName());
                editTextFirstName.setText(newProfile.getFirstName());
            }

            if (imagePreview.getDrawable() == null) {
                RequestQueue queue = VolleyHandler.getInstance(getApplicationContext()).getRequestQueue();

                ImageRequest imgRequest =
                        new ImageRequest(newProfile.getProfilePictureUri(1024, 768).toString(), new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                FileOutputStream out = null;
                                try {
                                    File sd = getApplicationContext().getFilesDir();
                                    File file = new File(sd, "profile.png");
                                    out = new FileOutputStream(file);
                                    response.compress(Bitmap.CompressFormat.PNG, 100, out);
                                    out.close();

                                    setMyImageUri(Uri.fromFile(file));

                                    InputStream imageStream = new FileInputStream(file);
                                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                                    imagePreview.setImageBitmap(bitmap);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (out != null) {
                                            out.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "Unable to fetch image.", Toast.LENGTH_LONG).show();
                                    }
                                });

                queue.add(imgRequest);
            }
        }
    };

    void saveSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    private Uri getMyImageUri() {
        return myImageUri;
    }

    private void setMyImageUri(Uri imageUri) {
        this.myImageUri = imageUri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_profile_edit);

        profileTracker.startTracking();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        uploadSpinner = (ProgressBar) findViewById(R.id.uploadSpinner);
        imagePreview = (ImageView) findViewById(R.id.imagePreview);
        //loginButton = (LoginButton) findViewById(R.id.loginButton);
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitterLoginButton);

        TwitterSession session = Twitter.getInstance().core.getSessionManager().getActiveSession();
        if (session != null) {
            String user = "@" + session.getUserName();
            twitterLoginButton.setText(user);
        }

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                String user = "@" + session.getUserName();
                twitterLoginButton.setText(user);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });


        Person person = PersonContract.getProfile(this);

        if (person != null) {
            editTextFirstName.setText(person.getFirstName());
            editTextLastName.setText(person.getLastName());
            editTextEmail.setText(person.getEmail());
            editTextPhone.setText(person.getPhone());

            uploadSpinner.setVisibility(View.GONE);

            String imageId = person.getPicture();
            if (imageId != null) {
                try {
                    File sd = getFilesDir();
                    File myImage = new File(sd, imageId);
                    FileInputStream in = new FileInputStream(myImage);
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    imagePreview.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    System.out.println("Couldn't load my profile image");
                }
            }
        }
    }

    //void updateProfile

    public void openGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select file to upload "), SELECT_IMAGE_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Facebook activity result sent to callback manager
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Twitter activity result
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_FROM_GALLERY) {     // if we were choosing photo
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                try {
                    // select image
                    InputStream myImage = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(myImage);

                    // scale down
                    float aspectRatio = bitmap.getWidth() /
                            (float) bitmap.getHeight();
                    int width = 1000;
                    int height = Math.round(width / aspectRatio);
                    bitmap = Bitmap.createScaledBitmap(
                            bitmap, width, height, false);

                    // save locally
                    PhotoManager pm = new PhotoManager(this);
                    String imageName = getString(R.string.my_profile_image);
                    pm.saveLocally(bitmap, imageName);
                    File sd = getFilesDir();
                    File file = new File(sd, imageName);
                    Uri localUri = Uri.fromFile(file);

                    // "swap" uri
                    setMyImageUri(localUri);
                    imagePreview.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onSaveClick(View view) {
        View focusView = null;
        /* reset errors */
        editTextEmail.setError(null);
        editTextPhone.setError(null);
        editTextLastName.setError(null);
        editTextFirstName.setError(null);

        String email = editTextEmail.getText().toString();
        if (!isEmailValid(email)) {
            editTextEmail.setError("Email invalid");
            focusView = editTextEmail;
        }

        if (editTextPhone.getText().length() == 0) {
            editTextPhone.setError("Phone required");
            focusView = editTextPhone;
        }

        if (editTextLastName.getText().length() == 0) {
            editTextLastName.setError("Last name required");
            focusView = editTextLastName;
        }

        if (editTextFirstName.getText().length() == 0) {
            editTextFirstName.setError("First name required");
            focusView = editTextFirstName;
        }

        if (focusView != null) {
            focusView.requestFocus();
            return;
        }

        uploadSpinner.setVisibility(View.GONE);
        if (getMyImageUri() != null) {
            uploadSpinner.setVisibility(View.VISIBLE);
            PhotoManager pm = new PhotoManager(this);
            pm.upload(getMyImageUri());
        } else {
            onFinalSave(null);
        }
    }

    /**
     * Called once photo has been uploaded to cloudinary.
     *
     * @param imageId Id of uploaded image
     */
    public void onFinalSave(String imageId) {
        if (imageId == null) {
            Person person = PersonContract.getProfile(this);
            if (person != null) {
                imageId = person.getPicture();
            }
        }
        uploadSpinner.setVisibility(View.GONE);

        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String email = editTextEmail.getText().toString();
        String phone = editTextPhone.getText().toString();
        String facebook = "";
        String twitter = "";

        Profile fbProfile = Profile.getCurrentProfile();

        if (fbProfile != null)
            facebook = fbProfile.getId();

        TwitterSession session = Twitter.getInstance().core.getSessionManager().getActiveSession();
        if (session != null)
            twitter = session.getUserName();

        Person person = new Person(null, firstName, lastName, phone, email, "", imageId, facebook, twitter);
        DBHandler.saveProfile(person, this);
        // at this point `person` should have an ID, as well as populated fields
    }

    private Boolean isEmailValid(String email) {
        return !email.isEmpty() && email.contains("@");
    }
}
