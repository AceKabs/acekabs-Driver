package com.acekabs.driverapp.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.acekabs.driverapp.fragments.DriverMapFragment;


public abstract class BaseFragment extends Fragment {

    protected NavigationCallBack navigationCallback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navigationCallback = (NavigationCallBack) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Intent mStartActivity = new Intent(getActivity(), DriverMapFragment.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, 0, mPendingIntent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        setRetainInstance(true);

//        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(getActivity()));

    }


    protected abstract boolean isBottomBarNeeded(boolean yes);

    /**
     * This method will load request fragment. if isBackedStacked is true then push that framgnet
     * to back stack.
     *
     * @param fragment
     * @param isBackStacked
     */
    public void navigateFragment(Fragment fragment, boolean isBackStacked, String Tag, boolean onResume) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        navigationCallback.setNavigationFragment(fragment, isBackStacked, Tag, onResume);
    }


}
