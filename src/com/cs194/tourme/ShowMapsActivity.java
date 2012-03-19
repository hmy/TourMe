package com.cs194.tourme;

import android.os.Bundle;

import com.creamy.hmy.tourme.R;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class ShowMapsActivity extends MapActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
