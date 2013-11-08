package com.cis350.sleeptracker;

import java.text.DecimalFormat;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cis350.sleeptracker.database.SleepLogHelper;

public class ChartActivity extends SleepTrackerActivity {
	private static final long DAY_IN_MILLISECONDS = 86400000;
	private static final long HOUR_IN_MILLISECONDS = 3600000;
	private static final long MONTH_IN_MILLISECONDS = 30 * DAY_IN_MILLISECONDS;
	private static final int WEEK = 7;
	private static final int MONTH = 30;
	private static final int YEAR = 12;

	private static final String[] EXCUSE_STRINGS = { "CAFFEINE", "ALCOHOL",
			"NICOTINE", "SUGAR", "SCREEN TIME", "EXERCISE" };
	// maybe we can add a little more excuse_strings here?

	private long today, thisMonth;
	private GraphicalView wChart, mChart, yChart;
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesDataset wDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesDataset yDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYMultipleSeriesRenderer wRenderer = new XYMultipleSeriesRenderer();
	private XYMultipleSeriesRenderer yRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mTotalSleepSeries = new XYSeries("Total Sleep");
	private XYSeries mNapSeries = new XYSeries("Nightime Sleep");
	private XYSeries wTotalSleepSeries = new XYSeries("Total Sleep");
	private XYSeries wNapSeries = new XYSeries("Nightime Sleep");
	private XYSeries yTotalSleepSeries = new XYSeries("Total Sleep");
	private XYSeriesRenderer totalRenderer, nightTimeRenderer;
	private SleepLogHelper mSleepLogHelper;
	private LinearLayout mMainLinearLayout;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		initChart(mRenderer, MONTH, "Days", false);
		initChart(wRenderer, WEEK, "Days", false);
		initChart(yRenderer, YEAR, "Months", true);

		today = (System.currentTimeMillis() / DAY_IN_MILLISECONDS * DAY_IN_MILLISECONDS)
				+ (8 * HOUR_IN_MILLISECONDS);
		thisMonth = (System.currentTimeMillis() / MONTH_IN_MILLISECONDS * MONTH_IN_MILLISECONDS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);
		SleepTrackerApplication applicationContext = ((SleepTrackerApplication) this
				.getApplicationContext());
		applicationContext.customizeActionBar(this);
		mSleepLogHelper = new SleepLogHelper(this);
		
		TabHost tabs = (TabHost) findViewById(R.id.tabHost);
		tabs.setBackgroundColor(getResources().getColor(R.color.background_color_chart));
		//((SleepTrackerApplication) this.getApplicationContext())
		//	.setColorScheme(tabs);
			// 
		tabs.setup();
		wChart = modifyChart(wChart, wRenderer, WEEK, wNapSeries,
				wTotalSleepSeries, wDataset);
		mChart = modifyChart(mChart, mRenderer, MONTH, mNapSeries,
				mTotalSleepSeries, mDataset);
		yChart = modifyChart(yChart, yRenderer, -1, null, yTotalSleepSeries,
				yDataset);

