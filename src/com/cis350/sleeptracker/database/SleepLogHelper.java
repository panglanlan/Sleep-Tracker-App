package com.cis350.sleeptracker.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.cis350.sleeptracker.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SleepLogHelper extends SleepTrackerDatabase {
	private static final String TAG = "SleepLogHelper";
	private static final int TABLE_VERSION = 5;
	private static final String TABLE_NAME = "sleep_log";
	public final static String ITEM_ASLEEP_TIME_LONG = "asleep_time_long";
	protected final static String ITEM_ASLEEP_TIME = "asleep_time";
	protected final static String ITEM_AWAKE_TIME = "awake_time";
	protected final static String ITEM_TYPE_OF_SLEEP = "type_of_sleep";
	protected final static String ITEM_TOTAL_SLEEP = "total_sleep";
	private final static long MIN_PER_HR = 60;
	private static final long HOUR_IN_MILLISECONDS = 3600000;
	
	public final static String[] ITEMS = { ITEM_ASLEEP_TIME, ITEM_AWAKE_TIME,
			ITEM_TYPE_OF_SLEEP, ITEM_TOTAL_SLEEP };
	public final static int[] ITEM_IDS = { R.id.asleep_time, R.id.awake_time,
			R.id.type_of_sleep, R.id.total_sleep };

	public static final String ASLEEP_TIME = "asleep_time";
	public static final String AWAKE_TIME = "awake_time";
	protected static final String TIME_SLEPT = "time_slept";
	public static final String NAP = "nap";
	public static final String RATING = "rating";
	public static final String COMMENTS = "comments";
	private static final String CONCENTRATION = "concentration";

	private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
			"MMM dd hh:mm a", Locale.US);
	protected static final String[] COLUMNS = { ASLEEP_TIME, AWAKE_TIME,
			TIME_SLEPT, NAP, RATING, CAFFEINE, ALCOHOL, NICOTINE, SUGAR, SCREEN_TIME,
			EXERCISE, COMMENTS, CONCENTRATION };

	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ " (" + ASLEEP_TIME + " LONG PRIMARY KEY, " + AWAKE_TIME + " LONG, "
			+ TIME_SLEPT + " LONG, " + NAP + " INT, " + RATING + " INT, " + CAFFEINE
			+ " INT, " + ALCOHOL + " INT, " + NICOTINE + " INT, " + SUGAR + " INT, "
			+ SCREEN_TIME + " INT, " + EXERCISE + " INT, " + COMMENTS
			+ " VARCHAR(255), "+ CONCENTRATION +" VARCHAR(10));";
	
	private static final String SELECT_LAST_ASLEEP_TIME="SELECT MAX ("+ ASLEEP_TIME +") FROM "+TABLE_NAME+";";

	private Map<String, ?> createItem(long longAsleepTime, String asleepTime,
			String awakeTime, String typeOfSleep, String totalSleep) {
		Map<String, String> item = new HashMap<String, String>();
		item.put(ITEM_ASLEEP_TIME_LONG, String.valueOf(longAsleepTime));
		item.put(ITEM_ASLEEP_TIME, asleepTime);
		item.put(ITEM_AWAKE_TIME, awakeTime);
		item.put(ITEM_TYPE_OF_SLEEP, typeOfSleep);
		item.put(ITEM_TOTAL_SLEEP, totalSleep);
		return item;
	}

	private SQLiteDatabase mDb;
	private DatabaseHelper mDbHelper;
	private static Context mContext;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			mContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

	public SleepLogHelper(Context context) {
		mDbHelper = new DatabaseHelper(context, TABLE_NAME, null, TABLE_VERSION);
		mDb = mDbHelper.getWritableDatabase();
	}

	public void close() {
		mDbHelper.close();
	}

	public boolean insertLog(long asleepTime, long awakeTime, boolean isNap) {
		ContentValues values = new ContentValues();
		values.put(ASLEEP_TIME, asleepTime);
		values.put(AWAKE_TIME, awakeTime);
		values.put(TIME_SLEPT, (awakeTime - asleepTime));
		if (isNap) {
			values.put(NAP, 1);
		} else {
			values.put(NAP, 0);
		}
		values.put(CAFFEINE, 0);
		values.put(ALCOHOL, 0);
		values.put(NICOTINE, 0);
		values.put(SUGAR, 0);
		values.put(SCREEN_TIME, 0);
		values.put(EXERCISE, 0);
		return (mDb.insert(TABLE_NAME, null, values) > 0);
	}

	public boolean updateConcentration(long asleepTime,String concentration){
		ContentValues values = new ContentValues();
		if(asleepTime==0){
			Cursor cursor=mDb.rawQuery(SELECT_LAST_ASLEEP_TIME,null);
			while(cursor.moveToNext())
				asleepTime=cursor.getLong(0);
		}
		values.put(CONCENTRATION, concentration);
		String whereClause=ASLEEP_TIME+ "="+asleepTime;
		return (mDb.update(TABLE_NAME, values, whereClause, null) > 0);
	}
	
	public boolean updateSleepType(long asleepTime, String sleepType){
		ContentValues values = new ContentValues();
		values.put(ITEM_TYPE_OF_SLEEP,sleepType);
		String whereClause = ASLEEP_TIME + "=" + asleepTime;
		return (mDb.update(TABLE_NAME, values, whereClause, null) > 0);
	}
	
	public boolean updateAsleepTime(long asleepTime, long newAsleepTime) {
		ContentValues values = new ContentValues();
		values.put(ASLEEP_TIME, newAsleepTime);
		long awakeTime = (Long)queryLog(asleepTime).get("AwakeTime");
		values.put(TIME_SLEPT, awakeTime - newAsleepTime);
		String whereClause = ASLEEP_TIME + "=" + asleepTime;
		return (mDb.update(TABLE_NAME, values, whereClause, null) > 0);
	}

	public boolean updateAwakeTime(long asleepTime, long awakeTime) {
		ContentValues values = new ContentValues();
		values.put(AWAKE_TIME, awakeTime);
		values.put(TIME_SLEPT, awakeTime - asleepTime);
		String whereClause = ASLEEP_TIME + "=" + asleepTime;
		return (mDb.update(TABLE_NAME, values, whereClause, null) > 0);
	}

	public boolean updateRating(long asleepTime, int rating) {
		ContentValues values = new ContentValues();
		values.put(RATING, rating);
		String whereClause = ASLEEP_TIME + "=" + asleepTime;
		return (mDb.update(TABLE_NAME, values, whereClause, null) > 0);
	}

	public boolean updateExcuses(long asleepTime, boolean[] excuses) {
		ContentValues values = new ContentValues();
		for (int i = 0; i < EXCUSES.length; i++) {
			if (excuses[i]) {
				values.put(EXCUSES[i], 1);
			} else {
				values.put(EXCUSES[i], 0);
			}
		}
		String whereClause = ASLEEP_TIME + "=" + asleepTime;
		return (mDb.update(TABLE_NAME, values, whereClause, null) > 0);
	}

	public boolean updateComments(long asleepTime, String comments) {
		ContentValues values = new ContentValues();
		values.put(COMMENTS, comments);
		String whereClause = ASLEEP_TIME + "=" + asleepTime;
		return (mDb.update(TABLE_NAME, values, whereClause, null) > 0);
	}

	public ArrayList<Map<String, ?>> queryAll() {
		String orderBy = ASLEEP_TIME + " DESC";
		Cursor cursor = mDb.query(TABLE_NAME, COLUMNS, null, null, null, null,
				orderBy);

		if (cursor != null) {
			ArrayList<Map<String, ?>> queryResult = new ArrayList<Map<String, ?>>();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				long asleepT = cursor.getLong(cursor
						.getColumnIndex(SleepLogHelper.ASLEEP_TIME));
				long awakeT = cursor.getLong(cursor
						.getColumnIndex(SleepLogHelper.AWAKE_TIME));
				String fAsleepTime = mSimpleDateFormat.format(new Date(asleepT));
				String fAwakeTime = "-";
				if (awakeT > 0) {
					fAwakeTime = mSimpleDateFormat.format(new Date(awakeT));
				}
				long elapsedT = awakeT - asleepT;
				long hours = TimeUnit.MILLISECONDS.toHours(elapsedT);
				long mins = TimeUnit.MILLISECONDS.toMinutes(elapsedT) % MIN_PER_HR;

				String totalSleep = String.format(Locale.US, "%d hours, %d minutes",
						hours, mins);

				if (elapsedT < 0) {
					totalSleep = mContext.getResources().getString(R.string.pending);
				}
				boolean wasNap = cursor.getInt(cursor
						.getColumnIndex(SleepLogHelper.NAP)) > 0;
				String typeOfSleep = mContext.getResources().getString(
						R.string.night_sleep);
				if (wasNap) {
					typeOfSleep = mContext.getResources().getString(R.string.nap);
				}
				queryResult.add(createItem(asleepT, fAsleepTime, fAwakeTime,
						typeOfSleep, totalSleep));
			}
			return queryResult;
		} else
			return null;
	}

	public HashMap<String, Object> queryLog(long asleepTime) {
		
		HashMap<String, Object> returnResults = new HashMap<String, Object>();
		String selection = ASLEEP_TIME + "=" + asleepTime;
		Cursor cursor = mDb.query(TABLE_NAME, COLUMNS, selection, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			long AsleepTime = cursor.getLong(cursor
					.getColumnIndex(SleepLogHelper.ASLEEP_TIME));
			long AwakeTime = cursor.getLong(cursor
					.getColumnIndex(SleepLogHelper.AWAKE_TIME));
			String conRating = cursor.getString(cursor.getColumnIndex(SleepLogHelper.CONCENTRATION));
			int rating = cursor.getInt(cursor.getColumnIndex(SleepLogHelper.RATING));
			String comments = cursor.getString(cursor
					.getColumnIndex(SleepLogHelper.COMMENTS));
			boolean wasNap = cursor.getInt(cursor.getColumnIndex(SleepLogHelper.NAP)) > 0;
			returnResults.put("AsleepTime", AsleepTime);
			returnResults.put("AwakeTime", AwakeTime);
			returnResults.put("rating", rating);
			returnResults.put("comments", comments);
			returnResults.put("wasNap", wasNap);
			returnResults.put("concentration", conRating);
			for (int i = 0; i < EXCUSES.length; i++) {
				int temp = cursor.getInt(cursor.getColumnIndex(SleepLogHelper.EXCUSES[i]));
				returnResults.put(EXCUSES[i], temp);
			}
		}
		return returnResults;
	}

	
	public Map<String,Double> queryLogDay(long startDay, long endDay) {
		String selection = ASLEEP_TIME + ">=" + startDay + " AND " + ASLEEP_TIME
				+ "<" + endDay;
		//return mDb.query(TABLE_NAME, COLUMNS, selection, null, null, null, null);
		double totalHoursSlept=0;
		double napHoursSlept=0;
		Cursor cursor=mDb.query(TABLE_NAME, COLUMNS, selection, null, null, null, null);
		Map<String,Double> result=new HashMap<String,Double>();
		if (cursor != null) {
			cursor.moveToFirst();
			for (int j = 0; j < cursor.getCount(); j++) {
				long startSleep = cursor.getLong(cursor
						.getColumnIndex(SleepLogHelper.ASLEEP_TIME));
				long endSleep = cursor.getLong(cursor
						.getColumnIndex(SleepLogHelper.AWAKE_TIME));
				double totalSleep = endSleep - startSleep;
				if (cursor.getInt(cursor.getColumnIndex(SleepLogHelper.NAP)) == 0) {
					totalHoursSlept += totalSleep / HOUR_IN_MILLISECONDS;
					napHoursSlept += totalSleep / HOUR_IN_MILLISECONDS;
				} else {
					totalHoursSlept += totalSleep / HOUR_IN_MILLISECONDS;
				}
				cursor.moveToNext();
			}
			result.put("totalHoursSlept", totalHoursSlept);
			result.put("napHoursSlept", napHoursSlept);
		}
		return result;
			
	}

	public double queryLogAvgMonth(long startDay, long endDay) {
		String rawSelection = "SELECT AVG(" + TIME_SLEPT + ") FROM " + TABLE_NAME
				+ " WHERE " + NAP + "=0 AND (" + ASLEEP_TIME + " BETWEEN " + startDay
				+ " AND " + endDay + ")";
		//return mDb.rawQuery(rawSelection, null);
		Cursor cursor=mDb.rawQuery(rawSelection, null);
		double temp=-1.0;
		if (cursor.moveToFirst()) {
			temp = cursor.getLong(0);
			temp = temp / HOUR_IN_MILLISECONDS;
		}
		return temp;
	}

	public double queryLogExcusesTime(String excuse) {
		String rawSelection = "SELECT AVG(" + TIME_SLEPT + "*1.0) FROM "
				+ TABLE_NAME + " WHERE " + excuse + ">0 AND " + NAP + "=0";
		//return mDb.rawQuery(rawSelection, null);
		Cursor timeSleptCursor=mDb.rawQuery(rawSelection, null);
		double avgTimeSlept=-1.0;
		if (timeSleptCursor.moveToFirst()) {
			avgTimeSlept = timeSleptCursor.getFloat(0) / HOUR_IN_MILLISECONDS;
		}
		return avgTimeSlept;
	}

	public double queryLogExcusesQuality(String excuse) {
		String rawSelection = "SELECT AVG(" + RATING + "*1.0) FROM " + TABLE_NAME
				+ " WHERE " + excuse + "=1";
		Cursor qualityCursor=mDb.rawQuery(rawSelection, null);
		double avgQuality=-1.0;
		if (qualityCursor.moveToFirst()) {
			avgQuality = qualityCursor.getFloat(0) / HOUR_IN_MILLISECONDS;
		}
		return avgQuality;
	}

	public int numEntries() {
		String selection = NAP + "=0";
		Cursor temp = mDb.query(TABLE_NAME, COLUMNS, selection, null, null, null,
				null);
		return temp.getCount();
	}

	public boolean deleteAllEntries() {
		return (mDb.delete(TABLE_NAME, null, null) > 0);
	}

	public boolean deleteEntry(long asleepTime) {
		String whereClause = ASLEEP_TIME + "=" + asleepTime;
		return (mDb.delete(TABLE_NAME, whereClause, null) > 0);
	}
}
