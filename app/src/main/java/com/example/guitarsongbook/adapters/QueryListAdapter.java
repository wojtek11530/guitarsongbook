package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.R;
import com.example.guitarsongbook.fragments.SearchFragment;
import com.example.guitarsongbook.model.SearchQuery;

import java.util.List;

public class QueryListAdapter extends RecyclerView.Adapter<QueryListAdapter.QueryViewHolder>{

    private final SearchFragment searchFragment;
    private final LayoutInflater mInflater;
    private List<SearchQuery> mSearchQueries;

    public QueryListAdapter(Context context, SearchFragment searchFragment) {
        this.searchFragment = searchFragment;
        mInflater = LayoutInflater.from(context);
    }

    public void setQueries(List<SearchQuery> queries) {
        this.mSearchQueries = queries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.query_list_rv_item, parent, false);
        return new QueryListAdapter.QueryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QueryViewHolder holder, int position) {
        if (mSearchQueries != null) {
            SearchQuery currentArtist = mSearchQueries.get(position);
            holder.mQueryTextView.setText(currentArtist.getMQueryText());
        } else {
            // Covers the case of data not being ready yet.
            holder.mQueryTextView.setText(R.string.no_queries);
        }
    }

    @Override
    public int getItemCount() {
        if (mSearchQueries != null) {
            return mSearchQueries.size();
        } else{
            return 0;
        }
    }

    public class QueryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mQueryTextView;

        public QueryViewHolder(@NonNull View itemView) {
            super(itemView);
            mQueryTextView = itemView.findViewById(R.id.query_txt_);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            SearchQuery searchQuery = mSearchQueries.get(position);
            searchFragment.setQuery(searchQuery.getMQueryText());
        }
    }
}
