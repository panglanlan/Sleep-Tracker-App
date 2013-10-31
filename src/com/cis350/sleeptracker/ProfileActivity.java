package com.cis350.sleeptracker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/** @author Michael Collis
 * @version 20131020 */
public class ProfileActivity extends SleepTrackerActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		((SleepTrackerApplication) this.getApplicationContext())
				.customizeActionBar(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void customizeActionBar(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			activity.getActionBar().setDisplayShowCustomEnabled(true);
			activity.getActionBar().setDisplayShowTitleEnabled(false);
			LayoutInflater inflator = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflator.inflate(R.layout.view_action_bar, null);
			((TextView) v.findViewById(R.id.title)).setText("");
			activity.getActionBar().setCustomView(v);
		}
	}
}
