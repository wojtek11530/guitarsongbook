package com.wojciechkorczynski.guitarsongbook.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wojciechkorczynski.guitarsongbook.GuitarSongbookViewModel;
import com.wojciechkorczynski.guitarsongbook.MainActivity;
import com.wojciechkorczynski.guitarsongbook.R;
import com.wojciechkorczynski.guitarsongbook.fragments.SearchFragment;
import com.wojciechkorczynski.guitarsongbook.fragments.SongDisplayFragment;
import com.wojciechkorczynski.guitarsongbook.model.Artist;
import com.wojciechkorczynski.guitarsongbook.model.MusicGenre;
import com.wojciechkorczynski.guitarsongbook.model.Song;
import com.l4digital.fastscroll.FastScroller;


import java.util.List;
import java.util.Random;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder>
        implements FastScroller.SectionIndexer {

    private Context context;
    private SearchFragment searchFragment;
    private final LayoutInflater mInflater;
    private List<Song> mSongs;
    private List<Artist> mArtists;
    private boolean animateTransition;
    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    public SongListAdapter(Context context, Fragment fragment) {
        this.context = context;
        this.searchFragment = fragment instanceof SearchFragment ? (SearchFragment) fragment : null;

        mGuitarSongbookViewModel = new ViewModelProvider(fragment).get(GuitarSongbookViewModel.class);
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
    public void onBindViewHolder(@NonNull final SongViewHolder holder, final int position) {
        if (mSongs != null) {
            final Song currentSong = mSongs.get(position);
            holder.mTitleTextView.setText(currentSong.getMTitle());

            MusicGenre musicGenre = currentSong.getMMusicGenre();
            if (musicGenre == null) {
                System.out.println("bla");
            } else {
                switch (musicGenre) {
                    case ROCK:
                        holder.mMusicGenreImageView.setImageResource(R.drawable.ic_rock);
                        break;
                    case POP:
                        holder.mMusicGenreImageView.setImageResource(R.drawable.ic_pop);
                        break;
                    case FOLK:
                        holder.mMusicGenreImageView.setImageResource(R.drawable.ic_folk);
                        break;
                    case DISCO_POLO:
                        holder.mMusicGenreImageView.setImageResource(R.drawable.ic_disco_polo);
                        break;
                    case COUNTRY:
                        holder.mMusicGenreImageView.setImageResource(R.drawable.ic_country);
                        break;
                    case REGGAE:
                        holder.mMusicGenreImageView.setImageResource(R.drawable.ic_reggae);
                        break;
                    case FESTIVE:
                        holder.mMusicGenreImageView.setImageResource(R.drawable.ic_festive);
                        break;
                    case SHANTY:
                        holder.mMusicGenreImageView.setImageResource(R.drawable.ic_shanty);
                        break;
                }
            }
            boolean favourite = currentSong.getMIsFavourite();
            if (favourite) {
                holder.mFavouriteImageButton.setImageResource(R.drawable.ic_heart);
            } else {
                holder.mFavouriteImageButton.setImageResource(R.drawable.ic_heart_border);
            }

            holder.mFavouriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentSong.switchIsFavourite();
                    mGuitarSongbookViewModel.updateIsFavourite(currentSong.getMId(), currentSong.getMIsFavourite());
                    notifyItemChanged(position);
                }
            });


            if (currentSong.getMArtistId() != null) {
                Artist artist = findArtistById(currentSong.getMArtistId());
                if (artist != null) {
                    holder.mArtistTextView.setText(artist.getMName());
                }
            } else {
                holder.mArtistTextView.setText("");
            }

        } else {
            holder.mTitleTextView.setText(R.string.no_songs);
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
        private final ImageButton mFavouriteImageButton;
        private final ImageView mMusicGenreImageView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.song_title_txt_);
            mArtistTextView = itemView.findViewById(R.id.artist_txt_);
            mFavouriteImageButton = itemView.findViewById(R.id.favourite_btn_);
            mMusicGenreImageView = itemView.findViewById(R.id.artist_img_);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            if (searchFragment != null) {
                searchFragment.insertCurrentQueryToDatabase();
            }
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
                FragmentTransaction fragmentTransaction =
                        ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                if (animateTransition) {
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                            R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                }
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.fragment_container_fl_, songDisplayFragment)
                        .commit();
            }
        }, 250);
    }

    public SongDisplayFragment getRandomSongDisplayFragment() {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(getItemCount());
        return getSongDisplayFragment(randomInt);
    }

}
