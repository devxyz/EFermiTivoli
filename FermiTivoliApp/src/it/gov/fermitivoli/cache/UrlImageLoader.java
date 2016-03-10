package it.gov.fermitivoli.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.model.C_NormalizedURL;
import it.gov.fermitivoli.util.ScreenUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class UrlImageLoader {

    //tempo di mantenimento dei dati in cache
    private static final int EXPIRE_TIME_DAYS = 15;
    final int loading_image_id;
    private final int default_width;
    private final int default_height;
    private final BitmapMemoryCache memoryCache = new BitmapMemoryCache();
    private final UrlFileCache cache;
    private final Handler handler = new Handler();//handler to display images in UI thread
    private final Context context;
    private final Map<ImageView, C_NormalizedURL> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, C_NormalizedURL>());
    private AbstractFragment fragment;

    public UrlImageLoader(AbstractFragment e, int waitingImageId) {
        this(e, -1, -1, waitingImageId);
    }

    public UrlImageLoader(AbstractFragment fragment, final int width, final int height, int waitingImageId) {
        this.fragment = fragment;
        this.context = fragment.getActivity();

        this.default_width = width;
        this.default_height = height;

        this.loading_image_id = waitingImageId;
        this.cache = fragment.getMainActivity().getCache();

    }

    public void displayImage(C_NormalizedURL url, ImageView imageView) {
        displayImage(url, imageView, default_width, default_height);
    }

    public void displayImage(C_NormalizedURL url, ImageView imageView, final int width, final int height) {


        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
            if (width > 0 && height > 0)
                imageView.setImageBitmap(ScreenUtil.scaleAndAdapt(bitmap, width, height));
            else
                imageView.setImageBitmap(bitmap);
        else {
            queuePhoto(url, imageView, width, height);
//            Bitmap b = ScreenUtil.getResourceAsBitmap(context, loading_image_id);
            //imageView.setImageBitmap(ScreenUtil.scaleAndAdapt(b, width, height));
            //          imageView.setImageBitmap(b);
        }
    }

    private void queuePhoto(final C_NormalizedURL url, final ImageView imageView, final int width, final int height) {
        final AsyncUrlLoaderCallback cc = cache.downloadFileAsync(url, EXPIRE_TIME_DAYS, TimeUnit.DAYS, new AsyncUrlLoaderCallback() {
            @Override
            public void onLoadFinished(C_NormalizedURL url, File f) {

                Bitmap bitmap = decodeFile(f);
                memoryCache.put(url, bitmap);

                BitmapDisplayer d = new BitmapDisplayer(bitmap, new PhotoToLoad(url, imageView), width, height);
                handler.post(d);
            }

            @Override
            public void onLoadError(C_NormalizedURL url, Throwable error) {
                imageView.setImageResource(R.drawable.cancel_red);
            }

            @Override
            public void onQueueForDownload(C_NormalizedURL url) {
                Bitmap b = ScreenUtil.getResourceAsBitmap(context, loading_image_id);
                imageView.setImageBitmap(b);
            }

            @Override
            public void onCancelled(C_NormalizedURL url) {

            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        });

        fragment.addTask(cc);

    }


    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            int scale = 1;

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        C_NormalizedURL tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }


    //Task for the queue
    private class PhotoToLoad {
        public C_NormalizedURL url;
        public ImageView imageView;

        public PhotoToLoad(C_NormalizedURL u, ImageView i) {
            url = u;
            imageView = i;
        }
    }


    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        private int width;
        private int height;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p, int width, int height) {
            bitmap = b;
            photoToLoad = p;
            this.width = width;
            this.height = height;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(ScreenUtil.scaleAndAdapt(bitmap, width, height));
            else {
                /*Bitmap b = ScreenUtil.getResourceAsBitmap(context, loading_image_id);
                photoToLoad.imageView.setImageBitmap(ScreenUtil.scaleAndAdapt(b, width, height));
*/
                photoToLoad.imageView.setImageResource(loading_image_id);
            }
        }
    }

}
