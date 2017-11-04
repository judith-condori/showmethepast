package com.tesis.yudith.showmethepast.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class BitmapTools {

    public static Bitmap scaleBitmap(Bitmap source, int maxArea) {
        int w = source.getWidth();
        int h = source.getHeight();
        int area = w*h;
        int newW = w;
        int newH = h;
        double scale;
        if (area > maxArea) {
            scale = Math.sqrt((double)maxArea / (w * h));
            newW = (int)(w*scale);
            newH = (int)(h*scale);
        }
        return Bitmap.createScaledBitmap(source,newW, newH, false);
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeFromBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static Bitmap fromAssets(Context context, String path) {
        try {
            InputStream fileStream = context.getAssets().open(path);
            return BitmapFactory.decodeStream(fileStream);
        }
        catch (Exception err) {
            err.printStackTrace();
        }

        return null;
    }

    public static Bitmap scale(Bitmap bitmap, float scale) {
        int newWidth = (int)(bitmap.getWidth() * scale);
        int newHeight = (int)(bitmap.getHeight() * scale);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }
}
