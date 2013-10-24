package com.cis350.sleeptracker.test;


import android.graphics.drawable.ColorDrawable;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cis350.sleeptracker.MainActivity;
import com.cis350.sleeptracker.R;
import com.cis350.sleeptracker.database.SleepLogHelper;

public class MainActivityTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private Button mSleepWakeButton;
	private MainActivity activity;
	private LinearLayout linearlayout;
	private int entries;
	private SleepLogHelper mSleepLogHelper;
	
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

}
