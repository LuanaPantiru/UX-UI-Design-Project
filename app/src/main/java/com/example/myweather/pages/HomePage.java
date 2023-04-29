package com.example.myweather.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myweather.R;
import com.example.myweather.api.ApiBuilder;
import com.example.myweather.model.MomentOfDay;
import com.example.myweather.model.WeatherApiModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class HomePage extends Fragment {

    public TextView buttonSeeMore;
    public TextView city;
    public TextView time;
    public TextView temp;
    public ImageView img;
    private String cityLocation;
    Bundle bundle;
    private List<MomentOfDay> weather = new ArrayList<>();

    public HomePage() {
        super(R.layout.home_page);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater,container,savedInstanceState);
        bundle = getArguments();

        time = v.findViewById(R.id.Time);
        temp = v.findViewById(R.id.Temperature);
        img = v.findViewById(R.id.imgIcon);
        city = v.findViewById(R.id.City);

        getInfoFromLocation();

        buttonSeeMore = v.findViewById(R.id.See_more);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getInfoFromLocation(){
        Call<WeatherApiModel> call;
        String lat, log;
        lat = bundle.getString("latitude");
        log = bundle.getString("longitude");
        call = ApiBuilder.getInstance().getWeather(lat, log, ApiBuilder.UNITS, ApiBuilder.APP_ID);
        call.enqueue(new Callback<WeatherApiModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<WeatherApiModel> call, @NonNull retrofit2.Response<WeatherApiModel> response) {
                weather = response.body().getList();
                cityLocation = response.body().getCity().getName();

                city.setText(cityLocation);

                configureInfo();
            }

            @Override
            public void onFailure(@NonNull Call<WeatherApiModel> call, @NonNull Throwable t) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void configureInfo() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentHour = dateFormat.format(date);
        time.setText(currentHour);

        MomentOfDay md = getMomentOfDay(currentHour);
        temp.setText(Math.round(Double.parseDouble(md.getMain().getTemp())) +"°C");
        setImg(md.getWeather().get(0).getMain(), roundHour(currentHour));
    }

    private MomentOfDay getMomentOfDay(String currentHour){
        int h = roundHour(currentHour);
        MomentOfDay md1 = weather.get(0);
        int md1Time = Integer.parseInt(md1.getDt_txt().substring(md1.getDt_txt().length()-8).substring(0,2));
        MomentOfDay md2 = weather.get(1);
        int md2Time = Integer.parseInt(md2.getDt_txt().substring(md2.getDt_txt().length()-8).substring(0,2));
        if(h-md1Time < md2Time - h){
            return md1;
        }else{
            return md2;
        }
    }

    private int roundHour(String hour){
        int min = Integer.parseInt(hour.substring(4));
        if(min<30){
            return Integer.parseInt(hour.substring(0,2));
        }else{
            return Integer.parseInt(hour.substring(0,2))+1;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImg(String description, int time){
        switch (description){
            case "Clouds":
                img.setImageDrawable(getResources().getDrawable(R.drawable.clouds,getActivity().getApplicationContext().getTheme()));
                break;
            case "Rain":
                img.setImageDrawable(getResources().getDrawable(R.drawable.rain,getActivity().getApplicationContext().getTheme()));
                break;
            case "Snow":
                img.setImageDrawable(getResources().getDrawable(R.drawable.snow,getActivity().getApplicationContext().getTheme()));
                break;
            case "Clear":
                if(time>7 && time<21){
                    img.setImageDrawable(getResources().getDrawable(R.drawable.sun,getActivity().getApplicationContext().getTheme()));
                }else{
                    img.setImageDrawable(getResources().getDrawable(R.drawable.moon,getActivity().getApplicationContext().getTheme()));
                }
                break;
        }
    }
}