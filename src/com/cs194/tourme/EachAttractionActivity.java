package com.cs194.tourme;

import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
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
		
		Log.d("abc", "select uri, description from Picture where attraction_id = (select id from Attraction " +
				"where name = \"" + AttractionsActivity.attractionName + "\") ");
		
		
		JSONArray AttractionDetail = dbHandler.getDataFromSql("select uri, description from Picture where attraction_id = (select id from Attraction where name = '" + AttractionsActivity.attractionName + "') ");

		Log.d("eachattraction", AttractionsActivity.attractionName);
		try {
			pictureUri = AttractionDetail.getJSONObject(0).getString("uri");
			description = AttractionDetail.getJSONObject(0).getString("description");
		} catch(JSONException e1){
			Log.d("Json error", e1.toString());
			Toast.makeText(getBaseContext(), "JSONException Has Occured" +
					" in Attractions Activity" ,Toast.LENGTH_LONG).show();
			pictureUri = "http://thegarageblog.com/garage/wp-content/uploads/noimageavailable.jpg";
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


}
