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

    private String URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20150213T145944Z.ca111d9a559d26b2.d078d31f6d32d5c17e70ba9ffeca68ea26b0269d";
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
                } catch (Exception e) {
                    Log.e("MainActivity", e.toString());
                }
                new TranslatedTextGetter(fromLang, toLang, text).execute();
            }
        });
    }

    // View - элемент, на котором сработало событие
    public void switchLang(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, LanguageList.class);
        startActivity(intent);
    }

    private class TranslatedTextGetter extends AsyncTask<String, Void, String> {

        private final TextView textView = (TextView)  findViewById(R.id.textView);
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
            String requestURL = URL + "&lang=" + fromLang + "-" + toLang + "&text=" + text;

            String translatedText = null;
            try {
                JSONObject json = HttpResponseGetter.getResponseByUrl(requestURL);
                translatedText = (String) json.getString("text");
                // убираем скобки и кавычки
                translatedText = translatedText.substring(2, translatedText.length() - 2);
            } catch (Exception e) {
                Log.e("MainActivity", e.toString());
            }
            return translatedText;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                dbHelper.insert(fromLang, text, toLang, result);
                textView.setText(result);
            }
            else {
                Log.e("MainActivity", "Response is null");
            }
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

    public void toHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}
