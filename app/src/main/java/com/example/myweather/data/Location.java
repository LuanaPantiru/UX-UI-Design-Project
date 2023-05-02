package com.example.myweather.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String city;
    private String country;
    private Boolean selected;

    public Location(String city, String country, Boolean selected) {
        this.city = city;
        this.country = country;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
