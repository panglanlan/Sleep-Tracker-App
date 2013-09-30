package com.cis350.sleeptracker.test;

import java.util.GregorianCalendar;

import android.test.ActivityInstrumentationTestCase2;
import com.cis350.sleeptracker.ModifyTimeActivity;
import com.cis350.sleeptracker.SleepLogHelper;

import android.database.Cursor;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Button;


import com.cis350.sleeptracker.R;

public class ModifyTimeActivityTests extends
		ActivityInstrumentationTestCase2<ModifyTimeActivity> {

	public ModifyTimeActivityTests() {
		super(ModifyTimeActivity.class);
		// TODO Auto-generated constructor stub
	}

	private Button saveButton;
	private ModifyTimeActivity activity;
	private TimePicker tp;
	private DatePicker dp;
	private SleepLogHelper mSleepLogHelper;
	protected static final String ASLEEP_TIME = "asleep_time";
	protected static final String AWAKE_TIME = "awake_time";
	
	protected void setUp() throws Exception{
		super.setUp();
		activity = getActivity();
		saveButton = (Button)activity.findViewById(R.id.save_button);
		tp = (TimePicker)activity.findViewById(R.id.time_picker);
		dp = (DatePicker)activity.findViewById(R.id.date_picker);
		mSleepLogHelper = new SleepLogHelper(activity);
	}
	
	public void testEditSleep(){
		mSleepLogHelper.deleteAllEntries();
		activity.setIsSleepTime(true);
		activity.runOnUiThread(new Runnable(){
			public void run(){
				mSleepLogHelper.insertLog(0,100000,false);
				tp.setCurrentHour(10);
				tp.setCurrentMinute(30);
				dp.updateDate(2013, 12, 15);
				saveButton.performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
		GregorianCalendar cal = new GregorianCalendar(dp.getYear(), dp.getMonth(),
				dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
		long time = cal.getTimeInMillis();
		long sleepTime = 0;
		Cursor cursor = mSleepLogHelper.queryLog(time);
		if (cursor != null) {
			cursor.moveToFirst();
			sleepTime = cursor.getLong(cursor.getColumnIndex(ASLEEP_TIME));
		}
		assertEquals(time, sleepTime);
	}
	
	public void testEditWake(){
		mSleepLogHelper.deleteAllEntries();
		activity.setIsSleepTime(false);
		activity.runOnUiThread(new Runnable(){
			public void run(){
				mSleepLogHelper.insertLog(0,100000,false);
				tp.setCurrentHour(22);
				tp.setCurrentMinute(0);
				dp.updateDate(2013, 2, 5);
				saveButton.performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
		GregorianCalendar cal = new GregorianCalendar(dp.getYear(), dp.getMonth(),
				dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
		long time = cal.getTimeInMillis();
		long awakeTime = 0;
		Cursor cursor = mSleepLogHelper.queryLog(0);
		if (cursor != null) {
			cursor.moveToFirst();
			awakeTime = cursor.getLong(cursor.getColumnIndex(AWAKE_TIME));
		}
		assertEquals(time, awakeTime);
	}
	
	public void testDontSave(){
		mSleepLogHelper.deleteAllEntries();
		activity.setIsSleepTime(true);
		activity.runOnUiThread(new Runnable(){
			public void run(){
				mSleepLogHelper.insertLog(0,100000,false);
				tp.setCurrentHour(10);
				tp.setCurrentMinute(30);
				dp.updateDate(2013, 12, 15);
				activity.finish();
			}
		});
		getInstrumentation().waitForIdleSync();
		long sleepTime = 0;
		Cursor cursor = mSleepLogHelper.queryLog(0);
		if (cursor != null) {
			cursor.moveToFirst();
			sleepTime = cursor.getLong(cursor.getColumnIndex(ASLEEP_TIME));
		}
		assertEquals(0, sleepTime);
	}
}
