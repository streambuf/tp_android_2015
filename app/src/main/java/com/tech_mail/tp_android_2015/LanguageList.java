package com.tech_mail.tp_android_2015;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.tech_mail.tp_android_2015.utils.LanguageListParser;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static android.app.PendingIntent.getActivity;
import static com.tech_mail.tp_android_2015.utils.HttpResponseGetter.streamToString;
import static java.lang.Thread.sleep;

public class LanguageList extends ActionBarActivity {
    private static final int ORIENTATION_LANDSCAPE = 2;
    private Map <String, ArrayList<String>> languageMap = new TreeMap<>();
    private String action;
    private String fromLang;
    private String toLang;
    private ListView langList;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String API_KEY = getResources().getString(R.string.API_KEY);
        String URL = getResources().getString(R.string.url_lang_list);
        String progressBar = "Downloading Language List";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);



//        int orient = this.getResources().getConfiguration().orientation;

        progress = new ProgressDialog(this);
        progress.setMessage(progressBar);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();

        Intent intent = getIntent();
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
        }

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

        new DownloadLanguageList().execute(URL + API_KEY);
    }

    private class DownloadLanguageList extends AsyncTask<String, Void, String> {
        public DownloadLanguageList() {}

        protected String doInBackground(String... urls) {
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                JSONObject json = new JSONObject(streamToString(in));
                String API_ARRAY_NAME = getResources().getString(R.string.api_langarray_name);

                languageMap = LanguageListParser.parseLanguageList(
                        LanguageListParser.getListFromJSON(
                                json.getJSONArray(API_ARRAY_NAME)
                        )
                );
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

            ArrayAdapter<String> adapter = new ArrayAdapter<>(LanguageList.this,
                    android.R.layout.simple_list_item_1, array);
            langList.setAdapter(adapter);

            progress.hide();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
