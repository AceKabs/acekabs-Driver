package com.acekabs.driverapp.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.acekabs.driverapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Adee09 on 1/26/2017.
 */

public class UtilityMethods {
    public static final double PI = 3.14159265;
    public static final double deg2radians = PI / 180.0;
    private static DisplayImageOptions options;

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    /**
     * Converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static String getManufacturer() {
        String manufacturer = String.valueOf(Build.VERSION.SDK_INT);
        return manufacturer;
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String getIMEI(Activity activity) {
        String imei = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            TelephonyManager telephonyManager = (TelephonyManager) activity
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) activity
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getPhoneCount() == 2) {
                imei = telephonyManager.getDeviceId(0);
                if (!TextUtils.isEmpty(imei)) {
                    imei += "-" + telephonyManager.getDeviceId(1);
                }
            } else {
                imei = telephonyManager.getDeviceId();
            }
        }
        return imei;

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static String convertToDegreeMinuteSeconds(double latitude) {
        latitude = Math.abs(latitude);
        int degree = (int) latitude;
        latitude *= 60;
        latitude -= (degree * 60.0d);
        int minute = (int) latitude;
        latitude *= 60;
        latitude -= (minute * 60.0d);
        int second = (int) (latitude * 1000.0d);

        StringBuilder sb = new StringBuilder();
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000,");
        return sb.toString();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(cs.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;

        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        //if (unit == "K") {
        //  dist = dist * 1.609344;
        // else if (unit == "N") {
        //dist = dist * 0.8684;
        //}
        return (dist);
    }


    public static double getDistance(double latitude1, double longitude1, double latitude2, double longitude2) {

        double lat1 = latitude1 * deg2radians;
        double lat2 = latitude2 * deg2radians;
        double lon1 = longitude1 * deg2radians;
        double lon2 = longitude2 * deg2radians;
        // Williams gives two formulae;
        // this is the more accurate for close distances.
        // In practice, the two differed only in the 8th or 9th place, for
        // separations as small as 1 degree.
        double radd = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2) / 2),
                2.0)
                + Math.cos(lat1)
                * Math.cos(lat2)
                * Math.pow(Math.sin((lon1 - lon2) / 2), 2.0)));

        return radd;
    }


    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    /* :: This function converts decimal degrees to radians : */
    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    /* :: This function converts radians to decimal degrees : */
    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static Uri getUrl(int res) {
        return Uri.parse("android.resource://com.acekabs.driverapp/" + res);
    }

    public static void displayImage(String imagePath, ImageView MyImageView) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.camera_image)
                .showImageForEmptyUri(R.drawable.camera_image)
                .showImageOnFail(R.drawable.camera_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance()
                .displayImage(imagePath.trim().toString(), MyImageView, options, new SimpleImageLoadingListener() {
                    boolean cacheFound;

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        List<String> memCache = MemoryCacheUtils.findCacheKeysForImageUri(imageUri, ImageLoader.getInstance().getMemoryCache());
                        cacheFound = !memCache.isEmpty();
                        if (!cacheFound) {
                            File discCache = ImageLoader.getInstance().getDiscCache().get(imageUri);
                            if (discCache != null) {
                                cacheFound = discCache.exists();
                            }
                        }
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (cacheFound) {
                            MemoryCacheUtils.removeFromCache(imageUri, ImageLoader.getInstance().getMemoryCache());
                            ImageLoader.getInstance().getDiskCache().remove(imageUri);
                            ImageLoader.getInstance().displayImage(imageUri, (ImageView) view);
                        }
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                    }
                });
    }

    public static double calculateDistance(LatLng source, LatLng dest) {
        double lat2 = source.latitude;
        double lon2 = source.longitude;
        double lat1 = dest.latitude;
        double lon1 = dest.longitude;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2radain(lat1)) * Math.sin(deg2radain(lat2)) + Math.cos(deg2radain(lat1)) * Math.cos(deg2radain(lat2)) * Math.cos(deg2radain(theta));
        dist = Math.acos(dist);
        dist = rad2degree(dist);
        dist = dist * 60 * 1.1515;
        return (dist * 1.609344);
    }

    //This function converts decimal degrees to radians
    private static double deg2radain(double deg) {
        return (deg * Math.PI / 180.0);
    }

    //This function converts radians to decimal degrees
    private static double rad2degree(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    static double round(float value) {
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
