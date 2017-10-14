package com.acekabs.driverapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.activity.MainHomeScreen;
import com.acekabs.driverapp.base.BaseActivity;
import com.acekabs.driverapp.base.BaseFragment;
import com.acekabs.driverapp.pojo.RouteDetails;
import com.acekabs.driverapp.pojo.Trips;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by ankit on 25-06-2017.
 */
public class BookinDetailFragment extends BaseFragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng pickupLatLng, dropLatLng;
    private Trips trips;
    private Bundle bundle;
    private ArrayList<LatLng> latLang = new ArrayList<>();
    private PolylineOptions lineOptions = null;
    private TextView pickupLocationTxt, dropLocationTxt, distanceTxtView, costTxtView, durationTxtView, backButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_history_details, container, false);

        bundle = this.getArguments();

        pickupLocationTxt = (TextView) convertView.findViewById(R.id.pickupLocationTxt);
        dropLocationTxt = (TextView) convertView.findViewById(R.id.dropLocationTxt);
        distanceTxtView = (TextView) convertView.findViewById(R.id.distanceTxtView);
        costTxtView = (TextView) convertView.findViewById(R.id.costTxtView);
        durationTxtView = (TextView) convertView.findViewById(R.id.durationTxtView);
        backButton = (TextView) convertView.findViewById(R.id.backButton);

        lineOptions = new PolylineOptions();

        if (bundle != null) {
            trips = bundle.getParcelable("data");
            if (!TextUtils.isEmpty(trips.getPickupLatitude()) && !TextUtils.isEmpty(trips.getPickupLongitude())) {
                pickupLatLng = new LatLng(Double.parseDouble(trips.getPickupLatitude()), Double.parseDouble(trips.getPickupLongitude()));
            }
            if (!trips.getRouteDetailsList().isEmpty()) {
                for (RouteDetails routeDetails : trips.getRouteDetailsList()) {
                    String latLng = routeDetails.getLocation();
                    if (latLng.contains(",")) {
                        String[] separated = latLng.split(",");
                        if (separated.length > 0 && !TextUtils.isEmpty(separated[0]) && !TextUtils.isEmpty(separated[1])) {
                            latLang.add(new LatLng(Double.parseDouble(separated[0]), Double.parseDouble(separated[1])));
                        }
                    }
                }
                pickupLocationTxt.setText(TextUtils.isEmpty(trips.getPickuplocationname()) ? "" : trips.getPickuplocationname());
                dropLocationTxt.setText(TextUtils.isEmpty(trips.getDroplocationname()) ? "" : trips.getDroplocationname());

                distanceTxtView.setText("Duration \n");
                distanceTxtView.append("");
                durationTxtView.setText("Distance \n");
                durationTxtView.append(TextUtils.isEmpty(trips.getDistance()) ? "" : trips.getDistance() + " Km");
                costTxtView.setText("Total Fare \n");
                costTxtView.append(TextUtils.isEmpty(trips.getTotalfare()) ? "" : trips.getTotalfare());
            }
        }
        if (!latLang.isEmpty()) {
            lineOptions.addAll(latLang);
            lineOptions.width(5);
            lineOptions.color(Color.RED);
        }


        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map_rout);
        mapFragment.getMapAsync(this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainHomeScreen) getActivity()).onBackPressed();
            }
        });
        return convertView;
    }


    @Override
    protected boolean isBottomBarNeeded(boolean yes) {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((BaseActivity) getActivity()).showBottomBar();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(googleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        CameraUpdate center = CameraUpdateFactory.newLatLng(pickupLatLng);
        mMap.moveCamera(center);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
        if (pickupLatLng != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(pickupLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.g_spot)));
        }
        if (dropLatLng != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(pickupLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_spot)));
        }
        if (lineOptions != null && !lineOptions.getPoints().isEmpty()) {
            mMap.addPolyline(lineOptions);
        }

    }
}
