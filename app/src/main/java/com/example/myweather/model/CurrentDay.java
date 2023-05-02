package com.example.myweather.model;

import java.util.List;

public class CurrentDay {
    private List<Weather> weather;
    private Caracteristics main;
    private Wind wind;
    private String name;
    private Sys sys;

    public CurrentDay(List<Weather> weather, Caracteristics main, Wind wind, String name, Sys sys) {
        this.weather = weather;
        this.main = main;
        this.wind = wind;
        this.name = name;
        this.sys = sys;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Caracteristics getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public String getName() {
        return name;
    }

    public Sys getSys() {
        return sys;
    }
}
