package com.example.guitarsongbook.fragments;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;


public class SettingsFragment extends PreferenceFragmentCompat {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.setAppBarTitle(getResources().getString(R.string.settings));
    }

    @Override
    public void onStop() {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.manageSharedPreferences();
        super.onStop();
    }
}
