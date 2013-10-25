package com.cis350.sleeptracker.test;

import java.util.List;
import java.util.Map;
import com.cis350.sleeptracker.DataActivity;
import com.cis350.sleeptracker.database.SleepLogHelper;
import android.test.ActivityInstrumentationTestCase2;

public class DataActivityTests extends
		ActivityInstrumentationTestCase2<DataActivity> {

	public DataActivityTests() {
		super(DataActivity.class);
	}

	private DataActivity activity;
	private SleepLogHelper mSleepLogHelper;
	private List<Map<String, ?>> mDataList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testQueryAllEmptyList() {
		activity = getActivity();
		assertNotNull(activity);
		mSleepLogHelper = new SleepLogHelper(activity);
		mSleepLogHelper.deleteAllEntries();
		assertNotNull(mSleepLogHelper);
		mDataList = mSleepLogHelper.queryAll();
		assertNotNull(mDataList);
		assertTrue(mDataList.size() == 0);
	}

}
