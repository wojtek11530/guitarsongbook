package com.example.guitarsongbook.fragments;


import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;


import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.setAppBarTitle(getResources().getString(R.string.settings));
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

}
