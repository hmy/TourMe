package com.creamy.hmy.tourme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class AttractionsActivity extends ExpandableListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attractions);
		
		final String NAME = "name";
		final String IMAGE = "image";
		final LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final ArrayList<HashMap<String, String>> headerData = new ArrayList<HashMap<String, String>>();

		final HashMap<String, String> group1 = new HashMap<String, String>();
		group1.put(NAME, "Group 1");
		headerData.add( group1 );

		final HashMap<String, String> group2 = new HashMap<String, String>();
		group2.put(NAME, "Group 2");
		headerData.add( group2);


		final ArrayList<ArrayList<HashMap<String, Object>>> childData = new ArrayList<ArrayList<HashMap<String, Object>>>();

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

				// Populate your custom view here
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
		TextView tv = (TextView) findViewById(R.id.textViewForExpand);
		tv.setText(""+groupPosition+"/"+childPosition+"/"+id);
	    // use groupPosition and childPosition to locate the current item in the adapter
	    return true;
	}

	public void showAttraction(View view) {
		Intent intent = new Intent(this, EachAttractionActivity.class);
		startActivity(intent);
	}	
}

