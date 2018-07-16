package com.example.vikaspandey.graphviewtutorial;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Random;

import info.plux.pluxapi.Communication;
import info.plux.pluxapi.bitalino.BITalinoCommunication;
import info.plux.pluxapi.bitalino.BITalinoCommunicationFactory;
import info.plux.pluxapi.bitalino.BITalinoException;
import info.plux.pluxapi.bitalino.BITalinoFrame;
import info.plux.pluxapi.bitalino.bth.OnBITalinoDataAvailable;

/**
 * Created by vikaspandey on 30/5/18.
 */

public class TabFragment1 extends Fragment implements  View.OnClickListener, OnBITalinoDataAvailable {
    private final String TAG = this.getClass().getSimpleName();
    private static final Random RANDOM = new Random();
    private int lastX = 0;
    LineGraphSeries<DataPoint> series;
    private Context context;
    private ImageButton scanButton;
    private ImageButton recordButton;
    public static   TextView resultsTextView;
    private ArrayList<String> sampleArray1 = new ArrayList<>();
    private ArrayList<String> deviceArray = new ArrayList<>();
    private ArrayList<BluetoothDevice> pairedDevice = new ArrayList<>();
    private BITalinoCommunication bitalino;
    private boolean isBITalino2 = false;
    public final static String EXTRA_DEVICE = "com.example.vikaspandey.graphviewtutorial.MainActivity.EXTRA_DEVICE";
    public final static String FRAME = "com.example.vikaspandey.graphviewtutorial.MainActivity.Frame";
    private BluetoothDevice bluetoothDevice;
    private Handler handler;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_WEIGHT = 1;
    private static final int REQUEST_ANOTHER_ONE = 2;



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            bluetoothDevice = data.getParcelableExtra("EXTRA_DEVICE");

            pairedDevice = data.getParcelableArrayListExtra("PAIRED_DEVICE");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        for (int i=0; i<5;i++) {
            sampleArray1.add("Item" + i);

        }




        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(!mBluetoothAdapter.getBondedDevices().isEmpty()){
            pairedDevice = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
        }
        for(BluetoothDevice device:pairedDevice){
            deviceArray.add(device.getName() + " " +device.getAddress());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);

        Bundle bundle = getArguments();
        if(bundle!=null) {
            bluetoothDevice = bundle.getParcelable("EXTRA_DEVICE");
        }

        resultsTextView = (TextView) view.findViewById(R.id.textView2);


        GraphView graphView = (GraphView) view.findViewById(R.id.graph_view);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        graphView.getGridLabelRenderer().setGridColor(getResources().getColor(android.R.color.holo_red_light));
        series = new LineGraphSeries<DataPoint>();


        // set manual X bounds
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-50);
        graphView.getViewport().setMaxY(50);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(4);
        graphView.getViewport().setMaxX(80);

        // enable scaling and scrolling
        //graphView.getViewport().setScalable(true);
        //graphView.getViewport().setScalableY(true);

        graphView.getViewport().setScrollable(true);
        series = new LineGraphSeries<DataPoint>();
        series.setColor(Color.BLACK);

        graphView.addSeries(series);

        RecyclerView recyclerView1 = (RecyclerView) view.findViewById(R.id.exercise_view);
        RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.devices_view);

        ItemAdapter adapter1 = new ItemAdapter(getContext(), sampleArray1);
        ItemAdapter adapter2 = new ItemAdapter(getContext(), deviceArray);
        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
        recyclerView1.setNestedScrollingEnabled(false);
        recyclerView2.setNestedScrollingEnabled(false);
        recyclerView1.setLayoutManager(new LinearLayoutManager(context));
        recyclerView2.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recordButton = (ImageButton) getActivity().findViewById(R.id.button);
        scanButton = (ImageButton) getActivity().findViewById(R.id.button2);
        recordButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
    }

    public void onClick(final View v) {
        switch (v.getId()) {

            case R.id.button:
                try {
                    recordButton.setBackgroundResource(R.drawable.ic_record);
                    Communication communication = Communication.getById(bluetoothDevice.getType());
                    bitalino = new BITalinoCommunicationFactory().getCommunication(communication,getActivity());
                    bitalino.connect(bluetoothDevice.getAddress());

                    bitalino.start(new int[]{0,1,2,3,4,5}, 1000);

                    // read until task is stopped

                    handler = new Handler(getActivity().getMainLooper()){
                        @Override
                        public void handleMessage(Message msg) {
                            Bundle bundle = msg.getData();
                            BITalinoFrame frame = bundle.getParcelable(FRAME);

                            Log.d(TAG, frame.toString());

                            if(frame != null){ //BITalino
                                resultsTextView.setText(frame.toString());
                            }
                        }
                    };



                } catch (BITalinoException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.button2:
                // ScanActivity  fm =new ScanActivity();
                //  android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                //transaction.replace(R.id.devices_view, fm);
                //transaction.addToBackStack(null);

                // Commit the transaction
                //transaction.commit();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DeviceFragment dialog = new DeviceFragment();
                dialog.show(fm,"device list");
                dialog.setTargetFragment(TabFragment1.this,1);
                //Intent in = new Intent(getContext(), ScanActivity.class);
                //startActivity(in);
                break;



            default:
                break;
        }


    }

    @Override
    public void onResume(){
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<500; i++) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });
                    try {
                        Thread.sleep(600);
                    }catch(InterruptedException ie){

                    }

                }

            }
        }).start();
    }

    private void addEntry(){


        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble()* 10d), true, 500);
    }

    @Override
    public void onBITalinoDataAvailable(BITalinoFrame bitalinoFrame) {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelable(FRAME, bitalinoFrame);
        message.setData(bundle);
        handler.sendMessage(message);
    }
}
