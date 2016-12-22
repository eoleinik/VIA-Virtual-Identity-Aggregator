package com.example.evgeniy.scanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileEditActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_FROM_GALLERY = 1;
    private Uri myImageUri = null;

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
        setContentView(R.layout.activity_profile_edit);

        Person person = PersonContract.getProfile(this);

        if (person != null) {
            ((TextView) findViewById(R.id.editTextFirstName)).setText(person.getFirstName());
            ((TextView) findViewById(R.id.editTextLastName)).setText(person.getLastName());
            ((TextView) findViewById(R.id.editTextEmail)).setText(person.getEmail());
            ((TextView) findViewById(R.id.editTextPhone)).setText(person.getPhone());

            findViewById(R.id.uploadSpinner).setVisibility(View.GONE);

            String imageId = person.getPicture();
            if (imageId != null) {
                try {
                    File sd = getFilesDir();
                    File myImage = new File(sd, imageId);
                    FileInputStream in = new FileInputStream(myImage);
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    ImageView imagePreview = (ImageView)findViewById(R.id.imagePreview);
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

                    ImageView imagePreview = (ImageView) findViewById(R.id.imagePreview);
                    imagePreview.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onSaveClick(View view) {
        ProgressBar spinner = (ProgressBar)findViewById(R.id.uploadSpinner);
        spinner.setVisibility(View.GONE);
        if (getMyImageUri() != null) {
            spinner.setVisibility(View.VISIBLE);
            PhotoManager pm = new PhotoManager(this);
            pm.upload(getMyImageUri());
        } else {
            onFinalSave(null);
        }
    }

    public void onFinalSave(String imageId) {
        if (imageId == null) {
            Person person = PersonContract.getProfile(this);
            if (person != null) {
                imageId = person.getPicture();
            }
        }
        ProgressBar spinner = (ProgressBar)findViewById(R.id.uploadSpinner);
        spinner.setVisibility(View.GONE);

        String firstName = ((TextView) findViewById(R.id.editTextFirstName)).getText().toString();
        String lastName = ((TextView) findViewById(R.id.editTextLastName)).getText().toString();
        String email = ((TextView) findViewById(R.id.editTextEmail)).getText().toString();
        String phone = ((TextView) findViewById(R.id.editTextPhone)).getText().toString();

        Person person = new Person(firstName, lastName, phone, email, "", imageId);
        DBHandler.saveProfile(person, this);
        // at this point `person` should have an ID, as well as populated fields
    }
}
