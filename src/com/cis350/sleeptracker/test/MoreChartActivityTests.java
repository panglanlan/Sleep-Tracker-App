package com.cis350.sleeptracker.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cis350.sleeptracker.*;
import com.cis350.sleeptracker.database.SleepLogHelper;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;

public class MoreChartActivityTests extends
		ActivityInstrumentationTestCase2<ChartActivity> {

	private SleepLogHelper mSleepLogHelper; 
	private ChartActivity activity;
	private long today;
	private static final long DAY_IN_MILLISECONDS = 86400000;
	private static final long HOUR_IN_MILLISECONDS = 3600000;
	private XYSeries wTotalSleep;
	private XYSeries wTotalNap;
	private XYMultipleSeriesDataset wDataset; 
	
	public MoreChartActivityTests(){
		super(ChartActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		
		wTotalSleep = new XYSeries("Total Sleep");
		wTotalNap = new XYSeries("Nighttime Sleep");
		wDataset = new XYMultipleSeriesDataset();
		
		mSleepLogHelper = new SleepLogHelper(activity);
		mSleepLogHelper.deleteAllEntries();
		today = (System.currentTimeMillis()/DAY_IN_MILLISECONDS*DAY_IN_MILLISECONDS) + (8*HOUR_IN_MILLISECONDS);
	}
	
	public void testAddDataWeekly(){
		mSleepLogHelper.insertLog(today-4*DAY_IN_MILLISECONDS, today-4*DAY_IN_MILLISECONDS+6*HOUR_IN_MILLISECONDS, false);
		mSleepLogHelper.insertLog(today-5*DAY_IN_MILLISECONDS+10*HOUR_IN_MILLISECONDS, today-5*DAY_IN_MILLISECONDS+12*HOUR_IN_MILLISECONDS, false);
		mSleepLogHelper.insertLog(today-3*DAY_IN_MILLISECONDS+10*HOUR_IN_MILLISECONDS, today-3*DAY_IN_MILLISECONDS+12*HOUR_IN_MILLISECONDS, true);
		mSleepLogHelper.insertLog(today-15*DAY_IN_MILLISECONDS+10*HOUR_IN_MILLISECONDS, today-15*DAY_IN_MILLISECONDS+12*HOUR_IN_MILLISECONDS, true);
		activity.addData(7, wTotalNap, wTotalSleep, wDataset);
		int night = 0;
		int total = 0;
		for(int i = 0; i < wTotalSleep.getItemCount();i++){
			if(wTotalSleep.getY(i) > 0)	
				total++;
		}
		assertEquals(3, total);
		for(int i = 0; i < wTotalNap.getItemCount();i++){
			if(wTotalNap.getY(i) > 0)	
				night++;
		}
		assertEquals(2, night);
	}
	
	public void testAddDataMonthly(){
		mSleepLogHelper.insertLog(today-4*DAY_IN_MILLISECONDS, today-4*DAY_IN_MILLISECONDS+6*HOUR_IN_MILLISECONDS, false);
		mSleepLogHelper.insertLog(today-5*DAY_IN_MILLISECONDS+10*HOUR_IN_MILLISECONDS, today-5*DAY_IN_MILLISECONDS+12*HOUR_IN_MILLISECONDS, false);
		mSleepLogHelper.insertLog(today-3*DAY_IN_MILLISECONDS+10*HOUR_IN_MILLISECONDS, today-3*DAY_IN_MILLISECONDS+12*HOUR_IN_MILLISECONDS, true);
		mSleepLogHelper.insertLog(today-15*DAY_IN_MILLISECONDS+10*HOUR_IN_MILLISECONDS, today-15*DAY_IN_MILLISECONDS+12*HOUR_IN_MILLISECONDS, true);
		activity.addData(30, wTotalNap, wTotalSleep, wDataset);
		int night = 0;
		int total = 0;
		for(int i = 0; i < wTotalSleep.getItemCount();i++){
			if(wTotalSleep.getY(i) > 0)	
				total++;
		}
		assertEquals(4, total);
		for(int i = 0; i < wTotalNap.getItemCount();i++){
			if(wTotalNap.getY(i) > 0)	
				night++;
		}
		assertEquals(2, night);
	}
	
	public void testYearly(){
		mSleepLogHelper.insertLog(today-4*DAY_IN_MILLISECONDS, today-4*DAY_IN_MILLISECONDS+6*HOUR_IN_MILLISECONDS, false);
		mSleepLogHelper.insertLog(today-5*DAY_IN_MILLISECONDS+10*HOUR_IN_MILLISECONDS, today-5*DAY_IN_MILLISECONDS+12*HOUR_IN_MILLISECONDS, false);
		mSleepLogHelper.insertLog(today-31*DAY_IN_MILLISECONDS+1*HOUR_IN_MILLISECONDS, today-31*DAY_IN_MILLISECONDS+5*HOUR_IN_MILLISECONDS, false);
		mSleepLogHelper.insertLog(today-32*DAY_IN_MILLISECONDS+1*HOUR_IN_MILLISECONDS, today-32*DAY_IN_MILLISECONDS+5*HOUR_IN_MILLISECONDS, false);
		activity.addYearlyData(wTotalSleep, wDataset);
		for(int i = 0; i < wTotalSleep.getItemCount();i++){
			if(wTotalSleep.getY(i) > 0)	
				assertEquals(4,wTotalSleep.getY(i),.001);
		}
	}
}
