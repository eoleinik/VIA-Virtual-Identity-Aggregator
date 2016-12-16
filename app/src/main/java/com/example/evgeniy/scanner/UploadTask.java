package com.example.evgeniy.scanner;

import android.content.Context;
import android.os.AsyncTask;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.InputStream;
import java.util.Map;

class UploadTask extends AsyncTask<InputStream, Void, String> {

    private Cloudinary cloudinary;
    private Context context;

    public void setCloudinary(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Exception exception;

    protected String doInBackground(InputStream... inputStream) {
        String image_id = null;
        try {
            Map<String, Object> uploadResult = this.cloudinary.uploader().upload(inputStream[0], ObjectUtils.emptyMap());
            String public_id = (String)uploadResult.get("public_id");
            String format = (String)uploadResult.get("format");
            image_id = public_id+"."+format;
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Unable to upload photo");
        }
        return image_id;
    }

    protected void onPostExecute(String image_id) {
        MainActivity act = (MainActivity)this.context;
        act.onFinalSave(image_id);
    }
}