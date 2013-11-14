package com.cis350.sleeptracker;

import android.os.Bundle;
import android.view.View;

/** @author Michael Collis
 * @version 20131020 */
public class ProfileActivity extends SleepTrackerActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		/*
		 * ((SleepTrackerApplication) this.getApplicationContext())
		 * .customizeActionBar(this);
		 */
	}

	public void onClickSave(View view) {
		((ProfileFragment) getSupportFragmentManager().findFragmentById(
				R.id.profileListFragment)).saveProfile();
		finish();
	}

	public void onClickCancel(View view) {
		finish();
	}

}
