package com.example.evgeniy.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.Cloudinary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

class PhotoManager {

    private final Context context;
    private final Cloudinary cloudinary;

    PhotoManager(Context context) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dsvdd2buq");
        config.put("api_key", "817523914298379");
        config.put("api_secret", "TiOAfSyIkAuW16GbSGtFmwq6wkA");
        this.cloudinary = new Cloudinary(config);
        this.context = context;
    }

    void upload(Uri inputUri) {
        UploadTask task = new UploadTask();
        task.setCloudinary(this.cloudinary);
        task.setContext(this.context);
        try {
            task.execute(inputUri);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    void downloadAndSaveLocally(final Person person) {
        RequestQueue queue = Volley.newRequestQueue(context);

        ImageRequest imgRequest =
                new ImageRequest(cloudinary.url().generate(person.getPicture()), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        FileOutputStream out = null;
                        try {
                            File sd = context.getFilesDir();
                            File file = new File(sd, person.getPicture());
                            out = new FileOutputStream(file);
                            response.compress(Bitmap.CompressFormat.PNG, 100, out);
                            DBHandler.contactPhotoDownloaded(context, person);
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
                                Toast.makeText(context, "Unable to fetch image.", Toast.LENGTH_LONG).show();
                            }
                        });


        queue.add(imgRequest);
    }

    void saveLocally(Uri inputUri, String filename) throws Exception {
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
