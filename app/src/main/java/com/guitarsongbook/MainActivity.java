package com.guitarsongbook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.guitarsongbook.fragments.NavigationFragment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


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
}
