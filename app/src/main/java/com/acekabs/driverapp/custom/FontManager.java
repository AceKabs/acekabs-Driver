package com.acekabs.driverapp.custom;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Adee09 on 5/7/2017.
 */

public class FontManager {
    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}
