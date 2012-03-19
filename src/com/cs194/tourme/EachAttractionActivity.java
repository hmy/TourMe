package com.cs194.tourme;

import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
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


		DatabaseHandler dbHandler = new DatabaseHandler ();
		JSONArray AttractionDetail = dbHandler.getDataFromSql("select p.uri, p.description from Picture p, Attraction a where a.city_id = " + 
				AttractionsActivity.cityId + " AND p.attraction_id = " + AttractionsActivity.attractionId );
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
		/*
				("San Francisco's Chinatown (Chinese: 唐人街; Mandarin Pinyin: tángrénjiē; Jyutping: tong4 jan4 gaai1) is the " +
				"oldest Chinatown in North America and the largest Chinese community outside Asia. Since its establishment in 1848, " +
				"it has been highly important and influential in the history and culture of ethnic Chinese immigrants to the United States " +
				"and North America. Chinatown is an active enclave that continues to retain its own customs, languages, places of worship, " +
				"social clubs, and identity. Popularly known as a city-within-a-city, it has developed its own government, traditions, over " +
				"300 restaurants, and as many shops.");
				textView.setText("The grizzly bear (Ursus arctos horribilis), also known as the silvertip bear, " +
				"the grizzly, or the North American brown bear, is a subspecies of brown bear (Ursus arctos) that " +
				"generally lives in the uplands of western North America. This subspecies is thought to descend from" +
				" Ussuri brown bears which crossed to Alaska from eastern Russia 100,000 years ago, though they did not" +
				" move south until 13,000 years ago.");
		 */
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
}
