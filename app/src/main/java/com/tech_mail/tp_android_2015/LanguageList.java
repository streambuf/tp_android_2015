package com.tech_mail.tp_android_2015;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.tech_mail.tp_android_2015.utils.HttpResponseGetter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.tech_mail.tp_android_2015.utils.HttpResponseGetter.streamToString;

public class LanguageList extends ActionBarActivity {

    private DatabaseHelper dbHelper;

    // TODO
    private final String URL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20150213T145944Z.ca111d9a559d26b2.d078d31f6d32d5c17e70ba9ffeca68ea26b0269d";

    private final String API_ARRAY_NAME = "dirs";
    private Map <String, ArrayList<String>> languageMap = new TreeMap<>();

    private List<String> getListFromJSON (JSONArray jArray) throws JSONException {
        List <String> languageList = new ArrayList<>();
        for (int i=0; i < jArray.length(); i++)
        {
            languageList.add(jArray.getString(i));
        }
        return languageList;
    }


    private void parseLanguageList (List<String> languages) {
        String curLang = "";
        ArrayList<String> curLangTo = new ArrayList<>();
        for (String pair : languages) {
            String[] array = pair.split("-");
            if (languageMap.containsKey(array[0])) {
                curLangTo.add(array[1]);
            }
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);

        String default_langs = "";
        new DownloadLanguageList(default_langs)
                .execute(URL);
    }

    private class DownloadLanguageList extends AsyncTask<String, Void, String> {

        private String languages;
        private final TextView languageList = (TextView)  findViewById(R.id.lang_list);

        public DownloadLanguageList(String languages) {
            this.languages = languages;
            languageList.setText(languages);
        }

        protected String doInBackground(String... urls) {
            String downloads = null;
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                JSONObject json = new JSONObject(streamToString(in));
                parseLanguageList(getListFromJSON(json.getJSONArray(API_ARRAY_NAME)));

                downloads = languageMap.keySet().toString();

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return downloads;

        }

        protected void onPostExecute(String result) {
            languageList.setText(result);
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
