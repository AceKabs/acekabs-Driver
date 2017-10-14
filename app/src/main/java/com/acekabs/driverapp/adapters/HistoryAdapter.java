package com.acekabs.driverapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.interfaces.BookingListCallBack;
import com.acekabs.driverapp.pojo.TripHistoryData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ankit on 25-06-2017.
 */

public class HistoryAdapter extends BaseAdapter {
    private TripHistoryData tripHistoryData;
    private Context mContext;
    private LayoutInflater inflater;
    private BookingListCallBack bookingListCallBack;

    public HistoryAdapter(TripHistoryData tripHistoryData, Context mContext, BookingListCallBack bookingListCallBack) {
        this.tripHistoryData = tripHistoryData;
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bookingListCallBack = bookingListCallBack;
    }

    @Override
    public int getCount() {
        return tripHistoryData.getHistory().size();
    }

    @Override
    public Object getItem(int i) {
        return tripHistoryData.getHistory().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            // inflate the layout
            convertView = inflater.inflate(R.layout.row_history, null);
            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.pickupLocationTxt = (TextView) convertView.findViewById(R.id.pickupLocationTxt);
            viewHolder.dropLocationTxt = (TextView) convertView.findViewById(R.id.dropLocationTxt);
            viewHolder.startDate = (TextView) convertView.findViewById(R.id.startDate);
            viewHolder.LocationTxt = (TextView) convertView.findViewById(R.id.LocationTxt);
            viewHolder.costTxtView = (TextView) convertView.findViewById(R.id.costTxtView);
            viewHolder.mainLayout = (RelativeLayout) convertView.findViewById(R.id.mainLayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.pickupLocationTxt.setText(TextUtils.isEmpty(tripHistoryData.getHistory().get(position).getPickuplocationname()) ?
                "" : tripHistoryData.getHistory().get(position).getPickuplocationname());
        viewHolder.dropLocationTxt.setText(TextUtils.isEmpty(tripHistoryData.getHistory().get(position).getDroplocationname()) ?
                "" : tripHistoryData.getHistory().get(position).getDroplocationname());
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(tripHistoryData.getHistory().get(position).getStarttime());
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM 'at' HH.mm a");
            Date currenTimeZone = (Date) calendar.getTime();
            sdf.format(currenTimeZone);
            viewHolder.startDate.setText(sdf.format(currenTimeZone));
        } catch (Exception e) {
        }
        viewHolder.LocationTxt.setText(TextUtils.isEmpty(tripHistoryData.getHistory().get(position).getPickuplocationname()) ?
                "" : tripHistoryData.getHistory().get(position).getPickuplocationname());
        viewHolder.costTxtView.setText(TextUtils.isEmpty(tripHistoryData.getHistory().get(position).getTotalfare()) ?
                "" : tripHistoryData.getHistory().get(position).getTotalfare());
        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingListCallBack.getItem(position);
            }
        });
        return convertView;


    }

    static class ViewHolder {
        TextView pickupLocationTxt, dropLocationTxt, startDate, LocationTxt, costTxtView;
        RelativeLayout mainLayout;
    }
}

