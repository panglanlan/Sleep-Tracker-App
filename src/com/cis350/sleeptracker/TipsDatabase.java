package com.cis350.sleeptracker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TipsDatabase {
	private static final String TAG = "TipsDatabase";
	private static final String DATABASE_NAME = "TipsDatabase";
	private static final int TABLE_VERSION = 1;

	protected static final String CAFFEINE = "caffeine";
	protected static final String ALCOHOL = "alcohol";
	protected static final String NICOTINE = "nicotine";
	protected static final String SUGAR = "sugar";
	protected static final String SCREEN_TIME = "screen_time";
	protected static final String EXERCISE = "exercise";

	protected static final String[] EXCUSES = { CAFFEINE, ALCOHOL, NICOTINE,
		SUGAR, SCREEN_TIME, EXERCISE };
	
	private class Tip {
		private String tip;
		private String[] tipExcuses;

		public Tip(String tip, String[] tipExcuses) {
			super();
			this.tip = tip;
			this.tipExcuses = tipExcuses;
		}

		public String getTip() {
			return tip;
		}

		public String[] getTipExcuses() {
			return tipExcuses;
		}
	}

	protected final Tip[] TIPS = {
	    new Tip("Use your bed primarily for sleeping (or other obvious bed-related activities). Avoid doing schoolwork, watching t.v. or using the computer.", new String[]{}),
		new Tip("Go to bed and wake up at approximately the same times. This helps your body\'s internal clock get tired and more awake at appropriate times.", new String[]{}),
		new Tip("Make sure your bed and bedroom is a comfortable place! Do you need more mattress or pillow support? Use ear plugs, a white noise machine, and/or a night mask if needed.", new String[]{}),
		new Tip("Do something relaxing in the 30 minutes leading up to bedtime. Try taking a warm shower, reading, or listening to music.", new String[]{}),
		new Tip("Avoid consuming alcohol in the 4 hours before bed. Alcohol tends to make you sleepy but actually interferes with deep sleep.", new String[]{ALCOHOL}),
		new Tip("Avoid using tobacco in the 4 hours before bed. Tobacco acts as a stimulant.", new String[]{NICOTINE}),
		new Tip("Avoid caffeine later than mid-day.", new String[]{CAFFEINE}),
		new Tip("Exercise! Aim for earlier in the day as exercise in the 4 hours before bedtime can actually wake your body up.", new String[]{}),
		new Tip("Turn your clock away from your bed. If you can\'t fall asleep then get up and do a quiet activity until you start to feel sleepy.", new String[]{}),
		new Tip("Decrease stress in whatever ways possible! If you lie awake thinking at night, keep a  journal near bed. Make a to-do list or write down any concerns you have. The journal is place to keep these things so they can\'t distract you overnight.", new String[]{})
	};
	
	private static final String TIP_TABLE_CREATE =
			"CREATE TABLE TIPS (" + 
					"tipId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
					"tip VARCHAR2(2048) UNIQUE);";

	private static final String EXCUSES_TABLE_CREATE =
			"CREATE TABLE EXCUSES (" + 
					"excuseId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
					"excuse VARCHAR2(2048) UNIQUE);";

	private static final String TIP_EXCUSES_TABLE_CREATE =
			"CREATE TABLE TIPS_EXCUSES (" + 
					"tipId INTEGER REFERENCES TIPS, " +
					"excuseId INTEGER REFERENCES EXCUSES, " +
					"PRIMARY KEY (tipId, excuseId));";

	private SQLiteDatabase mDb;
	private DatabaseHelper mDbHelper;
	private static Context mContext;

	private class DatabaseHelper extends SQLiteOpenHelper {
		private Tip[] allTips;

		public DatabaseHelper(Context context, String name, CursorFactory factory,
				int version, Tip[] tips) {
			super(context, name, factory, version);
			this.allTips = tips;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TIP_TABLE_CREATE);
			db.execSQL(EXCUSES_TABLE_CREATE);
			db.execSQL(TIP_EXCUSES_TABLE_CREATE);
			insertExcuses(db, EXCUSES);
			insertTips(db);
			/*
			Cursor temp = db.query("tips", null, null, null, null, null, null);
			Log.v("tips", DatabaseUtils.dumpCursorToString(temp));
			temp = db.query("excuses", null, null, null, null, null, null);
			Log.v("excuses", DatabaseUtils.dumpCursorToString(temp));
			temp = db.query("tips_excuses", null, null, null, null, null, null);
			Log.v("tips_excuses", DatabaseUtils.dumpCursorToString(temp));
			*/
		}

		private void insertExcuses(SQLiteDatabase db, String[] excuses) {
			for(String excuse : excuses){
				ContentValues values = new ContentValues();
				values.put("excuse", excuse);
				db.insert("EXCUSES", null, values);
			}
		}

		private void insertTips(SQLiteDatabase db) {
			// First is tip ; next are excuses
			for(Tip tip : allTips){
				String tipValue = tip.getTip();
				String[] tipExcuses = tip.getTipExcuses();
				int tipId = 0;

				ContentValues values = new ContentValues();
				values.put("tip", tipValue);
				db.insert("TIPS", null, values);
				tipId = getTipId(db, tipValue);

				for(String excuse : tipExcuses){
					values = new ContentValues();
					values.put("tipId", tipId);
					values.put("excuseId", getExcuseId(db, excuse));
					db.insert("TIPS_EXCUSES", null, values);
				}
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion);
			db.execSQL("DROP TABLE IF EXISTS TIPS");
			db.execSQL("DROP TABLE IF EXISTS EXCUSES");
			db.execSQL("DROP TABLE IF EXISTS TIPS_EXCUSES");
			onCreate(db);
		}
	}

	public TipsDatabase(Context context) {
		mDbHelper = new DatabaseHelper(context, DATABASE_NAME, null, TABLE_VERSION, TIPS);
		mDb = mDbHelper.getWritableDatabase();
		mContext = context;
	}

	private void appendCondition(StringBuilder strB, boolean append, String excuse) {
		if(append){
			strB.append(" OR excuseID = " + getExcuseId(mDb, excuse));
		}
	}
	
	public ArrayList<String> getFilteredTips(boolean nicotineUser, boolean alcoholUser, boolean caffeineUser) {
		StringBuilder conditions = new StringBuilder();
		appendCondition(conditions, !nicotineUser, NICOTINE);
		appendCondition(conditions, !alcoholUser, ALCOHOL);
		appendCondition(conditions, !caffeineUser, CAFFEINE);

		String query = "SELECT tip " +
				"FROM TIPS " +
				"WHERE tipId NOT IN ( " +
				"	SELECT DISTINCT tipId " +
				"	FROM TIPS_EXCUSES " +
				"	WHERE 1 = 2 " +
				conditions.toString() +
				"	); ";
		
		Cursor cursor = mDb.rawQuery(query, null);
		ArrayList<String> filteredTips = new ArrayList<String>();

		while(cursor.moveToNext()){
			filteredTips.add(cursor.getString(cursor.getColumnIndexOrThrow("tip")));
		}
		
		return filteredTips;
	}

	private Integer getTipId(SQLiteDatabase db, String tip) {
		String[] column = { "tipId" };
		Cursor cursor = db.query("TIPS", column, "tip = \"" + tip + "\"", null, null, null, null);
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndexOrThrow(column[0]));
	}

	private Integer getExcuseId(SQLiteDatabase db, String excuse) {
		String[] column = { "excuseId" };
		Cursor cursor = db.query("EXCUSES", column, "excuse = \"" + excuse + "\"", null, null, null, null);
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndexOrThrow(column[0]));
	}
}
