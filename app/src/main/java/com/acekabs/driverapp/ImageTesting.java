package com.acekabs.driverapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by srinivas on 13/1/17.
 */

public class ImageTesting extends Activity {

    ImageView imageView;

    @Override
    public void onCreate(Bundle save) {
        super.onCreate(save);
        setContentView(R.layout.testing_lay2);
        imageView = (ImageView) findViewById(R.id.image_courses);

    }
}
