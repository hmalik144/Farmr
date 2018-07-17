package com.example.h_mal.shift_tracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.example.h_mal.shift_tracker.Data.ShiftsContract.ShiftsEntry;
import com.example.h_mal.shift_tracker.Data.ShiftsDbHelper;

import java.io.File;

import static com.example.h_mal.shift_tracker.MainActivity.args;
import static com.example.h_mal.shift_tracker.MainActivity.filter;
import static com.example.h_mal.shift_tracker.MainActivity.selection;
import static com.example.h_mal.shift_tracker.MainActivity.sortOrder;


public class FragmentMain extends Fragment  implements
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static final int DEFAULT_LOADER = 0;
    public static int NEW_LOADER = 0;

    ShiftsCursorAdapter mCursorAdapter;
    ShiftsDbHelper shiftsDbhelper;
    LoaderManager.LoaderCallbacks defaultLoaderCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        MainActivity.setActionBarTitle(getString(R.string.app_name));

        filter = getActivity().getSharedPreferences("PREFS", 0);
        sortOrder = filter.getString("Filter",null);

        defaultLoaderCallback = this;

        ListView productListView = (ListView) rootView.findViewById(R.id.list_item_view);

        View emptyView = rootView.findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        mCursorAdapter = new ShiftsCursorAdapter((MainActivity) getActivity(), null);
        productListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(DEFAULT_LOADER, null, defaultLoaderCallback);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = (MainActivity.fragmentManager).beginTransaction();
                fragmentTransaction.replace(R.id.container,new FragmentAddItem()).addToBackStack("additem").commit();
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.delete_all:
                deleteAllProducts();
                return true;

            case R.id.help:
                new AlertDialog.Builder(getContext())
                        .setTitle("Help & Support:")
                        .setView(R.layout.dialog_layout)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        }).create().show();
                return true;

            case R.id.filter_data:
                FragmentTransaction fragmentTransaction = (MainActivity.fragmentManager).beginTransaction();
                fragmentTransaction.replace(R.id.container,new FilterDataFragment()).addToBackStack("filterdata").commit();
                return true;

            case R.id.sort_data:
                sortData();
                return true;

            case R.id.clear_filter:
                args = null;
                selection = null;
                NEW_LOADER = 0;
                getLoaderManager().restartLoader(DEFAULT_LOADER, null, this);
                return true;
            case R.id.export_data:
                if(checkStoragePermissions(getActivity())){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Export?")
                            .setMessage("Exporting current filtered data. Continue?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    ExportData();
                                }
                            }).create().show();
                }else{
                    Toast.makeText(getContext(), "Storage permissions required", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_favorite:
                new AlertDialog.Builder(getContext())
                        .setTitle("Info:")
                        .setMessage(retrieveInfo())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        }).create().show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortData(){
        final String[] grpname = {"Added","Date","Name"};
        final String[] sortQuery = {""};
        int checkedItem = -1;

        if(sortOrder != null && sortOrder.contains(ShiftsEntry._ID)){
            checkedItem = 0;
            sortQuery[0] = ShiftsEntry._ID;
        }else if(sortOrder != null && sortOrder.contains(ShiftsEntry.COLUMN_SHIFT_DATE)){
            checkedItem = 1;
            sortQuery[0] = ShiftsEntry.COLUMN_SHIFT_DATE;
        }else if(sortOrder != null && sortOrder.contains(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION)){
            checkedItem = 2;
            sortQuery[0] = ShiftsEntry.COLUMN_SHIFT_DESCRIPTION;
        }

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(getContext());
        //alt_bld.setIcon(R.drawable.icon);
        alt_bld.setTitle("Sort by:");
        alt_bld.setSingleChoiceItems(grpname, checkedItem, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        sortQuery[0] = ShiftsEntry._ID;
                        return;
                    case 1:
                        sortQuery[0] = ShiftsEntry.COLUMN_SHIFT_DATE;
                        return;
                    case 2:
                        sortQuery[0] = ShiftsEntry.COLUMN_SHIFT_DESCRIPTION;
                }

            }
        }).setPositiveButton("Ascending", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                sortOrder = sortQuery[0] + " ASC";
                filter.edit().putString("Filter",sortOrder).apply();
                getLoaderManager().restartLoader(DEFAULT_LOADER, null, defaultLoaderCallback);
                dialog.dismiss();
            }
        }).setNegativeButton("Descending", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                sortOrder = sortQuery[0] + " DESC";
                filter.edit().putString("Filter",sortOrder).apply();
                getLoaderManager().restartLoader(DEFAULT_LOADER, null, defaultLoaderCallback);
                dialog.dismiss();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void deleteAllProducts() {
    new AlertDialog.Builder(getContext())
            .setTitle("Delete?")
            .setMessage("Are you sure you want to delete all date?")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    int rowsDeleted = getActivity().getContentResolver().delete(ShiftsEntry.CONTENT_URI, null, null);
                    Toast.makeText(getContext(), rowsDeleted + " Items Deleted", Toast.LENGTH_SHORT).show();
                }
            }).create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(NEW_LOADER > DEFAULT_LOADER) {
            getLoaderManager().restartLoader(DEFAULT_LOADER, null, defaultLoaderCallback);
            System.out.println("reloading loader");
        }
    }

    private void ExportData() {

        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Storage permissions not granted", Toast.LENGTH_SHORT).show();
            return;
        }


        shiftsDbhelper = new ShiftsDbHelper(getContext());
        SQLiteDatabase database = shiftsDbhelper.getWritableDatabase();

        String[] projection_export = {
                ShiftsEntry.COLUMN_SHIFT_DESCRIPTION,
                ShiftsEntry.COLUMN_SHIFT_DATE,
                ShiftsEntry.COLUMN_SHIFT_TIME_IN,
                ShiftsEntry.COLUMN_SHIFT_TIME_OUT,
                ShiftsEntry.COLUMN_SHIFT_BREAK,
                ShiftsEntry.COLUMN_SHIFT_DURATION,
                ShiftsEntry.COLUMN_SHIFT_TYPE,
                ShiftsEntry.COLUMN_SHIFT_UNIT,
                ShiftsEntry.COLUMN_SHIFT_PAYRATE,
                ShiftsEntry.COLUMN_SHIFT_TOTALPAY};
        Cursor cursor = getActivity().getContentResolver().query(
                ShiftsEntry.CONTENT_URI,
                projection_export,
                selection,
                args,
                sortOrder);

        database.delete(ShiftsEntry.TABLE_NAME_EXPORT,null,null);

        float totalDuration = 0.00f;
        float totalUnits = 0.00f;
        float totalPay= 0.00f;

        try {
            while (cursor.moveToNext()) {
                final String descriptionColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION));
                final String dateColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DATE));
                final String timeInColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TIME_IN));
                final String timeOutColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TIME_OUT));
                final Float durationColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DURATION));
                final Integer breakOutColumnIndex = cursor.getInt(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_BREAK));
                final String typeColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TYPE));
                final Float unitColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_UNIT));
                final Float payrateColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_PAYRATE));
                final Float totalpayColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TOTALPAY));

                totalUnits = totalUnits + unitColumnIndex;
                totalDuration = totalDuration + durationColumnIndex;
                totalPay = totalPay + totalpayColumnIndex;

                ContentValues values = new ContentValues();
                values.put(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION, descriptionColumnIndex);
                values.put(ShiftsEntry.COLUMN_SHIFT_DATE, dateColumnIndex);
                values.put(ShiftsEntry.COLUMN_SHIFT_TIME_IN, timeInColumnIndex);
                values.put(ShiftsEntry.COLUMN_SHIFT_TIME_OUT, timeOutColumnIndex);
                values.put(ShiftsEntry.COLUMN_SHIFT_BREAK, breakOutColumnIndex);
                values.put(ShiftsEntry.COLUMN_SHIFT_DURATION, durationColumnIndex);
                values.put(ShiftsEntry.COLUMN_SHIFT_TYPE, typeColumnIndex);
                values.put(ShiftsEntry.COLUMN_SHIFT_UNIT, unitColumnIndex);
                values.put(ShiftsEntry.COLUMN_SHIFT_PAYRATE, payrateColumnIndex);
                values.put(ShiftsEntry.COLUMN_SHIFT_TOTALPAY, totalpayColumnIndex);

                database.insert(ShiftsEntry.TABLE_NAME_EXPORT, null, values);

            }
        } finally {
            ContentValues values = new ContentValues();
            values.put(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION, "");
            values.put(ShiftsEntry.COLUMN_SHIFT_DATE, "");
            values.put(ShiftsEntry.COLUMN_SHIFT_TIME_IN, "");
            values.put(ShiftsEntry.COLUMN_SHIFT_TIME_OUT, "");
            values.put(ShiftsEntry.COLUMN_SHIFT_BREAK, "Total duration:");
            values.put(ShiftsEntry.COLUMN_SHIFT_DURATION, totalDuration);
            values.put(ShiftsEntry.COLUMN_SHIFT_TYPE, "Total units:");
            values.put(ShiftsEntry.COLUMN_SHIFT_UNIT, totalUnits);
            values.put(ShiftsEntry.COLUMN_SHIFT_PAYRATE, "Total pay:");
            values.put(ShiftsEntry.COLUMN_SHIFT_TOTALPAY, totalPay);

            database.insert(ShiftsEntry.TABLE_NAME_EXPORT, null, values);
            cursor.close();
        }


        final String savePath = Environment.getExternalStorageDirectory() + "/ShifttrackerTemp";
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(getContext(), "shifts.db",savePath);

        sqLiteToExcel.exportSingleTable("shiftsexport","shifthistory.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

            }
            @Override
            public void onCompleted(String filePath) {
                Toast.makeText(getContext(), filePath, Toast.LENGTH_SHORT).show();
                Uri newPath = Uri.parse("file://" + savePath + "/" +"shifthistory.xls");

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent emailintent = new Intent(Intent.ACTION_SEND);

                emailintent.setType("application/vnd.ms-excel");
                emailintent.putExtra(Intent.EXTRA_SUBJECT, "historic shifts");
                emailintent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
                emailintent.putExtra(Intent.EXTRA_STREAM,newPath);

                startActivity(Intent.createChooser(emailintent, "Send Email"));
            }
            @Override
            public void onError(Exception e) {
                System.out.println("Error msg: " + e);
                Toast.makeText(getContext(), "Failed to Export data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String retrieveInfo(){
        String[] projection = {
                ShiftsEntry._ID,
                ShiftsEntry.COLUMN_SHIFT_DESCRIPTION,
                ShiftsEntry.COLUMN_SHIFT_DATE,
                ShiftsEntry.COLUMN_SHIFT_TIME_IN,
                ShiftsEntry.COLUMN_SHIFT_TIME_OUT,
                ShiftsEntry.COLUMN_SHIFT_BREAK,
                ShiftsEntry.COLUMN_SHIFT_DURATION,
                ShiftsEntry.COLUMN_SHIFT_TYPE,
                ShiftsEntry.COLUMN_SHIFT_UNIT,
                ShiftsEntry.COLUMN_SHIFT_PAYRATE,
                ShiftsEntry.COLUMN_SHIFT_TOTALPAY};
        Cursor cursor = getActivity().getContentResolver().query(
                ShiftsEntry.CONTENT_URI,
                projection,
                selection,
                args,
                sortOrder);
        float totalDuration = 0.0f;
        int countOfTypeH = 0;
        int countOfTypeP = 0;
        float totalUnits = 0;
        float totalPay = 0;
        int lines = 0;
        try {
            while (cursor.moveToNext()) {
                final Float durationColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DURATION));
                final String typeColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TYPE));
                final Float unitColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_UNIT));
                final Float totalpayColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TOTALPAY));

                totalDuration = totalDuration + durationColumnIndex;
                if (typeColumnIndex.contains("Hourly")){
                    countOfTypeH = countOfTypeH + 1;
                }else if (typeColumnIndex.contains("Piece")){
                    countOfTypeP = countOfTypeP +1;
                }
                totalUnits= totalUnits + unitColumnIndex;
                totalPay = totalPay + totalpayColumnIndex;
            }
        } finally {

            if ((cursor != null) && (cursor.getCount() > 0)) {
                lines = cursor.getCount();
                cursor.close();
            }

        }

        return buildInfoString(totalDuration,countOfTypeH,countOfTypeP,totalUnits,totalPay,lines);
    }

    public String buildInfoString(float totalDuration, int countOfTypeH, int countOfTypeP, float totalUnits, float totalPay, int lines){
        String textString;
        textString = lines + " Shifts";
        if (countOfTypeH != 0 && countOfTypeP != 0){
            textString = textString + " (" + countOfTypeH + " Hourly" + "/" + countOfTypeP + " Piece Rate)";
        }
        if(countOfTypeH != 0){
            textString = textString
                    + "\n" + "Total Hours: " + String.format("%.2f",totalDuration);
        }
        if(countOfTypeP != 0){
            textString = textString
                    + "\n" + "Total Units: " + String.format("%.2f",totalUnits);
        }

        if (totalPay != 0){
            textString = textString
                    + "\n" + "Total Pay: " + "$" + String.format("%.2f",totalPay);
        }

        return textString;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ShiftsEntry._ID,
                ShiftsEntry.COLUMN_SHIFT_DESCRIPTION,
                ShiftsEntry.COLUMN_SHIFT_DATE,
                ShiftsEntry.COLUMN_SHIFT_TIME_IN,
                ShiftsEntry.COLUMN_SHIFT_TIME_OUT,
                ShiftsEntry.COLUMN_SHIFT_BREAK,
                ShiftsEntry.COLUMN_SHIFT_DURATION,
                ShiftsEntry.COLUMN_SHIFT_TYPE,
                ShiftsEntry.COLUMN_SHIFT_PAYRATE,
                ShiftsEntry.COLUMN_SHIFT_UNIT,
                ShiftsEntry.COLUMN_SHIFT_TOTALPAY,};

        return new android.support.v4.content.CursorLoader(getContext(),
                ShiftsEntry.CONTENT_URI,
                projection,
                selection,
                args,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        System.out.println("request code" + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportDialog();
                }else{
                    Toast.makeText(getContext(), "Storage Permissions denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void exportDialog(){
        new AlertDialog.Builder(getContext())
                .setTitle("Export?")
                .setMessage("Exporting current filtered data. Continue?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ExportData();
                    }
                }).create().show();
    }
//    // Storage Permissions
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
//    /**
//     * Checks if the app has permission to write to device storage
//     *
//     * If the app does not has permission then the user will be prompted to grant permissions
//     *
//     * @param activity
//     */
//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//    }

    public static boolean checkStoragePermissions(Activity activity){
        boolean status = false;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED ){
            status = true;
        }

        return status;
    }

}
