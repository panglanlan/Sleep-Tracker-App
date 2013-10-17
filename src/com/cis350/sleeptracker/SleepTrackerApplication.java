/**
 * 
 */
package com.cis350.sleeptracker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

/** @author Michael Collis
 * @version 20131010 */
public class SleepTrackerApplication extends Application implements
		SensorEventListener {

	private static final float MIN_BRIGHTNESS = 1F;

	/*
	 * private SharedPreferences mPreferences =
	 * getSharedPreferences(MainActivity.MAIN, MODE_PRIVATE); //lead to a
	 * nullpointer exception when the application runs. //
	 */
	public void setColorScheme(View view) { //

		SharedPreferences mPreferences = getSharedPreferences(MainActivity.MAIN,
				MODE_PRIVATE);
		if (mPreferences != null
				&& !mPreferences.getBoolean(MainActivity.IS_ASLEEP, false)) {
			view.setBackgroundColor(getResources().getColor(
					R.color.background_color_awake));
		} else {
			view.setBackgroundColor(getResources().getColor(R.color.background_color));
		}
	}

	public void setColorScheme(XYMultipleSeriesRenderer renderer) {
		SharedPreferences mPreferences = getSharedPreferences(MainActivity.MAIN,
				MODE_PRIVATE);
		if (!mPreferences.getBoolean(MainActivity.IS_ASLEEP, false)) {
			renderer.setMarginsColor(getResources().getColor(
					R.color.background_color_awake));
		} else {
			renderer.setMarginsColor(getResources()
					.getColor(R.color.background_color));
		}
	}

	public void customizeActionBar(Activity activity) {
		activity.getActionBar().setDisplayShowCustomEnabled(true);
		activity.getActionBar().setDisplayShowTitleEnabled(false);
		LayoutInflater inflator = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.view_action_bar, null);
		((TextView) v.findViewById(R.id.title)).setText("");
		activity.getActionBar().setCustomView(v);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			/*
			 * WindowManager.LayoutParams layout =
			 * getApplicationContext().getActivity().getWindow().getAttributes();
			 * layout.screenBrightness = event.values[0] < MIN_BRIGHTNESS ?
			 * MIN_BRIGHTNESS : event.values[0] ; getWindow().setAttributes(layout);
			 */
		}

	}

}
