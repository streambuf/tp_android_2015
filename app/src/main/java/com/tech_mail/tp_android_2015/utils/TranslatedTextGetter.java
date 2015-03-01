package com.tech_mail.tp_android_2015.utils;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.tech_mail.tp_android_2015.DatabaseHelper;
import com.tech_mail.tp_android_2015.MainActivity;

import org.json.JSONObject;

import java.net.URLEncoder;

public class TranslatedTextGetter extends AsyncTask<String, Void, String> {

    private String fromLang;
    private String toLang;
    private String text;
    private String urlTranslate;
    private DatabaseHelper dbHelper;
    private Context context;
    private TextView textView;

    private TranslatedTextGetter() {

    }

    public static TranslatedTextGetter create() {
        return new TranslatedTextGetter();
    }

    @Override
    protected String doInBackground(String ... params) {
        String translatedText = null;

        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String requestURL = urlTranslate + "&lang=" + fromLang + "-" + toLang + "&text=" + encodedText;

            JSONObject json = HttpResponseGetter.getResponseByUrl(requestURL);
            Integer status = json.getInt("code");
            if (status != 200) {
                ShowMessage("Can't be translated: ");
            }
            else {
                translatedText = json.getString("text");
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
                dbHelper.insertTrans(fromLang, text.trim(), toLang, result);
            }
            textView.setText(result);
        }
        else {
            Log.e("MainActivity", "Response is null");
            ShowMessage("Can't be translated");
        }
    }

    private void ShowMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public TranslatedTextGetter fromLang(String fromLang) {
        this.fromLang = fromLang;
        return this;
    }

    public TranslatedTextGetter toLang(String toLang) {
        this.toLang = toLang;
        return this;
    }

    public TranslatedTextGetter text(String text) {
        this.text = text;
        return this;
    }

    public TranslatedTextGetter dbHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        return this;
    }

    public TranslatedTextGetter urlTranslate(String urlTranslate) {
        this.urlTranslate = urlTranslate;
        return this;
    }

    public TranslatedTextGetter context(Context context) {
        this.context = context;
        return this;
    }

    public TranslatedTextGetter textView(TextView textView) {
        this.textView = textView;
        return this;
    }
}
