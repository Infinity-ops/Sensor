package com.example.vikaspandey.graphviewtutorial;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class DevicePinFragment extends DialogFragment{
    private static final String TAG = "DevicePinDialog";

    public interface OnInputSelected{
        void sendInput(String input);
    }
    public OnInputSelected mOnInputSelected;

    //widgets
    private EditText mInput;
    private TextView mActionOk, mActionCancel,mInvalidInput;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_pin_fragment, container, false);
        mActionOk = view.findViewById(R.id.action_ok);
        mActionCancel = view.findViewById(R.id.action_cancel);
        mInput = view.findViewById(R.id.input);
        mInvalidInput = view.findViewById(R.id.invalidPassword);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input.");
                String input = mInput.getText().toString();
                if(!input.equals("1234")){
//
//                    //Easiest way: just set the value.
//                    MainFragment fragment = (MainFragment) getActivity().getFragmentManager().findFragmentByTag("MainFragment");
//                    fragment.mInputDisplay.setText(input);
                    Intent i = new Intent(getActivity().getApplicationContext(), TabFragment1.class);
                    i.putExtra("EXTRA_DEVICE", device);
                    i.putParcelableArrayListExtra("PAIRED_DEVICE",new ArrayList(mBluetoothAdapter.getBondedDevices()));
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                    getDialog().dismiss();

                   // mOnInputSelected.sendInput(input);
                    mInvalidInput.setEnabled(false);

                }
                else{
                    getShowsDialog();
                    mInvalidInput.setEnabled(true);
                }


                //getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputSelected = (OnInputSelected) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage() );
        }
    }
}
