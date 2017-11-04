package com.tesis.yudith.showmethepast.tools;

import android.graphics.Bitmap;
import android.util.SizeF;

import com.tesis.yudith.showmethepast.domain.collections.childs.ImageSizeInformation;

public class ZoomTools {

    public static double getMinimum(double a, double b) {
        if (a < b){
            return a;
        }
        return b;
    }
    public static SizeF getImageSize(Bitmap bmp, ImageSizeInformation zoom) {
        int imageW = bmp.getWidth();
        int imageH = bmp.getHeight();
        double reference = getMinimum(zoom.getScreenWidth(), zoom.getScreenHeight());
        double relation = (float)imageH / imageW;

        double finalW = reference * zoom.getAlterationWidth() * zoom.getScale();
        double finalH = reference * relation * zoom.getAlterationHeight() * zoom.getScale();

        return new SizeF((float)finalW, (float)finalH);
    }
}
