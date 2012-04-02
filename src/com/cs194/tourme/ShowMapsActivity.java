package com.cs194.tourme;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
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

	protected Drawable poiMarker;
	private Context currentContext;
	
	private Runnable waitForMapTimeTask = new Runnable() {
		public void run() {
			if(mapView.getLatitudeSpan()==0||mapView.getLongitudeSpan()== 360000000) {
				mapView.postDelayed(this, 100);
			} else {

				GeoPoint geoPointNW = mapView.getProjection().fromPixels(0, 0);
				GeoPoint geoPointSE = new GeoPoint (geoPointNW.getLatitudeE6() + mapView.getLatitudeSpan(),
						geoPointNW.getLongitudeE6() + mapView.getLongitudeSpan());

				ArrayList<GeoPoint> listOfPOIGeoPoint = new ArrayList<GeoPoint> ();
				ArrayList<String> listOfPOIName = new ArrayList<String> ();
				
				DatabaseHandler dbHandler = new DatabaseHandler ();
				JSONArray listOfPOI = dbHandler.getDataFromSql("select p.name, p.geolocX, p.geolocY " +
						"from POI p where p.tour_id = " +
						"any (select t.id from Tour t where t.attraction_id = " +
						"any (select a.id from Attraction a where a.geolocX > 37 and a.geolocX < 38 " +
						"and a.geolocY > -123 and a.geolocY < -122))");
				
				for (int index = 0 ; index < listOfPOI.length(); index++) {
					String poiName = null ; Float lat = null, lng = null;

					try {
						poiName = listOfPOI.getJSONObject(index).getString("name");
						lat = Float.parseFloat(listOfPOI.getJSONObject(index).getString("geolocY"));
						lng =  Float.parseFloat(listOfPOI.getJSONObject(index).getString("geolocX"));
					} catch (JSONException e) {
						Log.d("Json error", e.toString());
						Toast.makeText(currentContext, "JSONException Has Occured" +
								" in Attractions Route Activity" ,Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}

					listOfPOIGeoPoint.add((new GeoPoint ((int) (lat*1E6), (int) (lng*1E6))));
					listOfPOIName.add(poiName);
				}

				//add marker
				for (int index = 0 ; index < listOfPOIGeoPoint.size(); index++){
					CustomItemizedOverlay nearByPOIOverlay = new CustomItemizedOverlay(poiMarker, currentContext);
					OverlayItem overlayitem =
							new OverlayItem(listOfPOIGeoPoint.get(index), "Point of Interest", 
									listOfPOIName.get(index));
					Log.d("ABCDDD", listOfPOIGeoPoint.get(index) + " " + listOfPOIName.get(index));
					nearByPOIOverlay.addOverlay(overlayitem);
					mapView.getOverlays().add(nearByPOIOverlay);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		MyLocationListener.animateToMap = true;
		currentPosMarker =
				this.getResources().getDrawable(R.drawable.person_marker);
		poiMarker = this.getResources().getDrawable(R.drawable.map_marker2);
		currentContext = this;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		//setting mapview and controller
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mcForListener = mapView.getController();
		mcForListener.setZoom(14);

		//gps related
		mlocManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener(getApplicationContext());
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1*1000, 0, mlocListener);  

		mapView.postDelayed(waitForMapTimeTask, 100);

		//updates whenever you change location

		setMarkerNewLocation ();



	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}	

	protected void zoomToNewLocation (GeoPoint loc, MapController mc) {
		mc.animateTo(loc);
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
		GeoPoint currentPos = new GeoPoint ((int) (currLat * 1E6) , (int) (currLong * 1E6));

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

	@Override
	public void onPause() {
		super.onPause();
		mlocManager.removeUpdates(mlocListener);
	}

	// not sure if needed
	/*
	@Override
	public void onResume() {
		super.onResume();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1*1000, 0, mlocListener);  
	}
	 */





}

//		Berkeley GeoLocation 37.87309	-122.25921
