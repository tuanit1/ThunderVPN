package com.example.thundervpn.fragments;/*
 * *
 *  * Created by tuan on 1/27/22, 1:40 AM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/27/22, 1:40 AM
 *
 */

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thundervpn.R;
import com.example.thundervpn.adapters.CountryAdapter;
import com.example.thundervpn.asynctasks.GetAllCountryAsync;
import com.example.thundervpn.items.Country;
import com.example.thundervpn.listeners.GetAllCountryListener;
import com.example.thundervpn.listeners.ItemCountryClickListener;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.Methods;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class MyBottomSheetFragments extends BottomSheetDialogFragment {

    private ArrayList<Country> mCountries = new ArrayList<>();
    private ItemCountryClickListener listener;
    private ItemCountryClickListener listener2;
    private ProgressBar progressBar;
    private LinearLayout ll_main;
    private RecyclerView rv_country;
    private EditText edt_search;
    private View view;
    private RelativeLayout rl_empty;
    private TextView tv_title;
    private Methods methods;

    public MyBottomSheetFragments(ItemCountryClickListener listener, ArrayList<Country> arrayList_country) {
        this.listener = listener;
        this.mCountries = arrayList_country;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet, null);
        methods = new Methods(getContext());
        bottomSheetDialog.setContentView(view);

        Hook();

        setupUI();

        return bottomSheetDialog;
    }


    private void Hook() {
        rv_country = view.findViewById(R.id.rv_country);
        rl_empty = view.findViewById(R.id.rl_empty);
        ll_main = view.findViewById(R.id.ll_main);
        tv_title = view.findViewById(R.id.tv_title);
        progressBar = view.findViewById(R.id.progressBar);
        edt_search = view.findViewById(R.id.edt_search);
    }


    private void setupUI() {

        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if(mCountries.isEmpty()){
            rl_empty.setVisibility(View.VISIBLE);
            ll_main.setVisibility(View.GONE);
        }else{
            rl_empty.setVisibility(View.GONE);
            ll_main.setVisibility(View.VISIBLE);

            listener2 = new ItemCountryClickListener() {
                @Override
                public void onClick(int id) {
                    dismiss();
                }
            };

            rv_country.setLayoutManager(new LinearLayoutManager(getContext()));
            CountryAdapter adapter = new CountryAdapter(mCountries, listener, listener2);
            rv_country.setAdapter(adapter);

//            ArrayList<Country> arrayList_search = new ArrayList<>();
//
//            edt_search.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    arrayList_search.clear();
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    for(Country c : mCountries){
//                        if(c.getName().toLowerCase().contains(charSequence.toString().toLowerCase())){
//                            arrayList_search.add(c);
//                        }
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    adapter.setAdapterData(arrayList_search);
//                    adapter.notifyDataSetChanged();
//                }
//            });
        }
    }

}
