package com.cs194.tourme;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class ShowMapsActivity extends MapActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		double currLat = 0.0, currLong = 0.0;
		GeoPoint currentPos;
		MapController mc;
		MapView mapView;
		LocationManager mlocManager;
		LocationListener mlocListener;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mlocManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);

		mlocListener = new MyLocationListener(getApplicationContext());

		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

//		Berkeley GeoLocation 37.87309	-122.25921
		
		try {
			currLat = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
			currLong = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		} catch (Exception e) {
			Log.d("error in loc", e.toString());
			currLat = 37.87309;
			currLong = -122.25921;
		}
		
		currentPos = new GeoPoint ( (int) (currLat * 1E6) , (int) (currLong * 1E6));
		mc = mapView.getController();
		mc.animateTo(currentPos);
		mc.setZoom(17);
		mc.setCenter(currentPos);
		mapView.invalidate();
	
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
