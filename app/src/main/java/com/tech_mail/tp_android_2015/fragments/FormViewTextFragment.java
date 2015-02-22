package com.tech_mail.tp_android_2015.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tech_mail.tp_android_2015.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormViewTextFragment extends Fragment {


    public FormViewTextFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form_view_text, container, false);
    }


}
