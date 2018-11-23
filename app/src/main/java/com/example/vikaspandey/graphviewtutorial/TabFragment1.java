package com.example.vikaspandey.graphviewtutorial;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import database.DBHelper;
import database.Entity;
import info.plux.pluxapi.Communication;
import info.plux.pluxapi.Constants;
import info.plux.pluxapi.bitalino.BITalinoCommunication;
import info.plux.pluxapi.bitalino.BITalinoCommunicationFactory;
import info.plux.pluxapi.bitalino.BITalinoDescription;
import info.plux.pluxapi.bitalino.BITalinoException;
import info.plux.pluxapi.bitalino.BITalinoFrame;
import info.plux.pluxapi.bitalino.BITalinoState;
import info.plux.pluxapi.bitalino.bth.OnBITalinoDataAvailable;
import util.JSONHelper;
import util.SensorDataConverter;

import static info.plux.pluxapi.Constants.ACTION_COMMAND_REPLY;
import static info.plux.pluxapi.Constants.ACTION_DATA_AVAILABLE;
import static info.plux.pluxapi.Constants.ACTION_DEVICE_READY;
import static info.plux.pluxapi.Constants.ACTION_EVENT_AVAILABLE;
import static info.plux.pluxapi.Constants.ACTION_STATE_CHANGED;
import static info.plux.pluxapi.Constants.EXTRA_COMMAND_REPLY;
import static info.plux.pluxapi.Constants.EXTRA_DATA;
import static info.plux.pluxapi.Constants.EXTRA_STATE_CHANGED;
import static info.plux.pluxapi.Constants.IDENTIFIER;

/**
 * Created by vikaspandey on 30/5/18.
 */

