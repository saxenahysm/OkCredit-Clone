package com.android.aaroo.activity.otp_verify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.aaroo.MainActivity;
import com.android.aaroo.R;
import com.android.aaroo.activity.login.LoginActivity;
import com.android.aaroo.fragment.RegisterFragment;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.android.aaroo.helper.CustomTextWatcher;
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

public class OtpVerifyActivity extends AppCompatActivity {

    public static final String TAG = OtpVerifyActivity.class.getSimpleName();
    private static final int SMS_CONSENT_REQUEST = 2;
    Button verifyOptBtn;
    TextView mobile_tv, resend_tv, changeMob;
    EditText edt_otp1, edt_otp2, edt_otp3, edt_otp4;
    EditText[] editList;
    String verified_otp;
    public static String mobileStr;
    AppPrefrence appPrefrence;
    ProgressDialog progressDialog;

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
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        progressDialog = new ProgressDialog(this);
        appPrefrence = new AppPrefrence(this);

        mobile_tv = findViewById(R.id.tv_mobile);
        changeMob = findViewById(R.id.tv_change_no);
        resend_tv = findViewById(R.id.tv_resend);
        verifyOptBtn = findViewById(R.id.btn_verify);

        edt_otp1 = findViewById(R.id.otp1);
        edt_otp2 = findViewById(R.id.otp2);
        edt_otp3 = findViewById(R.id.otp3);
        edt_otp4 = findViewById(R.id.otp4);

        if (getIntent() != null) {

            mobileStr = getIntent().getStringExtra("mobile");
            //   verified_otp = getIntent().getStringExtra("otp");
            mobile_tv.setText("Enter verification code sent to\t" + mobileStr);
        }

        Task<Void> task = SmsRetriever.getClient(this).startSmsUserConsent(null);
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsVerificationReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null);

        changeMob.setTextColor(getResources().getColor(R.color.blue_300));
        changeMob.setEnabled(true);

        changeMob.setOnClickListener(v -> {

            changeMob.setTextColor(getResources().getColor(R.color.blue_500));
            changeMob.setEnabled(false);

            startActivity(new Intent(OtpVerifyActivity.this, LoginActivity.class));
            finish();
        });

        resend_tv.setOnClickListener(v -> {

            if (mobileStr != null && !mobileStr.equals("")) {

                resend_tv.setTextColor(getResources().getColor(R.color.blue_500));
                progressDialog.setMessage("please wait...");
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
                resend_tv.setEnabled(false);

                resendOtpFromLogin(mobileStr);
            }
        });

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
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

            progressDialog.show();
            progressDialog.setMessage("Please be patient");

            verified_otp = edt_otp1.getText().toString().trim() +
                    edt_otp2.getText().toString().trim() +
                    edt_otp3.getText().toString().trim() +
                    edt_otp4.getText().toString().trim();

            if (!verified_otp.equals("") && verified_otp.length() == 4) {

                verifyOptBtn.setEnabled(false);
                progressDialog.show();
                progressDialog.setMessage("please wait...");
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);


                loginUser(mobileStr, verified_otp);

            }


        });

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

    private TextView.OnEditorActionListener editorListener = (v, actionId, event) -> {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                Toast.makeText(OtpVerifyActivity.this, "Done", Toast.LENGTH_SHORT).show();
                actionDone();
                break;
            case EditorInfo.IME_ACTION_SEND:
                Toast.makeText(OtpVerifyActivity.this, "Send", Toast.LENGTH_SHORT).show();
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
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
                loginUser(mobileStr, verified_otp);
            }
        }
    }

    private void resendOtpFromLogin(String mobile) {
        progressDialog.show();

        AndroidNetworking.post(APIs.LOGIN_API)
                .addBodyParameter("mobile", mobile)
                .setTag("LOGIN API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.cancel();
                            verifyOptBtn.setEnabled(true);
                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                                Toast.makeText(OtpVerifyActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(OtpVerifyActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException ex) {
                            verifyOptBtn.setEnabled(true);

                            progressDialog.cancel();
                            resend_tv.setTextColor(getResources().getColor(R.color.blue_500));
                            resend_tv.setEnabled(true);
                            ex.printStackTrace();

                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        verifyOptBtn.setEnabled(true);
                        progressDialog.cancel();
                        resend_tv.setTextColor(getResources().getColor(R.color.blue_500));
                        resend_tv.setEnabled(true);

                    }
                });
    }

    private void loginUser(String LmobileStr, String LotpStr) {
        progressDialog.show();

        AndroidNetworking.post(APIs.CHECK_OTP_API)
                .addBodyParameter("mobile", LmobileStr)
                .addBodyParameter("otp", LotpStr)
                .setTag("Check OTP API")
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
                                Toast.makeText(OtpVerifyActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                                JSONArray jsonArrayData = response.getJSONArray("data");

                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);

                                    int userid = jsonObjectData.getInt("userid");
                                    String mobile = jsonObjectData.getString("mobile");

                                    //    Toast.makeText(OtpVerifyActivity.this, "uid" + userid, Toast.LENGTH_SHORT).show();
                                    appPrefrence.setMobile(mobile);
                                    appPrefrence.setUserID(String.valueOf(userid));
                                    appPrefrence.setIsLogin("1");
                                }

                                if (appPrefrence.getBizName().equals("") && appPrefrence.getBizName().equals("null")) {
                                    RegisterFragment registerFragment = new RegisterFragment();
                                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                                    if (prev != null) {
                                        ft.remove(prev);
                                    }
                                    ft.addToBackStack(null);
                                    registerFragment.show(ft, "dialog");

                                }else {
                                    startActivity(new Intent(OtpVerifyActivity.this,MainActivity.class));
                                    finish();
                                }

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(OtpVerifyActivity.this, resMsg, Toast.LENGTH_SHORT).show();
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
    ////////////////////////////OTp XML Backend Code///////////////////////////

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
            //Toast.makeText(OtpActivity.this,"True",Toast.LENGTH_LONG ).show();
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
            if (getCurrentFocus() != null) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SMS_CONSENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                    if (message != null) {
                        String otp = parseCode(message);
                        edt_otp1.setText(otp.substring(0, 1));
                        edt_otp2.setText(otp.substring(1, 2));
                        edt_otp3.setText(otp.substring(2, 3));
                        edt_otp4.setText(otp.substring(3, 4));

                        progressDialog.setMessage("please wait...");
                        progressDialog.setCancelable(true);
                        progressDialog.setCanceledOnTouchOutside(false);
                        loginUser(mobileStr, verified_otp);

                    }
                } else {
                }
                break;
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}