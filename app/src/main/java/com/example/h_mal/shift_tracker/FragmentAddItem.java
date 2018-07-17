package com.example.h_mal.shift_tracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.h_mal.shift_tracker.Data.ShiftsContract;

import java.util.Calendar;

public class FragmentAddItem extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;

    public static RadioGroup mRadioGroup;
    private RadioButton mRadioButtonOne;
    private RadioButton mRadioButtonTwo;
    private EditText mLocationEditText;
    private EditText mDateEditText;
    private TextView mDurationTextView;
    private EditText mTimeInEditText;
    private EditText mTimeOutEditText;
    private EditText mBreakEditText;
    private EditText mUnitEditText;
    private EditText mPayRateEditText;
    private TextView mTotalPayTextView;
    private LinearLayout hourlyDataView;
    private LinearLayout unitsHolder;
    private LinearLayout durationHolder;
    private LinearLayout wholeView;
    private ScrollView scrollView;
    private ProgressBar progressBarAI;
    private int mBreaks;

    private int mDay;
    private int mMonth;
    private int mYear;
    private int mHoursIn;
    private int mMinutesIn;
    private int mHoursOut;
    private int mMinutesOut;
    private float mUnits;
    private float mPayRate;
    private String mType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
        setHasOptionsMenu(true);

        progressBarAI = (ProgressBar) rootView.findViewById(R.id.pd_ai);
        scrollView = (ScrollView) rootView.findViewById(R.id.total_view);
        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.rg);
        mRadioButtonOne = (RadioButton) rootView.findViewById(R.id.hourly);
        mRadioButtonTwo = (RadioButton) rootView.findViewById(R.id.piecerate);
        mLocationEditText = (EditText) rootView.findViewById(R.id.locationEditText);
        mDateEditText = (EditText) rootView.findViewById(R.id.dateEditText);
        mTimeInEditText = (EditText) rootView.findViewById(R.id.timeInEditText);
        mBreakEditText = (EditText) rootView.findViewById(R.id.breakEditText);
        mTimeOutEditText = (EditText) rootView.findViewById(R.id.timeOutEditText);
        mDurationTextView = (TextView) rootView.findViewById(R.id.ShiftDuration);
        mUnitEditText = (EditText) rootView.findViewById(R.id.unitET);
        mPayRateEditText = (EditText) rootView.findViewById(R.id.payrateET);
        mTotalPayTextView = (TextView) rootView.findViewById(R.id.totalpayval);

        hourlyDataView = (LinearLayout) rootView.findViewById(R.id.hourly_data_holder);
        unitsHolder = (LinearLayout) rootView.findViewById(R.id.units_holder);
        durationHolder = (LinearLayout) rootView.findViewById(R.id.duration_holder);
        wholeView = (LinearLayout) rootView.findViewById(R.id.whole_view);

        mPayRate = 0.0f;
        mUnits = 0.0f;

        final Bundle b = getArguments();
        if(b != null) {
            mCurrentProductUri = Uri.parse(b.getString("uri"));
        }

        if (mCurrentProductUri == null) {
            MainActivity.setActionBarTitle(getString(R.string.add_item_title));
            wholeView.setVisibility(View.GONE);
        } else {
            MainActivity.setActionBarTitle(getString(R.string.edit_item_title));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mBreakEditText.setHint("insert break in minutes");

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (mRadioButtonOne.isChecked()){
                    mType = mRadioButtonOne.getText().toString();
                    wholeView.setVisibility(View.VISIBLE);
                    unitsHolder.setVisibility(View.GONE);
                    hourlyDataView.setVisibility(View.VISIBLE);
                    durationHolder.setVisibility(View.VISIBLE);
                }else if(mRadioButtonTwo.isChecked()){
                    mType = mRadioButtonTwo.getText().toString();
                    wholeView.setVisibility(View.VISIBLE);
                    unitsHolder.setVisibility(View.VISIBLE);
                    hourlyDataView.setVisibility(View.GONE);
                    durationHolder.setVisibility(View.GONE);
                }
            }
        });

        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To show current date in the datepicker
                if(TextUtils.isEmpty(mDateEditText.getText().toString().trim())) {
                    Calendar mcurrentDate = Calendar.getInstance();
                    mYear = mcurrentDate.get(Calendar.YEAR);
                    mMonth = mcurrentDate.get(Calendar.MONTH);
                    mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                }else{
                    String dateString = mDateEditText.getText().toString().trim();

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
                        mDateEditText.setText(
                                selectedyear + "-"
                                        + String.format("%02d", (selectedmonth = selectedmonth + 1)) +"-"
                                        + String.format("%02d", selectedday)
                        );
                        setDate(selectedyear, selectedmonth, selectedday);
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();  }
        });

        mTimeInEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeInEditText.getText().toString().equals("")) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    mHoursIn = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    mMinutesIn = mcurrentTime.get(Calendar.MINUTE);
                } else {
                    mHoursIn = Integer.parseInt((mTimeInEditText.getText().toString().subSequence(0,2)).toString());
                    mMinutesIn = Integer.parseInt((mTimeInEditText.getText().toString().subSequence(3,5)).toString());
                }
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String ddTime = String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute);
                        setTime(selectedMinute, selectedHour);
                        mTimeInEditText.setText(ddTime);
                    }
                }, mHoursIn, mMinutesIn, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        mTimeOutEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeOutEditText.getText().toString().equals("")) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    mHoursOut = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    mMinutesOut = mcurrentTime.get(Calendar.MINUTE);
                }else {
                    mHoursOut = Integer.parseInt((mTimeOutEditText.getText().toString().subSequence(0,2)).toString());
                    mMinutesOut = Integer.parseInt((mTimeOutEditText.getText().toString().subSequence(3,5)).toString());
                }
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String ddTime = String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute);
                        setTime2(selectedMinute,selectedHour);
                        mTimeOutEditText.setText(ddTime);
                    }
                }, mHoursOut, mMinutesOut, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        mTimeInEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft )
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                setDuration();
                calculateTotalPay();
            }
        });

        mTimeOutEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft )
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                setDuration();
                calculateTotalPay();
            }
        });

        mBreakEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft )
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                setDuration();
                calculateTotalPay();
            }
        });

        mUnitEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                if(mRadioButtonTwo.isChecked()) {
//                    mUnits = 0.0f;
//                    if (!mUnitEditText.getText().toString().equals("")){
//                        mUnits = Float.parseFloat(mUnitEditText.getText().toString());
//                    }
//                    mPayRate = 0.0f;
//                    if (!mPayRateEditText.getText().toString().equals("")){
//                        mPayRate = Float.parseFloat(mPayRateEditText.getText().toString());
//                    }
//                    Float total = mPayRate * mUnits;
//                    mTotalPayTextView.setText(String.valueOf(total));
//                }
                calculateTotalPay();

            }
        });

        mPayRateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateTotalPay();
            }

        });

        Button SubmitProduct = (Button) rootView.findViewById(R.id.submit);

        SubmitProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();

            }
        });

        return rootView;
    }

    private void calculateTotalPay(){
        Float total = 0.0f;
        mPayRate = 0.0f;
        if(mRadioButtonTwo.isChecked()) {
            mUnits = 0.0f;
            if (!mUnitEditText.getText().toString().equals("")) {
                mUnits = Float.parseFloat(mUnitEditText.getText().toString());
            }
            if (!mPayRateEditText.getText().toString().equals("")) {
                mPayRate = Float.parseFloat(mPayRateEditText.getText().toString());
            }
            total = mPayRate * mUnits;
            mTotalPayTextView.setText(String.valueOf(total));
        } else if(mRadioButtonOne.isChecked()){
            if (!mPayRateEditText.getText().toString().equals("")){
                mPayRate = Float.parseFloat(mPayRateEditText.getText().toString());
                total = mPayRate * calculateDuration(mHoursIn,mMinutesIn,mHoursOut,mMinutesOut,mBreaks);
            }
        }
        mTotalPayTextView.setText(String.format("%.2f",total));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    private void saveProduct() {

        String typeString = mType;
        String descriptionString = mLocationEditText.getText().toString().trim();
        if (TextUtils.isEmpty(descriptionString)) {
            Toast.makeText(getContext(), "please insert Location", Toast.LENGTH_SHORT).show();
            return;
        }
        String dateString = mDateEditText.getText().toString().trim();
        if (TextUtils.isEmpty(dateString)) {
            Toast.makeText(getContext(), "please insert Date", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeInString = "";
        String timeOutString = "";
        int breaks = 0;
        float units = 0;
        float duration = 0;
        float payRate = 0;
        String payRateString = mPayRateEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(payRateString)) {
            payRate = Float.parseFloat(payRateString);
        }
        float totalPay = 0;
        if (typeString.equals("Hourly")) {
            timeInString = mTimeInEditText.getText().toString().trim();
            if (TextUtils.isEmpty(timeInString)) {
                Toast.makeText(getContext(), "please insert Time in", Toast.LENGTH_SHORT).show();
                return;
            }
            timeOutString = mTimeOutEditText.getText().toString().trim();
            if (TextUtils.isEmpty(timeOutString)) {
                Toast.makeText(getContext(), "please insert Time out", Toast.LENGTH_SHORT).show();
                return;
            }
            String breakMins = mBreakEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(breakMins)) {
                breaks = Integer.parseInt(breakMins);
                if(((float)breaks / 60) > calculateDurationWithoutBreak(mHoursIn, mMinutesIn, mHoursOut, mMinutesOut)){
                    Toast.makeText(getContext(), "Break larger than duration", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            duration = calculateDuration(mHoursIn, mMinutesIn, mHoursOut, mMinutesOut, breaks);
            totalPay = duration * payRate;
        }else if(typeString.equals("Piece Rate")){
            String unitsString = mUnitEditText.getText().toString().trim();
            if (TextUtils.isEmpty(unitsString) || Float.parseFloat(unitsString) <= 0) {
                Toast.makeText(getContext(), "Insert Units", Toast.LENGTH_SHORT).show();
                return;
            }else{
                units = Float.parseFloat(unitsString);
            }
            duration = 0;
            totalPay = units * payRate;
        }

        ContentValues values = new ContentValues();
        values.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TYPE, typeString);
        values.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DESCRIPTION, descriptionString);
        values.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DATE, dateString);
        values.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TIME_IN, timeInString);
        values.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TIME_OUT, timeOutString);
        values.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DURATION, duration);
        values.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_BREAK, breaks);
        values.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_UNIT, units);
        values.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_PAYRATE, payRate);
        values.put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TOTALPAY, totalPay);

        if (mCurrentProductUri == null) {

            Uri newUri = getActivity().getContentResolver().insert(ShiftsContract.ShiftsEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(getContext(), getString(R.string.insert_item_failed),
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getContext(), getString(R.string.insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getActivity().getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(getContext(), getString(R.string.update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getString(R.string.update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        (MainActivity.fragmentManager).popBackStack("main",0);
    }

    private void setDuration (){
        Calendar mcurrentTime = Calendar.getInstance();
        mBreaks = 0;
        if (!mBreakEditText.getText().toString().equals("")){
            mBreaks = Integer.parseInt(mBreakEditText.getText().toString());
        }
        if (mTimeOutEditText.getText().toString().equals("")) {
            mHoursOut = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            mMinutesOut = mcurrentTime.get(Calendar.MINUTE);
        }else {
            mHoursOut = Integer.parseInt((mTimeOutEditText.getText().toString().subSequence(0,2)).toString());
            mMinutesOut = Integer.parseInt((mTimeOutEditText.getText().toString().subSequence(3,5)).toString());
        }
        if (mTimeInEditText.getText().toString().equals("")) {
            mHoursIn = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            mMinutesIn = mcurrentTime.get(Calendar.MINUTE);
        } else {
            mHoursIn = Integer.parseInt((mTimeInEditText.getText().toString().subSequence(0,2)).toString());
            mMinutesIn = Integer.parseInt((mTimeInEditText.getText().toString().subSequence(3,5)).toString());
        }
        mDurationTextView.setText(calculateDuration(mHoursIn,mMinutesIn,mHoursOut,mMinutesOut,mBreaks) + " hours");
    }

    private void setDate (int year, int month, int day){
        mYear = year;
        mMonth = month;
        mDay = day;
    }


    private void setTime (int minutes, int hours){
        mMinutesIn = minutes;
        mHoursIn = hours;

    }

    private void setTime2 (int minutes, int hours){
        mMinutesOut = minutes;
        mHoursOut = hours;

    }

    private float calculateDuration (int hoursIn, int minutesIn, int hoursOut, int minutesOut, int breaks){
        float duration;
        if (hoursOut > hoursIn){
            duration = (((float)hoursOut + ((float)minutesOut/60)) - ((float) hoursIn + ((float)minutesIn/60))) - ((float)breaks / 60);
        }else{
            duration = ((((float)hoursOut + ((float)minutesOut/60)) - ((float)hoursIn + ((float)minutesIn/60))) - ((float)breaks / 60) + 24);
        }

        String s = String.format("%.2f",duration);
        return Float.parseFloat(s);
    }

    private float calculateDurationWithoutBreak (int hoursIn, int minutesIn, int hoursOut, int minutesOut){
        float duration;
        if (hoursOut > hoursIn){
            duration = (((float)hoursOut + ((float)minutesOut/60)) - ((float) hoursIn + ((float)minutesIn/60)));
        }else{
            duration = ((((float)hoursOut + ((float)minutesOut/60)) - ((float)hoursIn + ((float)minutesIn/60))) + 24);
        }

        String s = String.format("%.2f",duration);
        return Float.parseFloat(s);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        progressBarAI.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
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

        return new android.support.v4.content.CursorLoader(getContext(),mCurrentProductUri,
                projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        progressBarAI.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
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

            mLocationEditText.setText(description);
            mDateEditText.setText(date);
            if (type.equals("Hourly") || type.equals("hourly")) {
                mRadioButtonOne.setChecked(true);
                mRadioButtonTwo.setChecked(false);
                mTimeInEditText.setText(timeIn);
                mTimeOutEditText.setText(timeOut);
                mBreakEditText.setText(Integer.toString(breaks));
                mDurationTextView.setText(String.format("%.2f", duration) + " Hours");
            } else if (type.equals("Piece Rate")) {
                mRadioButtonOne.setChecked(false);
                mRadioButtonTwo.setChecked(true);
                mUnitEditText.setText(Float.toString(unit));
            }
            mPayRateEditText.setText(String.format("%.2f", payrate));
            mTotalPayTextView.setText(String.format("%.2f", totalPay));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
