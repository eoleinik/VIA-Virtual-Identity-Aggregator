package com.example.evgeniy.scanner;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import static com.google.zxing.BarcodeFormat.QR_CODE;

public class ShowGeneratedBarcode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_generated_barcode);

        String input = "It is a period of civil war. Rebel spaceships, striking from a hidden base, " +
                "have won their first victory against the evil Galactic Empire. " +
                "During the battle, Rebel spies managed to steal secret plans to the Empire’s " +
                "ultimate weapon, the DEATH STAR, an armored space station with enough power to destroy an entire planet.";

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(input, QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < 300; i++) {//width
                for (int j = 0; j < 300; j++) {//height
                    bitmap.setPixel(i, j, result.get(i, j) ? Color.BLACK: Color.WHITE);
                }
            }

            ImageView tv1 = (ImageView) findViewById(R.id.imageView);
            tv1.setImageBitmap(bitmap);

        } catch(Exception e) {
            System.out.println("didn't manage to create the barcode");
        }


    }
}
