package com.cis350.sleeptracker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	protected static final String MAIN = "main";
	protected static final String IS_ASLEEP = "is_asleep";
	private static final String LAST_LAUNCH = "last_launch";
	private static final String TIP_POSITION = "tip_position";
	private static final String RECENT_SLEEP_TIME = "recent_sleep_time";
	private static final String IS_NAP = "is_nap";

	private SharedPreferences mPreferences;
	private LinearLayout mMainLinearLayout;
	private Button mSleepWakeButton;
	private SleepLogHelper mSleepLogHelper;

	private static MediaPlayer mPodcastPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SleepTrackerApplication applicationContext = ((SleepTrackerApplication) this.getApplicationContext());
		setContentView(R.layout.activity_main);
		applicationContext.customizeActionBar(this);

		mPreferences = getSharedPreferences(MAIN, MODE_PRIVATE);
		mMainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
		mSleepWakeButton = (Button) findViewById(R.id.sleep_wake_button);

		applicationContext.setColorScheme(mMainLinearLayout);
		if (!mPreferences.getBoolean(IS_ASLEEP, false)) {
			mSleepWakeButton.setText(getResources().getString(R.string.go_to_sleep));
		} else {
			mSleepWakeButton.setText(getResources().getString(R.string.wake_up));
		}
		mSleepLogHelper = new SleepLogHelper(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		String[] tips = getResources().getString(R.string.tips).split(":");
		TextView tip = (TextView) findViewById(R.id.tip);
		tip.setText(getTip(tips));
	}

	/*
	 * Takes in an array of tips and returns which one to display. A different tip
	 * is displayed each day. So if the last date recorded is not the current
	 * date, the next tip in the array should be returned. Otherwise, the tip at
	 * the saved position of the array should be returned.
	 */

	/*
	 * I guess we need to modified this, maybe the data structure of the tips?
	 * Since we should customize user's tips based on their records. Maybe
	 * hashmap? keys would be excuses and value would be the corresponding
	 * String[]? so that we could pick corresponding tips from different arrays
	 * based on the excuse the user gave last day, or one of the excuses if
	 * several were given? While if so, it may be relatively hard to keep several
	 * TIP_POSITION as what was doing below, maybe just random the index of tips
	 * we pick from the array? Just personal thinking:)
	 */

	private String getTip(String[] tips) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		int date = cal.get(Calendar.DAY_OF_MONTH);
		int lastDate = mPreferences.getInt(LAST_LAUNCH, -1);
		int position = mPreferences.getInt(TIP_POSITION, -1);
		if (date != lastDate) {
			position += 1;
			if (position >= tips.length) {
				position = 0;
			}
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putInt(LAST_LAUNCH, date);
			editor.putInt(TIP_POSITION, position);
			editor.commit();
		}
		return tips[position];
	}

	private void onClickSleepOrWakeUpdatePreferencesAndInterface(){
		SharedPreferences.Editor editor = mPreferences.edit();
		Boolean wasAsleep = mPreferences.getBoolean(IS_ASLEEP, false);
		int newBackgroundColor = wasAsleep ? R.color.background_color_awake : R.color.background_color;
		int newSleepWakeButtonString = wasAsleep ? R.string.go_to_sleep : R.string.wake_up;

		if(wasAsleep) {
			editor.putLong(RECENT_SLEEP_TIME, System.currentTimeMillis());
		}
		editor.putBoolean(IS_ASLEEP, !wasAsleep);
		editor.commit();
		mMainLinearLayout.setBackgroundColor(getResources().getColor(newBackgroundColor));
		mSleepWakeButton.setText(getResources().getString(newSleepWakeButtonString));
	}

	/*
	 * Method called when the large button is clicked to indicate going to sleep
	 * or waking up. Method changes background color and button text. When going
	 * to sleep, it also records the time and calls a method to prompt dialogs.
	 * When waking up, it also inserts a new sleep log into the local database,
	 * kills the podcast, and starts the LogActivity so that the user can rate and
	 * comment on his/her sleep.
	 */
	public void onClickSleepOrWake(View view) {
		Boolean wasAsleep = mPreferences.getBoolean(IS_ASLEEP, false);
		onClickSleepOrWakeUpdatePreferencesAndInterface();
		if (!wasAsleep) {
			displayNapAndAlertDialogs();
		} else {
			mSleepLogHelper.insertLog(mPreferences.getLong(RECENT_SLEEP_TIME, 0),
					System.currentTimeMillis(), mPreferences.getBoolean(IS_NAP, false));
			if (mPodcastPlayer != null) {
				mPodcastPlayer.stop();
				mPodcastPlayer.release();
			}
			Intent intent = new Intent(this, LogActivity.class);
			intent.putExtra(DataActivity.ITEM_ASLEEP_TIME_LONG,
					mPreferences.getLong(RECENT_SLEEP_TIME, 0));
			startActivity(intent);
		}
	}

	private AlertDialog buildDialog(String title, String message, String positiveMessage,
			DialogInterface.OnClickListener positiveListener, String negativeMessage,
			DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(title);
		dialogBuilder.setMessage(message);
		dialogBuilder.setPositiveButton(positiveMessage, positiveListener);
		dialogBuilder.setNegativeButton(negativeMessage, negativeListener);
		return dialogBuilder.create();
	}

	/*
	 * Method displays two consecutive dialogs. The first prompts the user to pick
	 * whether he/she is going to sleep for the night or taking a nap. The second
	 * asks the user whether he/she wants to listen to the podcast while falling
	 * asleep.
	 */
	private void displayNapAndAlertDialogs() {
		final AlertDialog podcastAlertDialog = buildDialog(getResources().getString(R.string.podcast_dialog_title),
				getResources().getString(R.string.podcast_dialog_title),
				"Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						mPodcastPlayer = MediaPlayer.create(MainActivity.this, R.raw.shs_podcast);
						mPodcastPlayer.start();
						dialog.dismiss();
					}
				},
				"No",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		final AlertDialog napAlertDialog = buildDialog(getResources().getString(R.string.nap_dialog_title),
				getResources().getString(R.string.nap_dialog_message),
				"Sleep",
				new napDialogOnClickListener(podcastAlertDialog, false),
				"Nap",
				new napDialogOnClickListener(podcastAlertDialog, true));

		napAlertDialog.show();
	}

	private class napDialogOnClickListener implements DialogInterface.OnClickListener {
		Boolean willNap;
		AlertDialog podcastAlertDialog;

		public napDialogOnClickListener(AlertDialog podcastAlertDialog,
				Boolean willNap) {
			super();
			this.willNap = willNap;
			this.podcastAlertDialog = podcastAlertDialog;
		}

		@Override
		public void onClick(DialogInterface dialog, int id) {
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putBoolean(IS_NAP, willNap);
			editor.commit();
			podcastAlertDialog.show();
		}
	}

	public void onClickData(View view) {
		Intent intent = new Intent(this, DataActivity.class);
		startActivity(intent);
	}

	public void onClickGraph(View view) {
		Intent intent = new Intent(this, ChartActivity.class);
		startActivity(intent);
	}

	public LinearLayout getLayout() {
		return mMainLinearLayout;
	}
}
