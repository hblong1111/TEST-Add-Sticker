package com.unusualapps.whatsappstickers.fragment.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unusualapps.whatsappstickers.Event.HomeActivityEvent;
import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.activities.CreateNewPackLocalActivity;
import com.unusualapps.whatsappstickers.activities.SettingActivity;
import com.unusualapps.whatsappstickers.adapter.PackLocalAdapter;
import com.unusualapps.whatsappstickers.db.AppDatabase;
import com.unusualapps.whatsappstickers.db.DatabaseModule;
import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.view_model.HomeActivityViewModel;
import com.unusualapps.whatsappstickers.view_model.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class PackCustomFragment extends Fragment implements View.OnClickListener {

    private ImageButton btnSetting;
    private Group grEmpty;
    private TextView btnCreatePack;
    private LinearLayout btnCreatePack2;
    private RecyclerView rcv;
    private Group grSize;

    AppDatabase db;

    HomeActivityViewModel model;
    HomeActivityEvent event;
    PackLocalAdapter adapter;

    List<PackLocal> list;


    private static PackCustomFragment INSTANCE;

    public static PackCustomFragment getInstance(HomeActivityEvent event) {
        if (INSTANCE == null) {
            INSTANCE = new PackCustomFragment(event);
        }
        return INSTANCE;
    }

    public PackCustomFragment(HomeActivityEvent event) {
        this.event = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_custom_pack, container, false);

        initView(view);

        initData();

        model.getListPackLocal().observe(getActivity(), packLocals -> {
            updateView(packLocals);

            list.clear();
            list.addAll(packLocals);
            adapter.notifyDataSetChanged();
        });
        return view;
    }

    private void updateView(List<PackLocal> packLocals) {
        if (packLocals != null && packLocals.size() > 0) {
            grEmpty.setVisibility(View.INVISIBLE);
            grSize.setVisibility(View.VISIBLE);
        } else {
            grSize.setVisibility(View.INVISIBLE);
            grEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        db = DatabaseModule.getInstance(getActivity().getApplication());

        model = new ViewModelProvider(getActivity(), new ViewModelFactory()).get(HomeActivityViewModel.class);

        list = new ArrayList<>();
        adapter = new PackLocalAdapter(list, db);
        rcv.setAdapter(adapter);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void initView(View view) {
        btnSetting = view.findViewById(R.id.btnSetting);
        grEmpty = view.findViewById(R.id.grEmpty);
        btnCreatePack = view.findViewById(R.id.btnCreatePack);
        btnCreatePack2 = view.findViewById(R.id.btnCreatePack2);
        rcv = view.findViewById(R.id.rcv);
        grSize = view.findViewById(R.id.grSize);

        btnCreatePack.setOnClickListener(this);
        btnCreatePack2.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreatePack:
            case R.id.btnCreatePack2:
                createNewPack();
                break;
            case R.id.btnSetting:
                settingApp();
                break;
        }
    }

    private void settingApp() {
        startActivity(new Intent(getContext(), SettingActivity.class));

    }

    private void createNewPack() {
        startActivity(new Intent(getContext(), CreateNewPackLocalActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        model.getListPackLocal(DatabaseModule.getInstance(getActivity().getApplication()));
    }
}
