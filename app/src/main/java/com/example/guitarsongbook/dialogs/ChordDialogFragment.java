package com.example.guitarsongbook.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.ChordDiagramPagerAdapter;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class ChordDialogFragment extends DialogFragment {


    private TextView mChordSymbolTextView;
    private ViewPager mChordDiagramViewPager;
    private ChordDiagramPagerAdapter mChordDiagramPagerAdapter;
    private CircleIndicator mCircleIndicator;


    public static final String CHORD_DIALOG_TITLE = "CHORD_DIALOG_TITLE";
    public static final String DIAGRAM_DRAWABLE_IDS = "DIAGRAM_DRAWABLE_IDS";

    public static ChordDialogFragment newInstance(String chordDialogTitle, ArrayList<Integer> chordDiagramDrawableIds) {
        ChordDialogFragment frag = new ChordDialogFragment();
        Bundle args = new Bundle();
        args.putString(CHORD_DIALOG_TITLE, chordDialogTitle);
        args.putSerializable(DIAGRAM_DRAWABLE_IDS, chordDiagramDrawableIds);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.dialog_chord_diagram, null);


        mChordSymbolTextView = dialogView.findViewById(R.id.chord_in_dialog_symbol_txt_);
        mChordDiagramViewPager = dialogView.findViewById(R.id.chordDiagramPager);
        mCircleIndicator = dialogView.findViewById(R.id.indicator);

        String chordDialogTitle;
        if (getArguments().containsKey(CHORD_DIALOG_TITLE)) {
            chordDialogTitle = getArguments().getString(CHORD_DIALOG_TITLE);
            mChordSymbolTextView.setText(chordDialogTitle);
        }

        ArrayList<Integer> chordDiagramDrawableIds = null;
        if (getArguments().containsKey(DIAGRAM_DRAWABLE_IDS)) {
            chordDiagramDrawableIds = (ArrayList<Integer>) getArguments().getSerializable(DIAGRAM_DRAWABLE_IDS);
            mChordDiagramPagerAdapter = new ChordDiagramPagerAdapter(getContext(),
                    chordDiagramDrawableIds);
            mChordDiagramViewPager.setAdapter(mChordDiagramPagerAdapter);

            mCircleIndicator.setViewPager(mChordDiagramViewPager);
        }

        builder.setView(dialogView);
        return builder.create();
    }
}
