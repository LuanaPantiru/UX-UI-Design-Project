package com.example.myweather.data;

import java.util.List;

public interface LocationRepository {

    void getAll(List<Location> locations);
    void add(String result);
}
