package com.cis350.sleeptracker.database;

import java.util.ArrayList;

import com.cis350.sleeptracker.UserHabits;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TipsDatabase extends SleepTrackerDatabase {
	private static final String TAG = "TipsDatabase";
	private static final String DATABASE_NAME = "TipsDatabase";
	private static final int TABLE_VERSION = 2;

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
		new Tip("The before-bed cigarette will keep you up at night.", new String[]{NICOTINE}),
		new Tip("Less sleep is associated with weight gain, while giving yourself an extra hour of sleep can equate to almost 15 pounds of weight loss.", new String[]{}),
		new Tip("The Midnight Snack is your enemy. After eating you are less likely to settle down for bed.", new String[]{SUGAR}),
		new Tip("If you MUST have dessert, switch it up and eat it before dinner. The earlier the sugar, the earlier to bed.", new String[]{SUGAR}),
		new Tip("The weekends are not a time to sleep in! By developing a regular sleep schedule and waking up at the same time each day (even on weekends), you will improve the quality and duration of your sleep.", new String[]{}),
		new Tip("Plan your work accordingly. Looking at blue light before bed (light from a computer screen or phone) makes it harder to fall asleep. By prioritizing your email-sending and paper writing earlier in the day, and your textbook reading later in the day, it will be easier for you to get to sleep.", new String[]{SCREEN_TIME}),
		new Tip("Sleep and Exercise go hand in hand. One cannot effectively occur without the other. If you do not get enough sleep, you are not reaping the full benefits of exercising AND if you are not exercising you are not having the most restful night of sleep possible!", new String[]{}),
		new Tip("It�s not just beauty sleep; it�s brain sleep as well. Not only can the effects of sleep be seen on your face and body, but lack of sleep affects your cognitive abilities as well and is even more immediate.", new String[]{}),
		new Tip("Would you take an exam legally intoxicated? Hopefully not. So why would you take one without sleep? One night without sleep leaves you performing like you were legally drunk at a blood alcohol content of .08. So, pulling that all-nighter cram session may not be the best idea after all.", new String[]{}),
		new Tip("Plans Past 50? If you are sleep deprived you are 20% more likely to die in 20 years. Get a good night�s sleep while you still can.", new String[]{}),
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

	private void appendCondition(StringBuilder strB, boolean doAppend, String excuse) {
		if(doAppend){
			strB.append(" OR excuse = \"" + excuse + "\"");
		}
	}
	
	public ArrayList<String> getFilteredTips(UserHabits userHabits) {
		StringBuilder conditions = new StringBuilder();
		for(String excuse : SleepTrackerDatabase.EXCUSES){
			appendCondition(conditions, !userHabits.getUserHabit(excuse), excuse);
		}

		String query = "SELECT tip " +
				"FROM TIPS " +
				"WHERE tipId NOT IN ( " +
				"	SELECT DISTINCT tipId " +
				"	FROM TIPS_EXCUSES TE, EXCUSES E " +
				"	WHERE TE.excuseId = E.excuseId " +
				"	AND ( 1 = 2 " +
					conditions.toString() +
				"	)); ";
		
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
