package com.android.aaroo.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.aaroo.R;
import com.android.aaroo.activity.setting.SettingActivity;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.android.aaroo.helper.DoneOnEditorActionListener;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;


public class OtpVerifyDialogFragment extends DialogFragment {

    private static final int SMS_CONSENT_REQUEST = 2;

    public static final String TAG = OtpVerifyDialogFragment.class.getSimpleName();

    public OtpVerifyDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    Button verifyOptBtn;
    TextView mobile_tv, resend_tv, changeMob;
    EditText edt_otp1, edt_otp2, edt_otp3, edt_otp4;
    EditText[] editList;
    String verified_otp;
    public static String mobileStr;
    AppPrefrence appPrefrence;
    ProgressDialog progressDialog;
    ImageView back;
    Activity activity;

/*
    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        try {
                            getActivity().startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        break;
                }
            }
        }
    };
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_otp_verify_dialog, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        appPrefrence = new AppPrefrence(getActivity());

        back = view.findViewById(R.id.back);
        mobile_tv = view.findViewById(R.id.tv_mobile);
        changeMob = view.findViewById(R.id.tv_change_no);
        resend_tv = view.findViewById(R.id.tv_resend);
        verifyOptBtn = view.findViewById(R.id.btn_otp_verify);

        edt_otp1 = view.findViewById(R.id.otp1);
        edt_otp2 = view.findViewById(R.id.otp2);
        edt_otp3 = view.findViewById(R.id.otp3);
        edt_otp4 = view.findViewById(R.id.otp4);

       /* Task<Void> task = SmsRetriever.getClient(getActivity()).startSmsUserConsent(null);
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        getActivity().registerReceiver(smsVerificationReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null);
*/

        edt_otp1.setOnEditorActionListener(new DoneOnEditorActionListener());
        edt_otp2.setOnEditorActionListener(new DoneOnEditorActionListener());
        edt_otp3.setOnEditorActionListener(new DoneOnEditorActionListener());

