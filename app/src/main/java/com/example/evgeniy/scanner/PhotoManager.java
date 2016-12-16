package com.example.evgeniy.scanner;

import android.content.Context;


import com.cloudinary.Cloudinary;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PhotoManager {

    private Cloudinary cloudinary;
    private Context context;

    public PhotoManager(Context context) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dsvdd2buq");
        config.put("api_key", "817523914298379");
        config.put("api_secret", "TiOAfSyIkAuW16GbSGtFmwq6wkA");
        this.cloudinary = new Cloudinary(config);
        this.context = context;
    }

    public void upload(InputStream inputStream)  {
        UploadTask task = new UploadTask();
        task.setCloudinary(this.cloudinary);
        task.setContext(this.context);
        try {
            task.execute(inputStream);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
