package com.tech_mail.tp_android_2015;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LanguageList extends ActionBarActivity {


    // TODO
    private final String URL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20150213T145944Z.ca111d9a559d26b2.d078d31f6d32d5c17e70ba9ffeca68ea26b0269d";

    private String streamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);
//        Intent intent = getIntent();
        final TextView languageList = (TextView)  findViewById(R.id.lang_list);

        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpGet request = new HttpGet(URL);

        InputStream inputStream = null;
        String languages = "Test";

        try {
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            languages = streamToString(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
        }

//        try {
//            JSONObject jObject = new JSONObject(languages);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        languageList.setText(languages);


//        URL url = null;
//        try {
////            url = new URL("https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20150213T145944Z.ca111d9a559d26b2.d078d31f6d32d5c17e70ba9ffeca68ea26b0269d");
//            url = new URL("http://www.google.com");
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//
//            try {
//                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                String languages = readStream(in);
//                languageList.setText(languages);
//            }
//            finally {
//                urlConnection.disconnect();
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



//        setContentView(languageList);
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
