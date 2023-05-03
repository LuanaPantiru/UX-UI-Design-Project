package com.example.myweather.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myweather.R;
import com.example.myweather.api.ApiBuilder;
//import com.example.myweather.data.DeleteOperation;
//import com.example.myweather.data.FindByCityOperation;
import com.example.myweather.data.DeleteOperation;
import com.example.myweather.data.FindByCityOperation;
import com.example.myweather.data.GetAllOperation;
import com.example.myweather.data.Location;
import com.example.myweather.data.LocationRepository;
//import com.example.myweather.data.UpdateOperation;
import com.example.myweather.data.UpdateOperation;
import com.example.myweather.model.CurrentDay;
import com.example.myweather.model.Weather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityDetails extends Fragment implements LocationRepository {

    private ImageView back;
    private ImageView icon;
    private TextView city;
    private TextView feels;
    private TextView min;
    private TextView max;
    private TextView wind;
    private TextView humidity;
    private TextView pressure;
    private TextView description;
    private ImageView delete;
    private Switch aSwitch;

    Bundle bundle;

    public CityDetails() {
        super(R.layout.city_details);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        icon = v.findViewById(R.id.imageView);
        city = v.findViewById(R.id.location);
        feels = v.findViewById(R.id.temperature);
        description = v.findViewById(R.id.description);
        min = v.findViewById(R.id.minTemp);
        max = v.findViewById(R.id.maxTemp);
        wind = v.findViewById(R.id.windValue);
        humidity = v.findViewById(R.id.humidityValue);
        pressure = v.findViewById(R.id.pressureValue);

        bundle = getArguments();
        Call<CurrentDay> call;
        String cityName = bundle.getString("city");

        call = ApiBuilder.getInstance().getByCity(cityName, ApiBuilder.UNITS, ApiBuilder.APP_ID);
        call.enqueue(new Callback<CurrentDay>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CurrentDay> call, Response<CurrentDay> response) {
                CurrentDay currentDay = response.body();
                city.setText(currentDay.getName()+", " +currentDay.getSys().getCountry());
                List<Weather> weatherList = currentDay.getWeather();
                String descriptionValue = weatherList.get(weatherList.size()-1).getMain();
                setImg(descriptionValue);
                description.setText(descriptionValue);
                String minValue = Math.round(Double.parseDouble(currentDay.getMain().getTemp_min())) + "°C";
                min.setText(minValue);
                String maxValue = Math.round(Double.parseDouble(currentDay.getMain().getTemp_max())) + "°C";
                max.setText(maxValue);
                String feelsValue = Math.round(Double.parseDouble(currentDay.getMain().getFeels_like())) + "°C";
                feels.setText(feelsValue);
                wind.setText(currentDay.getWind().getSpeed() + " km/h");
                humidity.setText(currentDay.getMain().getHumidity() + "%");
                pressure.setText(currentDay.getMain().getPressure()+"hPa");
            }

            @Override
            public void onFailure(Call<CurrentDay> call, Throwable t) {

            }
        });

        back = v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                Fragment frg = new Cities();
                frg.setArguments(bundle);
                fragmentTransaction.replace(R.id.cities, frg);
                fragmentTransaction.commit();
            }
        });

        delete = v.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteOperation(CityDetails.this).execute(cityName);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                Fragment frg = new Cities();
                fragmentTransaction.replace(R.id.cities,frg);
                fragmentTransaction.commit();
            }
        });

        aSwitch = v.findViewById(R.id.switch1);
        bundle = getArguments();
        Boolean selected = bundle.getBoolean("selected");
        aSwitch.setChecked(selected);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(compoundButton.getId() == R.id.switch1){
                    changeItem(cityName);
                }
            }
        });

        return v;
    }

    private void changeItem(String cityName) {
        new FindByCityOperation(this).execute(cityName);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void setImg(String description){
        switch (description){
            case "Clouds":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.clouds,getActivity().getApplicationContext().getTheme()));
                break;
            case "Rain":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.rain,getActivity().getApplicationContext().getTheme()));
                break;
            case "Snow":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.snow,getActivity().getApplicationContext().getTheme()));
                break;
            case "Clear":
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("HH");
                int time = Integer.parseInt(dateFormat.format(date));
                if(time>7 && time<21){
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.sun,getActivity().getApplicationContext().getTheme()));
                }else{
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.moon,getActivity().getApplicationContext().getTheme()));
                }
                break;
        }
    }

    @Override
    public void getAll(List<Location> locations) {
        for(Location loc : locations){
            if(loc.getSelected()){
                loc.setSelected(false);
                new UpdateOperation(this).execute(loc);
            }
        }
    }

    @Override
    public void add(String result) {

    }

    @Override
    public void delete(String result) {
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void findByCity(Location loc) {
        if(!loc.getSelected()) {
            new GetAllOperation(this).execute(new Object());
        }
        loc.setSelected(!loc.getSelected());
        new UpdateOperation(this).execute(loc);
    }

    @Override
    public void update(String result) {

    }

    @Override
    public void findBySelected(Location loc) {

    }
}
