package com.cs194.tourme;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class MyLocationListener extends ShowMapsActivity implements LocationListener

{

	Context currentContext;
	
	public MyLocationListener (Context appContext) {
		currentContext = appContext;
	}
	
	@Override

	public void onLocationChanged(Location loc)

	{
		loc.getLatitude();
		loc.getLongitude();
		String Text = "My current location is: " +

				"Latitude = " + loc.getLatitude() +

				"Longitude = " + loc.getLongitude();


		Toast.makeText ( currentContext,

				Text,

				2).show();

	}


	@Override

	public void onProviderDisabled(String provider)

	{

		Toast.makeText( currentContext,

				"Gps Disabled",

				Toast.LENGTH_SHORT ).show();

	}


	@Override

	public void onProviderEnabled(String provider)

	{

		Toast.makeText( currentContext,

				"Gps Enabled",

				Toast.LENGTH_SHORT).show();

	}


	@Override

	public void onStatusChanged(String provider, int status, Bundle extras)

	{


	}

} /* End of Class MyLocationListener */
