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
	private final double G_POINT = 2 * 9.8;
	private int counter = 0;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		Log.i("Accelerometer:", "onCreate");
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
		sensorManager.unregisterListener(this);
		Log.i("Accelerometer:", "onDestroy");
		super.onDestroy();
	}
	
	private class JoltCalculator implements Runnable {

		@Override
		public void run() {
			if(counter == 3) {
				Log.i("Accelerometer:", "--------- THREE SECONDS LEFT ----------");
				counter = 0;
			}
			if(checkForJolt()) {
				Log.i("Accelerometer:", "--------- HUSTON WE GOT A PROBLEM ----------");
				Log.i("Accelerometer:", "JOLT: "+ "X:" + x + " Y: " + y + " Z: " + z + " - " + checkForJolt());
				Log.i("Accelerometer:", "--------- HUSTON WE GOT A PROBLEM ----------");
				counter = 0;
			}
			Log.i("Accelerometer:", "TEST: "+ "X:" + x + " Y: " + y + " Z: " + z + " - " + checkForJolt());
			handler.removeCallbacks(this);
			handler.postDelayed(this, 100);
			counter++;
		}
	}

	private boolean checkForJolt() {
		float sum = (x * y) + (y * y) + (z * z); 
		double checkVar = Math.sqrt(Double.parseDouble(Float.toString(sum)));
		if(checkVar > G_POINT) { return true; }
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
