package com.cs194.tourme;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;

public class MyLocationListener extends ShowMapsActivity implements LocationListener {

	static public boolean animateToMap;
	Context currentContext;
	
	public MyLocationListener (Context appContext) {
		currentContext = appContext;
	}
	
	@Override
	public void onLocationChanged(Location loc)	{

		String Text = "My location changed to:" +
				" Latitude = " + loc.getLatitude() +
				" Longitude = " + loc.getLongitude() ;
		
		Toast.makeText (currentContext, Text, Toast.LENGTH_SHORT).show();
				
		GeoPoint newloc = new GeoPoint ((int) (loc.getLatitude()*1E6), 
				(int) (loc.getLongitude()*1E6)); 

		//this fixes the bug that moves the map when you are in diff. activity 
		if(MyLocationListener.animateToMap) {
			super.setMarkerNewLocation();
		}
		
		//deprecated animate
		//super.zoomToNewLocation(newloc, ShowMapsActivity.mcForListener);
		
	}


	@Override
	public void onProviderDisabled (String provider) {
		Toast.makeText( currentContext, "Gps Disabled", Toast.LENGTH_SHORT ).show();
	}


	@Override
	public void onProviderEnabled (String provider) {
		Toast.makeText( currentContext, "Gps Enabled", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
