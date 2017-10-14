package com.acekabs.driverapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.R;
import com.acekabs.driverapp.adapters.HistoryAdapter;
import com.acekabs.driverapp.base.BaseFragment;
import com.acekabs.driverapp.base.VolleyErrorHandling;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.firebase.FirebaseApplicationContext;
import com.acekabs.driverapp.interfaces.BookingListCallBack;
import com.acekabs.driverapp.pojo.TripHistoryData;
import com.acekabs.driverapp.restclient.RestApiService;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;
import com.acekabs.driverapp.utils.UtilityMethods;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adee09 on 3/12/2017.
 */

public class BookingHistoryFragment extends BaseFragment implements BookingListCallBack {

    private static final String TAG = "BookingHistory";
    private ListView listView;
    private SharePreferanceWrapperSingleton preferanceWrapperSingleton;
    private RestApiService mAPIService;
    private HistoryAdapter historyAdapter;
    private TripHistoryData tripHistoryData;
    private Context ctx;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ctx = getActivity();
        listView = (ListView) view.findViewById(R.id.listView);
        preferanceWrapperSingleton = SharePreferanceWrapperSingleton.getSingletonInstance();
        preferanceWrapperSingleton.setPref(getActivity());
        preferanceWrapperSingleton.setEditor();
        if (UtilityMethods.isNetworkAvailable(ctx)) {
            getHistory();
        } else {
            Toast.makeText(ctx, "Please connect with active internet!", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void getHistory() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Data. Please Wait");
        progressDialog.setTitle("Loading");
        progressDialog.show();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        final Map<String, String> mParam = new HashMap<String, String>();
        mParam.put("driverEmail", preferanceWrapperSingleton.getStringValueFromSharedPrefrences(Constants.userId));
        mParam.put("searchDate", formattedDate);

        Log.e(TAG,"Post Data=>");
        Log.e(TAG,"driverEmail=>"+mParam.get("driverEmail"));
        Log.e(TAG,"searchDate=>"+mParam.get("searchDate"));


        String url = ApplicationConstants.ALLTRIPSAPI;
        Log.e(TAG,"Url=>"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (historyAdapter == null) {
                            tripHistoryData = new Gson().fromJson(response, TripHistoryData.class);
                            if (!tripHistoryData.getHistory().isEmpty()) {
                                historyAdapter = new HistoryAdapter(tripHistoryData, getActivity(),
                                        (BookingListCallBack) BookingHistoryFragment.this);
                                listView.setAdapter(historyAdapter);
                            }
                        }
                        progressDialog.dismiss();
                        Log.d("rep", response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        VolleyErrorHandling.errorHandling(error, getActivity());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return mParam;
            }

        };

        FirebaseApplicationContext.getInstance().addToRequestQueue(stringRequest);

//        Calendar c = Calendar.getInstance();
//        System.out.println("Current time => "+c.getTime());
//
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String formattedDate = df.format(c.getTime());
//
//        mAPIService = ApiUtils.getAPIService(ApplicationConstants.RESTBASEURL);
//        mAPIService.getBookingHistory(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.userId), formattedDate).enqueue(new Callback<List<TripHistoryData>>() {
//
//            @Override
//            public void onResponse(Call<List<TripHistoryData>> call, Response<List<TripHistoryData>> response) {
//                Log.e("response", response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<List<TripHistoryData>> call, Throwable t) {
//                Log.e("response", "error");
//            }
//        });


    }


    @Override
    protected boolean isBottomBarNeeded(boolean yes) {
        return yes;
    }

    @Override
    public void getItem(int position) {
        if (tripHistoryData != null && !tripHistoryData.getHistory().isEmpty()) {
            BookinDetailFragment bookinDetailFragment = new BookinDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("data", tripHistoryData.getHistory().get(position));
            bookinDetailFragment.setArguments(bundle);
            navigateFragment(bookinDetailFragment, true, "BookinDetailFragment", false);
        }
    }
}
