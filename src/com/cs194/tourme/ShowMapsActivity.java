package com.cs194.tourme;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ShowMapsActivity extends MapActivity{

	static protected MapController mcForListener;
	static protected Drawable currentPosMarker;
	static protected CustomItemizedOverlay currentPositionOverlay = null;
	static protected String currCityName;
	static protected LocationManager mlocManager;
	static protected LocationListener mlocListener;
	static protected MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		MyLocationListener.animateToMap = true;
		currentPosMarker =
				this.getResources().getDrawable(R.drawable.person_marker);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		//setting mapview and controller
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mcForListener = mapView.getController();

		//gps related
		mlocManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener(getApplicationContext());
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 30*1000, 0, mlocListener);  

		//updates whenever you change location
		setMarkerNewLocation ();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}	

	protected void zoomToNewLocation (GeoPoint loc, MapController mc) {
		mc.animateTo(loc);
		mc.setZoom(13);
		mc.setCenter(loc);
	}

	private String getApproximateCity (GeoPoint pos) {
		JSONObject reverseGeo = ReverseGeoloc.getLocationInfo(pos, "us");
		String cityName = ReverseGeoloc.getCityName(reverseGeo);
		return cityName;
	}

	protected void setMarkerNewLocation() {
		//getting current location
		double currLat = 0.0, currLong = 0.0;
		try {
			currLat = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
			currLong = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		} catch (Exception e) {
			Log.d("error in loc", "error in showmapsactivity");
			e.printStackTrace();
			currLat = 37.87309;
			currLong = -122.25921;
		}
		GeoPoint currentPos = new GeoPoint ( (int) (currLat * 1E6) , (int) (currLong * 1E6));

		//gets approximte city name using getApproximateCity function
		currCityName = getApproximateCity(currentPos);

		//creating new current position marker
		OverlayItem overlayitem =
				new OverlayItem(currentPos, "Your Current Location", currCityName);
		if (ShowMapsActivity.currentPositionOverlay == null) {
			currentPositionOverlay = new CustomItemizedOverlay(currentPosMarker, this);
			currentPositionOverlay.addOverlay(overlayitem);
		} else {
			currentPositionOverlay.replaceOverlay(overlayitem);
		}

		//adding to mapview and zoom to new location
		mapView.getOverlays().add(currentPositionOverlay);
		this.zoomToNewLocation(currentPos, mcForListener);
		mapView.invalidate();

	}

}

//		Berkeley GeoLocation 37.87309	-122.25921
