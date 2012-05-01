package com.cs194.tourme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AttractionsActivity extends ExpandableListActivity {

	static String attractionName = "";
	final String NAME = "name";
	final String IMAGE = "image";	
	LayoutInflater layoutInflater;
	ArrayList<HashMap<String, String>> headerData;
	ArrayList<ArrayList<HashMap<String, Object>>> childData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		setContentView(R.layout.attractions);

		try {

			layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			headerData = new ArrayList<HashMap<String, String>>();
			childData = new ArrayList<ArrayList<HashMap<String, Object>>>();


			DatabaseHandler dbHandler = new DatabaseHandler ();

			/*
			select c.id, c.city
			from City c
			order by 
			abs((c.geolocX+180)^2-12081.506) + abs((c.geolocY+180)^2-48585.860)
			asc
			 */
			JSONArray listOfCities = dbHandler.getDataFromSql("select c.id, c.city " +
					"from City c " +
					"order by " +
					"(((c.geolocX)-" + LandingPageActivity.currLong + ") " +
					"* ((c.geolocX)-" + LandingPageActivity.currLong + "))" +
					"+(((c.geolocY)-" + LandingPageActivity.currLat + ") " +
					"* ((c.geolocY)-" + LandingPageActivity.currLat + "))"
					+ " asc");

			Log.d("AttractionActivity SQL","select c.id, c.city " +
					"from City c " +
					"order by " +
					"(((c.geolocX)-" + LandingPageActivity.currLong + ") " +
					"* ((c.geolocX)-" + LandingPageActivity.currLong + "))" +
					"+(((c.geolocY)-" + LandingPageActivity.currLat + ") " +
					"* ((c.geolocY)-" + LandingPageActivity.currLat + "))"
					+ " asc");

			for (int i = 0 ; i < listOfCities.length(); i++) {
				Log.d("AttractionActivity" , listOfCities.getJSONObject(i).toString());
			}

			for (int cityIndex = 0 ; cityIndex < listOfCities.length(); cityIndex++) {
				String cityName = listOfCities.getJSONObject(cityIndex).getString("city");
				Integer cityId = listOfCities.getJSONObject(cityIndex).getInt("id");
				HashMap <String, String> group = new HashMap <String, String> ();
				group.put(NAME, cityName);
				headerData.add(group);

				ArrayList<HashMap<String, Object>> eachCityData = new ArrayList<HashMap<String, Object>> ();
				childData.add(eachCityData);


				JSONArray listOfAttractionsFromCurrentCity = 
						dbHandler.getDataFromSql("select a.name from Attraction a where a.city_id = " 
								+  cityId + " " +
								"order by " +
								"(((a.geolocX)-" + LandingPageActivity.currLong + ") " +
								"* ((a.geolocX)-" + LandingPageActivity.currLong + "))" +
								"+(((a.geolocY)-" + LandingPageActivity.currLat + ") " +
								"* ((a.geolocY)-" + LandingPageActivity.currLat + "))"
								+ " asc");

				Log.d("AttractionActivity SQL","select a.name from Attraction a where a.city_id = " 
						+  cityId + " " +
						"order by " +
						"(((a.geolocX)-" + LandingPageActivity.currLong + ") " +
						"* ((a.geolocX)-" + LandingPageActivity.currLong + "))" +
						"+(((a.geolocY)-" + LandingPageActivity.currLat + ") " +
						"* ((a.geolocY)-" + LandingPageActivity.currLat + "))"
						+ " asc");


				for (int attractionIndex = 0 ; attractionIndex 
						< listOfAttractionsFromCurrentCity.length(); attractionIndex++) {
					String attractionName 
					= listOfAttractionsFromCurrentCity.getJSONObject(attractionIndex).getString("name");
					HashMap<String,Object> map = new HashMap<String,Object> ();
					map.put(NAME, attractionName);
					map.put(IMAGE, getResources().getDrawable(R.drawable.ic_launcher));
					eachCityData.add(map);
				}

			}

		} catch(JSONException e1){
			Log.d("Json error", e1.toString());
			Toast.makeText(getBaseContext(), "JSONException Has Occured" +
					" in Attractions Activity" ,Toast.LENGTH_SHORT).show();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}


		setListAdapter( new SimpleExpandableListAdapter(
				this,
				headerData,
				R.layout.parentview,
				new String[] { NAME },            // the name of the field data
				new int[] { R.id.attractionParentGroupName }, // the text field to populate with the field data
				childData,
				0,
				null,
				new int[] {}
				) {
			@Override
			public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
				final View v = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);


				// TODO is to make our custom view here
				((TextView)v.findViewById(R.id.name)).setText( 
						(String) ((Map<String,Object>)getChild(groupPosition, childPosition)).get(NAME) );
				((ImageView)v.findViewById(R.id.image)).setImageDrawable(
						(Drawable) ((Map<String,Object>)getChild(groupPosition, childPosition)).get(IMAGE) );

				return v;
			}

			@Override
			public View newChildView(boolean isLastChild, ViewGroup parent) {
				return layoutInflater.inflate(R.layout.childview, null, false);
			}
		}
				);		
		
		//getting gps
		LandingPageActivity.mlocManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
		LandingPageActivity.mlocListener = new MyLocationListener(getApplicationContext());
		LandingPageActivity.mlocManager.requestLocationUpdates( 
				LocationManager.GPS_PROVIDER, 60*1000, 0, LandingPageActivity.mlocListener);  

		try {
			LandingPageActivity.currLat = LandingPageActivity.mlocManager.
					getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
			LandingPageActivity.currLong = LandingPageActivity.mlocManager.
					getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
			
			Log.d("change in lag long", LandingPageActivity.currLat + " " + LandingPageActivity.currLong);
		} catch (Exception e) {
			Log.d("error in loc", "error in AttractionActivity");
			e.printStackTrace();
			LandingPageActivity.currLat = 37.87309;
			LandingPageActivity.currLong = -122.25921;
		}
		
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
			int childPosition, long id) {
		attractionName = (String) childData.get(groupPosition).get(childPosition).get(NAME);
		showAttractionRoute(v);
		return true;
	}


	public void showAttractionRoute(View view) {
		Intent intent = new Intent (this, AttractionRouteActivity.class);
		startActivity (intent);
	}

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
}

