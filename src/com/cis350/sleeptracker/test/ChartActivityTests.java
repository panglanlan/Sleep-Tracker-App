package com.cis350.sleeptracker.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cis350.sleeptracker.ChartActivity;
import com.cis350.sleeptracker.SleepLogHelper;
import org.achartengine.model.XYSeries;

public class ChartActivityTests extends ActivityInstrumentationTestCase2<ChartActivity> {

	public ChartActivityTests(){
		super(ChartActivity.class);
	}
	
	private ChartActivity activity;
	private SleepLogHelper mSleepLogHelper;
	private XYSeries totals;
	private XYSeries naps;
	double delta;
	
	protected void setUp() throws Exception {
		super.setUp();
		delta = .01;
		activity = getActivity();
		mSleepLogHelper = new SleepLogHelper(activity);
		mSleepLogHelper.deleteAllEntries();
		totals = activity.getTotalSeries();
		naps = activity.getNapSeries();
	}
	
	public void testLessNight(){
		activity.add(7,4,false);
		activity.add(6,1,false);
		activity.add(5,3,false);
		activity.finish();
		activity = this.getActivity();
		for(int i = 0; i < totals.getItemCount(); i++)
		{
			if(totals.getX(i) == 7)
				assertEquals(4,totals.getY(i),delta);
			if(totals.getX(i) == 6)
				assertEquals(1,totals.getY(i),delta);
			if(totals.getX(i) == 5)
				assertEquals(3,totals.getY(i),delta);
		}
	}
	
	public void testLessNap(){
		activity.add(7,2,true);
		activity.add(6,2,true);
		activity.add(5,4,true);
		activity.finish();
		activity = this.getActivity();
		for(int i = 0; i < naps.getItemCount(); i++)
		{
			if(naps.getX(i) == 7)
				assertEquals(2,naps.getY(i),delta);
			if(naps.getX(i) == 6)
				assertEquals(2,naps.getY(i),delta);
			if(naps.getX(i) == 5)
				assertEquals(4,naps.getY(i),delta);
		}
	}
	
	public void testLessMixed(){
		activity.add(7,2,false);
		activity.add(6,2,false);
		activity.add(6,1,true);
		activity.add(5,4,true);
		activity.finish();
		activity = this.getActivity();
		for(int i = 0; i < naps.getItemCount(); i++)
		{
			if(naps.getX(i) == 7)
				assertEquals(0,naps.getY(i),delta);
			if(naps.getX(i) == 6)
				assertEquals(1,naps.getY(i),delta);
			if(naps.getX(i) == 5)
				assertEquals(4,naps.getY(i),delta);
		}
		for(int i = 0; i < totals.getItemCount(); i++)
		{
			if(totals.getX(i) == 7)
				assertEquals(2,totals.getY(i),delta);
			if(totals.getX(i) == 6)
				assertEquals(2,totals.getY(i),delta);
			if(totals.getX(i) == 5)
				assertEquals(0,totals.getY(i),delta);
		}
	}
	
	public void testSevenNight(){
		activity.add(7,4,false);
		activity.add(6,1,false);
		activity.add(5,3,false);
		activity.add(4,10,false);
		activity.add(3,8,false);
		activity.add(2,4,false);
		activity.add(1,5,false);
		activity.finish();
		activity = this.getActivity();
		for(int i = 0; i < totals.getItemCount(); i++)
		{
			if(totals.getX(i) == 7)
				assertEquals(4,totals.getY(i),delta);
			if(totals.getX(i) == 6)
				assertEquals(1,totals.getY(i),delta);
			if(totals.getX(i) == 5)
				assertEquals(3,totals.getY(i),delta);
			if(totals.getX(i) == 4)
				assertEquals(10,totals.getY(i),delta);
			if(totals.getX(i) == 3)
				assertEquals(8,totals.getY(i),delta);
			if(totals.getX(i) == 2)
				assertEquals(4,totals.getY(i),delta);
			if(totals.getX(i) == 1)
				assertEquals(5,totals.getY(i),delta);
		}
	}
	
