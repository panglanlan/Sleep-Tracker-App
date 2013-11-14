
package com.cis350.sleeptracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.cis350.sleeptracker.database.SleepLogHelper;
import com.cis350.sleeptracker.database.SleepTrackerDatabase;


public class LogActivity extends SleepTrackerActivity {
	private static final int[] EXCUSE_CHECKBOXES = { R.id.excuse_checkbox1,
			R.id.excuse_checkbox2, R.id.excuse_checkbox3, R.id.excuse_checkbox4,
			R.id.excuse_checkbox5, R.id.excuse_checkbox6 };
	private static final int[] EXCUSE_IDS = { R.id.excuse1, R.id.excuse2,
			R.id.excuse3, R.id.excuse4, R.id.excuse5, R.id.excuse6 };
	private static final int EDITSLEEPCLK = 1;
	private static final int EDITWAKECLK = 2;
	private static final String CONCENTRATION="concentration";
	
	private SharedPreferences mPreferences;
	private LinearLayout mLinearLayout;
	private long mAsleepTime;
	private long mAwakeTime;
	private SleepLogHelper mSleepLogHelper;
	private SimpleDateFormat mSimpleDateFormat;
	private RatingBar mRatingBar;
	private EditText mCommentBox;
	private String mTypeOfSleep;
	//private RatingBar mConcentrationBar;
	private Spinner mconcentration_spinner;
	private static final float rotationX=90;
	
	private void populateSpinners() {
		mconcentration_spinner = (Spinner)findViewById(
				R.id.concentration_spinner);
		ArrayAdapter<CharSequence> concentrationAdapter = ArrayAdapter.createFromResource(this,
		        R.array.concentration_array, android.R.layout.simple_spinner_item);
		concentrationAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mconcentration_spinner.setAdapter(concentrationAdapter);
		
		String s_concentration=(String) mSleepLogHelper.queryLog(mAsleepTime).get(CONCENTRATION);
		if(s_concentration!=null&&!s_concentration.isEmpty()){
			mconcentration_spinner.setSelection(concentrationAdapter.getPosition(s_concentration));
		}
		
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * MainActivity.customizeActionBar(this); %cause a compilation error;
		 */
		// invoking the customizeActionBar method as following:
		SleepTrackerApplication applicationContext = ((SleepTrackerApplication) this
				.getApplicationContext());
		setContentView(R.layout.activity_log);
		applicationContext.customizeActionBar(this);

		mPreferences = getSharedPreferences(MainActivity.MAIN, MODE_PRIVATE);
		mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout);

		if (!mPreferences.getBoolean(MainActivity.IS_ASLEEP, false)) {
			mLinearLayout.setBackgroundColor(getResources().getColor(
					R.color.background_color_awake));
		} else {
			mLinearLayout.setBackgroundColor(getResources().getColor(
					R.color.background_color));
		}
		mAsleepTime = getIntent().getLongExtra(
				SleepLogHelper.ITEM_ASLEEP_TIME_LONG, 0);
		
		mAwakeTime = 0;
		mSleepLogHelper = new SleepLogHelper(this);
		mSimpleDateFormat = new SimpleDateFormat("MMM dd hh:mm a", Locale.US);
		mRatingBar = (RatingBar) findViewById(R.id.rating_bar);
		mCommentBox = (EditText) findViewById(R.id.comment_box);
		//mConcentrationBar=(RatingBar)findViewById(R.id.concentration_bar);
		
		if((Boolean) mSleepLogHelper.queryLog(mAsleepTime).get("wasNap")){
			//mConcentrationBar.setVisibility(View.GONE);
			((TextView)findViewById(R.id.concentration_header)).setVisibility(View.GONE);
			((Spinner)findViewById(R.id.concentration_spinner)).setVisibility(View.GONE);
		}
		else{
			
			this.populateSpinners();
		}
		
