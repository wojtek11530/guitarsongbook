package com.wojciechkorczynski.guitarsongbook.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.wojciechkorczynski.guitarsongbook.R;

public class SearchLaunchingFragment extends Fragment {

    private boolean animateTransition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Context context = getContext();
        assert context != null;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        animateTransition = sharedPref.getBoolean(
                context.getResources().getString(R.string.switch_animation_pref_key),
                true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.begin_searching) {
            runSearchViewFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void runSearchViewFragment() {
        SearchFragment searchFragment = SearchFragment.newInstance();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (animateTransition) {
            fragmentTransaction.
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        fragmentTransaction.replace(R.id.fragment_container_fl_, searchFragment)
                .addToBackStack(null).commit();
    }
}
