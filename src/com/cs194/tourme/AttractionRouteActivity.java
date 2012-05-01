package com.cs194.tourme;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class AttractionRouteActivity extends MapActivity {
	/** Called when the activity is first created. */

	MapView mapView;
	static String poiName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attractionroute);

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		Drawable mapMarker = this.getResources().getDrawable(R.drawable.map_marker);

		ArrayList<GeoPoint> tourGeoPoint = new ArrayList<GeoPoint> ();
		ArrayList<String> tourPOInames = new ArrayList<String> ();

		//for now I will put is as false
		//later put an toast or message to choose
		MyLocationListener.animateToMap = false;

		//in the spinner We would like to put name of Tour first!!!!
		tourPOInames.add("Please Select From The List");

		//SQL query and handling
		DatabaseHandler dbHandler = new DatabaseHandler ();
		JSONArray listOfPOI = dbHandler.getDataFromSql("select p.name, p.geolocX, p.geolocY from POI p where p.tour_id = " +
				"(select t.id from Tour t where t.attraction_id = " +
				"(select a.id from Attraction a where a.name = '" +
				AttractionsActivity.attractionName + "'));");

		//		HashMap<String, ArrayList<Float>> poiNameAndGeoloc = new HashMap<String, ArrayList<Float>> ();

		for (int index = 0 ; index < listOfPOI.length(); index++) {

			String poiName = null ; Float lat = null, lng = null;

			try {
				poiName = listOfPOI.getJSONObject(index).getString("name");
				lat = Float.parseFloat(listOfPOI.getJSONObject(index).getString("geolocY"));
				lng =  Float.parseFloat(listOfPOI.getJSONObject(index).getString("geolocX"));
			} catch (JSONException e) {
				Log.d("Json error", e.toString());
				Toast.makeText(getBaseContext(), "JSONException Has Occured" +
						" in Attractions Route Activity" ,Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}

			tourGeoPoint.add((new GeoPoint ((int) (lat*1E6), (int) (lng*1E6))));

			//I dont think i use below 3 lines yet!
			//			ArrayList<Float> tempGeoloc = new ArrayList<Float> ();
			//			tempGeoloc.add(lat);
			//			tempGeoloc.add(lng);

			tourPOInames.add(poiName);
			//			poiNameAndGeoloc.put(poiName, tempGeoloc);
		}

		//draw the paths
		for (int index = 0 ; index < tourGeoPoint.size()-1; index++){
			DrawPath(tourGeoPoint.get(index), tourGeoPoint.get(index+1), Color.rgb(150, 150, 150), mapView);
		}


		//add marker
		for (int index = 0 ; index < tourGeoPoint.size(); index++){
			CustomItemizedOverlay itemizedOverlay = new CustomItemizedOverlay(mapMarker, this);
			OverlayItem overlayitem =
					new OverlayItem(tourGeoPoint.get(index), AttractionsActivity.attractionName, 
							tourPOInames.get(index+1));
			//here index+1 b/c index 0 is name of the tour itself
			itemizedOverlay.addOverlay(overlayitem);
			mapView.getOverlays().add(itemizedOverlay);
		}

		mapView.getController().animateTo(tourGeoPoint.get(0));
		mapView.getController().setZoom(15);

		//sorts tourPOInames by alphabetical, inplace not in original order anymore
		Collections.sort(tourPOInames.subList(1, tourPOInames.size()-1));
		String [] poiNames = (String []) tourPOInames.toArray(new String[tourPOInames.size()]);

		//adapter for spinner
		ArrayAdapter<CharSequence> placesAdapter = 
				new ArrayAdapter<CharSequence> (this, android.R.layout.simple_spinner_item, poiNames);
		placesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = (Spinner) this.findViewById(R.id.spinnerChoosePOI);
		spinner.setPrompt(AttractionsActivity.attractionName);
		spinner.setAdapter(placesAdapter);
		spinner.setOnItemSelectedListener(new AttractionRouteCustomItemSelectedListener());

		//message
		Toast.makeText(this.getApplicationContext(), "You are looking at the Tour Route Map for "+
				AttractionsActivity.attractionName + " !", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private String mapDirectionUrlBuilder (double srcLat, double srcLong,
			double destLat, double destLong) {
		StringBuilder returnUrlString = new StringBuilder();
		returnUrlString.append("http://maps.google.com/maps?f=d&hl=en");
		//from
		returnUrlString.append("&saddr=");
		returnUrlString.append(Double.toString(srcLat / 1.0E6));
		returnUrlString.append(",");
		returnUrlString.append(Double.toString(srcLong / 1.0E6));
		//to
		returnUrlString.append("&daddr=");
		returnUrlString.append(Double.toString(destLat / 1.0E6));
		returnUrlString.append(",");
		returnUrlString.append(Double.toString(destLong / 1.0E6));
		returnUrlString.append("&ie=UTF8&0&om=0&output=kml");

		return returnUrlString.toString();
	}

	private void DrawPath(GeoPoint src, GeoPoint dest, int color, MapView myMapView) {


		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		Document doc = null;
		HttpURLConnection urlConnection = null;
		URL url = null;
		GeoPoint startGP, endGP = null, gp1, gp2;
		String urlString = mapDirectionUrlBuilder((double) src.getLatitudeE6(), (double) src.getLongitudeE6(),
				(double) dest.getLatitudeE6(), (double) dest.getLongitudeE6()); 

		Log.d("abc", "URL=" + urlString.toString());

		// get the kml (XML) doc. And parse it to get the coordinates(direction route).

		try {

			url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();

			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(urlConnection.getInputStream());

			if (doc.getElementsByTagName("GeometryCollection").getLength() > 0) {
				String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild()
						.getFirstChild().getNodeValue();

				//Log.d("abc", "path=" + path);

				String[] pairsOfRoute = path.split(" ");
				String[] longLat = pairsOfRoute[0].split(","); 
				// longLat[0]=longitude longLat[1]=latitude longLat[2]=height

				// src
				startGP = gp2 =  new GeoPoint((int) (Double.parseDouble(longLat[1]) * 1E6), 
						(int) (Double.parseDouble(longLat[0]) * 1E6));

				//below for loop colors all path returned by KML
				for (int i = 1; i < pairsOfRoute.length; i++) {// the last one would be crash
					longLat = pairsOfRoute[i].split(",");
					gp1 = gp2;
					// watch out! For GeoPoint, first:latitude, second:longitude
					gp2 = new GeoPoint((int) (Double.parseDouble(longLat[1]) * 1E6),
							(int) (Double.parseDouble(longLat[0]) * 1E6));
					myMapView.getOverlays().add(
							new RouteCustomOverLay(gp1, gp2, 2, color, getBaseContext()));
					//					Log.d("abc", "pair:" + pairsOfRoute[i]);
				}

				//connect disconnected roads end to destination
				myMapView.getOverlays().add(new RouteCustomOverLay(gp2, dest, 2, color, getBaseContext()));

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	//to be used with listener below
	public void showEachAttraction(View view) {
		Intent intent = new Intent (this, EachAttractionActivity.class);
		startActivity (intent);
	}

	//disable home button now
	@Override
	public void onAttachedToWindow() {  
	    Log.i("TESTE", "onAttachedToWindow");
	    this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	    super.onAttachedToWindow();  
	}
	

	public class AttractionRouteCustomItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			switch (pos) {
			case 0 : 
				break;

			default : 
				poiName = parent.getItemAtPosition(pos).toString();
				showEachAttraction(view);
				break;
			}
		}
		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}		
	}


}
