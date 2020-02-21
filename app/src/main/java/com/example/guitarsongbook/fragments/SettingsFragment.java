package com.example.guitarsongbook.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;


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
        Objects.requireNonNull(getActivity()).setTitle(Objects.requireNonNull(getContext()).getString(R.string.settings));
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

}
