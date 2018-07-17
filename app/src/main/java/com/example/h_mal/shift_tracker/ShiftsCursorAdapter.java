package com.example.h_mal.shift_tracker;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.h_mal.shift_tracker.Data.ShiftProvider;
import com.example.h_mal.shift_tracker.Data.ShiftsContract.ShiftsEntry;

/**
 * Created by h_mal on 26/12/2017.
 */

public class ShiftsCursorAdapter extends CursorAdapter {

    private final MainActivity activity;

    private Context mContext;

    ShiftProvider shiftProvider;

    public ShiftsCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_1, parent, false);
    }



    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mContext = context;

        TextView descriptionTextView = (TextView) view.findViewById(R.id.location);
        TextView dateTextView = (TextView) view.findViewById(R.id.date);
        TextView totalPay = (TextView) view.findViewById(R.id.total_pay);
        TextView hoursView = (TextView) view.findViewById(R.id.hours);
        TextView h = (TextView) view.findViewById(R.id.h);
        TextView minutesView = (TextView) view.findViewById(R.id.minutes);
        TextView m = (TextView) view.findViewById(R.id.m);
        ImageView editView = (ImageView) view.findViewById(R.id.imageView);

        h.setText("h");
        m.setText("m");

        final String typeColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TYPE));
        final String descriptionColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION));
        final String dateColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DATE));
        final Float durationColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DURATION));
        final Float unitsColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_UNIT));
        final Float totalpayColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TOTALPAY));

        descriptionTextView.setText(descriptionColumnIndex);
        dateTextView.setText(newDate(dateColumnIndex));
        totalPay.setText(String.format("%.2f",totalpayColumnIndex));

        if (typeColumnIndex.equals("Piece Rate") && durationColumnIndex == 0){
            hoursView.setText(String.valueOf(unitsColumnIndex));
            h.setText("");
            minutesView.setText("");
            m.setText("pcs");
        }else
//            if(typeColumnIndex.equals("Hourly") || typeColumnIndex.equals("hourly"))
        {
            hoursView.setText(timeValues(durationColumnIndex)[0]);
            minutesView.setText(timeValues(durationColumnIndex)[1]);
        }

        final long ID = cursor.getLong(cursor.getColumnIndexOrThrow(ShiftsEntry._ID));
        final Uri currentProductUri = ContentUris.withAppendedId(ShiftsEntry.CONTENT_URI, ID);

        final Bundle b = new Bundle();
        b.putString("uri",String.valueOf(currentProductUri));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                activity.clickOnViewItem(ID);
                FragmentTransaction fragmentTransaction = (MainActivity.fragmentManager).beginTransaction();
                FurtherInfoFragment fragment2 = new FurtherInfoFragment();
                fragment2.setArguments(b);
                fragmentTransaction.replace(R.id.container,fragment2).addToBackStack("furtherinfo").commit();
            }
        });

        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = (MainActivity.fragmentManager).beginTransaction();
                FragmentAddItem fragment3 = new FragmentAddItem();
                fragment3.setArguments(b);
                fragmentTransaction.replace(R.id.container,fragment3).addToBackStack("additem").commit();
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                System.out.println("long click: " + ID);

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete");
                builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteProduct(ID);
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

    }

    private void deleteProduct(Long id) {
        String[] args = new String [] {String.valueOf(id)};
//        String whereClause = String.format(ShiftsEntry._ID + " in (%s)", new Object[] { TextUtils.join(",", Collections.nCopies(args.length, "?")) }); //for deleting multiple lines

        mContext.getContentResolver().delete(ShiftsEntry.CONTENT_URI, ShiftsEntry._ID + "=?", args);
    }

    private String newDate(String dateString){

        String returnString = "01/01/2010";

        String year = dateString.substring(0, 4);
        String month = dateString.substring(5, 7);
        String day = dateString.substring(8);

        returnString = day + "-" + month + "-" + year;

        return returnString;
    }

    public static String[] timeValues(Float duration){

        int hours = (int) Math.floor(duration);
        int minutes = (int) ((duration - hours)*60);

        String hoursString = hours + "";
        String minutesString = String.format("%02d", minutes);

        return new String[]{hoursString,minutesString};
    }

}
