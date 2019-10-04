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
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager;

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
        disableNavigationViewScrollbars(navigationView);

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.popBackStack();
            SongListFragment songListFragment = SongListFragment.newInstance(null, null, false, navigationView.getCheckedItem().getItemId());
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

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

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
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_setting) {
            fragment = new SettingsFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_fl_, fragment).addToBackStack(null).commit();
        } else {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            if (id == R.id.nav_all_songs) {
                fragment = SongListFragment.newInstance(null, null, false, id);
            } else if (id == R.id.nav_favourite_songs) {
                fragment = SongListFragment.newInstance(null, null, true, id);
            } else if (id == R.id.nav_artists) {
                fragment = ArtistListFragment.newInstance(id);
            } else if (id == R.id.nav_polish_songs) {
                fragment = SongListFragment.newInstance(Kind.POLISH, null, false, id);
            } else if (id == R.id.nav_foreign) {
                fragment = SongListFragment.newInstance(Kind.FOREIGN, null, false, id);
            } else if (id == R.id.nav_rock) {
                fragment = SongListFragment.newInstance(null, MusicGenre.ROCK, false, id);
            } else if (id == R.id.nav_pop) {
                fragment = SongListFragment.newInstance(null, MusicGenre.POP, false, id);
            } else if (id == R.id.nav_folk) {
                fragment = SongListFragment.newInstance(null, MusicGenre.FOLK, false, id);
            } else if (id == R.id.nav_disco_polo) {
                fragment = SongListFragment.newInstance(null, MusicGenre.DISCO_POLO, false, id);
            }else if (id == R.id.nav_country) {
                fragment = SongListFragment.newInstance(null, MusicGenre.COUNTRY, false, id);
            }else if (id == R.id.nav_reggea) {
                fragment = SongListFragment.newInstance(null, MusicGenre.REGGEA, false, id);
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

    public void checkItem(int itemId){
        navigationView.getMenu().findItem(itemId).setChecked(true);
    }

    public void unCheckAllItemInNavigationDrawer(){
        unCheckAllMenuItems(navigationView.getMenu());
    }

    private void unCheckAllMenuItems(Menu menu){
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            if(item.hasSubMenu()) {
                // Un check sub menu items
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }
}
