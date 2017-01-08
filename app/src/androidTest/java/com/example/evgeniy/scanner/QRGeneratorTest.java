package com.example.evgeniy.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class QRGeneratorTest {
    @Test
    public void generatesCorrectQRCode() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        int personId = 99999;
        String filename = "testQR.png";
        Person person = new PersonBuilder().id(personId).buildPerson();

        // Tested action
        QRGenerator.generateAndSave(person, filename, appContext);

        // loading and decoding the produced QR code
        File sd = appContext.getFilesDir();
        File file = new File(sd, filename);
        FileInputStream in = new FileInputStream(file);
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();
        Result result = reader.decode(bBitmap);

        // Person ID should be preserved
        JSONObject jsonObject = new JSONObject(result.getText());
        int decodedId = jsonObject.getInt("id");
        assertEquals(personId, decodedId);
    }
}
