package com.cs194.tourme;

import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EachAttractionActivity extends LocalizedActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		String pictureUri = "";
		String description = "";

		super.onCreate(savedInstanceState);
		setContentView(R.layout.eachattraction);


		//		Log.d("eachattraction debug", "" + AttractionsActivity.cityId);
		//		Log.d("eachattraction debug", "" + AttractionsActivity.attractionId);

		DatabaseHandler dbHandler = new DatabaseHandler ();
		//		JSONArray AttractionDetail = dbHandler.getDataFromSql("select p.uri, p.description from Picture p, Attraction a where a.name = " + 
		//				AttractionsActivity.attractionName);
		//		JSONArray AttractionDetail = dbHandler.getDataFromSql("select uri, description from Picture, where attraction_id = " + 
		//				"where id from Attraction where id = \"" + AttractionsActivity.attractionName + "\"");
		Log.d("ABC", AttractionRouteActivity.poiName);
		//Log.d("abc", "select uri, description from Picture where attraction_id = (select id from Attraction " +
		//		"where name = \"" + AttractionsActivity.attractionName + "\") ");

		//save for later
		//JSONArray AttractionDetail = dbHandler.getDataFromSql("select uri, description from Picture where attraction_id = (select id from Attraction where name = '" + AttractionsActivity.attractionName + "') ");

		JSONArray AttractionDetail = dbHandler.getDataFromSql("select p.uri, p.description from Picture p where p.poi_id = " +
				"(select id from POI where POI.name = '" + AttractionRouteActivity.poiName + "')");

		Log.d("sqlquery", "select p.uri, p.description from Picture p where p.poi_id = " +
				"(select id from POI where POI.name = '" + AttractionRouteActivity.poiName + "')");
	
		//debug
		try{
			Log.d("eachattraction", AttractionDetail.getJSONObject(0).getString("uri"));
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
		try {
			pictureUri = AttractionDetail.getJSONObject(0).getString("uri");	
			description = AttractionDetail.getJSONObject(0).getString("description");
		} catch(JSONException e1){
			Log.d("Json error", e1.toString());
			Toast.makeText(getBaseContext(), "JSONException Has Occured" +
					" in Attractions Activity" ,Toast.LENGTH_LONG).show();
			// no need for below? taken care as default value for this column is this
			//			pictureUri = "http://thegarageblog.com/garage/wp-content/uploads/noimageavailable.jpg";
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		ImageView webView = (ImageView) findViewById(R.id.imageView1);
		//take care when Drawable is null
		Drawable drawable = LoadImageFromWeb (pictureUri);

		webView.setImageDrawable(drawable);


		TextView textView = (TextView) findViewById(R.id.textViewAttractionDescription);
		textView.setText (description); 

	}


	private Drawable LoadImageFromWeb(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (Exception e) {
			System.out.println("Error loading image " + e.getMessage());
			return null;
		}
	}

	public void buttonAudioOnClick(View v) {

		MediaPlayer mp = MediaPlayer.create(this.getBaseContext(), R.raw.chinatown);  

		mp.start();

	}

	public void buttonTakePictureOnClick(View v) {
		Intent intent = new Intent (this, FileUploaderActivity.class);
		startActivity (intent);
	}

}
