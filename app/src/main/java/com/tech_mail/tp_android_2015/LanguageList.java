package com.tech_mail.tp_android_2015;


import android.content.Intent;
import android.database.Cursor;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LanguageList extends ActionBarActivity {
    private Map <String, ArrayList<String>> languageMap = new HashMap<>();
    private String action;
    private String fromLang;
    private String toLang;
    private String [] langArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);

        Intent intent = getIntent();
        fetchParams(intent);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(LanguageList.this,
                android.R.layout.simple_list_item_1, langArray);

        ListView langList = (ListView) findViewById(R.id.lang_list);
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

                    setResult(RESULT_OK, ReturnLanguageIntent(selectedFromList, toLang, "lang_changed"));

                } else if (action.equals("to_lang_change")) {
                    ArrayList availableLangs = languageMap.get(fromLang);

                    if (availableLangs.contains(selectedFromList))
                        setResult(RESULT_OK,ReturnLanguageIntent(fromLang, selectedFromList, "lang_changed"));

                    else
                        Toast.makeText(
                                getApplicationContext(),
                                "No translation available for your language combination\nPlease, select another language",
                                Toast.LENGTH_SHORT
                        ).show();
                }
                finish();
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
            intent = new Intent(this, MainActivity.class);
            setResult(RESULT_CANCELED, intent);
            finish();
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

        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------

    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;

    private final static String LANG_SEPARATOR = "-";

    private Set<String> getLangsFromDB() {
        Cursor cursor = dbHelper.fetchLangPairs();
        Set<String> result = new HashSet<String>();

        if (cursor != null) {
            String [] langs;

            while(cursor.moveToNext()) {
                langs = cursor.getString(1).split(LANG_SEPARATOR, 2);
                result.add(langs[0]);
                result.add(langs[1]);
            }
            cursor.close();
        }
        return result;
    }
}
