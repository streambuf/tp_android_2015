package com.tech_mail.tp_android_2015;

import android.app.ListActivity;
import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class HistoryActivity extends ListActivity {

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
        Cursor cursor = dbHelper.fetchAll();

        String[] columns = new String[] {
                dbHelper.REQUEST,
                dbHelper.TRANS,
        };

        int[] views = new int[] {
                R.id.request,
                R.id.trans,
        };

        adapter = new SimpleCursorAdapter(this, R.layout.history_item, cursor, columns, views, 0);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " выбран", Toast.LENGTH_LONG).show();
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

        return super.onOptionsItemSelected(item);
    }
}
