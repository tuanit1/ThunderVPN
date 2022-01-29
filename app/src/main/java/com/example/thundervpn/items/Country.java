/*
 * *
 *  * Created by tuan on 1/29/22, 2:14 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/29/22, 2:14 PM
 *
 */

package com.example.thundervpn.items;

public class Country {
    private int id;
    private String name;
    private boolean isPremium;
    private String thumb;

    public Country(int id, String name, boolean isPremium, String thumb) {
        this.id = id;
        this.name = name;
        this.isPremium = isPremium;
        this.thumb = thumb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
