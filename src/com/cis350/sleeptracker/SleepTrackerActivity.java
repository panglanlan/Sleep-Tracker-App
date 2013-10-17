package com.cis350.sleeptracker;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;

public class SleepTrackerActivity extends Activity implements
		SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mLight;

	private static final float MIN_BRIGHTNESS = 1F;

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

}
