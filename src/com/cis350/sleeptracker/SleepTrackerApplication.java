/**
 * 
 */
package com.cis350.sleeptracker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/** @author Michael Collis
 * @version 20131010 */
public class SleepTrackerApplication extends Application {

	public void setColorScheme(View view) {
		SharedPreferences mPreferences = getSharedPreferences(MainActivity.MAIN,
				MODE_PRIVATE);
		if (!mPreferences.getBoolean(MainActivity.IS_ASLEEP, false)) {
			view.setBackgroundColor(getResources().getColor(
					R.color.background_color_awake));
		} else {
			view.setBackgroundColor(getResources().getColor(R.color.background_color));
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
}
