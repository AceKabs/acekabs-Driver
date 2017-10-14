package com.acekabs.driverapp.base;

import android.support.v4.app.Fragment;


public interface NavigationCallBack {

    void setNavigationFragment(Fragment fragment, boolean isBackStacked, String tag, boolean onResume);

}
