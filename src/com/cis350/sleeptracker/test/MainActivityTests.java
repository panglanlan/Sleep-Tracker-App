package com.cis350.sleeptracker.test;


import java.util.ArrayList;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cis350.sleeptracker.MainActivity;
import com.cis350.sleeptracker.R;
import com.cis350.sleeptracker.database.SleepLogHelper;
import com.cis350.sleeptracker.database.SleepTrackerDatabase;

public class MainActivityTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private static final String MAIN = "main";
	private Button mSleepWakeButton;
	private MainActivity activity;
	private LinearLayout linearlayout;
	private int entries;
	private SleepLogHelper mSleepLogHelper;
	SharedPreferences mPreferences;
	
	public MainActivityTests(){
		super(MainActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		mSleepWakeButton = (Button)activity.findViewById(R.id.sleep_wake_button);	
		linearlayout = activity.getLayout();
		mSleepLogHelper = new SleepLogHelper(activity);
		entries = mSleepLogHelper.numEntries();
		mPreferences = activity.getSharedPreferences(MAIN, 0);
	}
	
	public void testSleepColor(){
		activity.runOnUiThread(new Runnable(){
			public void run(){
				mSleepWakeButton.performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
		ColorDrawable d = (ColorDrawable)linearlayout.getBackground();
		int color = d.getColor();
		assertEquals(activity.getResources().getColor(R.color.background_color),color);
	}
	
	public void testWakeColorAndAddEntry(){
		activity.runOnUiThread(new Runnable(){
			public void run(){
				mSleepWakeButton.performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
		ColorDrawable d = (ColorDrawable)linearlayout.getBackground();
		int color = d.getColor();
		assertEquals(activity.getResources().getColor(R.color.background_color_awake),color);
		assertEquals(entries+1,mSleepLogHelper.numEntries());
	}
	
	public void testGetTipsWithAlcohol(){
		activity.runOnUiThread(new Runnable(){
			public void run(){
			}
		});
		
		ArrayList<String> tipsWithoutExcuse;
		ArrayList<String> tipsWithAlcohol;
		
		SharedPreferences.Editor editor = mPreferences.edit();
		for(String excuse : SleepTrackerDatabase.EXCUSES){
			editor.putBoolean(excuse, false);
		}
		editor.commit();
		tipsWithoutExcuse = activity.getFilteredTips();
		
		editor.putBoolean("alcohol", true);
		editor.commit();
		tipsWithAlcohol = activity.getFilteredTips();
		
		tipsWithAlcohol.removeAll(tipsWithoutExcuse);
		assertTrue(tipsWithAlcohol.size() > 0);
		assertTrue(tipsWithAlcohol.contains("Avoid consuming alcohol in the 4 hours before bed. Alcohol tends to make you sleepy but actually interferes with deep sleep."));
	}
}
