package com.example.h_mal.shift_tracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.h_mal.shift_tracker.Data.ShiftsContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.h_mal.shift_tracker.MainActivity.args;
import static com.example.h_mal.shift_tracker.MainActivity.selection;


public class FilterDataFragment extends Fragment {

    private String[] spinnerList = new String[]{"","Hourly","Piece Rate"};
    private List<String> listArgs = new ArrayList<String>();
    private EditText LocationET;
    private EditText dateFromET;
    private EditText dateToET;
    private Spinner typeSpinner;
    Calendar mcurrentDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_filter_data, container, false);
        MainActivity.setActionBarTitle(getString(R.string.title_activity_filter_data));

        mcurrentDate = Calendar.getInstance();

        LocationET = (EditText) rootView.findViewById(R.id.filterLocationEditText);
        dateFromET = (EditText) rootView.findViewById(R.id.fromdateInEditText);
        dateToET = (EditText) rootView.findViewById(R.id.filterDateOutEditText);
        typeSpinner = (Spinner) rootView.findViewById(R.id.TypeFilterEditText);

        if(selection != null && selection.contains(" AND " + ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DESCRIPTION + " LIKE ?")){
            String str = args[2];
            str = str.replace("%", "");
            LocationET.setText(str);
        }
        if(selection != null && !args[0].equals("2000-01-01")){
            dateFromET.setText(args[0]);
        }

        if(selection != null && args != null && !args[1].equals(String.valueOf(mcurrentDate.get(Calendar.YEAR) + "-"
                + String.format("%02d", (mcurrentDate.get(Calendar.MONTH) + 1)) + "-"
                + String.format("%02d", mcurrentDate.get(Calendar.DAY_OF_MONTH))))){
            dateToET.setText(args[1]);
        }

        dateFromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To show current date in the datepicker

                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                if(!dateFromET.getText().toString().equals("")) {
                    String dateString = dateFromET.getText().toString().trim();

                    mYear = Integer.parseInt(dateString.substring(0, 4));
                    mMonth = Integer.parseInt(dateString.substring(5, 7));
                    if (mMonth == 1){
                        mMonth = 0;
                    }else{
                        mMonth = mMonth -1;
                    }

                    mDay = Integer.parseInt(dateString.substring(8));
                }
                DatePickerDialog mDatePicker=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        dateFromET.setText(
                                selectedyear + "-"
                                        + String.format("%02d", (selectedmonth + 1)) + "-"
                                        + String.format("%02d", selectedday)
                        );
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();  }
        });

        dateToET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                if(!dateToET.getText().toString().equals("")) {
                    String dateString = dateToET.getText().toString().trim();

                    mYear = Integer.parseInt(dateString.substring(0, 4));
                    mMonth = Integer.parseInt(dateString.substring(5, 7));
                    if (mMonth == 1){
                        mMonth = 0;
                    }else{
                        mMonth = mMonth -1;
                    }

                    mDay = Integer.parseInt(dateString.substring(8));
                }

                DatePickerDialog mDatePicker=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        dateToET.setText(
                                selectedyear + "-"
                                        + String.format("%02d", (selectedmonth + 1)) + "-"
                                        + String.format("%02d", selectedday)
                        );
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();  }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerList);
        typeSpinner.setAdapter(adapter);
        if(selection != null && selection.contains(" AND " + ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TYPE + " IS ?")) {
            int spinnerPosition = adapter.getPosition(args[args.length-1]);
            typeSpinner.setSelection(spinnerPosition);
        }

        Button submit = (Button) rootView.findViewById(R.id.submitFiltered);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BuildQuery();
                args = listArgs.toArray(new String[0]);
                FragmentMain.NEW_LOADER = 1;
                MainActivity.fragmentManager.popBackStack();

            }
        });

        return rootView;
    }

    private void BuildQuery() {

        String dateQuery = ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DATE + " BETWEEN ? AND ?";
        String descQuery = " AND " + ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DESCRIPTION + " LIKE ?";
        String typeQuery = " AND " + ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TYPE + " IS ?";

        String dateFrom = "2000-01-01";

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateTo = df.format(c);

        if(!TextUtils.isEmpty(dateFromET.getText().toString().trim())){
            dateFrom = dateFromET.getText().toString().trim();
        }

        if(!TextUtils.isEmpty(dateToET.getText().toString().trim())){
            dateTo = dateToET.getText().toString().trim();
        }

        selection = dateQuery;
        listArgs.add(dateFrom);
        listArgs.add(dateTo);

        if (!TextUtils.isEmpty(LocationET.getText().toString().trim())){
            selection = selection + descQuery;
            listArgs.add("%" + LocationET.getText().toString().trim() + "%");
        }

        if (!typeSpinner.getSelectedItem().toString().equals("")){
            selection = selection + typeQuery;
            listArgs.add(typeSpinner.getSelectedItem().toString());
        }

    }

}
