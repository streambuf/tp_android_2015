package com.tech_mail.tp_android_2015;

import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.Set;
import java.util.TreeMap;

import static com.tech_mail.tp_android_2015.utils.HttpResponseGetter.streamToString;

public class LanguageList extends ActionBarActivity {

    // TODO
    private String API_KEY = "";
    private String URL = "";

    private final String API_ARRAY_NAME = "dirs";

    private final Map <String, String> langTransaltor = new HashMap<>();
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

        API_KEY = getResources().getString(R.string.API_KEY);
        URL = getResources().getString(R.string.url_lang_list);

//        TODO
        langTransaltor.put("sq", "Albanian");
        langTransaltor.put("en", "English");
        langTransaltor.put("ar", "Arabic");
        langTransaltor.put("hy", "Armenian");
        langTransaltor.put("az", "Azerbaijan");
        langTransaltor.put("be", "Belarusian");
        langTransaltor.put("bg", "Bulgarian");
        langTransaltor.put("bs", "Bosnian");
        langTransaltor.put("vi", "Vietnamese");
        langTransaltor.put("hu", "Hungarian");
        langTransaltor.put("nl", "Dutch");
        langTransaltor.put("el", "Greek");
        langTransaltor.put("en", "English");
        langTransaltor.put("ka", "Georgian");
        langTransaltor.put("da", "Danish");
        langTransaltor.put("he", "Hebrew");
        langTransaltor.put("id", "Indonesian");
        langTransaltor.put("it", "Italian");
        langTransaltor.put("is", "Icelandic");
        langTransaltor.put("es", "Spanish");
        langTransaltor.put("ca", "Catalan");
        langTransaltor.put("zh", "Chinese");
        langTransaltor.put("lv", "Latvian");
        langTransaltor.put("lt", "Lithuanian");
        langTransaltor.put("ms", "Malay");
        langTransaltor.put("mt", "Maltese");
        langTransaltor.put("mc", "Macedonian");
        langTransaltor.put("de", "German");
        langTransaltor.put("no", "Norwegian");
        langTransaltor.put("po", "Polish");
        langTransaltor.put("pt", "Portuguese");
        langTransaltor.put("ro", "Romanian");
        langTransaltor.put("ru", "Russian");
        langTransaltor.put("sr", "Serbian");
        langTransaltor.put("sk", "Slovak");
        langTransaltor.put("sl", "Slovenian");
        langTransaltor.put("th", "Thai");
        langTransaltor.put("tr", "Turkish");
        langTransaltor.put("uk", "Ukrainian");
        langTransaltor.put("fi", "Finnish");
        langTransaltor.put("fr", "French");
        langTransaltor.put("hr", "Croatian");
        langTransaltor.put("cs", "Czech");
        langTransaltor.put("sv", "Swedish");
        langTransaltor.put("et", "Estonian");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);

        String default_langs = "";
        new DownloadLanguageList(default_langs)
                .execute(URL + API_KEY);
    }

    private class DownloadLanguageList extends AsyncTask<String, Void, String> {

        private String languages;

        public DownloadLanguageList(String languages) {
            this.languages = languages;
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
            Set<String> set = languageMap.keySet();
            String[] array = new String[set.size()];
            set.toArray(array);

//            for (int i = 0; i < array.length; i++) {
//                array[i] = langTransaltor.get(array[i]);
//            }

            applyAdapter(array);
        }
    }

    public void applyAdapter (String[] array) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, array);
        ListView listView = (ListView) findViewById(R.id.lang_list);
        listView.setAdapter(adapter);
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
