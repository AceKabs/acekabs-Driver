package com.acekabs.driverapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by frenzin05 on 10/4/2017.
 */

public class ImageCompressAsyncTask extends AsyncTask<String,String,String> {
    private Context mContext;
    private String imageUri;
    private ExifInterface exifInterface;
    private String filenames;
    private OnImageCompressed mOnImageCompressed;
    private ImageLoadingUtils utils;
    public interface OnImageCompressed {
        void onCompressedImage(String path);
    }

    public ImageCompressAsyncTask(Context context, String imageUri, ExifInterface exifInterface, String filenames,ImageLoadingUtils utils) {
        this.mContext=context;
        this.imageUri=imageUri;
        this.exifInterface=exifInterface;
        this.filenames=filenames;
        this.utils=utils;
    }
    public void setOnImageCompressed(OnImageCompressed listener)
    {
        this.mOnImageCompressed = listener;
    }

    @Override
    protected String doInBackground(String... params) {

        return compressImage(imageUri,exifInterface,filenames);
    }

    public String compressImage(String imageUri, ExifInterface exifInterface, String filenames) {
        String filePath = imageUri;
        Bitmap scaledBitmap = null;
        Log.d("My Tag", "compressImage: ");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 4;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2,
                new Paint(Paint.FILTER_BITMAP_FLAG));
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        } else if (orientation == 3) {
            matrix.postRotate(180);
        } else if (orientation == 8) {
            matrix.postRotate(270);
        }
        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), matrix, true);
        FileOutputStream out;
        try {
            out = new FileOutputStream(filenames);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(new File(filenames)).toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mOnImageCompressed != null)
        {
            mOnImageCompressed.onCompressedImage(s);
        }
    }
}
