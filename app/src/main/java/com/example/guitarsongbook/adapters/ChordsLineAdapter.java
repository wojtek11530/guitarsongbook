package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.dialogs.ChordDialogFragment;
import com.example.guitarsongbook.model.Chord;

import java.util.ArrayList;

public class ChordsLineAdapter extends RecyclerView.Adapter<ChordsLineAdapter.ChordsLineHolder> {

    private Context context;
    private final LayoutInflater mInflater;

    private ArrayList<Chord> mChordsInLine;

    int mFontSize = 20;

    public ChordsLineAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSizePreferenceValue = sharedPref.getString(
                context.getResources().getString(R.string.chord_text_size_pref_key),
                context.getResources().getString(R.string.lyrics_and_chords_default_text_size));
        assert fontSizePreferenceValue != null;
        mFontSize = Integer.valueOf(fontSizePreferenceValue);

    }

    @NonNull
    @Override
    public ChordsLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.chord_in_line_rv_item, parent, false);
        return new ChordsLineAdapter.ChordsLineHolder(itemView);
    }

    public void setChordsInLine(ArrayList<Chord> chordsInLine) {
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

    public int getCharAmountOfAllChords() {
        int numberOfChars = 0;
        for (Chord chord : mChordsInLine) {
            numberOfChars += chord.getMSymbol().length();
        }
        return numberOfChars;
    }

    public class ChordsLineHolder extends RecyclerView.ViewHolder {

        private final TextView mChordInLineTextView;
        private Chord mChord;


        public ChordsLineHolder(@NonNull View itemView) {
            super(itemView);
            mChordInLineTextView = itemView.findViewById(R.id.chord_in_line_txt_);
            mChordInLineTextView.setTextSize(mFontSize);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Chord chordToDisplay = mChordsInLine.get(position);

                    String chordDialogTitle = context.getString(R.string.chord) + " " + mChord.getMSymbol();

                    String chordSymbolInLowerCase = mChord.getMSymbol().toLowerCase();
                    Resources resources = context.getResources();
                    String packageName = context.getPackageName();

                    ArrayList<Integer> chordDiagramDrawableIds = new ArrayList<>();

                    int numberOfDiagram = 1;
                    String fileName = chordSymbolInLowerCase + "_diagram_" + numberOfDiagram;
                    int diagramId = resources.getIdentifier(fileName, "drawable", packageName);

                    while (diagramId != 0) {
                        chordDiagramDrawableIds.add(diagramId);
                        numberOfDiagram++;
                        fileName = chordSymbolInLowerCase + "_diagram_" + numberOfDiagram;
                        diagramId = resources.getIdentifier(
                                fileName, "drawable", packageName);

                    }

                    DialogFragment newFragment = ChordDialogFragment.newInstance(chordDialogTitle, chordDiagramDrawableIds);
                    newFragment.show(((MainActivity) context).getSupportFragmentManager(), null);
                }
            });
        }

        public void bindTo(int position) {
            mChord = mChordsInLine.get(position);
            if (mChord != null) {
                mChordInLineTextView.setText(mChord.getMSymbol());
            }
        }
    }
}
