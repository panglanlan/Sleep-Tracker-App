package com.cis350.sleeptracker;

import java.util.HashMap;

import com.cis350.sleeptracker.database.SleepTrackerDatabase;

import android.content.SharedPreferences;

public class UserHabits {
	HashMap<String, Boolean> habits;
	
	public UserHabits(SharedPreferences preferences){
		habits = new HashMap<String, Boolean>();
		for(String excuse : SleepTrackerDatabase.EXCUSES){
			habits.put(excuse, preferences.getBoolean(excuse, true));
		}
	}
	
	public Boolean getUserHabit(String excuse){
		return habits.get(excuse);
	}
}
