package com.example.myweather.model;

public class DayOfWeek {
    private String dayName;
    private String minTemp;
    private String maxTemp;
    private String description;

    public DayOfWeek(String dayName, String minTemp, String maxTemp, String description) {
        this.dayName = dayName;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.description = description;
    }

    public String getDayName() {
        return dayName;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getDescription() {
        return description;
    }
}
