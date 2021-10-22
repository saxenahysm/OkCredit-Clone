package com.android.aaroo.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.aaroo.MainActivity;
import com.android.aaroo.R;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterFragment extends DialogFragment {
    public static final String TAG = RegisterFragment.class.getSimpleName();

    AppPrefrence appPrefrence;
    ProgressDialog progressDialog;
    Button btn_save;
    TextView tvSkip;
    EditText edt_businessName;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        appPrefrence = new AppPrefrence(getActivity());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        tvSkip = view.findViewById(R.id.tvSkip);
        btn_save = view.findViewById(R.id.card_view_submit);
        edt_businessName = view.findViewById(R.id.edt_businessName);
        btn_save.setEnabled(true);

        edt_businessName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    btn_save.setEnabled(true);
                    btn_save.setClickable(true);
                    btn_save.setBackground(getResources().getDrawable(R.drawable.verify_button_dark));
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                btn_save.setClickable(false);
                btn_save.setEnabled(false);
                btn_save.setBackground(getResources().getDrawable(R.drawable.verify_button_light));

            }
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() != 0) {
                    btn_save.setEnabled(true);
                    btn_save.setClickable(true);
                    btn_save.setBackground(getResources().getDrawable(R.drawable.verify_button_dark));
                }
            }
        });

        tvSkip.setOnClickListener(v -> {

            dismiss();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();

        });

        btn_save.setOnClickListener(v -> {

           // Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
            getRegisterUser();
        });

    }

    private void getRegisterUser() {

        JSONObject request_data = new JSONObject();
        try {

            request_data.put("userid", appPrefrence.getUserID());
            request_data.put("bussinessName",edt_businessName.getText().toString());
            Log.d(TAG, "getRegisterUser: "+request_data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(APIs.USER_DATA_SAVE_API)
                .addJSONObjectBody(request_data)
                .setTag("User Data Save API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.cancel();
                            btn_save.setEnabled(true);
                            Log.d(TAG, "onResponse: "+response.toString());

                            if(response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                                appPrefrence.setBizName(edt_businessName.getText().toString());
                                Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException ex) {
                            progressDialog.cancel();
                            btn_save.setEnabled(true);
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.cancel();
                        btn_save.setEnabled(true);
                    }
                });
    }
}