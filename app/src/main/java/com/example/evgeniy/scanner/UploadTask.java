package com.example.evgeniy.scanner;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.InputStream;
import java.util.Map;

class UploadTask extends AsyncTask<Uri, Void, String> {

    private Cloudinary cloudinary;
    private Context context;
    private Exception exception;

    void setCloudinary(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected String doInBackground(Uri... inputUri) {
        String image_id = null;
        try {
            Uri imageUri = inputUri[0];
            Map uploadParams = ObjectUtils.asMap(
                    "format", "png"
            );
            InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
            Map uploadResult = this.cloudinary.uploader().upload(imageStream, uploadParams);
            String public_id = (String)uploadResult.get("public_id");
            String format = (String)uploadResult.get("format");
            image_id = public_id+"."+format;
            new PhotoManager(context).saveLocally(imageUri, image_id);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Unable to upload photo");
        }
        return image_id;
    }

    protected void onPostExecute(String image_id) {
        ProfileEditActivity act = (ProfileEditActivity)this.context;
        act.onFinalSave(image_id);
    }
}