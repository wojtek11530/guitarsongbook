package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.daos.SongDao;
import com.example.guitarsongbook.fragments.SearchFragment;
import com.example.guitarsongbook.fragments.SongListFragment;
import com.example.guitarsongbook.model.Artist;
import com.l4digital.fastscroll.FastScroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder> implements FastScroller.SectionIndexer {

    private Context context;
    private SearchFragment searchFragment;
    private final LayoutInflater mInflater;
    private List<Artist> mArtists;
    private boolean animateTransition;

    private Map<Long, Integer> artistIdToSongsCountMap = new HashMap<>();

    public ArtistListAdapter(Context context) {
        this(context, null);
    }

    public ArtistListAdapter(Context context, SearchFragment searchFragment) {
        this.context = context;
        this.searchFragment = searchFragment;
        mInflater = LayoutInflater.from(context);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        animateTransition = sharedPref.getBoolean(
                context.getResources().getString(R.string.switch_animation_pref_key),
                true);
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.artist_list_rv_item, parent, false);
        return new ArtistListAdapter.ArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        if (mArtists != null) {
            Artist currentArtist = mArtists.get(position);
            holder.mArtistNameTextView.setText(currentArtist.getMName());

            if (!artistIdToSongsCountMap.isEmpty()) {
                long artistId = currentArtist.getMId();
                Integer count = artistIdToSongsCountMap.get(artistId);
                String songCount = getSongCountString(count);
                holder.mArtistSongsCountTextView.setText(songCount);
            } else {
                holder.mArtistSongsCountTextView.setText("");
            }

        } else {
            // Covers the case of data not being ready yet.
            holder.mArtistNameTextView.setText("No Artist");
        }
    }

    private String getSongCountString(Integer count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(count);
        String songs = getSongSingularOrPluralFormString(count);
        stringBuilder.append(" ");
        stringBuilder.append(songs);

        return stringBuilder.toString();
    }

    private String getSongSingularOrPluralFormString(Integer count) {
        String songsString;
        if (count == 0 || count >= 5) {
            songsString = context.getResources().getString(R.string.songs_plural);
        } else if (count == 1) {
            songsString = context.getResources().getString(R.string.song);
        } else {
            songsString = context.getResources().getString(R.string.songs_plural_for_2_3_4);
        }
        return songsString;
    }

    @Override
    public int getItemCount() {
        if (mArtists != null)
            return mArtists.size();
        else return 0;
    }

    public void setArtists(List<Artist> artists) {
        mArtists = artists;
        notifyDataSetChanged();
    }

    public void setArtistsSongsNumber(List<SongDao.ArtistSongsCount> artistSongsCounts) {
        artistIdToSongsCountMap.clear();
        for (SongDao.ArtistSongsCount artistSongsCount : artistSongsCounts) {
            Long artistId = artistSongsCount.getArtistId();
            int count = artistSongsCount.getSongsNumber();
            artistIdToSongsCountMap.put(artistId, count);
        }
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getSectionText(int position) {
        return mArtists.get(position).getMName().substring(0, 1).toUpperCase();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mArtistNameTextView;
        private final TextView mArtistSongsCountTextView;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            mArtistNameTextView = itemView.findViewById(R.id.artist_name_txt_);
            mArtistSongsCountTextView = itemView.findViewById(R.id.artist_songs_count_txt_);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (searchFragment != null) {
                searchFragment.insertCurrentQueryToDatabase();
            }
            startSongListFragment(position);
        }
    }

    private void startSongListFragment(int position) {
        SongListFragment songListFragment = getSongListFragment(position);
        changeFragmentWithDelay(songListFragment);
    }

    private void changeFragmentWithDelay(final SongListFragment songListFragment) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                if (animateTransition) {
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                }
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.fragment_container_fl_, songListFragment)
                        .commit();
            }
        }, 250);

    }

    private SongListFragment getSongListFragment(int position) {
        Artist artistsSongsToDisplay = mArtists.get(position);
        Long artistId = artistsSongsToDisplay.getMId();
        return SongListFragment.newInstance(artistId);
    }
}
