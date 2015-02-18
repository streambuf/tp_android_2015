package com.tech_mail.tp_android_2015;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tech_mail.tp_android_2015.utils.HttpResponseGetter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.tech_mail.tp_android_2015.utils.HttpResponseGetter.streamToString;

public class LanguageList extends ActionBarActivity {
    private Map <String, ArrayList<String>> languageMap = new TreeMap<>();
    private String action;
    private String fromLang;
    private String toLang;

    private List<String> getListFromJSON (JSONArray jArray) throws JSONException {
        List <String> languageList = new ArrayList<>();
        int length = jArray.length();
        for (int i=0; i < length; i++)
            languageList.add(jArray.getString(i));
        return languageList;
    }


    private void parseLanguageList (List<String> languages) {
        String curLang = "";
        ArrayList<String> curLangTo = new ArrayList<>();
        for (String pair : languages) {
            String[] array = pair.split("-");
            if (languageMap.containsKey(array[0]))
                curLangTo.add(array[1]);
            else {
                languageMap.put(curLang, curLangTo);
                curLangTo.clear();
                curLang = array[0];
                curLangTo.add(array[1]);
            }
        }
        languageMap.remove("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String API_KEY = getResources().getString(R.string.API_KEY);
        String URL = getResources().getString(R.string.url_lang_list);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);

//        TODO ничего из нижепреведенного не обязательно может присутствовать
        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        fromLang = intent.getStringExtra("from_lang");
        toLang = intent.getStringExtra("to_lang");

        new DownloadLanguageList(this)
                .execute(URL + API_KEY);
    }

    private class DownloadLanguageList extends AsyncTask<String, Void, String> {
        private LanguageList myActivity;
        public DownloadLanguageList(LanguageList activity) {
            this.myActivity = activity;
        }

        protected String doInBackground(String... urls) {
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                JSONObject json = new JSONObject(streamToString(in));
                String API_ARRAY_NAME = getResources().getString(R.string.api_langarray_name);
                parseLanguageList(getListFromJSON(json.getJSONArray(API_ARRAY_NAME)));
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return languageMap.keySet().toString();
        }
        protected void onPostExecute(String result) {
            String[] array;
            if (action.equals("to_lang_change")) {
                ArrayList availableLangs = languageMap.get(fromLang);
                array = new String[availableLangs.size()];
                availableLangs.toArray(array);
            }
            else {
                Set<String> set = languageMap.keySet();
                array = new String[set.size()];
                set.toArray(array);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(myActivity,
                    android.R.layout.simple_list_item_1, array);
            ListView langList = (ListView) findViewById(R.id.lang_list);
            langList.setAdapter(adapter);
            langList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                    String selectedFromList = (listView.getItemAtPosition(position).toString());
                    if (action.equals("from_lang_change")) {
                        fromLangChange(selectedFromList);
                    } else if (action.equals("to_lang_change")) {
                        toLangChange(selectedFromList);
                    }
                }

                public void fromLangChange(String selectedFromList) {
                    Intent intent = new Intent(myActivity, MainActivity.class);
                    intent.putExtra("from_lang", selectedFromList);
                    ArrayList availableLangs = languageMap.get(selectedFromList);
                    if (!availableLangs.contains(toLang))
                        toLang = (String) availableLangs.get(0);
                    intent.putExtra("to_lang", toLang);
                    intent.putExtra("action", "lang_changed");
                    startActivity(intent);
                }

                public void toLangChange (String selectedFromList) {
                    ArrayList availableLangs = languageMap.get(fromLang);
                    if (availableLangs.contains(selectedFromList)) {
                        Intent intent = new Intent(myActivity, MainActivity.class);
                        intent.putExtra("to_lang", selectedFromList);
                        intent.putExtra("from_lang", fromLang);
                        intent.putExtra("action", "lang_changed");
                        startActivity(intent);
                    } else
                        Toast.makeText(getApplicationContext(), "No translation available for your language combination\nPlease, select another language", Toast.LENGTH_SHORT).show();
                }
            });
        }

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
