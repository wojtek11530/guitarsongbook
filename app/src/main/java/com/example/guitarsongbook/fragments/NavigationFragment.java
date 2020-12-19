package com.example.guitarsongbook.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends SearchLaunchingFragment {

    private boolean animateTransition;

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
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        Context context = getContext();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        animateTransition = sharedPref.getBoolean(
                context.getResources().getString(R.string.switch_animation_pref_key),
                true);

//        configureScrollView(view);
        addOnCardViewsClickListeners(view);
        handleMainActivityFeatures();
        return view;
    }

//    private void configureScrollView(View view) {
//        ScrollView scrollView = view.findViewById(R.id.scroll_view);
//        scrollView.setOverScrollMode(View.OVER);
//    }

    private void addOnCardViewsClickListeners(View view) {
        CardView allSongsCardView = view.findViewById(R.id.all_songs_card_view);
        CardView favouriteSongsCardView = view.findViewById(R.id.fav_songs_card_view);
        CardView artistsCardView = view.findViewById(R.id.artists_card_view);
        CardView polishCardView = view.findViewById(R.id.polish_card_view);
        CardView foreignSongsCardView = view.findViewById(R.id.foreign_card_view);
        CardView settingsCardView = view.findViewById(R.id.settings_card_view);
        CardView rockCardView = view.findViewById(R.id.rock_card_view);
        CardView popCardView = view.findViewById(R.id.pop_card_view);
        CardView folkCardView = view.findViewById(R.id.folk_card_view);
        CardView discoPoloCardView = view.findViewById(R.id.disco_polo_card_view);
        CardView countryCardView = view.findViewById(R.id.country_card_view);
        CardView reggaeCardView = view.findViewById(R.id.reggae_card_view);
        CardView festiveCardView = view.findViewById(R.id.festive_card_view);
        CardView shantyCardView = view.findViewById(R.id.shanty_card_view);

        allSongsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance());
            }
        });
        favouriteSongsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(true));
            }
        });
        artistsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(ArtistListFragment.newInstance());
            }
        });

        polishCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(Kind.POLISH));
            }
        });
        foreignSongsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(Kind.FOREIGN));
            }
        });
        settingsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(new SettingsFragment());
            }
        });

        rockCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(MusicGenre.ROCK));
            }
        });
        popCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(MusicGenre.POP));
            }
        });
        folkCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(MusicGenre.FOLK));
            }
        });
        discoPoloCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(MusicGenre.DISCO_POLO));
            }
        });
        countryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(MusicGenre.COUNTRY));
            }
        });
        reggaeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(MusicGenre.REGGAE));
            }
        });
        festiveCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(MusicGenre.FESTIVE));
            }
        });

        shantyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentWithDelay(SongListFragment.newInstance(MusicGenre.SHANTY));
            }
        });
    }

    private void handleMainActivityFeatures() {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.setAppBarTitle(requireContext().getString(R.string.app_name));
    }

    private void changeFragmentWithDelay(final Fragment fragment) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                if (animateTransition) {
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                }
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.fragment_container_fl_, fragment)
                        .commit();
            }
        }, 150);
    }

}
