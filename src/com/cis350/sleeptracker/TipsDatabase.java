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
		private String[] allTips;

		public DatabaseHelper(Context context, String name, CursorFactory factory,
				int version, String[] allTips) {
			super(context, name, factory, version);
			this.allTips = allTips;
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
			for(String tip : allTips){
				String[] tokenizedTip = tip.split("µ");
				int tipId = 0;
				boolean isFirst = true;
				for(String tipToken : tokenizedTip){
					if(isFirst){
						ContentValues values = new ContentValues();
						values.put("tip", tipToken);
						db.insert("TIPS", null, values);
						tipId = getTipId(db, tipToken);
						isFirst = false;
					} else {
						ContentValues values = new ContentValues();
						values.put("tipId", tipId);
						values.put("excuseId", getExcuseId(db, tipToken));
						db.insert("TIPS_EXCUSES", null, values);
					}
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

	public TipsDatabase(Context context, String[] tips) {
		mDbHelper = new DatabaseHelper(context, DATABASE_NAME, null, TABLE_VERSION, tips);
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