		tabs.clearAllTabs();
		tabs.addTab(initspec("weekly", "Week", wChart, tabs));
		tabs.addTab(initspec("monthly", "Month", mChart, tabs));
		tabs.addTab(initspec("yearly", "Year", yChart, tabs));
		tabs.addTab(initspec("excuses", "Excuse Record", createExcusesTable(), tabs));
		setTabColor(tabs);
		mMainLinearLayout = (LinearLayout) findViewById(R.id.tab_linear_layout);
	}

	private GraphicalView modifyChart(GraphicalView chart,
			XYMultipleSeriesRenderer renderer, int numOfPoints, XYSeries nap,
			XYSeries total, XYMultipleSeriesDataset dataset) {
		if (chart == null) {
			if (numOfPoints == -1 && nap == null) {
				addYearlyData(yTotalSleepSeries, yDataset);
			} else {
				addData(numOfPoints, nap, total, dataset);
			}
			chart = ChartFactory.getBarChartView(ChartActivity.this, dataset,
					renderer, Type.STACKED);
		} else {
			chart.repaint();
		}
		return chart;
	}

	private TabHost.TabSpec initspec(String specstr, String indicator,
			final View chart, TabHost tab) {
		TabHost.TabSpec spec = tab.newTabSpec(specstr);
		spec.setIndicator(indicator);
		spec.setContent(new TabHost.TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				return chart;
			}
		});
		return spec;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_chart, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMainLinearLayout.setBackgroundColor(getResources().getColor(R.color.background_color_chart));

	}

	public void setTabColor(TabHost tabhost) {
		TextView tv;
		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
			tv = (TextView) tabhost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title);
			if (tv != null) {
				tv.setTextColor(getResources().getColor(R.color.off_white));
			}
		}
		tv = (TextView) tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
				.findViewById(android.R.id.title);
		if (tv != null) {
			tv.setTextColor(getResources().getColor(R.color.off_white));
		}
	}

	private View createExcusesTable() {
		TableLayout excusesTable = new TableLayout(this);
		TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		excusesTable.setLayoutParams(tableLayoutParams);
		excusesTable.setStretchAllColumns(true);
		TableRow columnLabels = new TableRow(this);
		TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		columnLabels.setLayoutParams(rowLayoutParams);
		columnLabels.setGravity(Gravity.CENTER);

		TextView excuseLabel = initLabel();
		TextView avgTimeLabel = initLabel();
		TextView avgQtyLabel = initLabel();

		excuseLabel.setText("");
		avgTimeLabel.setText("HOURS SLEPT");
		avgQtyLabel.setText("SLEEP QUALITY");
		columnLabels.addView(excuseLabel);
		columnLabels.addView(avgTimeLabel);
		columnLabels.addView(avgQtyLabel);

		excusesTable.addView(columnLabels, tableLayoutParams);

		String[] excuses = SleepLogHelper.EXCUSES;
		for (int i = 0; i < excuses.length; i++) {
			TableRow tr = modifiedtr(rowLayoutParams, excuses, i);
			// wrong table display is caused by the following line is outside the
			// excuses.length loop when refactoring.
			excusesTable.addView(tr, tableLayoutParams);
		}
		return excusesTable;
	}

	private TextView initLabel() {
		TextView label = new TextView(this);
		label.setTextColor(getResources().getColor(R.color.off_white));
		label.setTypeface(null, Typeface.BOLD_ITALIC);
		label.setGravity(Gravity.CENTER);
		return label;
	}

	private TableRow modifiedtr(TableRow.LayoutParams rowLayoutParams,
			String[] excuses, int excuses_index) {
		// DecimalFormat df = new DecimalFormat("0.00");
		double avgTimeSlept, avgQuality;
		TableRow tr = new TableRow(this);

		tr.setLayoutParams(rowLayoutParams);
		tr.setGravity(Gravity.CENTER);
		TextView excuse = initLabel();
		excuse.setText(EXCUSE_STRINGS[excuses_index]);
		tr.addView(excuse);

		avgTimeSlept = mSleepLogHelper.queryLogExcusesTime(excuses[excuses_index]);
		if (avgTimeSlept != -1.0) {
			tr.addView(getTextView(avgTimeSlept));
		}

		avgQuality = mSleepLogHelper.queryLogExcusesQuality(excuses[excuses_index]);
		if (avgQuality != -1.0) {
			tr.addView(getTextView(avgQuality));
		}

		return tr;
	}

	private TextView getTextView(double quantity) {
		DecimalFormat df = new DecimalFormat("0.00");
		String formate = df.format(quantity);
		if (Math.abs(quantity) < .0001) {
			formate = "N/A";
		}
		TextView avg = initLabel();
		avg.setText(formate);
		return avg;
	}

	private void initChart(XYMultipleSeriesRenderer renderer, int numEntries,
			String title, boolean ifYear) {

		((SleepTrackerApplication) this.getApplicationContext())
		.setColorScheme(renderer);
				
		renderer.setMargins(new int[] { 30, 60, 90, 30 });
		renderer.setXTitle(title);
		renderer.setYTitle("Hours");
		renderer.setLegendTextSize(24);
		renderer.setZoomRate(0.2f);
		renderer.setZoomEnabled(false, false);
		renderer.setPanEnabled(false, false);
		renderer.setBarSpacing(0.3f);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(24);
		renderer.setAxisTitleTextSize(35);
		renderer.setAxesColor(getResources().getColor(R.color.off_white));
		renderer.setGridColor(Color.GRAY);
		renderer.setShowGridX(true);
		renderer.setLabelsTextSize(20);
		renderer.setXLabelsColor(getResources().getColor(R.color.off_white));
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setYLabelsColor(0, getResources().getColor(R.color.off_white));

		renderer.setXAxisMin(0);
		renderer.setXAxisMax(numEntries + 1);

		if (!ifYear) {

			renderer.addSeriesRenderer(setRenderer(totalRenderer, true));
		}

		renderer.addSeriesRenderer(setRenderer(nightTimeRenderer, false));

	}

	private XYSeriesRenderer setRenderer(XYSeriesRenderer rendererPassIn,
			boolean renderflag) { // renderflag=T if for totalRenderer
		rendererPassIn = new XYSeriesRenderer();
		if (renderflag) {
			rendererPassIn.setColor(getResources().getColor(
					R.color.background_color));
		} else {
			rendererPassIn.setColor(getResources().getColor(R.color.off_white));
		}
		rendererPassIn.setFillPoints(true);
		rendererPassIn.setLineWidth(2);
		rendererPassIn.setChartValuesTextAlign(Align.CENTER);
		rendererPassIn.setChartValuesTextSize(18);
		rendererPassIn.setDisplayChartValues(true);
		return rendererPassIn;
	}

	public void addData(int numOfPoints, XYSeries nap, XYSeries total,
			XYMultipleSeriesDataset dataset) {
		/*
		 * Adds data starting yesterday
		 */
		dataset.addSeries(total);
		dataset.addSeries(nap);
		DecimalFormat df = new DecimalFormat("0.00");
		long startDay = today - (numOfPoints + 2) * DAY_IN_MILLISECONDS;
		long endDay = today - (numOfPoints + 1) * DAY_IN_MILLISECONDS;
		int count = 1;

		while (count < numOfPoints + 1) {
			startDay = startDay + DAY_IN_MILLISECONDS;
			endDay = endDay + DAY_IN_MILLISECONDS;
			double totalHoursSlept = 0;
			double napHoursSlept = 0;

			Map<String, Double> returnResult = mSleepLogHelper.queryLogDay(startDay,
					endDay);
			if (!returnResult.isEmpty()) {

				totalHoursSlept = returnResult.get("totalHoursSlept");
				napHoursSlept = returnResult.get("napHoursSlept");
				String formate = df.format(totalHoursSlept);
				double finalValue = Double.parseDouble(formate);
				total.add(count, finalValue);
				formate = df.format(napHoursSlept);
				finalValue = Double.parseDouble(formate);
				nap.add(count, finalValue);
			}
			count++;
		}
	}

	public void addYearlyData(XYSeries total, XYMultipleSeriesDataset dataset) {
		dataset.addSeries(total);
		DecimalFormat df = new DecimalFormat("0.00");
		long startMonth = thisMonth - 12 * MONTH_IN_MILLISECONDS;
		long endMonth = thisMonth - 11 * MONTH_IN_MILLISECONDS;
		for (int i = 1; i < 13; i++) {
			double temp = mSleepLogHelper.queryLogAvgMonth(startMonth, endMonth);
			if (temp != -1.0) {
				String formate = df.format(temp);
				double finalValue = Double.parseDouble(formate);
				total.add(i, finalValue);
			}
			startMonth = startMonth + MONTH_IN_MILLISECONDS;
			endMonth = endMonth + MONTH_IN_MILLISECONDS;
		}
	}

	public XYSeries getTotalSeries() {
		return mTotalSleepSeries;
	}

	public XYSeries getNapSeries() {
		return mNapSeries;
	}

	public void add(double x, double y, boolean isNap) {
		if (isNap) {
			mNapSeries.add(x, y);
		} else {
			mTotalSleepSeries.add(x, y);
		}
	}
}
