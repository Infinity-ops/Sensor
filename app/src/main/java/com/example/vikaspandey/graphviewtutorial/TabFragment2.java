package com.example.vikaspandey.graphviewtutorial;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by vikaspandey on 30/5/18.
 */

public class TabFragment2 extends Fragment {

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);

        ArrayList<String> sampleArray = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            sampleArray.add("Item" + i);
        }


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        ItemAdapter adapter = new ItemAdapter(getContext(), sampleArray);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }
}