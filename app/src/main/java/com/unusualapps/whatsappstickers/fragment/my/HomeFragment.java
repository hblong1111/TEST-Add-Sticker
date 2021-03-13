package com.unusualapps.whatsappstickers.fragment.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.unusualapps.whatsappstickers.Event.HomeActivityEvent;
import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.adapter.PackHomeFragmentAdapter;
import com.unusualapps.whatsappstickers.model.Data;
import com.unusualapps.whatsappstickers.view_model.HomeActivityViewModel;
import com.unusualapps.whatsappstickers.view_model.ViewModelFactory;

public class HomeFragment extends Fragment {
    private RecyclerView rcv;

    HomeActivityEvent event;

    Data data;

    PackHomeFragmentAdapter adapter;
    private static HomeFragment INSTANCE;

    public static HomeFragment getInstance(HomeActivityEvent event) {
        if (INSTANCE == null) {
            INSTANCE = new HomeFragment(event);
        }
        return INSTANCE;
    }

    public HomeFragment(HomeActivityEvent event) {
        this.event = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);

        init(view);

        return view;
    }

    private void init(View view) {

        rcv = view.findViewById(R.id.rcv);

        data = new Gson().fromJson(getString(R.string.data), Data.class);
        adapter = new PackHomeFragmentAdapter(data.listPack, event);


        rcv.setAdapter(adapter);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
