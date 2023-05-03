package com.example.myweather.pages;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myweather.R;
import com.example.myweather.adapter.DayOfWeekAdapter;
import com.example.myweather.api.ApiBuilder;
import com.example.myweather.data.FindBySelectedOperation;
import com.example.myweather.data.Location;
import com.example.myweather.data.LocationRepository;
import com.example.myweather.model.DayOfWeek;
import com.example.myweather.model.MomentOfDay;
import com.example.myweather.model.WeatherApiModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;

public class HomePage extends Fragment implements LocationRepository {

    public TextView buttonSeeMore;
    public TextView city;
    public TextView time;
    public TextView temp;
    public TextView description;
    public ImageView img;
    private String cityLocation;
    Bundle bundle;
    private List<MomentOfDay> weather = new ArrayList<>();
    public DayOfWeekAdapter adapter;
    public static List<DayOfWeek> weekList = new ArrayList<>();
    private final Bundle bundleSeeMore = new Bundle();

    public HomePage() {
        super(R.layout.home_page);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        bundle = getArguments();

        time = v.findViewById(R.id.Time);
        temp = v.findViewById(R.id.Temperature);
        img = v.findViewById(R.id.imgIcon);
        city = v.findViewById(R.id.City);
        description = v.findViewById(R.id.description);

        getInfo();

        buttonSeeMore = v.findViewById(R.id.See_more);
        buttonSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                Fragment frg = new SeeMore();
                frg.setArguments(bundleSeeMore);
                fragmentTransaction.replace(R.id.home_page, frg);
                fragmentTransaction.commit();
            }
        });
        return v;
    }

    private void getInfo() {
        new FindBySelectedOperation(this).execute(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.recycle_view_resume_week);

        adapter = new DayOfWeekAdapter(weekList);
        rv.setAdapter(adapter);
    }

    private void getInfoFromLocation() {
        Call<WeatherApiModel> call;
        String lat, log;
        lat = bundle.getString("latitude");
        log = bundle.getString("longitude");
        call = ApiBuilder.getInstance().getWeather(lat, log, ApiBuilder.UNITS, ApiBuilder.APP_ID);
        call.enqueue(new Callback<WeatherApiModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<WeatherApiModel> call, @NonNull retrofit2.Response<WeatherApiModel> response) {
                weather = response.body().getList();
                cityLocation = response.body().getCity().getName();
                String country = response.body().getCity().getCountry();
                city.setText(cityLocation + ", " + country);
                bundleSeeMore.putString("city", cityLocation);
                bundleSeeMore.putString("latitude", lat);
                bundleSeeMore.putString("longitude", log);

                configureInfo();
            }

            @Override
            public void onFailure(@NonNull Call<WeatherApiModel> call, @NonNull Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private void configureInfo() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentHour = dateFormat.format(date);
        time.setText(currentHour);

        MomentOfDay md = weather.get(0);
        description.setText(md.getWeather().get(0).getMain());
        temp.setText(Math.round(Double.parseDouble(md.getMain().getTemp())) + "°C");
        setImg(md.getWeather().get(0).getMain(), roundHour(currentHour));

        adapter = new DayOfWeekAdapter(weekList);
        weekResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void weekResume() {
        List<String> dayOfWeek = new ArrayList<>(Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));
        int nrDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        String currentDay = weather.get(0).getDt_txt().substring(0, 10);
        weather = weather.stream().filter(w -> !w.getDt_txt().contains(currentDay)).collect(Collectors.toList());
        while (weekList.size() < 5) {
            nrDay = nrDay == 6 ? 0 : nrDay + 1;
            String day = weather.get(0).getDt_txt().substring(0, 10);
            List<MomentOfDay> mdList = weather.stream().filter(w -> w.getDt_txt().contains(day)).collect(Collectors.toList());
            Double minTemp = mdList.stream().map(md -> Double.parseDouble(md.getMain().getTemp_min())).min(Double::compare).get();
            Double maxTemp = mdList.stream().map(md -> Double.parseDouble(md.getMain().getTemp_max())).max(Double::compare).get();
            weekList.add(new DayOfWeek(dayOfWeek.get(nrDay) + ", " + day, Math.round(minTemp) + "°C", Math.round(maxTemp) + "°C", weather.get(0).getWeather().get(0).getMain()));
            weather.removeAll(mdList);
        }
        adapter.submit(weekList);
    }

    private int roundHour(String hour) {
        int min = Integer.parseInt(hour.substring(4));
        if (min < 30) {
            return Integer.parseInt(hour.substring(0, 2));
        } else {
            return Integer.parseInt(hour.substring(0, 2)) + 1;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImg(String description, int time) {
        switch (description) {
            case "Clouds":
                img.setImageDrawable(getResources().getDrawable(R.drawable.clouds, getActivity().getApplicationContext().getTheme()));
                break;
            case "Rain":
                img.setImageDrawable(getResources().getDrawable(R.drawable.rain, getActivity().getApplicationContext().getTheme()));
                break;
            case "Snow":
                img.setImageDrawable(getResources().getDrawable(R.drawable.snow, getActivity().getApplicationContext().getTheme()));
                break;
            case "Clear":
                if (time > 7 && time < 21) {
                    img.setImageDrawable(getResources().getDrawable(R.drawable.sun, getActivity().getApplicationContext().getTheme()));
                } else {
                    img.setImageDrawable(getResources().getDrawable(R.drawable.moon, getActivity().getApplicationContext().getTheme()));
                }
                break;
        }
    }

    @Override
    public void getAll(List<Location> locations) {

    }

    @Override
    public void add(String result) {

    }

    @Override
    public void delete(String result) {

    }

    @Override
    public void findByCity(Location loc) {

    }

    @Override
    public void update(String result) {

    }

    @Override
    public void findBySelected(Location loc) {
        if(loc == null){
            getInfoFromLocation();
        }else{
            getInfoFromDB(loc);
        }
    }

    private void getInfoFromDB(Location loc) {
        Call<WeatherApiModel> call = ApiBuilder.getInstance().getWeatherByCity(loc.getCity(), ApiBuilder.UNITS, ApiBuilder.APP_ID);
        call.enqueue(new Callback<WeatherApiModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<WeatherApiModel> call, @NonNull retrofit2.Response<WeatherApiModel> response) {
                weather = response.body().getList();
                cityLocation = response.body().getCity().getName();
                String country = response.body().getCity().getCountry();
                city.setText(cityLocation + ", " + country);
                bundleSeeMore.putString("city",cityLocation);

                configureInfo();
            }

            @Override
            public void onFailure(@NonNull Call<WeatherApiModel> call, @NonNull Throwable t) {

            }
        });
    }
}
