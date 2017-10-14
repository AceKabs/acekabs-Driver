package com.acekabs.driverapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.activity.MainHomeScreen;
import com.acekabs.driverapp.base.BaseFragment;
import com.acekabs.driverapp.pojo.EarningData;

import java.util.ArrayList;

/**
 * Created by Vikas on 29-06-2017.
 */
public class DriverEarningDetailFragment extends BaseFragment {
    private Bundle bundle;
    private EarningData earningData;
    private ArrayList<Float> chartData;
    private ArrayList<String> axisData;
    private String earningDate;
    private TextView dateTxtView, totalEarningTxtView, backButton, durationTxtView, tripsCompletedTxtView;
    private ListView earningsListView;
    private DriverEarningAdapter driverEarningAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driverearnings_details, container, false);
        bundle = this.getArguments();

        dateTxtView = (TextView) view.findViewById(R.id.dateTxtView);
        totalEarningTxtView = (TextView) view.findViewById(R.id.totalEarningTxtView);
        backButton = (TextView) view.findViewById(R.id.backButton);
        durationTxtView = (TextView) view.findViewById(R.id.durationTxtView);
        tripsCompletedTxtView = (TextView) view.findViewById(R.id.tripsCompletedTxtView);
        earningsListView = (ListView) view.findViewById(R.id.earningsListView);

        durationTxtView.setText("Time Online \n");
        tripsCompletedTxtView.setText("Completed Trips \n");
        if (bundle != null) {
            earningData = bundle.getParcelable("earningData");
            chartData = (ArrayList<Float>) bundle.getSerializable("chartData");
            axisData = (ArrayList<String>) bundle.getSerializable("axisData");
            earningDate = bundle.getString("earningDate");
            dateTxtView.setText(earningDate);
            totalEarningTxtView.setText(earningData.getTotalWeekEarn());
            driverEarningAdapter = new DriverEarningAdapter(chartData, axisData, getActivity());
            earningsListView.setAdapter(driverEarningAdapter);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainHomeScreen) getActivity()).onBackPressed();
            }
        });

        return view;
    }

    @Override
    protected boolean isBottomBarNeeded(boolean yes) {
        return false;
    }

    private class DriverEarningAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<Float> chartData;
        private ArrayList<String> axisData;

        public DriverEarningAdapter(ArrayList<Float> chartData, ArrayList<String> axisData, Activity activity) {
            this.chartData = chartData;
            this.axisData = axisData;
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return chartData.size();
        }

        @Override
        public Object getItem(int position) {
            return chartData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                // inflate the layout
                convertView = inflater.inflate(R.layout.row_earning, null);
                // well set up the ViewHolder
                viewHolder = new ViewHolder();
                viewHolder.dayTxtView = (TextView) convertView.findViewById(R.id.dayTxtView);
                viewHolder.earningTxtView = (TextView) convertView.findViewById(R.id.earningTxtView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.dayTxtView.setText(chartData.get(position) + "");
            viewHolder.earningTxtView.setText(axisData.get(position));
            return convertView;
        }

        class ViewHolder {
            TextView dayTxtView, earningTxtView;

        }
    }
}
