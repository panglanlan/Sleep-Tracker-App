package com.cis350.sleeptracker;

import java.util.Arrays;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import com.cis350.sleeptracker.database.TipsDatabase;

/** @author Michael Collis
 * @version 20131020 */
public class ProfileFragment extends Fragment {

	private SharedPreferences mPreferences;
	private Spinner mClassYearSpinner;
	private Spinner mSchoolSpinner;
	private RadioGroup mSmokingSelector;
	private RadioGroup mAlcoholSelector;
	private RadioGroup mCoffeeSelector;
	private RadioGroup mExerciseSelector;
	private List<RadioButton> mSmokingList;
	private List<RadioButton> mAlcoholList;
	private List<RadioButton> mCoffeeList;
	private List<RadioButton> mExerciseList;

	private ArrayAdapter<String> mClassYearAdapter;
	private ArrayAdapter<String> mSchoolAdapter;

	private static final String PREF_CLASS_YEAR = "classYear";
	private static final String PREF_SCHOOL = "school";
	private static final String PREF_SMOKING = "pref_smoke";
	private static final String PREF_ALCOHOL = "pref_alcohol";
	private static final String PREF_COFFEE = "pref_coffee";
	private static final String PREF_EXERCISE = "pref_exercise";

	// TODO: THESE ARE BEING STORED AS INTS BY THE PROFILE AND ACCESSED AS
	// BOOLEANS BY THE USERHABITS INIT!
	/*
	 * private static final String PREF_SMOKING = TipsDatabase.getNicotine();
	 * private static final String PREF_ALCOHOL = TipsDatabase.getAlcohol();
	 * private static final String PREF_COFFEE = TipsDatabase.getCaffeine();
	 * private static final String PREF_EXERCISE = TipsDatabase.getExercise();
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mPreferences = getActivity().getApplicationContext().getSharedPreferences(
				MainActivity.MAIN, Context.MODE_PRIVATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_questions,
				container, false);
		buildSpinners(view);
		buildRadioButtons(view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		populateProfile();
	}

	private void populateProfile() {
		if (mPreferences != null) {
			String classYear = mPreferences.getString(PREF_CLASS_YEAR, getResources()
					.getStringArray(R.array.class_year_array)[0]);
			String school = mPreferences.getString(PREF_SCHOOL, getResources()
					.getStringArray(R.array.school_array)[0]);
			mClassYearSpinner.setSelection(mClassYearAdapter.getPosition(classYear));
			mSchoolSpinner.setSelection(mSchoolAdapter.getPosition(school));

			int smokingPref = mPreferences.getInt(PREF_SMOKING, -1);
			int alcoholPref = mPreferences.getInt(PREF_ALCOHOL, -1);
			int coffeePref = mPreferences.getInt(PREF_COFFEE, -1);
			int exercisePref = mPreferences.getInt(PREF_EXERCISE, -1);

			if (smokingPref >= 0) {
				mSmokingList.get(smokingPref).setChecked(true);
			}
			if (alcoholPref >= 0) {
				mAlcoholList.get(alcoholPref).setChecked(true);;
			}
			if (coffeePref >= 0) {
				mCoffeeList.get(coffeePref).setChecked(true);
			}
			if (exercisePref >= 0) {
				mExerciseList.get(exercisePref).setChecked(true);
			}
		}
	}

	private void buildSpinners(View view) {
		mClassYearSpinner = (Spinner) view
				.findViewById(R.id.profile_class_year_spinner);
		mClassYearAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, getResources()
						.getStringArray(R.array.class_year_array));
		mClassYearAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mClassYearSpinner.setAdapter(mClassYearAdapter);

		mSchoolSpinner = (Spinner) view.findViewById(R.id.profile_school_spinner);
		mSchoolAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, getResources()
						.getStringArray(R.array.school_array));
		mSchoolAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSchoolSpinner.setAdapter(mSchoolAdapter);
	}

	private void buildRadioButtons(View view) {
		mSmokingSelector = (RadioGroup) view
				.findViewById(R.id.profile_smoking_selector);
		mAlcoholSelector = (RadioGroup) view
				.findViewById(R.id.profile_alcohol_selector);
		mCoffeeSelector = (RadioGroup) view
				.findViewById(R.id.profile_coffee_selector);
		mExerciseSelector = (RadioGroup) view
				.findViewById(R.id.profile_exercise_selector);

		mSmokingList = Arrays.asList(new RadioButton[] {
				(RadioButton) view.findViewById(R.id.profile_smoking_radio_1),
				(RadioButton) view.findViewById(R.id.profile_smoking_radio_2),
				(RadioButton) view.findViewById(R.id.profile_smoking_radio_3),
				(RadioButton) view.findViewById(R.id.profile_smoking_radio_4),
				(RadioButton) view.findViewById(R.id.profile_smoking_radio_5) });

		mAlcoholList = Arrays.asList(new RadioButton[] {
				(RadioButton) view.findViewById(R.id.profile_alcohol_radio_1),
				(RadioButton) view.findViewById(R.id.profile_alcohol_radio_2),
				(RadioButton) view.findViewById(R.id.profile_alcohol_radio_3),
				(RadioButton) view.findViewById(R.id.profile_alcohol_radio_4),
				(RadioButton) view.findViewById(R.id.profile_alcohol_radio_5) });

		mCoffeeList = Arrays.asList(new RadioButton[] {
				(RadioButton) view.findViewById(R.id.profile_coffee_radio_1),
				(RadioButton) view.findViewById(R.id.profile_coffee_radio_2),
				(RadioButton) view.findViewById(R.id.profile_coffee_radio_3),
				(RadioButton) view.findViewById(R.id.profile_coffee_radio_4),
				(RadioButton) view.findViewById(R.id.profile_coffee_radio_5) });

		mExerciseList = Arrays.asList(new RadioButton[] {
				(RadioButton) view.findViewById(R.id.profile_exercise_radio_1),
				(RadioButton) view.findViewById(R.id.profile_exercise_radio_2),
				(RadioButton) view.findViewById(R.id.profile_exercise_radio_3),
				(RadioButton) view.findViewById(R.id.profile_exercise_radio_4),
				(RadioButton) view.findViewById(R.id.profile_exercise_radio_5) });
	}

	protected void saveProfile() {
		if (mPreferences != null) {
			Editor mPrefEditor = mPreferences.edit();
			mPrefEditor.putString(PREF_CLASS_YEAR,
					(String) mClassYearSpinner.getSelectedItem());
			mPrefEditor.putString(PREF_SCHOOL,
					(String) mSchoolSpinner.getSelectedItem());

			int smokingChecked = mSmokingSelector.getCheckedRadioButtonId();
			if (smokingChecked != -1) {
				int smokingPref = mSmokingSelector.indexOfChild(getView().findViewById(
						smokingChecked));
				mPrefEditor.putInt(PREF_SMOKING, smokingPref);
			} else {
				mPrefEditor.putInt(PREF_SMOKING, -1);
			}

			int alcoholChecked = mAlcoholSelector.getCheckedRadioButtonId();
			if (alcoholChecked != -1) {
				int alcoholPref = mAlcoholSelector.indexOfChild(getView().findViewById(
						alcoholChecked));
				mPrefEditor.putInt(PREF_ALCOHOL, alcoholPref);
			} else {
				mPrefEditor.putInt(PREF_ALCOHOL, -1);
			}

			int coffeeChecked = mCoffeeSelector.getCheckedRadioButtonId();
			if (coffeeChecked != -1) {
				int coffeePref = mCoffeeSelector.indexOfChild(getView().findViewById(
						coffeeChecked));
				mPrefEditor.putInt(PREF_COFFEE, coffeePref);
			} else {
				mPrefEditor.putInt(PREF_COFFEE, -1);
			}

			int exerciseChecked = mExerciseSelector.getCheckedRadioButtonId();
			if (exerciseChecked != -1) {
				int exercisePref = mExerciseSelector.indexOfChild(getView()
						.findViewById(exerciseChecked));
				mPrefEditor.putInt(PREF_EXERCISE, exercisePref);
			} else {
				mPrefEditor.putInt(PREF_EXERCISE, -1);
			}
			mPrefEditor.apply();
		}
	}
}
