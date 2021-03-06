package com.cis350.sleeptracker.test;

import java.util.GregorianCalendar;
import android.test.ActivityInstrumentationTestCase2;
import com.cis350.sleeptracker.ModifyTimeActivity;
import android.database.Cursor;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Button;
import com.cis350.sleeptracker.R;
import com.cis350.sleeptracker.database.SleepLogHelper;

public class ModifyTimeActivityTests extends
		ActivityInstrumentationTestCase2<ModifyTimeActivity> {

	public ModifyTimeActivityTests() {
		super(ModifyTimeActivity.class);
	}

	private Button saveButton;
	private ModifyTimeActivity activity;
	private TimePicker tp;
	private DatePicker dp;
	private SleepLogHelper mSleepLogHelper;
	protected static final String ASLEEP_TIME = "asleep_time";
	protected static final String AWAKE_TIME = "awake_time";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		saveButton = (Button) activity.findViewById(R.id.save_button);
		tp = (TimePicker) activity.findViewById(R.id.time_picker);
		dp = (DatePicker) activity.findViewById(R.id.date_picker);
		mSleepLogHelper = new SleepLogHelper(activity);
	}

	public void testEditSleep() {
		mSleepLogHelper.deleteAllEntries();
		activity.setIsSleepTime(true);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mSleepLogHelper.insertLog(0, 100000, false);
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
		sleepTime = (Long) mSleepLogHelper.queryLog(time).get("AsleepTime");

		assertEquals(time, sleepTime);
	}

	public void testEditWake() {
		mSleepLogHelper.deleteAllEntries();
		activity.setIsSleepTime(false);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mSleepLogHelper.insertLog(0, 100000, false);
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
		awakeTime = (Long) mSleepLogHelper.queryLog(0).get("AwakeTime");
		assertEquals(time, awakeTime);
	}

	public void testDontSave() {
		mSleepLogHelper.deleteAllEntries();
		activity.setIsSleepTime(true);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mSleepLogHelper.insertLog(0, 100000, false);
				tp.setCurrentHour(10);
				tp.setCurrentMinute(30);
				dp.updateDate(2013, 12, 15);
				activity.finish();
			}
		});
		getInstrumentation().waitForIdleSync();
		long sleepTime = 0;
		sleepTime = (Long) mSleepLogHelper.queryLog(0).get("AsleepTime");
		assertEquals(0, sleepTime);
	}
}