	public void testSevenNap(){
		activity.add(7,4,true);
		activity.add(6,1,true);
		activity.add(5,3,true);
		activity.add(4,10,true);
		activity.add(3,8,true);
		activity.add(2,4,true);
		activity.add(1,5,true);
		activity.finish();
		activity = this.getActivity();
		for(int i = 0; i < naps.getItemCount(); i++)
		{
			if(naps.getX(i) == 7)
				assertEquals(4,naps.getY(i),delta);
			if(naps.getX(i) == 6)
				assertEquals(1,naps.getY(i),delta);
			if(naps.getX(i) == 5)
				assertEquals(3,naps.getY(i),delta);
			if(naps.getX(i) == 4)
				assertEquals(10,naps.getY(i),delta);
			if(naps.getX(i) == 3)
				assertEquals(8,naps.getY(i),delta);
			if(naps.getX(i) == 2)
				assertEquals(4,naps.getY(i),delta);
			if(naps.getX(i) == 1)
				assertEquals(5,naps.getY(i),delta);
		}
	}
	
	public void testSevenMixed(){
		activity.add(7,4,false);
		activity.add(6,1,false);
		activity.add(5,3,false);
		activity.add(4,10,false);
		activity.add(3,8,false);
		activity.add(2,4,false);
		activity.add(1,5,false);
		activity.add(5,1,true);
		activity.add(4,6,true);
		activity.add(3,4,true);
		activity.finish();
		activity = this.getActivity();
		for(int i = 0; i < naps.getItemCount(); i++)
		{
			if(naps.getX(i) == 7)
				assertEquals(0,naps.getY(i),delta);
			if(naps.getX(i) == 6)
				assertEquals(0,naps.getY(i),delta);
			if(naps.getX(i) == 5)
				assertEquals(1,naps.getY(i),delta);
			if(naps.getX(i) == 4)
				assertEquals(6,naps.getY(i),delta);
			if(naps.getX(i) == 3)
				assertEquals(4,naps.getY(i),delta);
			if(naps.getX(i) == 2)
				assertEquals(0,naps.getY(i),delta);
			if(naps.getX(i) == 1)
				assertEquals(0,naps.getY(i),delta);
		}
		for(int i = 0; i < totals.getItemCount(); i++)
		{
			if(totals.getX(i) == 7)
				assertEquals(4,totals.getY(i),delta);
			if(totals.getX(i) == 6)
				assertEquals(1,totals.getY(i),delta);
			if(totals.getX(i) == 5)
				assertEquals(3,totals.getY(i),delta);
			if(totals.getX(i) == 4)
				assertEquals(10,totals.getY(i),delta);
			if(totals.getX(i) == 3)
				assertEquals(8,totals.getY(i),delta);
			if(totals.getX(i) == 2)
				assertEquals(4,totals.getY(i),delta);
			if(totals.getX(i) == 1)
				assertEquals(5,totals.getY(i),delta);
		}
	}
	
	public void testMoreNight(){
		activity.add(9,2,false);
		activity.add(8,12,false);
		activity.add(7,4,false);
		activity.add(6,1,false);
		activity.add(5,3,false);
		activity.add(4,10,false);
		activity.add(3,8,false);
		activity.add(2,4,false);
		activity.add(1,5,false);
		activity.finish();
		activity = this.getActivity();
		for(int i = 0; i < totals.getItemCount(); i++)
		{
			if(totals.getX(i) == 9)
				assertEquals(2,totals.getY(i),delta);
			if(totals.getX(i) == 8)
				assertEquals(12,totals.getY(i),delta);
			if(totals.getX(i) == 7)
				assertEquals(4,totals.getY(i),delta);
			if(totals.getX(i) == 6)
				assertEquals(1,totals.getY(i),delta);
			if(totals.getX(i) == 5)
				assertEquals(3,totals.getY(i),delta);
			if(totals.getX(i) == 4)
				assertEquals(10,totals.getY(i),delta);
			if(totals.getX(i) == 3)
				assertEquals(8,totals.getY(i),delta);
			if(totals.getX(i) == 2)
				assertEquals(4,totals.getY(i),delta);
			if(totals.getX(i) == 1)
				assertEquals(5,totals.getY(i),delta);
		}
	}
}
