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

	static MapController mcForListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//definitions
		double currLat = 0.0, currLong = 0.0;
		GeoPoint currentPos;
		MapController mapController;
		MapView mapView;

		LocationManager mlocManager;
		LocationListener mlocListener;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mcForListener = mapController;

		//gps related
		mlocManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener(getApplicationContext());
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 0, mlocListener);  

		try {
			currLat = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
			currLong = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		} catch (Exception e) {
			Log.d("error in loc", e.toString());
			currLat = 37.87309;
			currLong = -122.25921;
		}

		currentPos = new GeoPoint ( (int) (currLat * 1E6) , (int) (currLong * 1E6));
		/*
		mc = mapView.getController();
		mc.animateTo(currentPos);
		mc.setZoom(15);
		mc.setCenter(currentPos);
		mapView.invalidate();
		 */


		// Reverse Geocoding language hard-coded to be us, but change laterO
		JSONObject reverseGeo = ReverseGeoloc.getLocationInfo(currentPos, "us");
		String cityName = ReverseGeoloc.getCityName(reverseGeo);
		Log.d("debugging", cityName);

		//marker to let you know where you are at
		Drawable drawable = this.getResources().getDrawable(R.drawable.person_marker);
		CustomItemizedOverlay itemizedOverlay = new CustomItemizedOverlay(drawable, this);
		OverlayItem overlayitem =
				new OverlayItem(currentPos, "Your Current Location", cityName);
		itemizedOverlay.addOverlay(overlayitem);

		mapView.getOverlays().add(itemizedOverlay);

		//redundant can remove if you wnat
//		mapController = mapView.getController();

		this.zoomToNewLocation(currentPos, mapController);
		mapView.invalidate();

		Toast.makeText(this.getApplicationContext(), "Click on the marker for Approximate Location", Toast.LENGTH_LONG).show();




		/* This below code should work on the actual device itself
		 * however it does not work on emulator (known bug)
		 * please see http://code.google.com/p/android/issues/detail?id=8816
		 * used workaround using google map api directly
		 * http://code.google.com/p/android/issues/detail?id=8816 under 21st comment
		 * 
		Geocoder gcd = new Geocoder(this.getApplicationContext(), Locale.getDefault());
		List<Address> addresses  = null;
		try {
			addresses = gcd.getFromLocation(currentPos.getLatitudeE6()/1.0E6, currentPos.getLongitudeE6()/1.0E6, 1);
		} catch (IOException e) {
			Log.e("IOException reverse geocoding", e.getMessage());
		} catch (Exception e) {
			Log.e("Exception reverse geocoding", e.getMessage());
		}
		if (addresses.size() > 0) 
			Log.d("GPSactivity", addresses.get(0).getLocality());
		 */



		/** below done by ji
		//		List<Overlay> mapOverlays = mapView.getOverlays();

		// draw star at berkeley, if you click the star then the message pops up
		// or you can customize 



		Drawable drawable = this.getResources().getDrawable(R.drawable.person_marker);
		CustomItemizedOverlay itemizedOverlay = new CustomItemizedOverlay(drawable, this);

		GeoPoint point = new GeoPoint(37873090, -122259210);

		OverlayItem overlayitem =
				new OverlayItem(point, "Attraction", "Berkeley City");

		itemizedOverlay.addOverlay(overlayitem);

//		mapOverlays.add(itemizedOverlay);
		mapView.getOverlays().add(itemizedOverlay);

		MapController mapController = mapView.getController();

		mapController.animateTo(point);
		mapController.setZoom(15);

		 */
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
	
}



//		Berkeley GeoLocation 37.87309	-122.25921
