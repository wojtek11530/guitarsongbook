package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.fragments.SongDisplayFragment;
import com.example.guitarsongbook.fragments.SongListFragment;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Song;

import java.util.List;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder>{

    private Context context;
    private final LayoutInflater mInflater;
    private List<Artist> mArtists;

    public ArtistListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
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
            Artist current = mArtists.get(position);
            holder.mArtistNameTextView.setText(current.getMName());

        } else {
            // Covers the case of data not being ready yet.
            holder.mArtistNameTextView.setText("No Artist");
        }
    }

    @Override
    public int getItemCount() {
        if (mArtists != null)
            return mArtists.size();
        else return 0;
    }

    public void setArtists(List<Artist> artists){
        mArtists = artists;
        notifyDataSetChanged();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mArtistNameTextView;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            mArtistNameTextView = itemView.findViewById(R.id.artist_name_txt_);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            Artist artistsSongsToDisplay = mArtists.get(position);
            Long artistId = artistsSongsToDisplay.getMId();

            SongListFragment songListFragment = SongListFragment.newInstance(artistId);
            ((MainActivity) context).getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container_fl_, songListFragment).addToBackStack(null).commit();
        }
    }
}