public class TabFragment1 extends Fragment implements View.OnClickListener, OnBITalinoDataAvailable {
    private final String TAG = this.getClass().getSimpleName();
    private static final Random RANDOM = new Random();
    private int lastX = 0;
    private LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[0]);
    private Context context;
    private ImageButton scanButton;
    private ImageButton recordButton;
    public static TextView resultsTextView;
    private ArrayList<String> recordArray = new ArrayList<>();
    private ArrayList<String> deviceArray = new ArrayList<>();
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private ArrayList<BluetoothDevice> pairedDevice = new ArrayList<>();
    private BITalinoCommunication bitalino;
    private boolean isBITalino2 = false;
    public final static String EXTRA_DEVICE = "com.example.vikaspandey.graphviewtutorial.MainActivity.EXTRA_DEVICE";
    public final static String FRAME = "com.example.vikaspandey.graphviewtutorial.MainActivity.Frame";
    private BluetoothDevice bluetoothDevice;
    private Handler handler;
    SensorDataConverter sdc;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_WEIGHT = 1;
    private static final int REQUEST_ANOTHER_ONE = 2;
    private Runnable mTimer;
    private int record = 0;
    private Constants.States currentState = Constants.States.DISCONNECTED;
    GraphView graphView;
    private View view;
    private boolean isUpdateReceiverRegistered = false;
    DBHelper dbHelper;
    private ItemAdapter adapter;
    private SQLiteDatabase db;
    private RecyclerView itemRecyclerView;
    private Paint p = new Paint();
    private List<DataPoint> values = new ArrayList<>();
    private Entity entity = new Entity();
    private List<Double> yvalues = new ArrayList<>();
    private Gson gson = new Gson();
    final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            bluetoothDevice = data.getParcelableExtra("EXTRA_DEVICE");
            devices.add(bluetoothDevice);
            pairedDevice = data.getParcelableArrayListExtra("PAIRED_DEVICE");
        }
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (requestCode == Activity.RESULT_OK) {
            pairedDevice = intent.getParcelableArrayListExtra("PAIRED_DEVICE");
            bluetoothDevice = intent.getParcelableExtra("EXTRA_DEVICE");
            devices.add(bluetoothDevice);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
        File f = new File("/data/data/" + context.getPackageName());
        String[] fileNames  = f.list();

        for (int i=0;i< fileNames.length;i++) {
            String file = fileNames[i];
            if(file.contains(".json")) {
                recordArray.add( file.substring(0, file.indexOf("json") - 1));

            }

        }


        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.getBondedDevices().isEmpty()) {
            pairedDevice .addAll(mBluetoothAdapter.getBondedDevices());
        }
        for (BluetoothDevice device : pairedDevice) {
            deviceArray.add(device.getName() + " " + device.getAddress());
            devices.add(device);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        dbHelper =new  DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        Bundle bundle = getArguments();
        if (bundle != null) {
            bluetoothDevice = bundle.getParcelable("EXTRA_DEVICE");
            devices.add(bluetoothDevice);
        }

        resultsTextView = (TextView) view.findViewById(R.id.textView2);
        graphView = (GraphView) view.findViewById(R.id.graph_view);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        graphView.getGridLabelRenderer().setGridColor(Color.parseColor("#FF4081"));
        series = new LineGraphSeries<DataPoint>();


        // set manual X bounds
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-1.5);
        graphView.getViewport().setMaxY(2);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX((new Date().getTime()) - 10000);
        graphView.getViewport().setMaxX((new Date().getTime()) + 10000);

        // enable scaling and scrolling
        graphView.getViewport().setScalable(true);
        //graphView.getViewport().setScalableY(true);

        graphView.getViewport().setScrollable(true);

        series.setColor(Color.DKGRAY);

        final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

        graphView.getGridLabelRenderer().setLabelFormatter(
                new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            Date d = new Date((long) value);
                            return sdf.format(d);
                        } else {
                            // show currency for y values
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });


        handler = new Handler(getActivity().getApplicationContext().getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                final BITalinoFrame frame = bundle.getParcelable("FRAME");

                Log.d(TAG, frame.toString());

                if(frame != null){ //BITalino
                    resultsTextView.setText(frame.toString());
                   // resultsTextView.setText(String.valueOf(System.currentTimeMillis())+"..."+String.valueOf(sdc.scaleECG(2,frame.getAnalog(2))));

                    getActivity().runOnUiThread(mTimer = new Runnable() {
                        @Override
                        public void run() {
                            DataPoint dp = new DataPoint(new Date().getTime(), sdc.scaleECG(2,frame.getAnalog(2)));
                            series.appendData(dp,true,10000);

                            values.add(dp);

                            handler.postDelayed(this, 5000);

                        }

                    });

                    entity.setDataPointList(values);
                    entity.setTimestamp(getDateTime());
                }

            }

        };




        graphView.addSeries(series);

         itemRecyclerView = (RecyclerView) view.findViewById(R.id.exercise_view);
        RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.devices_view);

        adapter = new ItemAdapter(getContext(), recordArray);
        DeviceAdapter adapter2 = new DeviceAdapter(getContext(), devices);
        itemRecyclerView.setAdapter(adapter);
        itemRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = itemRecyclerView.indexOfChild(v);
                Log.d("Tag", String.valueOf(itemPosition));
            }
        });

        recyclerView2.setAdapter(adapter2);
        itemRecyclerView.setNestedScrollingEnabled(false);
        recyclerView2.setNestedScrollingEnabled(false);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView2.setLayoutManager(new LinearLayoutManager(context));




        initSwipe();
        return view;
    }

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private boolean swipeBack = false;
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = ((ItemViewHolder)viewHolder).getAdapterPosition();
                String result="";
                if (direction == ItemTouchHelper.LEFT){
                    JSONHelper.deleteFile(recordArray.get(position),getContext());
                    adapter.removeItem(position);

                } else {
                    //View itemView=((ItemViewHolder)viewHolder).itemView;
                    //((ViewGroup) itemView).removeView(itemView);
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    adapter.getItemViewType(position);
                    recordArray.get(position);

                    String  dataString =JSONHelper.mReadJsonData(recordArray.get(position)+".json",getContext());
                    //new HTTPAsyncTask().execute("http://192.168.0.26:8080/save",dataString);
                    new HTTPAsyncTask().execute("http://129.70.148.61:8080/save",dataString);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView=((ItemViewHolder)viewHolder).itemView ;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_save);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(itemRecyclerView);

    }

    private String HttpPost(String myUrl,String requestString) throws IOException, JSONException {
        String result = "";
        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        // 2. build JSON object
        JSONArray jsonObject = buidJsonObject(requestString);

        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response message
        return conn.getResponseMessage()+"";

    }

    private JSONArray buidJsonObject(String requestString) throws JSONException {

        Entity obj= gson.fromJson(requestString,Entity.class);
        JSONArray array = new JSONArray();
        for(DataPoint data: obj.getDataPointList()){
            JSONObject json = new JSONObject();
            json.put("xvalue",data.getX());
            json.put("yvalue",data.getY());
            array.put(json);
        }
        return array;
    }

    private void setPostRequestContent(HttpURLConnection conn, JSONArray json)  {
        try {
            //OutputStream os = conn.getOutputStream();
            // BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(json.toString());
            Log.i(TabFragment1.class.toString(), json.toString());
            writer.flush();
            writer.close();
            // os.close();
        }catch(MalformedURLException error) {
            error.printStackTrace();
        }
        catch(SocketTimeoutException error) {
            error.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return HttpPost(urls[0],urls[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG,result);
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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
                        if(record == 0) {
                            recordButton.setImageResource(R.drawable.ic_record);
                            //recordButton.setBackgroundResource(R.drawable.ic_record);
                            Bundle bundle = getArguments();
                            if (bundle != null) {
                                bluetoothDevice = bundle.getParcelable("EXTRA_DEVICE");
                                devices.add(bluetoothDevice);
                            }
                            Communication communication = Communication.getById(bluetoothDevice.getType());
                            Log.d(TAG, "Communication: " + communication.name());
                            if (communication.equals(Communication.DUAL)) {
                                communication = Communication.BLE;
                            }
                            bitalino = new BITalinoCommunicationFactory().getCommunication(communication, getActivity(), this);

                            boolean isconnected = bitalino.connect(bluetoothDevice.getAddress());
                            Thread.sleep(5000);

                            bitalino.start(new int[]{0, 1, 2, 3, 4, 5}, 1);
                            Thread.sleep(10000);
                            record = 1;
                        }else{
                            recordButton.setImageResource(R.drawable.ic_start);
                            bitalino.stop();
                            record = 0;
                            handler.removeCallbacks(mTimer);
                            series.clearReference(graphView);
                            //for(DataPoint data:values) {
                               // Log.d(TAG, String.valueOf(Double.parseDouble(String.valueOf(data.getX()))));
                                //dbHelper.insert((long)data.getX(),data.getY(),getDateTime());
                            //}
                            String listString = gson.toJson(entity,Entity.class);
                            JSONHelper.mCreateAndSaveFile(df.format(new Date())+".json",listString,getContext() );

                        }
                    } catch (BITalinoException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
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
                dialog.show(fm, "device list");
                dialog.setTargetFragment(TabFragment1.this, 1);
                //Intent in = new Intent(getContext(), ScanActivity.class);
                //startActivity(in);
                break;


            default:
                break;
        }


    }

    @Override
    public void onPause() {
        handler.removeCallbacks(mTimer);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(updateReceiver, makeUpdateIntentFilter());
        isUpdateReceiverRegistered = true;
    }



    @Override
    public void onBITalinoDataAvailable(BITalinoFrame bitalinoFrame) {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelable("FRAME", bitalinoFrame);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    /*
     * Local Broadcast
     */
    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(ACTION_STATE_CHANGED.equals(action)){
                String identifier = intent.getStringExtra(IDENTIFIER);
                Constants.States state = Constants.States.getStates(intent.getIntExtra(EXTRA_STATE_CHANGED, 0));

                //Log.i(TAG, identifier + " -> " + state.name());

                resultsTextView.setText(state.name());

                switch (state){
                    case NO_CONNECTION:
                        break;
                    case LISTEN:
                        break;
                    case CONNECTING:
                        break;
                    case CONNECTED:
                        break;
                    case ACQUISITION_TRYING:
                        break;
                    case ACQUISITION_OK:
                        break;
                    case ACQUISITION_STOPPING:
                        break;
                    case DISCONNECTED:
                        break;
                    case ENDED:
                        break;

                }
            }
            else if(ACTION_DATA_AVAILABLE.equals(action)){
                if(intent.hasExtra(EXTRA_DATA)){
                    Parcelable parcelable = intent.getParcelableExtra(EXTRA_DATA);
                    if(parcelable.getClass().equals(BITalinoFrame.class)){ //BITalino
                        BITalinoFrame frame = (BITalinoFrame) parcelable;
                        resultsTextView.setText(frame.toString());
                    }
                }
            }
            else if(ACTION_COMMAND_REPLY.equals(action)){
                String identifier = intent.getStringExtra(IDENTIFIER);

                if(intent.hasExtra(EXTRA_COMMAND_REPLY) && (intent.getParcelableExtra(EXTRA_COMMAND_REPLY) != null)){
                    Parcelable parcelable = intent.getParcelableExtra(EXTRA_COMMAND_REPLY);
                    if(parcelable.getClass().equals(BITalinoState.class)){ //BITalino
                        Log.d(TAG, ((BITalinoState)parcelable).toString());
                        resultsTextView.setText(parcelable.toString());
                    }
                    else if(parcelable.getClass().equals(BITalinoDescription.class)){ //BITalino
                        isBITalino2 = ((BITalinoDescription)parcelable).isBITalino2();
                        resultsTextView.setText("isBITalino2: " + isBITalino2 + "; FwVersion: " + String.valueOf(((BITalinoDescription)parcelable).getFwVersion()));

//                        if(identifier.equals(identifierBITalino2) && bitalino2 != null){
//                            try {
//                                bitalino2.start(new int[]{0,1,2,3,4,5}, 1);
//                            } catch (BITalinoException e) {
//                                e.printStackTrace();
//                            }
//                        }
                    }
                }
            }
        }
    };


    private IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STATE_CHANGED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        intentFilter.addAction(ACTION_EVENT_AVAILABLE);
        intentFilter.addAction(ACTION_DEVICE_READY);
        intentFilter.addAction(ACTION_COMMAND_REPLY);
        return intentFilter;
    }



}
