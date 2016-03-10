package it.gov.fermitivoli.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.ViewGroup;

/**
 * Created by stefano on 04/03/15.
 */
public class ScreenUtil {

    public static Point getSize(Activity c) {

        Display display = c.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static void fullScreen(Dialog d) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        d.getWindow().setLayout(width, height);

    }

    public static void fullWidthScreen(Dialog d) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        d.getWindow().setLayout(width, height);

    }

    public static Bitmap getResourceAsBitmap(Context context, int id) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), id);
        return icon;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap scaleAndAdapt(Bitmap bitmap, int width, int height) {

        if (width > 0 && height > 0 && bitmap != null) {
            double scaleX = width / (bitmap.getWidth() * 1.);
            double scaleY = height / (bitmap.getHeight() * 1.);
            double scaleXY = Math.min(scaleX, scaleY);
            int newWidth = (int) (bitmap.getWidth() * scaleXY);
            int newHeight = (int) (bitmap.getHeight() * scaleXY);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            // bitmap.recycle();
            if (DebugUtil.DEBUG__ScreenUtil)
                System.out.println(" RESIZE preferred size = " + width + "," + height +
                        " - original size = " + bitmap.getWidth() + "," + bitmap.getHeight() +
                        " - scale " + scaleXY + " new size:" + newWidth + "," + newHeight);

            return resized;
        } else
            return bitmap;
    }

    public static Bitmap scaleExactly(Bitmap bitmap, int width, int height) {

        if (width > 0 && height > 0 && bitmap != null) {
            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        } else
            return bitmap;
    }
}
