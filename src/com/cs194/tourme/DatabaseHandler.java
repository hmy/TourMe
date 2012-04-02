package com.cs194.tourme;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;

import android.util.Log;

public class DatabaseHandler {
	private String result = null;
	private InputStream is = null;
	private StringBuilder sb=null;
	private final String PHPSERVERLOCATION = "http://10.10.65.50/test.php?sql=";

	public JSONArray getDataFromSql(String sql) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		try{
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 2000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 2500;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			//"\\s" same as " "

			//caret signs need this....
			String lessThan = URLEncoder.encode("<", "UTF-8");
			String greaterThan = URLEncoder.encode(">", "UTF-8");
			String query = sql.replaceAll("\\s", "%20").replaceAll("<", lessThan).replaceAll(">", greaterThan);

			HttpPost httppost = new HttpPost(PHPSERVERLOCATION + query);
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
		} catch(Exception e){
			Log.d("DBHANDLER ERROR", "caught Exception e, Most likely Query returned a null");
			e.printStackTrace();
			return new JSONArray();
		}
	}
}
