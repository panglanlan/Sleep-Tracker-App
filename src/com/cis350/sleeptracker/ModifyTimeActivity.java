package com.cis350.sleeptracker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.cis350.sleeptracker.database.SleepLogHelper;

public class ModifyTimeActivity extends SleepTrackerActivity {
	private TimePicker mTimePicker;
	private DatePicker mDatePicker;
	private SleepLogHelper mSleepLogHelper;
	private boolean mIsSleepTime;
	private long mAsleepTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_time);
		
		mSleepLogHelper = new SleepLogHelper(this);
		mIsSleepTime = false;
		mAsleepTime = getIntent().getLongExtra(SleepLogHelper.ASLEEP_TIME, 0);
		long originalTime = getIntent().getLongExtra(SleepLogHelper.AWAKE_TIME, -1);
		if (originalTime == -1) {
			mIsSleepTime = true;
			originalTime = mAsleepTime;
		}
		setTimePicker(originalTime);
		
	}

	private void setTimePicker(long orgtime) {
		mTimePicker = (TimePicker) findViewById(R.id.time_picker);
		mDatePicker = (DatePicker) findViewById(R.id.date_picker);

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(orgtime);
		mTimePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		mTimePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
		mDatePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH), null);
	}

	public void onClickSave(View view) {
		GregorianCalendar cal = new GregorianCalendar(mDatePicker.getYear(),
				mDatePicker.getMonth(), mDatePicker.getDayOfMonth(),
				mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
		long time = cal.getTimeInMillis();
		
		if (mIsSleepTime) {
			long awakeTime = (Long)mSleepLogHelper.queryLog(mAsleepTime).get("AwakeTime");
			if (time > awakeTime) {
				new AlertDialog.Builder(this)
				.setTitle(R.string.time_error_title)
				.setMessage(R.string.time_error_message)
				.setNegativeButton(R.string.time_error_button,
						new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface, int i){
					}        
				}).show();    
			}
			else {
				updateTime(mIsSleepTime, time);
				finish();
			}
		}
		else {
			if (mAsleepTime > time) {
				new AlertDialog.Builder(this)
				.setTitle(R.string.time_error_title)
				.setMessage(R.string.time_error_message)
				.setPositiveButton(R.string.time_error_button,
						new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface, int i){
					}        
				}).show();    
			}
			else {
				updateTime(mIsSleepTime, time);
				finish();
			}
		}
	}

	private void updateTime(boolean isSlptime, long time) {
		Intent intent = new Intent();
		if (isSlptime) {
			mSleepLogHelper.updateAsleepTime(mAsleepTime, time);
			intent.putExtra(SleepLogHelper.ASLEEP_TIME, time);
		} else {
			mSleepLogHelper.updateAwakeTime(mAsleepTime, time);
			intent.putExtra(SleepLogHelper.AWAKE_TIME, time);
		}
		setResult(RESULT_OK, intent);
	}

	// For testing purposes
	public void setIsSleepTime(boolean b) {
		mIsSleepTime = b;
	}
}
