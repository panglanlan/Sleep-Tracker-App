/**
 * 
 */
package com.cis350.sleeptracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.view.View;

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

}
