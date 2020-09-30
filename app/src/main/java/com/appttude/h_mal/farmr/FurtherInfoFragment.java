package com.appttude.h_mal.farmr;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appttude.h_mal.farmr.Data.ShiftsContract;


public class FurtherInfoFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DEFAULT_LOADER = 0;

    private TextView typeTV;
    private TextView descriptionTV;
    private TextView dateTV;
    private TextView times;
    private TextView breakTV;
    private TextView durationTV;
    private TextView unitsTV;
    private TextView payRateTV;
    private TextView totalPayTV;
    private LinearLayout hourlyDetailHolder;
    private LinearLayout unitsHolder;
    private LinearLayout wholeView;
    private ProgressBar progressBarFI;
    private Button editButton;

    private Uri CurrentUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_futher_info, container, false);

        MainActivity.setActionBarTitle(getString(R.string.further_info_title));

        setHasOptionsMenu(true);

        progressBarFI = (ProgressBar) rootView.findViewById(R.id.progressBar_info);
        wholeView = (LinearLayout) rootView.findViewById(R.id.further_info_view);
        typeTV = (TextView)rootView.findViewById(R.id.details_shift);
        descriptionTV = (TextView)rootView.findViewById(R.id.details_desc);
        dateTV = (TextView)rootView.findViewById(R.id.details_date);
        times = (TextView)rootView.findViewById(R.id.details_time);
        breakTV = (TextView)rootView.findViewById(R.id.details_breaks);
        durationTV = (TextView)rootView.findViewById(R.id.details_duration);
        unitsTV = (TextView)rootView.findViewById(R.id.details_units);
        payRateTV = (TextView)rootView.findViewById(R.id.details_pay_rate);
        totalPayTV = (TextView)rootView.findViewById(R.id.details_totalpay);
        editButton = (Button) rootView.findViewById(R.id.details_edit);
        hourlyDetailHolder = (LinearLayout) rootView.findViewById(R.id.details_hourly_details);
        unitsHolder = (LinearLayout) rootView.findViewById(R.id.details_units_holder);

        final Bundle b = getArguments();
        CurrentUri = Uri.parse(b.getString("uri"));

        getLoaderManager().initLoader(DEFAULT_LOADER, null, this);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = (MainActivity.fragmentManager).beginTransaction();
                Fragment fragment = new FragmentAddItem();
                fragment.setArguments(b);
                fragmentTransaction.replace(R.id.container,fragment).addToBackStack("additem").commit();
            }
        });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        progressBarFI.setVisibility(View.VISIBLE);
        wholeView.setVisibility(View.GONE);
        String[] projection = {
                ShiftsContract.ShiftsEntry._ID,
                ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DESCRIPTION,
                ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DATE,
                ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TIME_IN,
                ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TIME_OUT,
                ShiftsContract.ShiftsEntry.COLUMN_SHIFT_BREAK,
                ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DURATION,
                ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TYPE,
                ShiftsContract.ShiftsEntry.COLUMN_SHIFT_PAYRATE,
                ShiftsContract.ShiftsEntry.COLUMN_SHIFT_UNIT,
                ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TOTALPAY,};

        return new android.support.v4.content.CursorLoader(getContext(),CurrentUri,
                projection,null,null,null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        progressBarFI.setVisibility(View.GONE);
        wholeView.setVisibility(View.VISIBLE);
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int descriptionColumnIndex = cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DESCRIPTION);
            int dateColumnIndex = cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DATE);
            int timeInColumnIndex = cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TIME_IN);
            int timeOutColumnIndex = cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TIME_OUT);
            int breakColumnIndex = cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_BREAK);
            int durationColumnIndex = cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DURATION);
            int typeColumnIndex = cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TYPE);
            int unitColumnIndex = cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_UNIT);
            int payrateColumnIndex = cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_PAYRATE);
            int totalPayColumnIndex = cursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TOTALPAY);

            String type = cursor.getString(typeColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String timeIn = cursor.getString(timeInColumnIndex);
            String timeOut = cursor.getString(timeOutColumnIndex);
            int breaks = cursor.getInt(breakColumnIndex);
            float duration = cursor.getFloat(durationColumnIndex);
            float unit = cursor.getFloat(unitColumnIndex);
            float payrate = cursor.getFloat(payrateColumnIndex);
            float totalPay = cursor.getFloat(totalPayColumnIndex);

            String durationString = ShiftsCursorAdapter.timeValues(duration)[0] + " Hours " + ShiftsCursorAdapter.timeValues(duration)[1] + " Minutes ";
            if(breaks != 0){
                durationString = durationString + " (+ " + Integer.toString(breaks) + " minutes break)";
            }

            typeTV.setText(type);
            descriptionTV.setText(description);
            dateTV.setText(date);
            String totalPaid = "";
            String currency = "$";
            if(type.equals("Hourly")){
                hourlyDetailHolder.setVisibility(View.VISIBLE);
                unitsHolder.setVisibility(View.GONE);
                times.setText(timeIn + " - " + timeOut);
                breakTV.setText(Integer.toString(breaks) + "mins");
                durationTV.setText(durationString);
                totalPaid = String.format("%.2f",duration) + " Hours @ " + currency + String.format("%.2f",payrate) + " per Hour" + "\n"
                        + "Equals: " + currency + String.format("%.2f",totalPay);
            }else if(type.equals("Piece Rate")){
                hourlyDetailHolder.setVisibility(View.GONE);
                unitsHolder.setVisibility(View.VISIBLE);
                unitsTV.setText(String.format("%.2f",unit));
                totalPaid = String.format("%.2f",unit) + " Units @ " + currency + String.format("%.2f",payrate) + " per Unit" + "\n"
                        + "Equals: " + currency + String.format("%.2f",totalPay);
            }

            payRateTV.setText(String.format("%.2f",payrate));
            totalPayTV.setText(totalPaid);

        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

}
