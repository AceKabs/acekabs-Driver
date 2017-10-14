package com.acekabs.driverapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.R;
import com.acekabs.driverapp.base.BaseFragment;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.pojo.EarningData;
import com.acekabs.driverapp.restclient.RestApiService;
import com.acekabs.driverapp.utils.ApiUtils;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Adee09 on 3/12/2017.
 */

public class DriverEarningsFragment extends BaseFragment implements View.OnClickListener {

    private BarChart earningchart;
    private ProgressDialog dialog;
    private RestApiService mAPIService;
    private Context context;
    private String JsonString = "{\n" +
            " \"LastTripEarn\": 110,\n" +
            "  \"TotalWeekEarn\": 267,\n" +
            "  \"Monday\": 101.5,\n" +
            "  \"Thursday\": 0,\n" +
            "  \"Friday\": 0, \n" +
            "  \"Sunday\": 0,\n" +
            "  \"Wednesday\": 0,\n" +
            "  \"Tuesday\": 165.5,\n" +
            "  \"Saturday\": 0\n" +
            "}";
    private SharePreferanceWrapperSingleton preferanceWrapperSingleton;
    private ArrayList<Float> chartData;
    private ArrayList<String> axisData;
    private TextView tvLastTripEarningData;
    private TextView tvTotalTripEarningData;
    private TextView tvPrevious;
    private TextView tvNext;
    private TextView tvSelectedDate;
    private SimpleDateFormat sdf;
    private SimpleDateFormat simpleDateFormat;
    private Calendar calendar;
    private EarningData earningData;
    private LinearLayout earningLayout;
    @Override
    protected boolean isBottomBarNeeded(boolean yes) {
        return yes;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driverearnings, container, false);
        calendar = Calendar.getInstance();
        earningLayout = (LinearLayout) view.findViewById(R.id.earningLayout);
        earningchart = (BarChart) view.findViewById(R.id.earningchart);
        tvLastTripEarningData = (TextView) view.findViewById(R.id.tvLastTripEarningData);
        tvTotalTripEarningData = (TextView) view.findViewById(R.id.tvTotalTripEarningData);
        tvPrevious = (TextView) view.findViewById(R.id.tvPrevious);
        tvNext = (TextView) view.findViewById(R.id.tvNext);
        tvSelectedDate = (TextView) view.findViewById(R.id.tvSelectedDate);
        tvNext.setText(">");
        tvPrevious.setText("<");
        tvNext.setOnClickListener(this);
        tvPrevious.setOnClickListener(this);
        context = getActivity();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        sdf = new SimpleDateFormat("MMM dd-MM,yyyy", Locale.ENGLISH);
        tvSelectedDate.setText(Html.fromHtml("This Week <br/>" + sdf.format(new Date())));
        tvSelectedDate.setTag(simpleDateFormat.format(new Date()));
        chartData = new ArrayList<>();
        axisData = new ArrayList<>();
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait while getting details.");
        dialog.setCancelable(false);
        preferanceWrapperSingleton = SharePreferanceWrapperSingleton.getSingletonInstance();
        preferanceWrapperSingleton.setPref(context);
        preferanceWrapperSingleton.setEditor();
        if (isNetworkAvailable()) {
            getEarningData();
        } else {
            Toast.makeText(context, "Please connect with Internet!!", Toast.LENGTH_SHORT).show();
        }
        earningLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (earningData == null) {
                    return;
                }
                if (chartData.isEmpty()) {
                    return;
                }
                DriverEarningDetailFragment driverEarningDetailFragment = new DriverEarningDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("earningData", earningData);
                bundle.putSerializable("chartData", chartData);
                bundle.putSerializable("axisData", axisData);
                bundle.putString("earningDate", tvSelectedDate.getTag().toString());
                driverEarningDetailFragment.setArguments(bundle);
                navigateFragment(driverEarningDetailFragment, true, "DriverEarningDetailFragment", false);

            }
        });

        return view;
    }

    private void getEarningData() {
        dialog.show();
        mAPIService = ApiUtils.getAPIService(ApplicationConstants.RESTBASEURL);
        mAPIService.getEarningHistory(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.userId), tvSelectedDate.getTag().toString()).enqueue(new Callback<EarningData>() {

            @Override
            public void onResponse(Call<EarningData> call, Response<EarningData> response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                setupChartData(response.body());
            }

            @Override
            public void onFailure(Call<EarningData> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void setupChartData(EarningData data) {
        if (data != null) {
            earningData = data;
            if (!TextUtils.isEmpty(data.getTotalWeekEarn())) {
                chartData.clear();
                axisData.clear();
                chartData.add(Float.valueOf(data.getSunday()));
                chartData.add(Float.valueOf(data.getMonday()));
                chartData.add(Float.valueOf(data.getTuesday()));
                chartData.add(Float.valueOf(data.getWednesday()));
                chartData.add(Float.valueOf(data.getThursday()));
                chartData.add(Float.valueOf(data.getFriday()));
                chartData.add(Float.valueOf(data.getSaturday()));
                axisData.add("SU");
                axisData.add("MO");
                axisData.add("TU");
                axisData.add("WE");
                axisData.add("TH");
                axisData.add("FR");
                axisData.add("SA");
                tvTotalTripEarningData.setText(String.valueOf("$ " + data.getTotalWeekEarn()));
                tvLastTripEarningData.setText(String.valueOf("$ " + data.getLastTripEarn()));
            }
            BarData mData = new BarData(getXAxisValues(), getDataSet());
            earningchart.setData(mData);
//        earningchart.animateXY(2000, 2000);
//        earningchart.invalidate();
            earningchart.setDrawGridBackground(false);
//        earningchart.animateY(5000);
            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            earningchart.setMaxVisibleValueCount(7);
            // scaling can now only be done on x- and y-axis separately
            earningchart.setPinchZoom(false);
            earningchart.setDrawGridBackground(false);
            XAxis xAxis = earningchart.getXAxis();
            xAxis.setTextColor(Color.WHITE);
            xAxis.setTextSize(15.0f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            YAxis leftAxis = earningchart.getAxisLeft();
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setTextColor(Color.WHITE);
            earningchart.getAxisRight().setEnabled(false);
            YAxis rightAxis = earningchart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            earningchart.setDrawBarShadow(false);
            earningchart.setBorderColor(android.R.color.transparent);
            earningchart.setDescription("");
            earningchart.invalidate();
        } else {
            Toast.makeText(context, "Data not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        dataSets.clear();
        valueSet1.clear();
        BarEntry v1e1 = new BarEntry(chartData.get(0), 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(chartData.get(1), 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(chartData.get(2), 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(chartData.get(3), 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(chartData.get(4), 4); // May
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(chartData.get(5), 5); // Jun
        valueSet1.add(v1e6);
        BarEntry v1e7 = new BarEntry(chartData.get(6), 5); // Jun
        valueSet1.add(v1e7);
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "");
        barDataSet1.setColor(Color.parseColor("#00a3cc"));
        barDataSet1.setDrawValues(false);
        dataSets.add(barDataSet1);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.clear();
        xAxis.add("SU");
        xAxis.add("M");
        xAxis.add("TU");
        xAxis.add("W");
        xAxis.add("TH");
        xAxis.add("F");
        xAxis.add("SA");
        return xAxis;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        if (tvPrevious == v) {
            calendar.add(Calendar.DATE, -1);
            Date date = calendar.getTime();
            tvSelectedDate.setText(Html.fromHtml("This Week <br/>" + sdf.format(date)));
            tvSelectedDate.setTag(simpleDateFormat.format(date));
            if (isNetworkAvailable()) {
                getEarningData();
            } else {
                Toast.makeText(context, "Please connect with Internet!!", Toast.LENGTH_SHORT).show();
            }

        } else if (tvNext == v) {
            calendar.add(Calendar.DATE, 1);
            Date date = calendar.getTime();
            tvSelectedDate.setText(Html.fromHtml("This Week <br/>" + sdf.format(date)));
            tvSelectedDate.setTag(simpleDateFormat.format(date));
            if (isNetworkAvailable()) {
                getEarningData();
            } else {
                Toast.makeText(context, "Please connect with Internet!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
