package com.example.guitarsongbook;

import android.os.Bundle;

import com.example.guitarsongbook.fragments.ArtistListFragment;
import com.example.guitarsongbook.fragments.SettingsFragment;
import com.example.guitarsongbook.fragments.SongListFragment;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;

import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager;
    private Integer chosenItemId;
    private int currentItemId;

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
        drawer.addDrawerListener(this);
        disableNavigationViewScrollbars(navigationView);

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.popBackStack();
            SongListFragment songListFragment = SongListFragment.newInstance(null, null, false, navigationView.getCheckedItem().getItemId());
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_fl_, songListFragment)
                    .commit();
        } else {
            adjustDisplayingDrawerIndicator();
        }

        // Set back button
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                adjustDisplayingDrawerIndicator();
            }
        });

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    }

    private void adjustDisplayingDrawerIndicator() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            toggle.setDrawerIndicatorEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            toggle.syncState();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        if (currentItemId != item.getItemId()) {
            chosenItemId = item.getItemId();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setCurrentItemId(int itemId) {
        currentItemId = itemId;
        uncheckAllItemInNavigationDrawer();
        navigationView.getMenu().findItem(itemId).setChecked(true);
    }

    public void uncheckAllItemInNavigationDrawer() {
        uncheckAllMenuItems(navigationView.getMenu());
    }

    private void uncheckAllMenuItems(Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                // Un check sub menu items
                uncheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        if (chosenItemId != null) {
            Fragment fragment = getFragmentForItemId();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (fragment != null) {
                if (chosenItemId == R.id.nav_setting) {
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right);
                } else {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentTransaction
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                }
                fragmentTransaction
                        .replace(R.id.fragment_container_fl_, fragment)
                        .commit();
                currentItemId = chosenItemId;
                chosenItemId = null;
            }
        }
    }

    private Fragment getFragmentForItemId() {
        Fragment fragment = null;

        if (chosenItemId == R.id.nav_setting) {
            fragment = new SettingsFragment();
        } else if (chosenItemId == R.id.nav_all_songs) {
            fragment = SongListFragment.newInstance(null, null, false, chosenItemId);
        } else if (chosenItemId == R.id.nav_favourite_songs) {
            fragment = SongListFragment.newInstance(null, null, true, chosenItemId);
        } else if (chosenItemId == R.id.nav_artists) {
            fragment = ArtistListFragment.newInstance(chosenItemId);
        } else if (chosenItemId == R.id.nav_polish_songs) {
            fragment = SongListFragment.newInstance(Kind.POLISH, null, false, chosenItemId);
        } else if (chosenItemId == R.id.nav_foreign) {
            fragment = SongListFragment.newInstance(Kind.FOREIGN, null, false, chosenItemId);
        } else if (chosenItemId == R.id.nav_rock) {
            fragment = SongListFragment.newInstance(null, MusicGenre.ROCK, false, chosenItemId);
        } else if (chosenItemId == R.id.nav_pop) {
            fragment = SongListFragment.newInstance(null, MusicGenre.POP, false, chosenItemId);
        } else if (chosenItemId == R.id.nav_folk) {
            fragment = SongListFragment.newInstance(null, MusicGenre.FOLK, false, chosenItemId);
        } else if (chosenItemId == R.id.nav_disco_polo) {
            fragment = SongListFragment.newInstance(null, MusicGenre.DISCO_POLO, false, chosenItemId);
        } else if (chosenItemId == R.id.nav_country) {
            fragment = SongListFragment.newInstance(null, MusicGenre.COUNTRY, false, chosenItemId);
        } else if (chosenItemId == R.id.nav_reggea) {
            fragment = SongListFragment.newInstance(null, MusicGenre.REGGAE, false, chosenItemId);
        } else if (chosenItemId == R.id.nav_festive) {
            fragment = SongListFragment.newInstance(null, MusicGenre.FESTIVE, false, chosenItemId);
        } else if (chosenItemId == R.id.nav_shanty) {
            fragment = SongListFragment.newInstance(null, MusicGenre.SHANTY, false, chosenItemId);
        }
        return fragment;
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
