package com.example.vikaspandey.graphviewtutorial;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {

    private Context context;
    private ArrayList<BluetoothDevice> arrayList;

    private int itemTypeDefault = 1;

    public DeviceAdapter(Context context, ArrayList<BluetoothDevice> list) {
        this.context = context;
        this.arrayList = list;
    }

    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == itemTypeDefault) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new DeviceHolder(context, view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new DeviceHolder(context, view);
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, int position) {
        if (holder instanceof DeviceHolder) {
            DeviceHolder viewHolder = (DeviceHolder) holder;
            viewHolder.bindViewWithData(viewHolder, this, arrayList.get(position),
                    position);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemTypeDefault;
    }


}

