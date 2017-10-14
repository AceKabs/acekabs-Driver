package com.acekabs.driverapp.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.fragments.BookingHistoryFragment;
import com.acekabs.driverapp.fragments.DriverEarningsFragment;
import com.acekabs.driverapp.fragments.DriverMapFragment;
import com.acekabs.driverapp.fragments.MyAccountFragment;


public abstract class BaseActivity extends AppCompatActivity implements NavigationCallBack, View.OnClickListener {

    public RelativeLayout bottomBar;
    private LinearLayout homelayout;
    private LinearLayout earninglayout;
    private LinearLayout historylayout;
    private LinearLayout accountlayout;
    private ImageView main_screen_home;
    private ImageView main_screen_earnning;
    private ImageView main_screen_history;
    private ImageView main_screen_account;
    private TextView main_hometv;
    private TextView main_earningtv;
    private TextView main_historytv;
    private TextView main_accounttv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        bottomBar = (RelativeLayout) findViewById(R.id.bottomBar);
        homelayout = (LinearLayout) findViewById(R.id.homelayout);
        earninglayout = (LinearLayout) findViewById(R.id.earninglayout);
        historylayout = (LinearLayout) findViewById(R.id.historylayout);
        accountlayout = (LinearLayout) findViewById(R.id.accountlayout);
        main_screen_home = (ImageView) findViewById(R.id.main_screen_home);
        main_screen_earnning = (ImageView) findViewById(R.id.main_screen_earnning);
        main_screen_history = (ImageView) findViewById(R.id.main_screen_history);
        main_screen_account = (ImageView) findViewById(R.id.main_screen_account);
        main_hometv = (TextView) findViewById(R.id.main_hometv);
        main_earningtv = (TextView) findViewById(R.id.main_earningtv);
        main_historytv = (TextView) findViewById(R.id.main_historytv);
        main_accounttv = (TextView) findViewById(R.id.main_accounttv);
        homelayout.setOnClickListener(this);
        earninglayout.setOnClickListener(this);
        historylayout.setOnClickListener(this);
        accountlayout.setOnClickListener(this);
    }

    public void setNavigationFragment(Fragment fragment, boolean isBackStacked, String tag, boolean onResume) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (onResume) {
            transaction.replace(R.id.container, fragment, tag);
        } else {
            transaction.add(R.id.container, fragment, tag);
        }
        if (isBackStacked) {
            transaction.addToBackStack(tag);
        }
        // Commit the transaction
        transaction.commitAllowingStateLoss();
        fragmentSetup(fragment);
    }

    private void fragmentSetup(Fragment fragment) {
        if (((BaseFragment) fragment).isBottomBarNeeded(true)) {
            bottomBar.setVisibility(View.VISIBLE);
        } else {
            bottomBar.setVisibility(View.GONE);
        }
    }

    public void showBottomBar() {
        bottomBar.setVisibility(View.VISIBLE);

    }


    @Override
    public void onClick(View v) {
        if (v == homelayout) {
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            main_screen_home.setImageResource(R.drawable.home_dark);
            main_hometv.setTextColor(Color.BLACK);
            main_screen_earnning.setImageResource(R.drawable.earning_white);
            main_earningtv.setTextColor(Color.WHITE);
            main_screen_history.setImageResource(R.drawable.history_white);
            main_historytv.setTextColor(Color.WHITE);
            main_screen_account.setImageResource(R.drawable.account_white);
            main_accounttv.setTextColor(Color.WHITE);
            setNavigationFragment(new DriverMapFragment(), true, "DriverMapFragment", false);
        } else if (v == earninglayout) {
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            main_screen_home.setImageResource(R.drawable.home_white);
            main_hometv.setTextColor(Color.WHITE);
            main_screen_earnning.setImageResource(R.drawable.earning_black);
            main_earningtv.setTextColor(Color.BLACK);
            main_screen_history.setImageResource(R.drawable.history_white);
            main_historytv.setTextColor(Color.WHITE);
            main_screen_account.setImageResource(R.drawable.account_white);
            main_accounttv.setTextColor(Color.WHITE);
            setNavigationFragment(new DriverEarningsFragment(), true, "DriverEarningsFragment", false);
        } else if (v == historylayout) {
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            main_screen_home.setImageResource(R.drawable.home_white);
            main_hometv.setTextColor(Color.WHITE);
            main_screen_earnning.setImageResource(R.drawable.earning_white);
            main_earningtv.setTextColor(Color.WHITE);
            main_screen_history.setImageResource(R.drawable.history_black);
            main_historytv.setTextColor(Color.BLACK);
            main_screen_account.setImageResource(R.drawable.account_white);
            main_accounttv.setTextColor(Color.WHITE);
            setNavigationFragment(new BookingHistoryFragment(), true, "BookingHistoryFragment", false);
        } else if (v == accountlayout) {
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            main_screen_home.setImageResource(R.drawable.home_white);
            main_hometv.setTextColor(Color.WHITE);
            main_screen_earnning.setImageResource(R.drawable.earning_white);
            main_earningtv.setTextColor(Color.WHITE);
            main_screen_history.setImageResource(R.drawable.history_white);
            main_historytv.setTextColor(Color.WHITE);
            main_screen_account.setImageResource(R.drawable.account_black);
            main_accounttv.setTextColor(Color.BLACK);
            setNavigationFragment(new MyAccountFragment(), true, "MyAccountFragment", false);
        }
    }
}
