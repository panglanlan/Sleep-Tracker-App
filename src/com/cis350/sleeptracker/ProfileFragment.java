/**
 * 
 */
package com.cis350.sleeptracker;

import com.cis350.sleeptracker.database.TipsDatabase;

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
import android.widget.RadioGroup;
import android.widget.Spinner;

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

	private static final String PREF_CLASS_YEAR = "classYear";
	private static final String PREF_SCHOOL = "school";
	private static final String PREF_SMOKING = TipsDatabase.getNicotine();
	private static final String PREF_ALCOHOL = TipsDatabase.getAlcohol();
	private static final String PREF_COFFEE = TipsDatabase.getCaffeine();
	private static final String PREF_EXERCISE = TipsDatabase.getExercise();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_questions,
				container, false);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mPreferences = getActivity().getApplicationContext().getSharedPreferences(
				MainActivity.MAIN, Context.MODE_PRIVATE);
		populateSpinners();
		populateRadioButtons();
	}

	private void populateSpinners() {
		mClassYearSpinner = (Spinner) getView().findViewById(
				R.id.profile_class_year_spinner);
		ArrayAdapter<String> classYearAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_dropdown_item,
				getResources().getStringArray(R.array.class_year_array));
		classYearAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mClassYearSpinner.setAdapter(classYearAdapter);

		mSchoolSpinner = (Spinner) getView().findViewById(
				R.id.profile_school_spinner);
		ArrayAdapter<String> schoolAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_dropdown_item,
				getResources().getStringArray(R.array.school_array));
		schoolAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSchoolSpinner.setAdapter(schoolAdapter);

		if (mPreferences != null) {
			String classYear = mPreferences.getString(PREF_CLASS_YEAR, getResources()
					.getStringArray(R.array.class_year_array)[0]);
			String school = mPreferences.getString(PREF_SCHOOL, getResources()
					.getStringArray(R.array.school_array)[0]);
			mClassYearSpinner.setSelection(classYearAdapter.getPosition(classYear));
			mSchoolSpinner.setSelection(schoolAdapter.getPosition(school));
		}
	}

	private void populateRadioButtons() {
		mSmokingSelector = (RadioGroup) getView().findViewById(
				R.id.profile_smoking_selector);
		mAlcoholSelector = (RadioGroup) getView().findViewById(
				R.id.profile_alcohol_selector);
		mCoffeeSelector = (RadioGroup) getView().findViewById(
				R.id.profile_coffee_selector);
		mExerciseSelector = (RadioGroup) getView().findViewById(
				R.id.profile_exercise_selector);

		if (mPreferences != null) {
			int smokingPref = mPreferences.getInt(PREF_SMOKING, -1);
			int alcoholPref = mPreferences.getInt(PREF_ALCOHOL, -1);
			int coffeePref = mPreferences.getInt(PREF_COFFEE, -1);
			int exercisePref = mPreferences.getInt(PREF_EXERCISE, -1);
			if (smokingPref >= 0) {
				mSmokingSelector.check(smokingPref);
			}
			if (alcoholPref >= 0) {
				mAlcoholSelector.check(alcoholPref);
			}
			if (coffeePref >= 0) {
				mCoffeeSelector.check(coffeePref);
			}
			if (exercisePref >= 0) {
				mExerciseSelector.check(exercisePref);
			}
		}
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
