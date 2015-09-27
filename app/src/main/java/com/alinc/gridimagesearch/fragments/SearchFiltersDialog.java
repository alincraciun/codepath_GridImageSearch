package com.alinc.gridimagesearch.fragments;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.alinc.gridimagesearch.R;

/**
 * Created by alinc on 9/26/15.
 */
public class SearchFiltersDialog extends DialogFragment {

    private OnCompleteListener mListener;

    public SearchFiltersDialog() {
    }

    public static SearchFiltersDialog newInstance() {
        SearchFiltersDialog frag = new SearchFiltersDialog();
        Bundle args = new Bundle();
        args.putString("imageSize", "Search Filters");
        args.putString("imageType", "Search Filters");
        args.putString("siteFilter", "Search Filters");
        args.putString("colorFilter", "Search Filters");
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.filter_fragment, container);

        Button btCancel= (Button) view.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        Button saveButton = (Button) view.findViewById(R.id.btSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilters();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(R.string.filter_dialog_title);

        /*TextView tvImageSize = (TextView) view.findViewById(R.id.tvImageSize);
        tvImageSize.setTypeface(Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "roboto.ttf"));
        TextView tvImageType = (TextView) view.findViewById(R.id.tvImageType);
        tvImageType.setTypeface(Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "roboto.ttf"));
        TextView tvColorFilter = (TextView) view.findViewById(R.id.tvColorFilter);
        tvColorFilter.setTypeface(Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "roboto.ttf"));
        TextView tvSiteFilter = (TextView) view.findViewById(R.id.tvSiteFilter);
        tvSiteFilter.setTypeface(Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "roboto.ttf"));
*/
        Spinner spImageSize = (Spinner) getView().findViewById(R.id.spImageSize);
        Spinner spColorFilter = (Spinner) getView().findViewById(R.id.spColorFilter);
        Spinner spImageType = (Spinner) getView().findViewById(R.id.spImageType);
        EditText etSiteFilter = (EditText) getView().findViewById(R.id.etSiteFilter);

        setSpinnerToValue(spImageSize, getArguments().getString("imageSize"));
        setSpinnerToValue(spImageType, getArguments().getString("imageType"));
        setSpinnerToValue(spColorFilter, getArguments().getString("colorFilter"));
        etSiteFilter.setText(getArguments().getString("siteFilter"));

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


    }

    public static interface OnCompleteListener {
        public abstract void onComplete(String spImageSize, String spImageType, String spColorFilter, String etSiteFilter);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener) activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public void setFilters() {
        Spinner spImageSize = (Spinner) getView().findViewById(R.id.spImageSize);
        Spinner spColorFilter = (Spinner) getView().findViewById(R.id.spColorFilter);
        Spinner spImageType = (Spinner) getView().findViewById(R.id.spImageType);
        EditText etSiteFilter = (EditText) getView().findViewById(R.id.etSiteFilter);

        this.mListener.onComplete(
                spImageSize.getSelectedItem().toString()
                , spImageType.getSelectedItem().toString()
                , spColorFilter.getSelectedItem().toString()
                , etSiteFilter.getText().toString()
        );

        getDialog().dismiss();
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }

}
