package com.tech_mail.tp_android_2015;


import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LanguageList extends ActionBarActivity {
    private Map <String, ArrayList<String>> languageMap = new HashMap<>();
    private String action;
    private String fromLang;
    private String toLang;
    private String [] langArray;
    private ListView langList;

    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);

        dbHelper = new DatabaseHelper(this, null);

        Intent intent = getIntent();
        fetchParams(intent);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(LanguageList.this,
                android.R.layout.simple_list_item_1, langArray);

        langList = (ListView) findViewById(R.id.lang_list);
        langList.setAdapter(adapter);
        langList = (ListView) findViewById(R.id.lang_list);
        langList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                String selectedFromList = (listView.getItemAtPosition(position).toString());

                if (action.equals("from_lang_change")) {
                    ArrayList availableLangs = languageMap.get(selectedFromList);

                    if (!availableLangs.contains(toLang))
                        toLang = (String) availableLangs.get(0);

                    startActivity(ReturnLanguageIntent(selectedFromList, toLang, "lang_changed"));

                } else if (action.equals("to_lang_change")) {
                    ArrayList availableLangs = languageMap.get(fromLang);

                    if (availableLangs.contains(selectedFromList))
                        startActivity(ReturnLanguageIntent(fromLang, selectedFromList, "lang_changed"));
                    else
                        Toast.makeText(
                                getApplicationContext(),
                                "No translation available for your language combination\nPlease, select another language",
                                Toast.LENGTH_SHORT
                        ).show();
                }
            }
        });
    }

    public void fetchParams (Intent intent) {
        action = intent.getStringExtra("action");

        if (! (action.equals("from_lang_change") || (action.equals("to_lang_change"))) ) {
            Toast.makeText(
                    getApplicationContext(),
                    "Unexpected action brought you here",
                    Toast.LENGTH_SHORT
            ).show();
//            TODO Error activity
            startActivity(ReturnLanguageIntent("", "", ""));
        }
        else {
            fromLang = intent.getStringExtra("from_lang");
            toLang = intent.getStringExtra("to_lang");
            languageMap = (HashMap <String, ArrayList<String>>) intent.getSerializableExtra("map");
        }

        if (action.equals("from_lang_change")) {
            Set<String> set = languageMap.keySet();
            langArray = new String[set.size()];
            set.toArray(langArray);
        }
        else {
            ArrayList availableLangs = languageMap.get(fromLang);
            langArray = new String[availableLangs.size()];
            availableLangs.toArray(langArray);
        }
    }

    private Intent ReturnLanguageIntent(String from, String to, String action) {
        Intent intent = new Intent(LanguageList.this, MainActivity.class);
        intent.putExtra("to_lang", to);
        intent.putExtra("from_lang", from);
        intent.putExtra("action", action);
        return intent;
    }

    private void displayListView() {
        Cursor cursor = dbHelper.fetchLangPairs();

        String[] columns = new String[] {
            DatabaseHelper.LANG_PAIR,
        };

        int[] views = new int[] {
            //TODO: set view item id in layout (R.id.lang)
        };

        //TODO: set layout
        adapter = new SimpleCursorAdapter(this, R.layout.history_item, cursor, columns, views, 0);

        ListView listView = (ListView) findViewById(R.id.lang_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
            Cursor cursor = (Cursor) listView.getItemAtPosition(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_language_list, menu);
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
