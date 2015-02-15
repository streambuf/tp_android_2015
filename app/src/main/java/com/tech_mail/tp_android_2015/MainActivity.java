package com.tech_mail.tp_android_2015;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.InputStream;


public class MainActivity extends ActionBarActivity {

    private final String URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20150213T145944Z.ca111d9a559d26b2.d078d31f6d32d5c17e70ba9ffeca68ea26b0269d&text=cat&lang=en-ru";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonTranslate = (Button) findViewById(R.id.translate);
        final EditText editText = (EditText) findViewById(R.id.editText);
        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("EditText", editText.getText().toString());
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet request = new HttpGet(URL);

                InputStream inputStream = null;
                String translatedText = null;
            }
        });
    }

    // View - элемент, на котором сработало событие
    public void switchLang(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, LanguageList.class);
        startActivity(intent);
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
