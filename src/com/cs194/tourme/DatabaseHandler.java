package com.cs194.tourme;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.util.Log;

public class DatabaseHandler {
	private String result = null;
	private InputStream is = null;
	private StringBuilder sb=null;
	private final String PHPSERVERLOCATION = "http://10.10.66.7/test.php?sql=";

	public JSONArray getDataFromSql(String sql) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		try{
			HttpClient httpclient = new DefaultHttpClient();
			//"\\s" same as " "
			HttpPost httppost = new HttpPost(PHPSERVERLOCATION + sql.replaceAll("\\s", "+"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");

			Log.d("DBHANDLER", sb.toString());

			String line="0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result=sb.toString();
			
			
			Log.d("DBHANDLER", sb.toString());
			return new JSONArray(result);
		}catch(Exception e){
			return null;
		}
	}
}
