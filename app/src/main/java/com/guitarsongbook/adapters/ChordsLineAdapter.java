package com.guitarsongbook.adapters;

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

import com.guitarsongbook.MainActivity;
import com.guitarsongbook.R;
import com.guitarsongbook.dialogs.ChordDialogFragment;
import com.guitarsongbook.model.Chord;

import java.util.ArrayList;

public class ChordsLineAdapter extends RecyclerView.Adapter<ChordsLineAdapter.ChordsLineHolder> {

    private Context context;
    private final LayoutInflater mInflater;

    private ArrayList<Chord> mChordsInLine;

    int mFontSize;

    public ChordsLineAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSizePreferenceValue = sharedPref.getString(
                context.getResources().getString(R.string.chord_text_size_pref_key),
                context.getResources().getString(R.string.lyrics_and_chords_default_text_size));
        mFontSize = Integer.parseInt(fontSizePreferenceValue);
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
                    String chordDialogTitle = context.getString(R.string.chord) + " " + mChord.getSymbolToDisplay();

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

                    DialogFragment newDialogFragment = ChordDialogFragment.newInstance(chordDialogTitle, chordDiagramDrawableIds);
                    newDialogFragment.show(((MainActivity) context).getSupportFragmentManager(), null);
                }
            });
        }

        public void bindTo(int position) {
            mChord = mChordsInLine.get(position);
            if (mChord != null) {
                String chordSymbol = mChord.getSymbolToDisplay();
                mChordInLineTextView.setText(chordSymbol);
            }
        }
    }
}
