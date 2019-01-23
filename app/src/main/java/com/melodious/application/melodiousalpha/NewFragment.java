package com.melodious.application.melodiousalpha;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewFragment extends Fragment {
    RecyclerView newRecyclerView;
    MelodyAdapter melodyAdapter;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.new_uploads, container, false);
        //loading data into recycler view
        newRecyclerView = (RecyclerView) view.findViewById(R.id.new_uploads_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        newRecyclerView.setLayoutManager(linearLayoutManager);
        newRecyclerView.setHasFixedSize(true);


        loadNewMelodies();
        return view;
    }

    public void loadNewMelodies(){
        String[] data = {"Angel", "Raju", "Sanjoy",};
    }
}
