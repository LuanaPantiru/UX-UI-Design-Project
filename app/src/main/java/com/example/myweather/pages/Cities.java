package com.example.myweather.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myweather.R;
import com.example.myweather.adapter.CityAdapter;
import com.example.myweather.adapter.OnItemClickListener;
import com.example.myweather.data.GetAllOperation;
import com.example.myweather.data.Location;
import com.example.myweather.data.LocationRepository;

import java.util.ArrayList;
import java.util.List;

public class Cities extends Fragment implements LocationRepository, OnItemClickListener {

    private List<Location> cities = new ArrayList<>();
    private CityAdapter adapter;
    private EditText filterValue;
    private ImageView filter;
    private View add;
    RecyclerView rv;

    public Cities() { super(R.layout.cities);}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater,container,savedInstanceState);

        add = v.findViewById(R.id.add);
        filterValue = v.findViewById(R.id.searchLocation);
        filter = v.findViewById(R.id.search);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = filterValue.getText().toString().toLowerCase();
                if(value.length() == 0){
                    adapter.submit(cities);
                }else{
                    ArrayList<Location> citiesFiltered = new ArrayList<>();
                    for (Location city : cities) {
                        if (city.getCity().toLowerCase().contains(value) || city.getCountry().toLowerCase().contains(value)) {
                            citiesFiltered.add(city);
                        }
                    }
                    adapter.submit(citiesFiltered);
                }
//                ArrayList<Location> citiesFiltered = new ArrayList<>();
////                String value = editable.toString().toLowerCase();
////                for(Location city : cities){
////                    if(city.getCity().toLowerCase().contains(value) || city.getCountry().toLowerCase().contains(value)){
////                        citiesFiltered.add(city);
////                    }
////                }
////                adapter.submit(citiesFiltered);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add.setVisibility(View.GONE);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                Fragment frg = new NewCity();
                fragmentTransaction.replace(R.id.cities,frg);
                fragmentTransaction.commit();
            }
        });
        rv = view.findViewById(R.id.recycle_view_cities);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(),2);
        rv.setLayoutManager(gridLayoutManager);
        new GetAllOperation(this).execute(new Object());
    }



    @Override
    public void getAll(List<Location> locations) {
        cities = locations;
        adapter = new CityAdapter(locations, this);
        rv.setAdapter(adapter);

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

    }

    @Override
    public void onItemClick(Location city) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,R.anim.exit_left_to_right);
        Fragment frg = new CityDetails();
        Bundle bundle = new Bundle();
        bundle.putString("city",city.getCity());
        bundle.putBoolean("selected", city.getSelected());
        frg.setArguments(bundle);
        fragmentTransaction.replace(R.id.cities,frg);
        fragmentTransaction.commit();
    }
}
