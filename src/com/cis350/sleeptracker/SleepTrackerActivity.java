package com.cis350.sleeptracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.support.v4.app.FragmentActivity;

public class SleepTrackerActivity extends FragmentActivity implements
		SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mLight;

	private static final float MIN_BRIGHTNESS = SensorManager.LIGHT_FULLMOON;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (mLight != null) {
			WindowManager.LayoutParams layout = getWindow().getAttributes();
			layout.screenBrightness = event.values[0] < MIN_BRIGHTNESS ? MIN_BRIGHTNESS
					: event.values[0];
			Log.d("Light sensor event triggered", "Setting brightness to "
					+ layout.screenBrightness + " lux");
			getWindow().setAttributes(layout);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mLight,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

}
