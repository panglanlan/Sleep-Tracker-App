package com.cis350.sleeptracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cis350.sleeptracker.database.SleepLogHelper;
import com.cis350.sleeptracker.database.TipsDatabase;

public class MainActivity extends SleepTrackerActivity {
	static final String MAIN = "main";
	static final String IS_ASLEEP = "is_asleep";
	private static final String LAST_LAUNCH = "last_launch";
	private static final String TIP_POSITION = "tip_position";
	private static final String RECENT_SLEEP_TIME = "recent_sleep_time";
	private static final String IS_NAP = "is_nap";
	

	private SharedPreferences mPreferences;
	private LinearLayout mMainLinearLayout;
	private Button mSleepWakeButton;
	private SleepLogHelper mSleepLogHelper;
	private TipsDatabase mTipsDatabase;

	private static MediaPlayer mPodcastPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SleepTrackerApplication applicationContext = ((SleepTrackerApplication) this
				.getApplicationContext());
		setContentView(R.layout.activity_main);
		applicationContext.customizeActionBar(this);

		mPreferences = getSharedPreferences(MAIN, MODE_PRIVATE);
		mMainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
		mSleepWakeButton = (Button) findViewById(R.id.sleep_wake_button);

		// applicationContext.setColorScheme(mMainLinearLayout);
		if (!mPreferences.getBoolean(IS_ASLEEP, false)) {
			mSleepWakeButton.setText(getResources().getString(R.string.go_to_sleep));
		} else {
			mSleepWakeButton.setText(getResources().getString(R.string.wake_up));
		}
		mSleepLogHelper = new SleepLogHelper(this);

		mTipsDatabase = new TipsDatabase(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		TextView tip = (TextView) findViewById(R.id.tip);
		tip.setText(getTip());
	}
	
	// Public for testability
	public ArrayList<String> getFilteredTips(){
		return mTipsDatabase.getFilteredTips(new UserHabits(mPreferences));
	}

	private String getTip() {
		ArrayList<String> filteredTips = getFilteredTips();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		int date = cal.get(Calendar.DAY_OF_MONTH);
		int lastDate = mPreferences.getInt(LAST_LAUNCH, -1);
		int position = mPreferences.getInt(TIP_POSITION, -1);
		if (date != lastDate) {
			position += 1;
			if (position >= filteredTips.size()) {
				position = 0;
			}
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putInt(LAST_LAUNCH, date);
			editor.putInt(TIP_POSITION, position);
			editor.commit();
		}
		return filteredTips.get(position);
	}

