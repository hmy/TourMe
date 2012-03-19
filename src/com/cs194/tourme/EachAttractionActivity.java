package com.cs194.tourme;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class EachAttractionActivity extends LocalizedActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eachattraction);
		
	//	ImageView webView = (ImageView) findViewById(R.id.imageView1);
	//	ImageView.loadUrl("https://encrypted-tbn2.google.com/images?q=tbn:ANd9GcTpLknb3YAya1o4vpmtjmqEUXwlc8iEUrX7L_NFnpVassSeyw7Y8");

		TextView textView = (TextView) findViewById(R.id.textViewAttractionDescription);
		textView.setText("The grizzly bear (Ursus arctos horribilis), also known as the silvertip bear, the grizzly, or the North American brown bear, is a subspecies of brown bear (Ursus arctos) that generally lives in the uplands of western North America. This subspecies is thought to descend from Ussuri brown bears which crossed to Alaska from eastern Russia 100,000 years ago, though they did not move south until 13,000 years ago.");
	}


}
