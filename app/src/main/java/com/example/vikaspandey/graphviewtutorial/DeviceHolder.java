package com.example.vikaspandey.graphviewtutorial;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeviceHolder extends RecyclerView.ViewHolder{
        TextView deviceName;
        TextView deviceAddress;
        LinearLayout layout;

    public DeviceHolder(View itemView){
        super(itemView);
        deviceName = itemView.findViewById(R.id.text);
        deviceAddress = itemView.findViewById(R.id.address);
        layout= itemView.findViewById(R.id.llayout);
        }
}

