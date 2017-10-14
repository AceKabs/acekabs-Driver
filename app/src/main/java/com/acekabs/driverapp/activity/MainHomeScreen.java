package com.acekabs.driverapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.WindowManager;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.base.BaseActivity;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.fragments.DriverMapFragment;
import com.acekabs.driverapp.pojo.FcmRequestObject;
import com.acekabs.driverapp.utils.CommonUtilities;
import com.google.gson.Gson;

/**
 * Created by srinivas on 4/1/17.
 */
public class MainHomeScreen extends BaseActivity {
    private static final String TAG = "MainHomeScreen";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationFragment(new DriverMapFragment(), true, "DriverMapFragment", false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume()");
        if(Constants.isPushFromNotification)
        {
            if(getIntent().hasExtra("payload_data"))
            {
                String data=getIntent().getStringExtra("payload_data");
                Log.e(TAG,"Payload_data=>"+data);

                Gson gson = new Gson();
                FcmRequestObject fcmRequestObject = gson.fromJson(data, FcmRequestObject.class);
                CommonUtilities.displayMessage(this.getApplicationContext(), fcmRequestObject);
            }
            Constants.isPushFromNotification=false;
        }

    }

    @Override
    public void onBackPressed() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainHomeScreen.this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.close_application))
                    .setNegativeButton(getString(R.string.cancelText), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setCancelable(false);

            builder.create();
            builder.show();


        }

    }
}
