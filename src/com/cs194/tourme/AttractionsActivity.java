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
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AttractionsActivity extends ExpandableListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.attractions);

		final String NAME = "name";
		final String IMAGE = "image";
		final LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final ArrayList<HashMap<String, String>> headerData = new ArrayList<HashMap<String, String>>();
		final ArrayList<ArrayList<HashMap<String, Object>>> childData = new ArrayList<ArrayList<HashMap<String, Object>>>();

		try {


			DatabaseHandler dbHandler = new DatabaseHandler ();
			//change the query make space into +
			JSONArray listOfCities = dbHandler.getDataFromSql("select City.id, City.city from City");

			for (int cityIndex = 0 ; cityIndex < listOfCities.length(); cityIndex++) {
				String cityName = listOfCities.getJSONObject(cityIndex).getString("city");
				Integer cityId = listOfCities.getJSONObject(cityIndex).getInt("id");
				HashMap <String, String> group = new HashMap <String, String> ();
				group.put(NAME, cityName);
				headerData.add(group);

				ArrayList<HashMap<String, Object>> eachCityData = new ArrayList<HashMap<String, Object>> ();
				childData.add(eachCityData);

				JSONArray listOfAttractionsFromCurrentCity = 
						dbHandler.getDataFromSql("select a.name from Attraction a where a.city_id = " +  cityId);
				for (int attractionIndex = 0 ; attractionIndex 
						< listOfAttractionsFromCurrentCity.length(); attractionIndex++) {
					String attractionName 
					= listOfAttractionsFromCurrentCity.getJSONObject(attractionIndex).getString("name");
//					Log.d("AttractionsActivity", "" + listOfAttractionsFromCurrentCity.length());
					HashMap<String,Object> map = new HashMap<String,Object> ();
					map.put(NAME, attractionName);
					map.put(IMAGE, getResources().getDrawable(R.drawable.ic_launcher));
					eachCityData.add(map);
				}

			}

		} catch(JSONException e1){
			Log.d("Json error", e1.toString());
			Toast.makeText(getBaseContext(), "JSONException Has Occured" +
					" in Attractions Activity" ,Toast.LENGTH_LONG).show();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		/*
		final HashMap<String, String> group1 = new HashMap<String, String>();
		group1.put(NAME, "Group 1");
		headerData.add( group1 );

		final HashMap<String, String> group2 = new HashMap<String, String>();
		group2.put(NAME, "Group 2");
		headerData.add( group2);




		final ArrayList<HashMap<String, Object>> group1data = new ArrayList<HashMap<String, Object>>();
		childData.add(group1data);

		final ArrayList<HashMap<String, Object>> group2data = new ArrayList<HashMap<String, Object>>();
		childData.add(group2data);


		// Set up some sample data in both groups
		for( int i=0; i<10; ++i) {
			final HashMap<String, Object> map = new HashMap<String,Object>();
			map.put(NAME, "Child " + i );
			map.put(IMAGE, getResources().getDrawable(R.drawable.ic_launcher));
			( i%2==0 ? group1data : group2data ).add(map);
		}
		 */
		setListAdapter( new SimpleExpandableListAdapter(
				this,
				headerData,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { NAME },            // the name of the field data
				new int[] { android.R.id.text1 }, // the text field to populate with the field data
				childData,
				0,
				null,
				new int[] {}
				) {
			@Override
			public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
				final View v = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);

				
				// TODO is to make our custom view here
				((TextView)v.findViewById(R.id.name)).setText( (String) ((Map<String,Object>)getChild(groupPosition, childPosition)).get(NAME) );
				((ImageView)v.findViewById(R.id.image)).setImageDrawable( (Drawable) ((Map<String,Object>)getChild(groupPosition, childPosition)).get(IMAGE) );

				return v;
			}

			@Override
			public View newChildView(boolean isLastChild, ViewGroup parent) {
				return layoutInflater.inflate(R.layout.attractions, null, false);
			}
		}
				);		
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
			int childPosition, long id) {
		TextView tv = (TextView) findViewById(R.id.name);
		tv.setText(""+groupPosition+"/"+childPosition+"/"+id);
		showAttraction(v);	
		// use groupPosition and childPosition to locate the current item in the adapter
		return true;
	}

	public void showAttraction(View view) {
		Intent intent = new Intent(this, EachAttractionActivity.class);
		startActivity(intent);
	}	
}

