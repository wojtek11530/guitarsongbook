package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.fragments.SongDisplayFragment;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Song;
import com.l4digital.fastscroll.FastScroller;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> implements FastScroller.SectionIndexer {


    private Context context;
    private final LayoutInflater mInflater;
    private List<Song> mSongs;
    private List<Artist> mArtists;
    private boolean animateTransition;


    public SongListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        animateTransition = sharedPref.getBoolean(
                context.getResources().getString(R.string.switch_animation_pref_key),
                true);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.song_list_rv_item, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongViewHolder holder, int position) {
        if (mSongs != null) {
            Song currentSong = mSongs.get(position);
            holder.mTitleTextView.setText(currentSong.getMTitle());

            if (currentSong.getMArtistId() != null) {
                Artist artist = findArtistById(currentSong.getMArtistId());
                if (artist != null) {
                    holder.mArtistTextView.setText(artist.getMName());
                }
            } else {
                holder.mArtistTextView.setText("");
            }
        } else {
            holder.mTitleTextView.setText("No Song");
        }
    }

    private Artist findArtistById(Long mArtistId) {
        Artist artistToReturn = null;
        for (Artist artist : mArtists) {
            if (mArtistId.equals(artist.getMId())) {
                artistToReturn = artist;
            }
        }
        return artistToReturn;
    }

    public void setSongs(List<Song> songs) {
        mSongs = songs;
        if (mArtists != null) {
            notifyDataSetChanged();
        }
    }

    public void setArtists(List<Artist> artists) {
        mArtists = artists;
        if (mSongs != null) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (mSongs != null && mArtists != null)
            return mSongs.size();
        else return 0;
    }

    @Override
    public CharSequence getSectionText(int position) {
        return mSongs.get(position).getMTitle().substring(0, 1).toUpperCase();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mTitleTextView;
        private final TextView mArtistTextView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.song_title_txt_);
            mArtistTextView = itemView.findViewById(R.id.artist_txt_);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            startSongDisplayFragment(position);
        }
    }

    private void startSongDisplayFragment(int position) {
        SongDisplayFragment songDisplayFragment = getSongDisplayFragment(position);
        changeFragmentWithDelay(songDisplayFragment);
    }

    private SongDisplayFragment getSongDisplayFragment(int position) {
        Song songToDisplay = mSongs.get(position);
        Long songId = songToDisplay.getMId();
        Long artistId = songToDisplay.getMArtistId();
        SongDisplayFragment songDisplayFragment;
        if (artistId == null) {
            songDisplayFragment = SongDisplayFragment.newInstance(songId);
        } else {
            songDisplayFragment = SongDisplayFragment.newInstance(songId, artistId);
        }
        return songDisplayFragment;
    }

    private void changeFragmentWithDelay(final SongDisplayFragment songDisplayFragment) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                if (animateTransition) {
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                }
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.fragment_container_fl_, songDisplayFragment)
                        .commit();
            }
        }, 250);
    }


}
