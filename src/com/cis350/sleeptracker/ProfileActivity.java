package com.cis350.sleeptracker;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/** @author Michael Collis
 * @version 20131020 */
public class ProfileActivity extends SleepTrackerActivity {

	private Button mSave;
	private Button mCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		((SleepTrackerApplication) this.getApplicationContext())
				.customizeActionBar(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mSave = (Button) findViewById(R.id.profileSaveButton);
		mSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((ProfileFragment) ProfileActivity.this.getSupportFragmentManager()
						.findFragmentById(R.layout.fragment_profile_questions))
						.saveProfile();
				finish();
			}
		});

		mCancel = (Button) findViewById(R.id.profileCancelButton);
		mCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
