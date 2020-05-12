package com.example.guitarsongbook.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guitarsongbook.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends SearchLaunchingFragment {


    public static NavigationFragment newInstance() { //String query) {
        return new NavigationFragment();
    }


    public NavigationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }
}
