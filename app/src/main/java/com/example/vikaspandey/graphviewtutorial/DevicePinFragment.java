package com.example.vikaspandey.graphviewtutorial;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DevicePinFragment extends DialogFragment {
    private static final String TAG = "DevicePinDialog";
    private BluetoothDevice device;

    public interface OnInputSelected {
        void sendInput(String input);
    }

    public OnInputSelected mOnInputSelected;

    //widgets
    private EditText mInput;
    private TextView mActionOk, mActionCancel,mInvalidPassword;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_pin_fragment, container, false);
        mActionOk = view.findViewById(R.id.action_ok);
        mInvalidPassword = view.findViewById(R.id.invalidPassword);
        mActionCancel = view.findViewById(R.id.action_cancel);
        mInput = view.findViewById(R.id.input);
        mInvalidPassword.setEnabled(false);
        mInvalidPassword.setClickable(false);
        mInvalidPassword.setVisibility(view.GONE);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("EXTRA_DEVICE")) {
            device = bundle.getParcelable("EXTRA_DEVICE");
        } else if (bundle == null) {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        }


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

                if (!input.equals(R.string.pin)) {
                    mInvalidPassword.setVisibility(view.VISIBLE);
                    mInput.setText("");
                    Intent i = new Intent(getActivity().getApplicationContext(), TabFragment1.class);
                    i.putExtra("EXTRA_DEVICE", device);
                    i.putExtra("PAIRED_DEVICE", device);

                    return;
                    //getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                   // startActivity(i);
                    //mOnInputSelected.sendInput(input)
                }
                getDialog().dismiss();
            }
        });

        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputSelected = (OnInputSelected) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage());
        }
    }
}
