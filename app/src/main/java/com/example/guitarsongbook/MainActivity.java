package com.example.guitarsongbook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.guitarsongbook.fragments.NavigationFragment;
import com.google.android.material.appbar.AppBarLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private final int DEFAULT_ACTION_BAR_ELEVATION = 2;

    Toolbar toolbar;
    FragmentManager fragmentManager;
    private SharedPreferences sharedPref;
    private View mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews();
        configureFragmentManager();
        manageSharedPreferences();

        if (savedInstanceState == null) {
            startFragmentForLaunchedApp();
        } else {
            adjustDisplayingUpButton();
        }
    }

    private void initializeViews() {
        setContentView(R.layout.activity_main);
        mAppBarLayout = findViewById(R.id.app_bar_layout);
        initToolbar();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        if (fragmentManager.getBackStackEntryCount() > 0) {
//            fragmentManager.popBackStack();
//        } else {
//            super.onBackPressed();
//        }
//    }

    private void configureFragmentManager() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                adjustDisplayingUpButton();
            }
        });
    }

    public void manageSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void startFragmentForLaunchedApp() {
        fragmentManager.popBackStack();

        NavigationFragment navigationFragment = NavigationFragment.newInstance();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .replace(R.id.fragment_container_fl_, navigationFragment)
                .commit();
    }

    private void adjustDisplayingUpButton() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        if (fragmentManager.getBackStackEntryCount() > 0) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        } else {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }

    public void setAppBarTitle(String title) {
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(title);
    }

    public void removeAppBarShadow() {
        mAppBarLayout.setElevation(0.0f);
    }

    public void addAppBarShadow() {
        float elevationInDp = 4;
        float elevationInPx = elevationInDp * getResources().getDisplayMetrics().density;
        mAppBarLayout.setElevation(elevationInPx);
    }
}
