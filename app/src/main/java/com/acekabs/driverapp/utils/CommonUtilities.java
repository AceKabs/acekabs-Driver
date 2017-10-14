package com.acekabs.driverapp.utils;

/**
 * Created by Adee09 on 2/25/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.acekabs.driverapp.pojo.FcmRequestObject;

public final class CommonUtilities {
    public static final String DISPLAY_MESSAGE_ACTION = "com.acekabs.driverapp.DISPLAY_MESSAGE";
    public static final String EXTRA_MESSAGE = "data";

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, FcmRequestObject message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_MESSAGE, message);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }
}