        edt_otp1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                validTextOTP();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                validTextOTP();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validTextOTP();
            }
        });
        edt_otp2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                validTextOTP();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                validTextOTP();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validTextOTP();
            }
        });
        edt_otp3.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                validTextOTP();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                validTextOTP();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validTextOTP();

            }
        });
        edt_otp4.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    edt_otp4.setOnEditorActionListener(editorListener);
                    //  InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    //  inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    validTextOTP();
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                validTextOTP();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                validTextOTP();

            }
        });

        editList = new EditText[]{edt_otp1, edt_otp2, edt_otp3, edt_otp4};


        if (edt_otp1.getText().toString().equals("") && edt_otp2.getText().toString().equals("") && edt_otp3.getText().toString().equals("") && edt_otp4.getText().toString().equals("")) {

            verifyOptBtn.setEnabled(false);
            verifyOptBtn.setClickable(false);
            verifyOptBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_light));

        }

        edt_otp1.addTextChangedListener(new PinTextWatcher(0));
        edt_otp2.addTextChangedListener(new PinTextWatcher(1));
        edt_otp3.addTextChangedListener(new PinTextWatcher(2));
        edt_otp4.addTextChangedListener(new PinTextWatcher(3));


        edt_otp1.setOnKeyListener(new PinOnKeyListener(0));
        edt_otp2.setOnKeyListener(new PinOnKeyListener(1));
        edt_otp3.setOnKeyListener(new PinOnKeyListener(2));
        edt_otp4.setOnKeyListener(new PinOnKeyListener(3));

        verifyOptBtn.setOnClickListener(v -> {

            verified_otp = edt_otp1.getText().toString().trim() +
                    edt_otp2.getText().toString().trim() +
                    edt_otp3.getText().toString().trim() +
                    edt_otp4.getText().toString().trim();

            if (!verified_otp.equals("") && verified_otp.length() == 4) {

                verifyOptBtn.setEnabled(false);
                progressDialog.setMessage("please wait...");
                progressDialog.show();
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
                verifyOtp(mobileStr, verified_otp);
            }

        });
    }

    private TextView.OnEditorActionListener editorListener = (v, actionId, event) -> {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                actionDone();
                break;
            case EditorInfo.IME_ACTION_SEND:
                Toast.makeText(getActivity(), "Send", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    };

    private void actionDone() {

        verified_otp = edt_otp1.getText().toString().trim() +
                edt_otp2.getText().toString().trim() +
                edt_otp3.getText().toString().trim() +
                edt_otp4.getText().toString().trim();

        if (!verified_otp.equals("")) {
            verifyOptBtn.setEnabled(false);
            progressDialog.setCanceledOnTouchOutside(false);

            if (mobileStr != null) {
                verifyOptBtn.setEnabled(false);
                progressDialog.setMessage("please wait...");
                progressDialog.show();
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
                verifyOtp(mobileStr, verified_otp);
            }
        }
    }

    public class PinTextWatcher implements TextWatcher {

        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = " ";

        PinTextWatcher(int currentIndex) {
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editList.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = newTypedString;

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0));
            // TODO: We can fill out other EditTexts

            editList[currentIndex].removeTextChangedListener(this);
            editList[currentIndex].setText(text);
            editList[currentIndex].setSelection(text.length());
            editList[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
        }

        private void moveToNext() {
            if (!isLast)
                editList[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast) { // isLast is optional
                editList[currentIndex].clearFocus();
                hideKeyboard();
            }
        }

        private void moveToPrevious() {
            if (!isFirst)
                editList[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editList)
                if (editText.getText().toString().trim().length() == 0)
                    return false;

            return true;
        }

        private void hideKeyboard() {
            if (getActivity().getCurrentFocus() != null) {

                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

            }
        }

    }

    public class PinOnKeyListener implements View.OnKeyListener {

        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (editList[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editList[currentIndex - 1].requestFocus();
            }
            return false;
        }
    }

    public boolean validTextOTP() {

        verified_otp = edt_otp1.getText().toString().trim() +
                edt_otp2.getText().toString().trim() +
                edt_otp3.getText().toString().trim() +
                edt_otp4.getText().toString().trim();
        if (verified_otp.length() == 4) {
            verifyOptBtn.setEnabled(true);
            verifyOptBtn.setClickable(true);
            verifyOptBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_dark));
            return true;
        } else {
            verifyOptBtn.setEnabled(false);
            verifyOptBtn.setClickable(false);
            verifyOptBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_light));
            return false;
        }
    }

    private void verifyOtp(String LmobileStr, String LotpStr) {
      /*  JSONObject request_data = new JSONObject();
        try {
            request_data.put("userid", appPrefrence.getUserID());
            request_data.put("otp", LotpStr);
        } catch (JSONException e) {
            e.printStackTrace();
        } */
        AndroidNetworking.post(APIs.CHECK_MOBILE_OTP_API)
                .addBodyParameter("userid", appPrefrence.getUserID())
                .addBodyParameter("otp", LotpStr)
                //.addJSONObjectBody(request_data)
                .setTag("Verify OTP API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            progressDialog.cancel();
                            Log.d(TAG, "onResponse: " + response.toString());
                            verifyOptBtn.setEnabled(true);

                            if (response.get("success").equals(true)) {
                                String resMsg = response.getString("msg");
                                Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();
                                dismiss();
                                appPrefrence.setMobile(LmobileStr);
                                EnterNewMobileDialogFragment enterNewMobileDialogFragment = new EnterNewMobileDialogFragment();
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
                                if (prev != null) {
                                    ft.remove(prev);
                                }
                                ft.addToBackStack(null);
                                enterNewMobileDialogFragment.show(ft, "dialog");


                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException ex) {
                            verifyOptBtn.setEnabled(true);
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.cancel();
                        verifyOptBtn.setEnabled(true);
                    }
                });
    }

 /*   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: Execute"+requestCode);
            Toast.makeText(getActivity(), "Execute", Toast.LENGTH_SHORT).show();
            switch (requestCode) {

                case SMS_CONSENT_REQUEST:
                    Toast.makeText(getActivity(), "Res 1", Toast.LENGTH_SHORT).show();
                    if (resultCode == RESULT_OK) {
                        Toast.makeText(getActivity(), "Res 2", Toast.LENGTH_SHORT).show();
                        String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                        if (message != null) {
                            Toast.makeText(getActivity(), "Res 3", Toast.LENGTH_SHORT).show();
                            String otp = parseCode(message);
                            edt_otp1.setText(otp.substring(0, 1));
                            edt_otp2.setText(otp.substring(1, 2));
                            edt_otp3.setText(otp.substring(2, 3));
                            edt_otp4.setText(otp.substring(3, 4));

                            progressDialog.setMessage("please wait...");
                            progressDialog.setCancelable(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            verifyOtp(mobileStr, verified_otp);
                        }
                    }
                    break;
            }
        }

    }

    public String parseCode(String message) {
        String code = "";
        try {
            Pattern p = Pattern.compile("\\b\\d{4}\\b");
            Matcher m = p.matcher(message.toString());
            while (m.find()) {
                code = m.group(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }
*/

}