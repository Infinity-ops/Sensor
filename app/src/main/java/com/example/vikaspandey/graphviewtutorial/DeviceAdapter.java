package com.example.vikaspandey.graphviewtutorial;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder>{
    private ArrayList<BluetoothDevice> devices;
        Context c;
    private LayoutInflater mInflator;

    public DeviceAdapter(Context c,ArrayList<BluetoothDevice> devices) {
        this.c = c;
        this.devices = devices;
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list,parent,false) ;
         DeviceHolder viewHolder = new DeviceHolder(v);
        return viewHolder;
        }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {

        final BluetoothDevice device = devices.get(position);
        holder.deviceAddress.setText(device.getAddress());
        holder.deviceName.setText(device.getName());
    }

    @Override
    public int getItemCount() {
            return devices.size();
            }
}

