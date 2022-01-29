/*
 * *
 *  * Created by tuan on 1/29/22, 5:25 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/29/22, 5:25 PM
 *
 */

package com.example.thundervpn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thundervpn.R;
import com.example.thundervpn.items.Country;
import com.example.thundervpn.listeners.ItemCountryClickListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder>{

    private ArrayList<Country> mCountries;
    private ItemCountryClickListener listener;
    private ItemCountryClickListener listener2;
    private Context context;

    public CountryAdapter(ArrayList<Country> mCountries, ItemCountryClickListener listener, ItemCountryClickListener listener2) {
        this.mCountries = mCountries;
        this.listener = listener;
        this.listener2 = listener2;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_country_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Country country = mCountries.get(position);
        holder.tv_name.setText(country.getName());

        if(country.getId() == 0){
            Picasso.get()
                    .load(R.drawable.global)
                    .error(R.drawable.global)
                    .into(holder.iv_image);
        }else {
            Picasso.get()
                    .load(country.getThumb())
                    .error(R.drawable.global)
                    .into(holder.iv_image);
        }



        if(country.isPremium()){
            holder.iv_premium.setVisibility(View.VISIBLE);
        }else {
            holder.iv_premium.setVisibility(View.GONE);
        }

        holder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(country.getId());
                listener2.onClick(country.getId());
            }
        });

    }

    public void setAdapterData(ArrayList<Country> arrayList){
        this.mCountries = arrayList;
    }

    @Override
    public int getItemCount() {
        return mCountries.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_image, iv_premium;
        TextView tv_name;
        RelativeLayout rl_item;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            iv_image = itemView.findViewById(R.id.iv_image);
            iv_premium = itemView.findViewById(R.id.iv_premium);
            tv_name = itemView.findViewById(R.id.tv_name);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }
}
