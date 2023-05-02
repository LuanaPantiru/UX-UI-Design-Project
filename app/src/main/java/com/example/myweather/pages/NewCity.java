package com.example.myweather.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myweather.R;
import com.example.myweather.api.ApiBuilder;
import com.example.myweather.data.AddOperation;
import com.example.myweather.data.Location;
import com.example.myweather.data.LocationRepository;
import com.example.myweather.model.CurrentDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewCity extends Fragment implements LocationRepository{

    private ImageView back;
    private EditText input;
    private ImageView search;
    private TextView city;
    private Button save;

    public NewCity() { super(R.layout.new_city);}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        back = v.findViewById(R.id.back);
        input = v.findViewById(R.id.searchLocation);
        search = v.findViewById(R.id.search);
        city = v.findViewById(R.id.cityName);
        save = v.findViewById(R.id.save);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                String cityName = input.getEditableText().toString();
                if(cityName == null || cityName.length() == 0){
                    Toast.makeText(getActivity(), "Write a city", Toast.LENGTH_SHORT).show();
                }
                else{
                    Call<CurrentDay> call = ApiBuilder.getInstance().getByCity(cityName, ApiBuilder.UNITS, ApiBuilder.APP_ID);
                    call.enqueue(new Callback<CurrentDay>() {
                        @Override
                        public void onResponse(Call<CurrentDay> call, Response<CurrentDay> response) {
                            if(response.body() != null){

                                CurrentDay currentDay = response.body();
                                city.setText(response.body().getName());
                                save.setVisibility(v.VISIBLE);
                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Location loc = new Location(city.getText().toString(), currentDay.getSys().getCountry(),false);
                                        new AddOperation(NewCity.this).execute(loc);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                                R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                                        Fragment frg = new Cities();
                                        fragmentTransaction.replace(R.id.cities,frg);
                                        fragmentTransaction.commit();
                                    }
                                });
                            }else{
                                Toast.makeText(getActivity(), "The city doesn't exist", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<CurrentDay> call, Throwable t) {
                        }
                    });
                }
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void getAll(List<Location> locations) {

    }

    @Override
    public void add(String result) {
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
    }
}
