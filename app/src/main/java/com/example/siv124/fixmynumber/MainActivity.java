package com.example.siv124.fixmynumber;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    
    ListView lvNumbers;
    ArrayList<String> numbers;
    ArrayList<String> ids;
    private static final String SQL_WHERE_BY_ID = BaseColumns._ID + "=?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvNumbers = (ListView) findViewById(R.id.numbersList);
        numbers = new ArrayList<>();
        ids = new ArrayList<>();
        getNumbers();
        fillList();
    }

    private void fillList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, numbers);
        lvNumbers.setAdapter(adapter);
    }

    private void getNumbers() {
        Cursor numbersCursor = getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//        Cursor numbersCursor = getContentResolver()
//                .query(ContactsContract.Data.CONTENT_URI, null, null, null, null);
        while (numbersCursor.moveToNext()) {
            String number = numbersCursor.getString(
                    numbersCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            String number = numbersCursor.getString(
//                    numbersCursor.getColumnIndex(ContactsContract.Data.DATA1));
            String id = numbersCursor.getString(
                    numbersCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            numbers.add(number);
            ids.add(id);
        }
    }

    public void changeNumbers(View view) {
        for (int i = 0; i < numbers.size(); i++) {
            ArrayList<ContentProviderOperation> op = new ArrayList<ContentProviderOperation>();
            op.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(SQL_WHERE_BY_ID, new String[]{ids.get(i)})
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, numbers.get(i) + "00")
                    .build());
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, op);
            } catch (Exception e) {
                Log.e("Exception: ", e.getMessage());
            }
        }
        numbers = new ArrayList<>();
        getNumbers();
        fillList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