		queryLogAndInit();
	}

	private void queryLogAndInit() {
		int rating;
		//int concentration_rating;
		String comments;
		Boolean wasNap;

		mAwakeTime = (Long) mSleepLogHelper.queryLog(mAsleepTime).get("AwakeTime");
		rating = (Integer) mSleepLogHelper.queryLog(mAsleepTime).get("rating");
		comments = (String) mSleepLogHelper.queryLog(mAsleepTime).get("comments");
		wasNap = (Boolean) mSleepLogHelper.queryLog(mAsleepTime).get("wasNap");
	
		mRatingBar.setRating(rating);
		mCommentBox.setText(comments);

		if (wasNap) {
			mTypeOfSleep = getResources().getString(R.string.nap);
		} else {
			this.populateSpinners();
			mTypeOfSleep = getResources().getString(R.string.night_sleep);
		}
		
		setExcusesCheckBox();
	}

	private void setExcusesCheckBox() {
		for (int i = 0; i < EXCUSE_CHECKBOXES.length; i++) {
			boolean checked = (Integer) mSleepLogHelper.queryLog(mAsleepTime).get(
					(SleepLogHelper.getExcuses())[i]) > 0;
			CheckBox checkBox = (CheckBox) findViewById(EXCUSE_CHECKBOXES[i]);
			checkBox.setChecked(checked);
		}
	}

	/*
	 * Method formats the asleep time, the awake time, and the total time slept.
	 * It sets this information to be displayed by the corresponding TextViews.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		String fAsleepTime = mSimpleDateFormat.format(new Date(mAsleepTime));
		String fAwakeTime = "-";
		if (mAwakeTime != 0) {
			fAwakeTime = mSimpleDateFormat.format(new Date(mAwakeTime));
		}
		long elapsedTime = TimeUnit.MILLISECONDS.toMinutes(mAwakeTime)
				- TimeUnit.MILLISECONDS.toMinutes(mAsleepTime);
		String totalSleep = String.format(
				Locale.US,
				"%d hours, %d minutes",
				TimeUnit.MINUTES.toHours(elapsedTime),
				elapsedTime
						- TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(elapsedTime)));
		if (elapsedTime < 0) {
			totalSleep = getResources().getString(R.string.pending);
		}
		
		setTimeDisplay(totalSleep, fAsleepTime, fAwakeTime);
	}

	public void onClickSave(View view) {
		
		if(!(Boolean) mSleepLogHelper.queryLog(mAsleepTime).get("wasNap"))
			mSleepLogHelper.updateConcentration(mAsleepTime, (String)mconcentration_spinner.getSelectedItem());
			
		mSleepLogHelper.updateRating(mAsleepTime, (int) mRatingBar.getRating());
		mSleepLogHelper.updateComments(mAsleepTime, mCommentBox.getText()
				.toString());
		boolean[] excuses = new boolean[EXCUSE_CHECKBOXES.length];
		for (int i = 0; i < EXCUSE_CHECKBOXES.length; i++) {
			CheckBox checkBox = (CheckBox) findViewById(EXCUSE_CHECKBOXES[i]);
			excuses[i] = checkBox.isChecked();
		}
		mSleepLogHelper.updateExcuses(mAsleepTime, excuses);
		updatePreferences(excuses);

		finish();
	}

	private void updatePreferences(boolean[] excuses) {
		if (mPreferences != null) {
			Editor mPrefEditor = mPreferences.edit();
			String[] excusesName = SleepTrackerDatabase.getExcuses();
			for(int i = 0; i < excusesName.length; i++){
				if(excuses[i]){
					int oldValue = mPreferences.getInt(excusesName[i], 0);
					if(oldValue < SleepTrackerDatabase.getMaxExcuseValue()){
						mPrefEditor.putInt(excusesName[i], oldValue + 1);
					}
				}
			}
			mPrefEditor.apply();
		}
	}

	public void onClickEditSleep(View view) {
		
		intentModifyTime(EDITSLEEPCLK);
	}

	public void onClickEditWake(View view) {
		
		intentModifyTime(EDITWAKECLK);
	}

	public void onClickExcuse(View view) {
		int id = view.getId();
		for (int i = 0; i < EXCUSE_IDS.length; i++) {
			if (EXCUSE_IDS[i] == id) {
				CheckBox checkBox = (CheckBox) findViewById(EXCUSE_CHECKBOXES[i]);
				checkBox.toggle();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			mAsleepTime = data.getLongExtra(SleepLogHelper.ASLEEP_TIME, mAsleepTime);
		} else if (requestCode == 2 && resultCode == RESULT_OK) {
			mAwakeTime = data.getLongExtra(SleepLogHelper.AWAKE_TIME, mAwakeTime);
		}
	}

	private void intentModifyTime(int whichClicked) {
		Intent intent = new Intent(this, ModifyTimeActivity.class);
		intent.putExtra(SleepLogHelper.ASLEEP_TIME, mAsleepTime);
		if (EDITSLEEPCLK == whichClicked) {
			startActivityForResult(intent, 1);
		} else if (EDITWAKECLK == whichClicked) {
			intent.putExtra(SleepLogHelper.AWAKE_TIME, mAwakeTime);
			startActivityForResult(intent, 2);
		}

	}

	private void setTimeDisplay(String totalSleep, String fAsleepTime,
			String fAwakeTime) {
		TextView totalSleepText = (TextView) findViewById(R.id.total_sleep);
		totalSleepText.setText(mTypeOfSleep + ": " + totalSleep);
		TextView asleepText = (TextView) findViewById(R.id.asleep_time);
		asleepText.setText(fAsleepTime);
		TextView awakeText = (TextView) findViewById(R.id.awake_time);
		awakeText.setText(fAwakeTime);
	}

}
