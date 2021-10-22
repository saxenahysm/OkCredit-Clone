package com.android.aaroo.activity.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.aaroo.R;
import com.android.aaroo.activity.account.custom.CustomerCashBookTxnActivity;
import com.android.aaroo.activity.account.supp.SupplierCashBookTxnActivity;
import com.android.aaroo.fragment.supplier.SupplierFragment;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountStatement extends AppCompatActivity {
    public static final String TAG = SupplierFragment.class.getSimpleName();
    TextView custAmountTv, supAmountTv, countCustomTv, countSuppTv;
    AppPrefrence appPrefrence;
    LinearLayout ll_supplier,ll_customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_statement);
        appPrefrence = new AppPrefrence(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        custAmountTv = findViewById(R.id.tvCustAmount);
        supAmountTv = findViewById(R.id.tvsupAmount);
        countCustomTv = findViewById(R.id.countCustom);
        countSuppTv = findViewById(R.id.countSupp);
        ll_customer = findViewById(R.id.ll_customer);
        ll_supplier = findViewById(R.id.ll_supplier);

        if (!appPrefrence.getUsername().equals("null")&&!appPrefrence.getUsername().equals("")) {
            getSupportActionBar().setTitle(appPrefrence.getUsername());
        }else {
            getSupportActionBar().setTitle("Account");
        }

        getCustomerCashBookBalance();
        getSupplierCashBookBalance();

        ll_customer.setOnClickListener(v -> {
        startActivity(new Intent(AccountStatement.this, CustomerCashBookTxnActivity.class));

        });

        ll_supplier.setOnClickListener(v -> {
            startActivity(new Intent(AccountStatement.this, SupplierCashBookTxnActivity.class));

        });

    }

    private void getSupplierCashBookBalance() {

        AndroidNetworking.get(APIs.SUPPLIERCashBookBalance_API)
                .addQueryParameter("userid", appPrefrence.getUserID())
                .setTag("Supplier Cashbook Balance API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //progressDialog.cancel();
                            Log.d(TAG, "onResponse: " + response.toString());
                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                                JSONObject jsonObjData = response.getJSONObject("data");
                                String countSupplier = jsonObjData.getString("countSupplier");
                                double balance = response.getDouble("balance");


                               // supAmountTv.setText("₹ " + balance);
                                countSuppTv.setText(countSupplier + " Supplier");

                                if (balance < 0.0) {
                                    // negative Due
                                    double minusAmount = (-1)*(balance);

                                    supAmountTv.setTextColor(getResources().getColor(R.color.red_700));
                                    supAmountTv.setText("₹ "+String.valueOf(minusAmount));
                                    // holder.status.setText("DUE");


                                }else if (balance == 0.0){
                                    // it's a positive Advance
                                    supAmountTv.setTextColor(getResources().getColor(R.color.green_700));
                                    supAmountTv.setText("₹ "+String.valueOf(balance));
                                    // holder.status.setText("DUE");
                                } else {
                                    // it's a positive Advance
                                    supAmountTv.setTextColor(getResources().getColor(R.color.green_700));
                                    supAmountTv.setText("₹ "+String.valueOf(balance));
                                    //  holder.status.setText("ADVANCE");
                                }

                                //  Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");
                                JSONObject jsonObjData = response.getJSONObject("data");
                                String countSupplier = jsonObjData.getString("countSupplier");
                                String balance = response.getString("Balance");
                                //  Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException ex) {

                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                        // progressDialog.cancel();
                    }
                });

    }

    private void getCustomerCashBookBalance() {

        AndroidNetworking.get(APIs.CUSTOMERCashBookBalance_API)
                .addQueryParameter("userid", appPrefrence.getUserID())
                .setTag("Customer Cashbook Balance API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //progressDialog.cancel();
                            Log.d(TAG, "onResponse: " + response.toString());
                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                                JSONObject jsonObjData = response.getJSONObject("data");
                                String countCusotmer = jsonObjData.getString("countCusotmer");
                                double balance = response.getDouble("balance");

                               // custAmountTv.setText("₹ " + balance);
                                countCustomTv.setText(countCusotmer + " Customers");

                                if (balance < 0.0) {
                                    // negative Due
                                    double minusAmount = (-1)*(balance);

                                    custAmountTv.setTextColor(getResources().getColor(R.color.red_700));
                                    custAmountTv.setText("₹ "+String.valueOf(minusAmount));
                                   // holder.status.setText("DUE");


                                }else if (balance == 0.0){
                                    // it's a positive Advance
                                    custAmountTv.setTextColor(getResources().getColor(R.color.green_700));
                                    custAmountTv.setText("₹ "+String.valueOf(balance));
                                   // holder.status.setText("DUE");
                                } else {
                                    // it's a positive Advance
                                    custAmountTv.setTextColor(getResources().getColor(R.color.green_700));
                                    custAmountTv.setText("₹ "+String.valueOf(balance));
                                  //  holder.status.setText("ADVANCE");
                                }
                                //  Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");
                                JSONObject jsonObjData = response.getJSONObject("data");
                                String countSupplier = jsonObjData.getString("countSupplier");
                                String balance = response.getString("Balance");
                                //  Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException ex) {

                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                        // progressDialog.cancel();
                    }
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}