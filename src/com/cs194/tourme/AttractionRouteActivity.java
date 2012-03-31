package com.cs194.tourme;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class AttractionRouteActivity extends MapActivity {
	/** Called when the activity is first created. */

	MapView mapView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attractionroute);

		
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		Drawable mapMarker = this.getResources().getDrawable(R.drawable.map_marker);
		
		ArrayList<GeoPoint> tourGeoPoint = new ArrayList<GeoPoint> ();
		tourGeoPoint.add(new GeoPoint ((int) (40.7649*1E6), (int) (-73.9734*1E6)));
		tourGeoPoint.add(new GeoPoint ((int) (40.7746*1E6), (int) (-73.9702*1E6)));
		tourGeoPoint.add(new GeoPoint ((int) (40.7808*1E6), (int) (-73.9741*1E6)));
		tourGeoPoint.add(new GeoPoint ((int) (40.7919*1E6), (int) (-73.9587*1E6)));
		tourGeoPoint.add(new GeoPoint ((int) (40.7965*1E6), (int) (-73.951*1E6)));
		tourGeoPoint.add(new GeoPoint ((int) (40.7763*1E6), (int) (-73.9641*1E6)));

		//draw Path
		for (int index = 0 ; index < tourGeoPoint.size()-1; index++){
			DrawPath(tourGeoPoint.get(index), tourGeoPoint.get(index+1), Color.rgb(150, 150, 150), mapView);
		}

		//add marker
		for (int index = 0 ; index < tourGeoPoint.size(); index++){
			CustomItemizedOverlay itemizedOverlay = new CustomItemizedOverlay(mapMarker, this);
			OverlayItem overlayitem =
					new OverlayItem(tourGeoPoint.get(index), null, null);
			itemizedOverlay.addOverlay(overlayitem);
			mapView.getOverlays().add(itemizedOverlay);
		}
		mapView.getController().animateTo(tourGeoPoint.get(0));
		mapView.getController().setZoom(15);

		
		//now fill in the spinner items
		/*
		String places[] = new String[3];
		places[0] = "Central Park SE corner";
		places[1] = "The Lake";
		places[2] = "American Museum of Natural History";
		*/
		ArrayList<String> places = new ArrayList<String> ();
		places.add("Central Park");
		String[] place = places.toArray(new String[places.size()]);

		
//		ArrayAdapter<CharSequence> placesAdapter = new ArrayAdapter<CharSequence> (this, R.id.spinnerChoosePOI, places);
//		placesAdapter.setDropDownViewResource(R.id.spinnerChoosePOI);
		ArrayAdapter<CharSequence> placesAdapter = new ArrayAdapter<CharSequence> (this, android.R.layout.simple_spinner_item, place);
		placesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = (Spinner) this.findViewById(R.id.spinnerChoosePOI);
		spinner.setAdapter(placesAdapter);
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

}
