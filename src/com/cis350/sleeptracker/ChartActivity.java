package com.cis350.sleeptracker;

import java.text.DecimalFormat;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class ChartActivity extends Activity{
	private static final long DAY_IN_MILLISECONDS = 86400000;
	private static final long HOUR_IN_MILLISECONDS = 3600000;
	private static final long MONTH_IN_MILLISECONDS = 30 * DAY_IN_MILLISECONDS;
	private static final int WEEK = 7;
	private static final int MONTH = 30;
	private static final int YEAR = 12;

	private static final String[] EXCUSE_STRINGS = {"CAFFEINE", "ALCOHOL", "NICOTINE", "SUGAR",
		"SCREEN TIME", "EXERCISE"};
		//maybe we can add a little more excuse_strings here?
	
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
    
    private SharedPreferences mPreferences;
    private SleepLogHelper mSleepLogHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mPreferences = getSharedPreferences(MainActivity.MAIN, MODE_PRIVATE);
		initChart(mRenderer, MONTH, "Days", false);
		initChart(wRenderer, WEEK, "Days", false);
		initChart(yRenderer, YEAR, "Months", true);
		
		today = (System.currentTimeMillis()/DAY_IN_MILLISECONDS*DAY_IN_MILLISECONDS) + (8*HOUR_IN_MILLISECONDS);
		thisMonth = (System.currentTimeMillis()/MONTH_IN_MILLISECONDS*MONTH_IN_MILLISECONDS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);
		MainActivity.customizeActionBar(this);
		mSleepLogHelper = new SleepLogHelper(this);

		
		TabHost tabs = (TabHost)findViewById(R.id.tabHost);
        tabs.setBackgroundColor(getResources().getColor(R.color.background_color_awake));
        
        if (mPreferences.getBoolean(MainActivity.IS_ASLEEP, false))
        	tabs.setBackgroundColor(getResources().getColor(R.color.background_color));
        
        /*
        if (!mPreferences.getBoolean(MainActivity.IS_ASLEEP, false)) {
        	tabs.setBackgroundColor(getResources().getColor(R.color.background_color_awake));
        } else {
        	tabs.setBackgroundColor(getResources().getColor(R.color.background_color));
        }
        */
        
        tabs.setup();

        modifyChart(wChart, wRenderer,WEEK, wNapSeries, wTotalSleepSeries, wDataset);
        modifyChart(mChart, mRenderer,MONTH, mNapSeries, mTotalSleepSeries, mDataset);
        modifyChart(yChart,yRenderer,-1,null,yTotalSleepSeries, yDataset);
        
        /*
        if (wChart == null) {
        	addData(WEEK, wNapSeries, wTotalSleepSeries, wDataset);
            wChart = ChartFactory.getBarChartView(ChartActivity.this, wDataset, wRenderer, Type.STACKED);
        } else 
        	wChart.repaint();
        
        if (mChart == null){
        	addData(MONTH, mNapSeries, mTotalSleepSeries, mDataset);
        	mChart = ChartFactory.getBarChartView(ChartActivity.this, mDataset, mRenderer, Type.STACKED);
        } else
        	mChart.repaint();
        
        if (yChart == null){
        	addYearlyData(yTotalSleepSeries, yDataset);
        	yChart = ChartFactory.getBarChartView(ChartActivity.this, yDataset, yRenderer, Type.STACKED);
        } else
        	yChart.repaint();
        */
        
        tabs.clearAllTabs();
        
        tabs.addTab(initspec("weekly", "Week",  wChart, tabs));
        tabs.addTab(initspec("monthly", "Month",  mChart, tabs));
        tabs.addTab(initspec("yearly", "Year",  yChart, tabs));
        tabs.addTab(initspec("excuses","Excuse Record",createExcusesTable(),tabs));  
        					//changed "Excuse Data" to "Excuse Record", hopefully sounds better.
        
        /*TabHost.TabSpec spec2 = tabs.newTabSpec("monthly");
        spec2.setIndicator("Month");
        spec2.setContent(new TabHost.TabContentFactory(){
			public View createTabContent(String tag) {
				return mChart;
			}
        });
        tabs.addTab(spec2); 
        
        TabHost.TabSpec spec3 = tabs.newTabSpec("yearly");
        spec3.setIndicator("Year");
        spec3.setContent(new TabHost.TabContentFactory(){
			public View createTabContent(String tag) {
				return yChart;
			}
        });
        tabs.addTab(spec3);
        
        TabHost.TabSpec spec4 = tabs.newTabSpec("excuses");
        spec4.setIndicator("Excuse Data");
        spec4.setContent(new TabHost.TabContentFactory(){
			public View createTabContent(String tag) {
				return createExcusesTable();
			}
        });
        tabs.addTab(spec4);
        */
        setTabColor(tabs);
	}

	private void modifyChart(GraphicalView chart,XYMultipleSeriesRenderer renderer, int numOfPoints, XYSeries nap, XYSeries total, XYMultipleSeriesDataset dataset){
		if (chart == null) {
			if(numOfPoints==-1&&nap==null)
        		addYearlyData(yTotalSleepSeries, yDataset);
			else
				addData(numOfPoints, nap, total, dataset);
			
        	chart = ChartFactory.getBarChartView(ChartActivity.this, dataset, renderer, Type.STACKED);
        } else 
        	chart.repaint();
	}
	
	private TabHost.TabSpec initspec(String specstr, String indicator,  final View chart, TabHost tab){
		TabHost.TabSpec spec = tab.newTabSpec(specstr);
        spec.setIndicator(indicator);
        spec.setContent(new TabHost.TabContentFactory(){
			public View createTabContent(String tag) {
				return chart;
			}
        });
        return spec;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_chart, menu);
		return true;
	}
	
	protected void onResume() {
		super.onResume();
        
    }
	
	public void setTabColor(TabHost tabhost) {
		TextView tv;
        for(int i=0;i<tabhost.getTabWidget().getChildCount();i++){
            tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            if (tv != null)
            	tv.setTextColor(getResources().getColor(R.color.off_white));
        }
        tv = (TextView) tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(android.R.id.title);
        if (tv != null)
        	tv.setTextColor(getResources().getColor(R.color.off_white));
	}
	
	private View createExcusesTable(){
		TableLayout excusesTable = new TableLayout(this);
		TableLayout.LayoutParams tableLayoutParams =
				new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, 1.0f);
		excusesTable.setLayoutParams(tableLayoutParams);
		excusesTable.setStretchAllColumns(true);
		TableRow columnLabels = new TableRow(this);
		TableRow.LayoutParams rowLayoutParams =
				new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, 1.0f);
		columnLabels.setLayoutParams(rowLayoutParams);
		columnLabels.setGravity(Gravity.CENTER);
		
		TextView excuseLabel=initLabel();
		TextView avgTimeLabel = initLabel();
		TextView avgQtyLabel = initLabel();

		excuseLabel.setText("");
		avgTimeLabel.setText("HOURS SLEPT");
		avgQtyLabel.setText("SLEEP QUALITY");
		columnLabels.addView(excuseLabel);
		columnLabels.addView(avgTimeLabel);
		columnLabels.addView(avgQtyLabel);
		
		excusesTable.addView(columnLabels, tableLayoutParams);
		
		TableRow tr=modifiedtr(rowLayoutParams);
		excusesTable.addView(tr, tableLayoutParams);
		return excusesTable;
	}
	
	
	private TextView initLabel(){
		TextView label= new TextView(this);
		label.setTextColor(getResources().getColor(R.color.off_white));
		label.setTypeface(null, Typeface.BOLD_ITALIC);
		label.setGravity(Gravity.CENTER);
		return label;
	}
	
	private TableRow modifiedtr(TableRow.LayoutParams rowLayoutParams){
		//DecimalFormat df = new DecimalFormat("0.00");
		double avgTimeSlept, avgQuality;
		String[] excuses = SleepLogHelper.EXCUSES;
		
		TableRow tr = new TableRow(this);
		for (int i=0; i<excuses.length; i++){
			
			tr.setLayoutParams(rowLayoutParams);
			tr.setGravity(Gravity.CENTER);
			TextView excuse = initLabel();
			excuse.setText(EXCUSE_STRINGS[i]);
			
			/*
			 * excuse.setTextColor(getResources().getColor(R.color.off_white));
			excuse.setTypeface(null, Typeface.BOLD_ITALIC);
			excuse.setGravity(Gravity.CENTER);
			*/
			
			tr.addView(excuse);
			
			Cursor timeSleptCursor = mSleepLogHelper.queryLogExcusesTime(excuses[i]);
			if(timeSleptCursor.moveToFirst()){
				avgTimeSlept = timeSleptCursor.getFloat(0)/HOUR_IN_MILLISECONDS;
				tr.addView(getTextView(avgTimeSlept));
			}
			
			Cursor qualityCursor = mSleepLogHelper.queryLogExcusesQuality(excuses[i]);
			if (qualityCursor.moveToFirst()) {
				avgQuality = qualityCursor.getFloat(0);
				tr.addView(getTextView(avgQuality));
			}
		}
		return tr;
	}
	
	private TextView getTextView(double quantity){
		DecimalFormat df = new DecimalFormat("0.00");
		String formate = df.format(quantity);
		if (Math.abs(quantity) < .0001) {
			formate = "N/A";
		}
		TextView avg=initLabel();
		avg.setText(formate);
		/*
		TextView avg = new TextView(this);
		avg.setTextColor(getResources().getColor(R.color.off_white));
		avg.setGravity(Gravity.CENTER);
		*/
		return avg;
	}
	
	private void initChart(XYMultipleSeriesRenderer renderer, int numEntries, String title, boolean ifYear) {
		if (!mPreferences.getBoolean(MainActivity.IS_ASLEEP, false)) {
			renderer.setMarginsColor(getResources().getColor(R.color.background_color_awake));
		} else {
			renderer.setMarginsColor(getResources().getColor(R.color.background_color));
		}
		renderer.setMargins(new int[] {30, 60, 90, 30});
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
        	
        /*
	        totalRenderer = new XYSeriesRenderer();
	        totalRenderer.setColor(getResources().getColor(R.color.background_color_awake_green));
	        totalRenderer.setFillPoints(true);
	        totalRenderer.setLineWidth(2);
	        totalRenderer.setChartValuesTextAlign(Align.CENTER);
	        totalRenderer.setChartValuesTextSize(18);
	        totalRenderer.setDisplayChartValues(true);
	      */
	        renderer.addSeriesRenderer(setRenderer(totalRenderer,true));
        }
        
        /*
        nightTimeRenderer = new XYSeriesRenderer();
        nightTimeRenderer.setColor(getResources().getColor(R.color.off_white));
        nightTimeRenderer.setFillPoints(true);
        nightTimeRenderer.setLineWidth(2);
        nightTimeRenderer.setChartValuesTextAlign(Align.CENTER);
        nightTimeRenderer.setChartValuesTextSize(18);
        nightTimeRenderer.setDisplayChartValues(true);
        */
        renderer.addSeriesRenderer(setRenderer(nightTimeRenderer,false));
        
    }
	
	private XYSeriesRenderer setRenderer(XYSeriesRenderer rendererPassIn, boolean renderflag){ //renderflag=T if for totalRenderer
		rendererPassIn = new XYSeriesRenderer();
		if (renderflag)
			rendererPassIn.setColor(getResources().getColor(R.color.background_color_awake_green));
		else
			rendererPassIn.setColor(getResources().getColor(R.color.off_white));
        rendererPassIn.setFillPoints(true);
        rendererPassIn.setLineWidth(2);
        rendererPassIn.setChartValuesTextAlign(Align.CENTER);
        rendererPassIn.setChartValuesTextSize(18);
        rendererPassIn.setDisplayChartValues(true);
        return rendererPassIn;
	}
	
	public void addData(int numOfPoints, XYSeries nap, XYSeries total, XYMultipleSeriesDataset dataset) {
	/*	Adds data starting yesterday
	 */
	    dataset.addSeries(total);
	    dataset.addSeries(nap);
		DecimalFormat df = new DecimalFormat("0.00");
		long startDay = today - (numOfPoints+2)*DAY_IN_MILLISECONDS;
		long endDay = today - (numOfPoints+1)*DAY_IN_MILLISECONDS;
		int count = 1;
		
		while (count<numOfPoints+1){
			startDay = startDay + DAY_IN_MILLISECONDS;
			endDay = endDay + DAY_IN_MILLISECONDS;
			double totalHoursSlept = 0;
			double napHoursSlept = 0;
			Cursor cursor = mSleepLogHelper.queryLogDay(startDay, endDay);
			if (cursor != null){
				cursor.moveToFirst();
				for (int j=0; j<cursor.getCount(); j++) {
					long startSleep = cursor.getLong(cursor.getColumnIndex(SleepLogHelper.ASLEEP_TIME));
					long endSleep = cursor.getLong(cursor.getColumnIndex(SleepLogHelper.AWAKE_TIME));
					double totalSleep = endSleep - startSleep;
					if (cursor.getInt(cursor.getColumnIndex(SleepLogHelper.NAP)) == 0){
						totalHoursSlept += totalSleep /HOUR_IN_MILLISECONDS;
						napHoursSlept += totalSleep / HOUR_IN_MILLISECONDS;
					}
					else 
						totalHoursSlept += totalSleep /HOUR_IN_MILLISECONDS;
					
					cursor.moveToNext();
				}
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
	
	public void addYearlyData(XYSeries total, XYMultipleSeriesDataset dataset){
		dataset.addSeries(total);
		DecimalFormat df = new DecimalFormat("0.00");
		long startMonth = thisMonth - 12 * MONTH_IN_MILLISECONDS;
		long endMonth = thisMonth - 11 * MONTH_IN_MILLISECONDS;
		for (int i=1; i<13; i++){
			Cursor cursor = mSleepLogHelper.queryLogAvgMonth(startMonth, endMonth);
			if (cursor.moveToFirst()){
				double temp = cursor.getLong(0);
				temp = temp/HOUR_IN_MILLISECONDS;
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
		if (isNap)
			mNapSeries.add(x,y);
		else
			mTotalSleepSeries.add(x,y);
	}
}
