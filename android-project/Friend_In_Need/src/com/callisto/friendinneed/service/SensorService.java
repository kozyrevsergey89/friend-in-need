package com.callisto.friendinneed.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SensorService extends Service implements SensorEventListener{

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Handler handler;
	private float x, y, z;
	private JoltCalculator joltCalculator;
	private final double G_POINT = 1.3 * 9.8;
	private int counter = 0;
	private String[] seconds;
	private boolean jolt = false;
	private int checker = 0;
	private Poster poster;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		Log.i("Accelerometer:", "onCreate");
		seconds = new String[7];
		poster = new Poster();
	}
	
	
	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		handler = new Handler();
		joltCalculator = new JoltCalculator();
		Log.i("Accelerometer:", "onStartCommand");
		handler.removeCallbacks(joltCalculator);
		handler.postDelayed(joltCalculator, 100);
		return START_STICKY;
	}

	@Override
	public void onAccuracyChanged(final Sensor sensor, final int accuracy) { /* nothing */ }
	
	@Override
	public void onSensorChanged(final SensorEvent event) {
		x = event.values[0];
		y = event.values[1];
		z = event.values[2];
	}
	
	@Override
	public void onDestroy() {
		shutDown();
		Log.i("Accelerometer:", "onDestroy");
		super.onDestroy();
	}
	
	private String getDataString() {
		String header = "{ data: \"[ ";
		String footer = " ]\"}";
		String secondsString = "";
		for (int i = 0; i < seconds.length; i++) {
			
			if (seconds[i] != null && seconds[i].contains("null")) {
				continue;
			}
			
			if (i != seconds.length - 1) {
				secondsString += seconds[i] + ",";
			}
			secondsString += seconds[i];
		}
		return header + secondsString + footer;
	}
	
	
	private class JoltCalculator implements Runnable {

		@Override
		public void run() {
			
			seconds[counter] = x + ", " + y + ", " + z;
			
			if (!jolt) {
				if (checkForJolt()) {
					for(String string : seconds) {
						Log.i("Accelerometer:", "JOLT: "+ string);
					}
					counter = 0;
					jolt = true;
				}
				
			} else {
				if(!checkLatesSeconds()) { checker++; }
				Log.i("Accelerometer:", "AFTER_JOLT: "+ checkLatesSeconds() + " -checker: " + checker);
				if (counter == 6 && checker == 0) { 
					jolt = false;
					Log.i("Accelerometer:", "Send json to server");
					Log.i("Accelerometer:", getDataString());
					poster.setData(getDataString());
					//poster.postData();
					getGpsLocation();
					Log.i("Accelerometer:", poster.getGoogleMapString());
					
					//get gps coords
					//send xml to sms service
					//set short link
					checker = 0;
			    }
			}
			
			if (counter == 6) { 
				counter = 0;
				seconds = new String[7];
				jolt = false;
		    }
			
			handler.removeCallbacks(this);
			handler.postDelayed(this, 500);
			counter++;
		}
	}
	
	private void getGpsLocation() {
		poster.getGPSCoords(this);
	}
	
	private boolean checkForJolt() {
		float sum = (x * y) + (y * y) + (z * z); 
		double checkVar = Math.sqrt(Double.parseDouble(Float.toString(sum)));
		if(checkVar > G_POINT) { return true; }
		return false;
	}
	
	private boolean checkLatesSeconds() {
		float sum = (x * y) + (y * y) + (z * z);
		double checkVar = Math.sqrt(Double.parseDouble(Float.toString(sum)));
		if (checkVar > 9 || checkVar < 10.5) {
			return true;
		}
		return false;
	}
	
	private void shutDown() {
		Log.i("Accelerometer:", "shut down");
		handler.removeCallbacks(joltCalculator);
		sensorManager.unregisterListener(this);
		stopSelf();
	}
	
	@Override
	public IBinder onBind(final Intent intent) { return null; }
	
	
	
}
