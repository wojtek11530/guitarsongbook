package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.R;
import com.example.guitarsongbook.model.Chord;

import java.util.ArrayList;
import java.util.List;

public class ChordsLineAdapter extends RecyclerView.Adapter<ChordsLineAdapter.ChordsLineHolder>{

    private Context context;
    private final LayoutInflater mInflater;

    private ArrayList<Chord> mChordsInLine;

    public ChordsLineAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChordsLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.chord_in_line_rv_item, parent, false);
        return new ChordsLineAdapter.ChordsLineHolder(itemView);
    }

    public void setChordsInLine(ArrayList<Chord> chordsInLine){
        mChordsInLine = chordsInLine;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ChordsLineHolder holder, int position) {
        holder.bindTo(position);
    }

    @Override
    public int getItemCount() {
        return mChordsInLine.size();
    }

    public class ChordsLineHolder extends RecyclerView.ViewHolder {

        private final TextView mChordInLineTextView;
        private Chord mChord;


        public ChordsLineHolder(@NonNull View itemView) {
            super(itemView);
            mChordInLineTextView = itemView.findViewById(R.id.chord_in_line_txt_);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Toast toast = Toast.makeText(context, mChordsInLine.get(position).getMSymbol(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }

        public void bindTo(int position) {
            mChord = mChordsInLine.get(position);
            if (mChord!=null) {
                mChordInLineTextView.setText(mChord.getMSymbol());
            }
        }
    }
}
