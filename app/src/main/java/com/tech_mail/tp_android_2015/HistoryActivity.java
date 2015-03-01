package com.tech_mail.tp_android_2015;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class HistoryActivity extends ActionBarActivity {

    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DatabaseHelper(this, null);
        displayListView();
    }

    private void displayListView() {
        Cursor cursor = dbHelper.fetchHistory();

        String[] columns = new String[] {
            DatabaseHelper.REQUEST,
            DatabaseHelper.TRANS,
        };

        int[] views = new int[] {
            R.id.request,
            R.id.trans,
        };

        adapter = new SimpleCursorAdapter(this, R.layout.history_item, cursor, columns, views, 0);

        ListView listView = (ListView) findViewById(R.id.history_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
            Cursor cursor = (Cursor) listView.getItemAtPosition(position);
            String trans = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANS));
            Toast.makeText(getApplicationContext(), trans, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return super.onCreateOptionsMenu(menu);
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
        else if (id == R.id.action_clear) {
            dbHelper.deleteHistory();
            displayListView();
        }

        return super.onOptionsItemSelected(item);
    }
}
