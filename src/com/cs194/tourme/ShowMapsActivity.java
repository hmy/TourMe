package com.cs194.tourme;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.OverlayItem;

public class ShowMapsActivity extends MapActivity implements MapViewMovementListener {

	//static protected MapController mcForListener;
	static protected Drawable currentPosMarker;
	static protected CustomItemizedOverlay currentPositionOverlay = null;
	static protected String currCityName;
	//static public LocationManager mlocManager;
	//static public LocationListener mlocListener;
	static protected DetectMovementMapView mapView;

	protected Drawable poiMarker;
	private Context currentContext;

	static CustomItemizedOverlay nearByPOIOverlay = null;


	private Runnable waitForMapTimeTask = new Runnable() {
		public void run() {
			if(mapView.getLatitudeSpan()==0 || mapView.getLongitudeSpan()== 360000000) {
				mapView.postDelayed(this, 100);
			} else {
				setCloseByPOIMarkers();
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
		mapView = (DetectMovementMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		LandingPageActivity.mcForListener = mapView.getController();
		LandingPageActivity.mcForListener.setZoom(13);

		/*
		//gps related
		LandingPageActivity.mlocManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
		LandingPageActivity.mlocListener = new MyLocationListener(getApplicationContext());
		LandingPageActivity.mlocManager.requestLocationUpdates( 
				LocationManager.GPS_PROVIDER, 30*1000, 0, LandingPageActivity.mlocListener);  
		*/

		mapView.postDelayed(waitForMapTimeTask, 100);

		//updates whenever you change location	
		setMarkerNewLocation ();

		mapView.setOnPanListener(this);


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
		try {
			LandingPageActivity.currLat = LandingPageActivity.mlocManager.
					getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
			LandingPageActivity.currLong = LandingPageActivity.mlocManager.
					getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		} catch (Exception e) {
			Log.d("error in loc", "error in showmapsactivity");
			e.printStackTrace();
			LandingPageActivity.currLat = 37.87309;
			LandingPageActivity.currLong = -122.25921;
		}
		GeoPoint currentPos = new GeoPoint ((int) (LandingPageActivity.currLat * 1E6) , 
				(int) (LandingPageActivity.currLong * 1E6));

		//gets approximte city name using getApproximateCity function
		currCityName = getApproximateCity(currentPos);

		//creating new current position marker
		OverlayItem overlayitem =
				new OverlayItem(currentPos, "Your Current Location", currCityName);
		if (currentPositionOverlay == null) {
			currentPositionOverlay = new CustomItemizedOverlay(currentPosMarker, this);
			currentPositionOverlay.addOverlay(overlayitem);
		} else {
			currentPositionOverlay.replaceOverlay(overlayitem);
		}

		//adding to mapview and zoom to new location
		mapView.getOverlays().add(currentPositionOverlay);
		this.zoomToNewLocation(currentPos, LandingPageActivity.mcForListener);
		mapView.invalidate();

	}

	public void setCloseByPOIMarkers () {

		//I want this as I want to remove existing overlays
		mapView.getOverlays().remove(ShowMapsActivity.nearByPOIOverlay);

		GeoPoint geoPointNW = mapView.getProjection().fromPixels(0, 0);
		GeoPoint geoPointSE = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());

		ArrayList<GeoPoint> listOfPOIGeoPoint = new ArrayList<GeoPoint> ();
		ArrayList<String> listOfPOIName = new ArrayList<String> ();

		DatabaseHandler dbHandler = new DatabaseHandler ();
		JSONArray listOfPOI = dbHandler.getDataFromSql("select p.name, p.geolocX, p.geolocY " +
				"from POI p where p.tour_id = " +
				"any (select t.id from Tour t where t.attraction_id = " +
				"any (select a.id from Attraction a where " +
				"a.geolocY < " + geoPointNW.getLatitudeE6()/1.0E6 + 
				" and a.geolocY > " + geoPointSE.getLatitudeE6()/1.0E6 +
				" and a.geolocX < " + geoPointSE.getLongitudeE6()/1.0E6 +
				" and a.geolocX > " + geoPointNW.getLongitudeE6()/1.0E6 + 
				"))");

		//if listOfPOI is null then no need to put markers and such
		if (listOfPOI != null) {
			for (int index = 0 ; index < listOfPOI.length(); index++) {
				String poiName = null ; Float lat = null, lng = null;

				try {
					poiName = listOfPOI.getJSONObject(index).getString("name");
					lat = Float.parseFloat(listOfPOI.getJSONObject(index).getString("geolocY"));
					lng =  Float.parseFloat(listOfPOI.getJSONObject(index).getString("geolocX"));
				} catch (JSONException e) {
					Log.d("Json error", e.toString());
					Toast.makeText(currentContext, "JSONException Has Occured" +
							" in Attractions Route Activity" ,Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}

				listOfPOIGeoPoint.add((new GeoPoint ((int) (lat*1E6), (int) (lng*1E6))));
				listOfPOIName.add(poiName);
			}

			//add marker
			for (int index = 0 ; index < listOfPOIGeoPoint.size(); index++){
				nearByPOIOverlay = new CustomItemizedOverlay(poiMarker, currentContext);
				OverlayItem overlayitem =
						new OverlayItem(listOfPOIGeoPoint.get(index), "Point of Interest", 
								listOfPOIName.get(index));
				Log.d("ABCDDD", listOfPOIGeoPoint.get(index) + " " + listOfPOIName.get(index));
				nearByPOIOverlay.addOverlay(overlayitem);
				mapView.getOverlays().add(nearByPOIOverlay);

			}
		}
	}

	@Override
	public void onChange() {
		this.setCloseByPOIMarkers();
	}

	/*
	@Override
	public void onAttachedToWindow() {  
	    Log.i("TESTE", "onAttachedToWindow");
	    this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	    super.onAttachedToWindow();  
	}
	*/
	
	/*
	@Override
	public void onResume() {
		super.onResume();
		LandingPageActivity.mlocManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 30*1000, 0, LandingPageActivity.mlocListener);  
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LandingPageActivity.mlocManager.removeUpdates(LandingPageActivity.mlocListener);
	}
	

	*/
}

//		Berkeley GeoLocation 37.87309	-122.25921
