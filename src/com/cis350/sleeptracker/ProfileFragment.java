package com.cis350.sleeptracker;

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
import android.widget.Spinner;

/** @author Michael Collis
 * @version 20131112 */
public class ProfileFragment extends Fragment {

	private SharedPreferences mPreferences;
	private Spinner mClassYearSpinner;
	private Spinner mSchoolSpinner;
	private Spinner mGenderSpinner;

	private ArrayAdapter<String> mClassYearAdapter;
	private ArrayAdapter<String> mSchoolAdapter;
	private ArrayAdapter<String> mGenderAdapter;

	private static final String PREF_CLASS_YEAR = "classYear";
	private static final String PREF_SCHOOL = "school";
	private static final String PREF_GENDER = "gender";

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
			String gender = mPreferences.getString(PREF_GENDER, getResources()
					.getStringArray(R.array.gender_array)[0]);
			mClassYearSpinner.setSelection(mClassYearAdapter.getPosition(classYear));
			mSchoolSpinner.setSelection(mSchoolAdapter.getPosition(school));
			mGenderSpinner.setSelection(mGenderAdapter.getPosition(gender));
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

		mGenderSpinner = (Spinner) view.findViewById(R.id.profile_gender_spinner);
		mGenderAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, getResources()
						.getStringArray(R.array.gender_array));
		mGenderAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mGenderSpinner.setAdapter(mGenderAdapter);
	}

	protected void saveProfile() {
		if (mPreferences != null) {
			Editor mPrefEditor = mPreferences.edit();
			mPrefEditor.putString(PREF_CLASS_YEAR,
					(String) mClassYearSpinner.getSelectedItem());
			mPrefEditor.putString(PREF_SCHOOL,
					(String) mSchoolSpinner.getSelectedItem());
			mPrefEditor.putString(PREF_GENDER,
					(String) mGenderSpinner.getSelectedItem());
			mPrefEditor.apply();
		}
	}
}
