package com.example.evgeniy.scanner;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

class PhotoManager {

    private final Cloudinary cloudinary;

    PhotoManager() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dsvdd2buq");
        config.put("api_key", "817523914298379");
        config.put("api_secret", "TiOAfSyIkAuW16GbSGtFmwq6wkA");
        this.cloudinary = new Cloudinary(config);
    }

    String upload(InputStream inputStream) {
        String url = "";
        try {
            Map uploadResult = this.cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap());
            url = (String)uploadResult.get("public_id");
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Unable to upload photo");
        }
        return url;
    }

}
