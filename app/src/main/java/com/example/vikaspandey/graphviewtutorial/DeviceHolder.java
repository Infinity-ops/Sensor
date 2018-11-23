package com.example.vikaspandey.graphviewtutorial;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DeviceHolder extends RecyclerView.ViewHolder {
    private Context context;
    BluetoothDevice device;
    TextView deviceName;
    TextView deviceAddress;


    public DeviceHolder(Context context,View itemView) {
        super(itemView);
        this.context = context;
        deviceName = (TextView)itemView.findViewById(R.id.text);
        deviceAddress = (TextView)itemView.findViewById(R.id.address);
    }

    public void bindViewWithData(RecyclerView.ViewHolder viewHolder, DeviceAdapter adapter,
                                 final BluetoothDevice device, int position) {

        deviceName.setText(device.getName());
        deviceAddress.setText(device.getAddress());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabFragment1 fragment = new TabFragment1();
                FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                Set<BluetoothDevice> deviceSet = new HashSet<BluetoothDevice>();
                deviceSet.add(device);
                Bundle args = new Bundle();
                args.putParcelable("EXTRA_DEVICE", device);
                args.putParcelableArrayList("PAIRED_DEVICE", new ArrayList(deviceSet));
                fragment.setArguments(args);
                FragmentTransaction ft = fm.beginTransaction();
                ft.show(fragment);
                ft.commit();


            }
        });



    }

    public void removeItem(int position) {
       // device.remove(position);
      //  notifyItemRemoved(position);
      //  notifyItemRangeChanged(position, countries.size());
    }
}

