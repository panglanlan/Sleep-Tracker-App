package com.cis350.sleeptracker.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cis350.sleeptracker.ChartActivity;
import com.cis350.sleeptracker.LogActivity;
import com.cis350.sleeptracker.database.SleepLogHelper;

public class LogActivityTest extends ActivityInstrumentationTestCase2<LogActivity>{

	public LogActivityTest(Class<LogActivity> activityClass) {
		super(activityClass);
		
	}
	
	private LogActivity activity;
	private SleepLogHelper mSleepLogHelper;
	private long today;
	private static final long DAY_IN_MILLISECONDS = 86400000;
	private static final long HOUR_IN_MILLISECONDS = 3600000;
	
	protected void setUp() throws Exception {
		super.setUp();
		activity=getActivity();
		mSleepLogHelper = new SleepLogHelper(activity);
		mSleepLogHelper.deleteAllEntries();
		today = (System.currentTimeMillis()/DAY_IN_MILLISECONDS*DAY_IN_MILLISECONDS) + (8*HOUR_IN_MILLISECONDS);
	}
	
	public void testQueryLog(){
		mSleepLogHelper.insertLog(today-5*DAY_IN_MILLISECONDS+10*HOUR_IN_MILLISECONDS, today-5*DAY_IN_MILLISECONDS+12*HOUR_IN_MILLISECONDS, false);
		mSleepLogHelper.updateRating(today-5*DAY_IN_MILLISECONDS+10*HOUR_IN_MILLISECONDS, 3);
		activity.setAsleepTime(today-5*DAY_IN_MILLISECONDS+10*HOUR_IN_MILLISECONDS);
		assertEquals(today-5*DAY_IN_MILLISECONDS+12*HOUR_IN_MILLISECONDS,activity.getmAwakeTime());
		assertEquals(3,activity.getRating());
		
		
	}

}
