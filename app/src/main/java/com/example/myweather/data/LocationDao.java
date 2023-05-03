package com.example.myweather.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LocationDao {

    @Query("Select * from location")
    List<Location> getAll();

    @Insert
    void insertAll(Location... locations);

    @Query("delete from location where city Like :name")
    void delete(String name);

    @Query("select * from location where city Like :cityName")
    Location findByCity(String cityName);

    @Update
    void updateLocation(Location... locations);
}
