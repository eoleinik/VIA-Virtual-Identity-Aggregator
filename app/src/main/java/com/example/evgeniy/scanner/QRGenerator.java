package com.example.evgeniy.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.zxing.BarcodeFormat.QR_CODE;

public class QRGenerator {

    public static void generateAndSave(Person person, String filename, Context context) {

        Map<String, String> dataToEncode = new HashMap<String, String>();
        dataToEncode.put("id", ""+person.getId());
        dataToEncode.put("firstName", person.getFirstName());
        dataToEncode.put("lastName", person.getLastName());
        dataToEncode.put("email", person.getEmail());
        dataToEncode.put("phone", person.getPhone());
        JSONObject jsonData = new JSONObject(dataToEncode);

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix result = writer.encode(jsonData.toString(), QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < 300; i++) {//width
                for (int j = 0; j < 300; j++) {//height
                    bitmap.setPixel(i, j, result.get(i, j) ? Color.BLACK: Color.WHITE);
                }
            }

            FileOutputStream out = null;

            try {
                File sd = context.getFilesDir();
                File file = new File(sd, filename);
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                // PNG is a lossless format, the compression factor (100) is ignored
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

        } catch(Exception e) {
            System.out.println("Didn't manage to create the barcode");
        }
    }

}
