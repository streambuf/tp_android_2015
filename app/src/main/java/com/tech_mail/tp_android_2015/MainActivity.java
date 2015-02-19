package com.tech_mail.tp_android_2015;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        Button buttonFrom = (Button) findViewById(R.id.from_lang);
        Button buttonTo = (Button) findViewById(R.id.to_lang);

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        String fromLang = intent.getStringExtra("from_lang");
        String toLang = intent.getStringExtra("to_lang");

        if (action != null) {
            if (action.equals("lang_changed")) {
                buttonFrom.setText(fromLang);
                buttonTo.setText(toLang);
            }
        }

        Button buttonTranslate = (Button) findViewById(R.id.translate);
        final EditText editText = (EditText) findViewById(R.id.editText);

        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromLang = ((Button) findViewById(R.id.from_lang)).getText().toString();
                String toLang = ((Button) findViewById(R.id.to_lang)).getText().toString();

                String text = editText.getText().toString();
                if (text.length() == 0) {
                    ShowMessage("Field must not be empty");
                }
                try {
                    new TranslatedTextGetter(fromLang, toLang, text).execute();
                }
                catch (Exception e) {
                    ShowMessage("Can't be translated");
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

        final Button buttonFromLang = (Button) findViewById(R.id.from_lang);
        final Button buttonToLang = (Button) findViewById(R.id.to_lang);

        buttonFromLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LanguageList.class);
                intent.putExtra("to_lang", buttonToLang.getText().toString());
                intent.putExtra("from_lang", buttonFromLang.getText().toString());
                intent.putExtra("action", "from_lang_change");
                startActivity(intent);
            }
        });

         buttonToLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LanguageList.class);
                intent.putExtra("to_lang", buttonToLang.getText().toString());
                intent.putExtra("from_lang", buttonFromLang.getText().toString());
                intent.putExtra("action", "to_lang_change");
                startActivity(intent);
            }
        });

        Button buttonSwitchLang = (Button) findViewById(R.id.switch_lang);
        buttonSwitchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromLang = buttonFromLang.getText().toString();
                buttonFromLang.setText(buttonToLang.getText().toString());
                buttonToLang.setText(fromLang);
            }
        });

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
            String translatedText = null;

            try {
                String encodedText = URLEncoder.encode(text, "UTF-8");
                String requestURL = urlTranslate + "&lang=" + fromLang + "-" + toLang + "&text=" + encodedText;

                JSONObject json = HttpResponseGetter.getResponseByUrl(requestURL);
                Integer status = (Integer) json.getInt("code");
                if (status != 200) {
                    ShowMessage("Can't be translated: ");
                }
                else {
                    translatedText = (String) json.getString("text");
                    translatedText = translatedText.substring(2, translatedText.length() - 2);
                }
            } catch (Exception e) {
                Log.e("MainActivity", e.toString());
            }
            return translatedText;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (! text.equals("") &&  ! result.equals("")) {
                    dbHelper.insert(fromLang, text, toLang, result);
                }
                setTextView(result);
            }
            else {
                Log.e("MainActivity", "Response is null");
                ShowMessage("Can't be translated");
            }
        }
    }

    private void setTextView(String text) {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(text);
    }

    private void ShowMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
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
