package com.acekabs.driverapp.firebase;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.acekabs.driverapp.base.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import net.danlew.android.joda.JodaTimeAndroid;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Links the app with firebase.
 */
@ReportsCrashes(mailTo = "team@acekabs.com")
public class FirebaseApplicationContext extends MultiDexApplication {
    public static final String TAG = FirebaseApplicationContext.class.getSimpleName();
    public static VolleySingleton volleyQueueInstance;
    private static FirebaseApplicationContext mInstance;
    private RequestQueue mRequestQueue;

    public static synchronized FirebaseApplicationContext getInstance() {
        return mInstance;
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 100 MiB
        config.tasksProcessingOrder(QueueProcessingType.FIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        MultiDex.install(this);
        Firebase.setAndroidContext(this);
        JodaTimeAndroid.init(this);
        ACRA.init(this);
        instantiateVolleyQueue();
        initImageLoader(getApplicationContext());
    }

    public void instantiateVolleyQueue() {
        volleyQueueInstance = VolleySingleton.getInstance(getApplicationContext());
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());

        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
//        e.printStackTrace(); // not all Android versions will print the stack trace automatically
//
//        String thread_stack_trace = thread.getStackTrace().toString();
//
//        String thrw = e.getMessage();
//
//        try {
//            Log.e("thread_stack_trace", thread_stack_trace);
//            Log.e("thrw", thrw + " throwable " + e.getLocalizedMessage());
//
//        } catch (Exception ee) {
//            ee.printStackTrace();
//        }
//
////        Intent intent = new Intent(getBaseContext(), DashboardActivityNew.class);
////        //     intent.setAction ("com.mydomain.SEND_LOG"); // see step 5.
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
////        startActivity(intent);
//
//        System.exit(1); // kill off the crashed app
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
