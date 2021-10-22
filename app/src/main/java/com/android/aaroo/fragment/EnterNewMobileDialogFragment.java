package com.android.aaroo.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.aaroo.MainActivity;
import com.android.aaroo.R;
import com.android.aaroo.activity.setting.SettingActivity;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;


public class EnterNewMobileDialogFragment extends DialogFragment {

    public static final String TAG = EnterNewMobileDialogFragment.class.getSimpleName();

    public EnterNewMobileDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    AppPrefrence appPrefrence;
    EditText edt_mobile;
    LinearLayout ll_submitBtn;
    String mobileNo;
    ProgressDialog progressDialog;
    ImageView back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_enter_mobile_dialog, container, false);

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appPrefrence = new AppPrefrence(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        back = view.findViewById(R.id.back);
        ll_submitBtn = view.findViewById(R.id.ll_verifyMoblie);
        edt_mobile = view.findViewById(R.id.edt_mobile);

        edt_mobile.setOnEditorActionListener(editorListener);

        back.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingActivity.class));
            getActivity().finish();
        });


        edt_mobile.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {

                    ll_submitBtn.setEnabled(true);
                    ll_submitBtn.setClickable(true);
                    ll_submitBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_dark));

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                ll_submitBtn.setClickable(false);
                ll_submitBtn.setEnabled(false);
                ll_submitBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_light));

            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() == 10) {
                    ll_submitBtn.setEnabled(true);
                    ll_submitBtn.setClickable(true);
                    ll_submitBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_dark));

                }
            }
        });
        ll_submitBtn.setClickable(false);
        ll_submitBtn.setEnabled(false);
        ll_submitBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_light));

        ll_submitBtn.setOnClickListener(v -> {
            mobileNo = edt_mobile.getText().toString().trim();

            if (TextUtils.isEmpty(edt_mobile.getText().toString()) || edt_mobile.length() < 10) {

                edt_mobile.setError("Invalid Number!");
                edt_mobile.setFocusable(true);

                Snackbar snackbar = Snackbar.make(view.findViewById(android.R.id.content), "Please enter 10 digit mobile number", Snackbar.LENGTH_LONG);
                snackbar.show();
                return;

            } else {

                progressDialog.setMessage("please wait...");
                progressDialog.show();
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
                ll_submitBtn.setEnabled(false);

                getSwitchNumber(mobileNo);
            }

        });

    }

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                    actionDone();

                    break;
                case EditorInfo.IME_ACTION_SEND:
                    Toast.makeText(getActivity(), "Send", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    };

    private void actionDone() {
        mobileNo = edt_mobile.getText().toString().trim();

        if (TextUtils.isEmpty(edt_mobile.getText().toString()) && edt_mobile.length() < 10) {

            edt_mobile.setError("Invalid Number!");
            edt_mobile.setFocusable(true);

            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter 10 digit mobile number", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;

        }
        progressDialog.setMessage("please wait...");
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        ll_submitBtn.setEnabled(false);
        getSwitchNumber(mobileNo);
    }

    private void getSwitchNumber(final String mobile) {
        AndroidNetworking.post(APIs.CHANGE_MOBILE_API)
                .addBodyParameter("userid", appPrefrence.getUserID())
                .addBodyParameter("mobile", mobile)
                .setTag("New Number API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.cancel();
                            ll_submitBtn.setEnabled(true);
                            Log.d(TAG, "onResponse: " + response.toString());
                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                                Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();
                                dismiss();
                                //   Intent intent =new Intent(getActivity(), SettingActivity.class);
                                //  startActivity(intent);
                                getActivity().finish();

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");
                                Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException ex) {
                            progressDialog.cancel();
                            ll_submitBtn.setEnabled(true);
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.d(TAG, "onError: " + error);
                        progressDialog.cancel();
                        ll_submitBtn.setEnabled(true);
                    }
                });
    }

}