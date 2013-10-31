/**
 * 
 */
package com.cis350.sleeptracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/** @author Michael Collis
 * @version 20131020 */
public class ProfileFragment extends Fragment {

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
		// if (activity instanceof OnItemSelectedListener) {
		// listener = (OnItemSelectedListener) activity;
		// } else
		// throw new ClassCastException(activity.toString()
		// + " must implemenet MyListFragment.OnItemSelectedListener");
	}

}
