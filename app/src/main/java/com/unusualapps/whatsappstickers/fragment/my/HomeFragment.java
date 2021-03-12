package com.unusualapps.whatsappstickers.fragment.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.unusualapps.whatsappstickers.Event.HomeActivityEvent;
import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.adapter.PackHomeFragmentAdapter;
import com.unusualapps.whatsappstickers.model.Data;

public class HomeFragment extends Fragment {
    private RecyclerView rcv;

    HomeActivityEvent event;

    Data data;

    PackHomeFragmentAdapter adapter;

    public HomeFragment(HomeActivityEvent event) {
        this.event = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);

        rcv = view.findViewById(R.id.rcv);

        data = new Gson().fromJson(getString(R.string.data), Data.class);
        adapter = new PackHomeFragmentAdapter(data.listPack, event);


        rcv.setAdapter(adapter);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