	private void onClickSleepOrWakeUpdatePreferencesAndInterface() {
		SharedPreferences.Editor editor = mPreferences.edit();
		Boolean wasAsleep = mPreferences.getBoolean(IS_ASLEEP, false);
		int newBackgroundColor = wasAsleep ? R.color.background_color_awake
				: R.color.background_color;
		int newSleepWakeButtonString = wasAsleep ? R.string.go_to_sleep
				: R.string.wake_up;

		if (!wasAsleep) {
			editor.putLong(RECENT_SLEEP_TIME, System.currentTimeMillis());
		}
		editor.putBoolean(IS_ASLEEP, !wasAsleep);
		editor.commit();
		mMainLinearLayout.setBackgroundColor(getResources().getColor(
				newBackgroundColor));
		mSleepWakeButton
				.setText(getResources().getString(newSleepWakeButtonString));
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
			displayNapAndConcentrationAndAlertDialogs();
		} else {
			mSleepLogHelper.insertLog(mPreferences.getLong(RECENT_SLEEP_TIME, 0),
					System.currentTimeMillis(), mPreferences.getBoolean(IS_NAP, false));
			if (mPodcastPlayer != null) {
				mPodcastPlayer.stop();
				mPodcastPlayer.release();
			}
			Intent intent = new Intent(this, LogActivity.class);
			intent.putExtra(SleepLogHelper.ITEM_ASLEEP_TIME_LONG,
					mPreferences.getLong(RECENT_SLEEP_TIME, 0));
			startActivity(intent);
		}
	}

	private AlertDialog buildDialog(String title, String message,
			String positiveMessage, DialogInterface.OnClickListener positiveListener,
			String negativeMessage, DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(title);
		dialogBuilder.setMessage(message);
		dialogBuilder.setPositiveButton(positiveMessage, positiveListener);
		dialogBuilder.setNegativeButton(negativeMessage, negativeListener);
		return dialogBuilder.create();
	}

	private AlertDialog buildconcentrationDialog(DialogInterface.OnClickListener concentrationListener ){
		
		final AlertDialog.Builder concentrationAlertDialog = new AlertDialog.Builder(this);
		concentrationAlertDialog.setTitle(R.string.concentration_dialog_title);
		concentrationAlertDialog.setItems(R.array.concentration_array, concentrationListener );
	    return concentrationAlertDialog.create();
	} 

	// Parameters have to be 24-hour format
	private boolean checkBetweenHours(int now, int begin, int end) {
		if(begin < end){
			return now >= begin && now <= end;
		} else {
			return now >= begin || now <= end;
		}
	}

	private void displayNapAndConcentrationAndAlertDialogs() {
		final int napBeginHour = 10; // 24-hour format
		final int napStopHour = 16;
		final int sleepBeginHour = 20;
		final int sleepStopHour = 5;
		Time now = new Time();
		now.setToNow();
		int currentHour = now.hour;

		final AlertDialog podcastAlertDialog = buildDialog(getResources()
				.getString(R.string.podcast_dialog_title),
				getResources().getString(R.string.podcast_dialog_title), "Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						mPodcastPlayer = MediaPlayer.create(MainActivity.this,
								R.raw.shs_podcast);
						mPodcastPlayer.start();
						dialog.dismiss();
					}
				}, "No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		
		final AlertDialog concentrationDialog=buildconcentrationDialog(new concentrationOnClickListener(podcastAlertDialog));

		// Try to infer if sleep or nap
		if(checkBetweenHours(currentHour, napBeginHour, napStopHour)){
			changeIsNapPreference(true);
			podcastAlertDialog.show();
		} else if(checkBetweenHours(currentHour, sleepBeginHour, sleepStopHour)){
			changeIsNapPreference(false);
			concentrationDialog.show();
		} else{
			final AlertDialog napAlertDialog = buildDialog(
					getResources().getString(R.string.nap_dialog_title), getResources()
					.getString(R.string.nap_dialog_message), "Sleep",
					new napDialogOnClickListener(concentrationDialog,false), "Nap",
					new napDialogOnClickListener(podcastAlertDialog, true));

			napAlertDialog.show();
		}
	}

	private void changeIsNapPreference(boolean willNap){
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putBoolean(IS_NAP, willNap);
		editor.commit();
	}

	private class napDialogOnClickListener implements
			DialogInterface.OnClickListener {
		Boolean willNap;
		AlertDialog nextAlertDialog;

		public napDialogOnClickListener(AlertDialog nextAlertDialog,
				Boolean willNap) {
			super();
			this.willNap = willNap;
			this.nextAlertDialog = nextAlertDialog;
		}

		@Override
		public void onClick(DialogInterface dialog, int id) {
			changeIsNapPreference(willNap);
			nextAlertDialog.show();
		}
	}

	private class concentrationOnClickListener implements
		DialogInterface.OnClickListener {
		AlertDialog podcastAlertDialog;

		public concentrationOnClickListener(AlertDialog podcastAlertDialog) {
			super();
			this.podcastAlertDialog = podcastAlertDialog;;
		}

		@Override
		public void onClick(DialogInterface dialog, int id) {

			String[] concentration_array=getResources().getStringArray(R.array.concentration_array);
			String concentration=concentration_array[id];
			mSleepLogHelper.updateConcentration(concentration);
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
