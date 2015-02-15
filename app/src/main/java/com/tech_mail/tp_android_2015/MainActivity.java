package com.tech_mail.tp_android_2015;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tech_mail.tp_android_2015.utils.HttpResponseGetter;

import org.json.JSONObject;


import java.net.URLEncoder;

public class MainActivity extends ActionBarActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this, null);

        Button buttonTranslate = (Button) findViewById(R.id.translate);
        final EditText editText = (EditText) findViewById(R.id.editText);

        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromLang = ((Button) findViewById(R.id.from_lang)).getText().toString();
                String toLang = ((Button) findViewById(R.id.to_lang)).getText().toString();

                String text = editText.getText().toString();
                try {
                    text = URLEncoder.encode(text, "UTF-8");
                    new TranslatedTextGetter(fromLang, toLang, text).execute();
                }
                catch (Exception e) {
                    setTextView("Can't be translated");
                    Log.e("MainActivity", e.toString());
                }

            }
        });

        Button buttonToHistory = (Button) findViewById(R.id.to_history);
        buttonToHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setTextView(String text) {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(text);
    }

    // View - элемент, на котором сработало событие
    public void switchLang(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, LanguageList.class);
        startActivity(intent);
    }

    private class TranslatedTextGetter extends AsyncTask<String, Void, String> {

        private String fromLang;
        private String toLang;
        private String text;

        public TranslatedTextGetter(String fromLang, String toLang, String text) {
            this.fromLang = fromLang;
            this.toLang = toLang;
            this.text = text;
        }

        @Override
        protected String doInBackground(String ... params) {
            String urlTranslate = getResources().getString(R.string.url_translate);
            String requestURL = urlTranslate + "&lang=" + fromLang + "-" + toLang + "&text=" + text;
            String translatedText = null;
            try {
                JSONObject json = HttpResponseGetter.getResponseByUrl(requestURL);
                Integer status = (Integer) json.getInt("code");
                if (status != 200) {
                    setTextView("Can't be translated: " + json.toString());
                } else {
                    translatedText = (String) json.getString("text");
                    // убираем скобки и кавычки
                    translatedText = translatedText.substring(2, translatedText.length() - 2);
                }
            } catch (Exception e) {
                Log.e("MainActivity", e.toString());
                setTextView("Can't be translated");
            }
            return translatedText;
        }

        @Override
        protected void onPostExecute(String result) {
            dbHelper.insert(fromLang, text, toLang, result);
            setTextView(result);
        }
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
