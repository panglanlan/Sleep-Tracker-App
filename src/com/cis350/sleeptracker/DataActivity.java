package com.cis350.sleeptracker;

import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DataActivity extends SleepTrackerActivity {

	private ListView mDataListView;
	private SleepLogHelper mSleepLogHelper;
	private List<Map<String, ?>> mDataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);
		((SleepTrackerApplication) this.getApplicationContext())
				.customizeActionBar(this);
		mDataListView = (ListView) findViewById(R.id.data_list);
		mSleepLogHelper = new SleepLogHelper(this);
		((SleepTrackerApplication) this.getApplicationContext())
				.setColorScheme(mDataListView);

		mDataListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				long asleepTime = Long.valueOf((String) mDataList.get(position).get(
						SleepLogHelper.ITEM_ASLEEP_TIME_LONG));
				Intent intent = new Intent(view.getContext(), LogActivity.class);
				intent.putExtra(SleepLogHelper.ITEM_ASLEEP_TIME_LONG, asleepTime);
				startActivity(intent);
			}
		});

		mDataListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final long asleepTime = Long.valueOf((String) mDataList.get(position)
						.get(SleepLogHelper.ITEM_ASLEEP_TIME_LONG));
				AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(
						DataActivity.this);
				deleteDialogBuilder.setTitle(getResources().getString(
						R.string.delete_dialog_title));
				deleteDialogBuilder.setMessage(getResources().getString(
						R.string.delete_dialog_message));
				deleteDialogBuilder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								mSleepLogHelper.deleteEntry(asleepTime);
								onResume();
								dialog.dismiss();
							}
						});
				deleteDialogBuilder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog deleteDialog = deleteDialogBuilder.create();
				deleteDialog.show();
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDataList = mSleepLogHelper.queryAll();
		mDataListView.setAdapter(new SimpleAdapter(this, mDataList,
				R.layout.data_item, SleepLogHelper.ITEMS, SleepLogHelper.ITEM_IDS));
	}
}
