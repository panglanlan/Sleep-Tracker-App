package com.cis350.sleeptracker.database;

public class SleepTrackerDatabase {
	protected static final String CAFFEINE = "caffeine";
	protected static final String ALCOHOL = "alcohol";
	protected static final String NICOTINE = "nicotine";
	protected static final String SUGAR = "sugar";
	protected static final String SCREEN_TIME = "screen_time";
	protected static final String EXERCISE = "exercise";
	protected static final int MAX_EXCUSE_VALUE = 5;

	protected static final String[] EXCUSES = { CAFFEINE, ALCOHOL, NICOTINE,
		SUGAR, SCREEN_TIME, EXERCISE };

	public static String getCaffeine() {
		return CAFFEINE;
	}

	public static String getAlcohol() {
		return ALCOHOL;
	}

	public static String getNicotine() {
		return NICOTINE;
	}

	public static String getSugar() {
		return SUGAR;
	}

	public static String getScreenTime() {
		return SCREEN_TIME;
	}

	public static String getExercise() {
		return EXERCISE;
	}

	public static String[] getExcuses() {
		return EXCUSES;
	}

	public static int getMaxExcuseValue() {
		return MAX_EXCUSE_VALUE;
	}

}
