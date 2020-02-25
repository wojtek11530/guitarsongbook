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

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    NavigationView navigationView;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager;
    private Integer chosenItemId;
    private Integer currentItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews();
        configureFragmentManager();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (savedInstanceState == null) {
            startFragmentForLaunchedApp();
        } else {
            adjustDisplayingDrawerIndicator();
        }
    }

    private void startFragmentForLaunchedApp() {
        fragmentManager.popBackStack();


        SongListFragment songListFragment =
                SongListFragment.newInstance(Objects.requireNonNull(navigationView.getCheckedItem()).getItemId());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .replace(R.id.fragment_container_fl_, songListFragment)
                .commit();
    }

    private void initializeViews() {
        setContentView(R.layout.activity_main);
        initToolbar();
        initNavigationView();
        initDrawer();
    }

    private void initNavigationView() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        disableNavigationViewScrollbars(navigationView);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        initToggleForDrawerAndToolbar();
        drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(this);
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    private void initToggleForDrawerAndToolbar() {
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void configureFragmentManager() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                adjustDisplayingDrawerIndicator();
            }
        });
    }

    private void adjustDisplayingDrawerIndicator() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            toggle.setDrawerIndicatorEnabled(false);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } else {
            toggle.setDrawerIndicatorEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            toggle.syncState();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (currentItemId == null || currentItemId != item.getItemId()) {
            chosenItemId = item.getItemId();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setCurrentItemId(int itemId) {
        currentItemId = itemId;
        uncheckAllItemInNavigationDrawer();
        navigationView.getMenu().findItem(itemId).setChecked(true);
    }

    public void uncheckAllItemInNavigationDrawer() {
        currentItemId = null;
        uncheckAllMenuItems(navigationView.getMenu());
    }

    private void uncheckAllMenuItems(Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
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
                    setTransactionForSettingFragment(fragmentTransaction);
                } else {
                    setTransactionForNoSettingFragments(fragmentTransaction);
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
        } else {

            List fragments = getSupportFragmentManager().getFragments();
            Fragment currentFragment = (Fragment) fragments.get(fragments.size() - 1);
            if (currentFragment instanceof SongListFragment){
                SongListFragment.mBundleRecyclerViewState = null;
                ((SongListFragment) currentFragment).setSaveRecyclerViewState(false);
            }
            if (chosenItemId == R.id.nav_all_songs) {
                fragment = SongListFragment.newInstance(chosenItemId);
            } else if (chosenItemId == R.id.nav_favourite_songs) {
                fragment = SongListFragment.newInstance(true, chosenItemId);
            } else if (chosenItemId == R.id.nav_artists) {
                fragment = ArtistListFragment.newInstance(chosenItemId);
            } else if (chosenItemId == R.id.nav_polish_songs) {
                fragment = SongListFragment.newInstance(Kind.POLISH, chosenItemId);
            } else if (chosenItemId == R.id.nav_foreign) {
                fragment = SongListFragment.newInstance(Kind.FOREIGN, chosenItemId);
            } else if (chosenItemId == R.id.nav_rock) {
                fragment = SongListFragment.newInstance(MusicGenre.ROCK, chosenItemId);
            } else if (chosenItemId == R.id.nav_pop) {
                fragment = SongListFragment.newInstance(MusicGenre.POP, chosenItemId);
            } else if (chosenItemId == R.id.nav_folk) {
                fragment = SongListFragment.newInstance(MusicGenre.FOLK, chosenItemId);
            } else if (chosenItemId == R.id.nav_disco_polo) {
                fragment = SongListFragment.newInstance(MusicGenre.DISCO_POLO, chosenItemId);
            } else if (chosenItemId == R.id.nav_country) {
                fragment = SongListFragment.newInstance(MusicGenre.COUNTRY, chosenItemId);
            } else if (chosenItemId == R.id.nav_reggea) {
                fragment = SongListFragment.newInstance(MusicGenre.REGGAE, chosenItemId);
            } else if (chosenItemId == R.id.nav_festive) {
                fragment = SongListFragment.newInstance(MusicGenre.FESTIVE, chosenItemId);
            } else if (chosenItemId == R.id.nav_shanty) {
                fragment = SongListFragment.newInstance(MusicGenre.SHANTY, chosenItemId);
            }
        }
        return fragment;
    }

    private void setTransactionForSettingFragment(FragmentTransaction fragmentTransaction) {
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(
                R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
    }

    private void setTransactionForNoSettingFragments(FragmentTransaction fragmentTransaction) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    public void onDrawerStateChanged(int newState) {

    }

    public void setAppBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }
}
