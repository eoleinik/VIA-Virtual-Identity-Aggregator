package com.example.evgeniy.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;


import com.cloudinary.Cloudinary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public void upload(Uri inputUri)  {
        UploadTask task = new UploadTask();
        task.setCloudinary(this.cloudinary);
        task.setContext(this.context);
        try {
            task.execute(inputUri);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void saveLocally(Uri inputUri, String filename) throws Exception {
        // Suitable only for PNG's
        FileOutputStream out = null;
        Exception e_copy = null;
        try {
            File sd = context.getFilesDir();
            File file = new File(sd, filename);
            out = new FileOutputStream(file);
            InputStream imageStream = context.getContentResolver().openInputStream(inputUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e_copy = e;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (e_copy != null) {
            throw e_copy;
        }
    }

}
