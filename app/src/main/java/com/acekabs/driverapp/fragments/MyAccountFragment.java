package com.acekabs.driverapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.base.BaseFragment;

/**
 * Created by Adee09 on 3/12/2017.
 */

public class MyAccountFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driverearnings, container, false);
        return view;
    }

    @Override
    protected boolean isBottomBarNeeded(boolean yes) {
        return yes;
    }
}
