package com.example.guitarsongbook;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.guitarsongbook.fragments.ArtistListFragment;
import com.example.guitarsongbook.fragments.SongListFragment;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager;

    private SharedPreferences mPreferences;
    private final String sharedPrefFile = "com.example.guitarsongbook";
    private final String SEARCH_KEY = "SEARCH_KEY";

    private boolean mSearching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.popBackStack();
            SongListFragment songListFragment = SongListFragment.newInstance(null, null, false);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_fl_, songListFragment)
                    .commit();
        } else {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                toggle.setDrawerIndicatorEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                toggle.setDrawerIndicatorEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                toggle.syncState();
            }
        }

        // Set back button
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    toggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    toggle.setDrawerIndicatorEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.syncState();
                }
            }
        });

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        mSearching = mPreferences.getBoolean(SEARCH_KEY, false);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() > 0) {
            //mSearching = false;
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {

        } else {
            Fragment fragment = null;
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            if (id == R.id.nav_all_songs) {
                fragment = SongListFragment.newInstance(null, null, false);
            } else if (id == R.id.nav_favourite_songs) {
                fragment = SongListFragment.newInstance(null, null, true);
            } else if (id == R.id.nav_artists) {
                fragment = ArtistListFragment.newInstance();
            } else if (id == R.id.nav_polish_songs) {
                fragment = SongListFragment.newInstance(Kind.POLISH, null, false);
            } else if (id == R.id.nav_foreign) {
                fragment = SongListFragment.newInstance(Kind.FOREIGN, null, false);
            } else if (id == R.id.nav_rock) {
                fragment = SongListFragment.newInstance(null, MusicGenre.ROCK, false);
            } else if (id == R.id.nav_pop) {
                fragment = SongListFragment.newInstance(null, MusicGenre.POP, false);
            } else if (id == R.id.nav_folk) {
                fragment = SongListFragment.newInstance(null, MusicGenre.FOLK, false);
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragment != null) {
                fragmentTransaction.replace(R.id.fragment_container_fl_, fragment).commit();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean(SEARCH_KEY, mSearching);
        preferencesEditor.apply();
    }
}
