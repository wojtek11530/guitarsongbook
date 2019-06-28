package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.fragments.SongDisplayFragment;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Song;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> {

    private Context context;
    private final LayoutInflater mInflater;
    private List<Song> mSongs;
    private List<Artist> mArtists;


    public SongListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
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
            Song current = mSongs.get(position);
            holder.mTitleTextView.setText(current.getMTitle());
            //System.out.println(current.getMArtistId());
            System.out.println("Current id: " + String.valueOf(current.getMArtistId()));

            Artist artist = findArtistById(current.getMArtistId());
            if (artist != null) {
                holder.mArtistTextView.setText(artist.getMName());
            }
        } else {
            // Covers the case of data not being ready yet.
            holder.mTitleTextView.setText("No Song");
        }
    }

    private Artist findArtistById(Long mArtistId) {
        Artist  artistToReturn = null;
        for (Artist artist:mArtists){
            if (mArtistId.equals(artist.getMId())){
                artistToReturn = artist;
            }
        }
        return artistToReturn;
    }

    public void setSongs(List<Song> songs){
        mSongs = songs;
        notifyDataSetChanged();
    }

    public void setArtists(List<Artist> artists){
        mArtists = artists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mSongs != null)
            return mSongs.size();
        else return 0;
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

            Song songToDisplay = mSongs.get(position);
            Long songId = songToDisplay.getMId();
            Long artistId = songToDisplay.getMArtistId();

            SongDisplayFragment songDisplayFragment = SongDisplayFragment.newInstance(songId, artistId);
            ((MainActivity) context).getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container_fl_, songDisplayFragment).addToBackStack(null).commit();
        }
    }
}
