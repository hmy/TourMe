package com.cs194.tourme;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

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

		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 30*10000, 0, mlocListener);

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
		mc.setZoom(10);
		mc.setCenter(currentPos);
		mapView.invalidate();


		List<Overlay> mapOverlays = mapView.getOverlays();
		// draw star at berkeley, if you click the star then the message pops up
		// or you can customize 
		Drawable drawable = this.getResources().getDrawable(R.drawable.star_small);
		CustomItemizedOverlay itemizedOverlay =
				new CustomItemizedOverlay(drawable, this);

		GeoPoint point = new GeoPoint(37873090, -122259210);
		OverlayItem overlayitem =
				new OverlayItem(point, "Attraction", "Berkeley City");

		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);

		MapController mapController = mapView.getController();

		mapController.animateTo(point);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
