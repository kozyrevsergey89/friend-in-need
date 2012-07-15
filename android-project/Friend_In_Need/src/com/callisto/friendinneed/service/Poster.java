package com.callisto.friendinneed.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class Poster {

	private HttpClient httpClient;
	private HttpPost httpPost;
	private String data;
	private double longtitude, latitude;
	
	public Poster() {
		httpClient = new DefaultHttpClient();
		httpPost = new HttpPost("http://172.29.8.56:8080");
	}
	
	public void postData() {
		try {
			List<NameValuePair> values = new ArrayList<NameValuePair>(1);
			values.add(new BasicNameValuePair("data", data));
			httpPost.setEntity(new UrlEncodedFormEntity(values));
			HttpResponse response = httpClient.execute(httpPost);
			Log.i("Accelerometer:", response.getStatusLine().toString());
		}catch (ClientProtocolException ex) {
			Log.i("ERROR", ex.getMessage());
		}catch(IOException ex) {
			Log.i("ERROR", ex.getMessage());
		}
	}
	
	public void setData(final String string) {
		this.data = string;
	}
	
	public void getGPSCoords(final Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		longtitude = location.getLongitude();
		latitude = location.getLatitude();
	}
	
	public double getLongtitude() {
		return longtitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public String getGoogleMapString() {
		return "http://maps.google.com/maps/api/staticmap?markers="
				.concat(String.valueOf(latitude) + "," + String.valueOf(longtitude))
				.concat("&zoom=14&size=400x400&sensor=false");
	}
	
	/*
	public File setJsonIntoFile() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File file = new File("/sdcard/coords.txt");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					Log.i("ERROR", e.getMessage());
				}
			}
			
			
			
		}
	}
	*/
	
}
