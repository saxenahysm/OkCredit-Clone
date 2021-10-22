package com.android.aaroo.activity.account.supp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.aaroo.R;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SupplierCashBookTxnActivity extends AppCompatActivity {

    public static final String TAG = SupplierCashBookTxnActivity.class.getSimpleName();

    RecyclerView rv_supplier;
    SupplierCashbookTxnListAdapter adapter;
    public ArrayList<SupplierTxnModel> txnArrayList;
    AppPrefrence appPrefrence;
    String dateString;
    ShimmerFrameLayout lazzyLoader;
    ProgressDialog progressDialog;
    LinearLayout ll_not_found;
    TextView tv_currentBal,tv_currentStatus,tv_payment,tv_credit,tv_payCount,tv_credCount;

    double netCredit = 0.0, netPayment = 0.0, netBalance = 0.0;
    String currentStatus = "";
    private int credCount = 0,payCount=0;

    CardView card_dashboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_cash_book);

        getSupportActionBar().setTitle("CashBook");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        lazzyLoader = (ShimmerFrameLayout) findViewById(R.id.lazzyloader);
        lazzyLoader.startShimmerAnimation();

        appPrefrence = new AppPrefrence(this);
        rv_supplier = findViewById(R.id.rv_txn);
        lazzyLoader = findViewById(R.id.lazzyloader);
        ll_not_found = findViewById(R.id.ll_not_found);

        card_dashboard = findViewById(R.id.card_dashboard);
        tv_currentBal = findViewById(R.id.tv_currentBal);
        tv_currentStatus = findViewById(R.id.tv_currentStatus);
        tv_payment = findViewById(R.id.tv_payment);
        tv_credit = findViewById(R.id.tv_credit);
        tv_payCount = findViewById(R.id.tv_payCount);
        tv_credCount = findViewById(R.id.tv_credCount);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        lazzyLoader.setVisibility(View.VISIBLE);
        card_dashboard.setVisibility(View.GONE);
        rv_supplier.setVisibility(View.GONE);
        ll_not_found.setVisibility(View.GONE);

        rv_supplier.setHasFixedSize(true);
        rv_supplier.setLayoutManager(linearLayoutManager);

        txnArrayList = new ArrayList<>();

        long date = System.currentTimeMillis();
        //  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        dateString = df.format(date);

        progressDialog.setMessage("please wait...");
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        getAllTransaction();

        adapter = new SupplierCashbookTxnListAdapter(txnArrayList, SupplierCashBookTxnActivity.this);
        rv_supplier.setAdapter(adapter);
    }

    private void getAllTransaction() {

        AndroidNetworking.get(APIs.SUPPLIER_CASHBOOK_API)
                .addQueryParameter("userid", appPrefrence.getUserID())
               // .addQueryParameter("fromdate", dateString)
               // .addQueryParameter("todate", dateString)
                .setTag("Customer Cashbook API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.cancel();
                            Log.d(TAG, "onResponse: "+response.toString());

                            if (response.get("success").equals(true)) {

                                card_dashboard.setVisibility(View.VISIBLE);
                                rv_supplier.setVisibility(View.VISIBLE);
                                ll_not_found.setVisibility(View.GONE);
                                lazzyLoader.setVisibility(View.GONE);
                                lazzyLoader.stopShimmerAnimation();
                                String resMsg = response.getString("msg");
                                JSONArray jsonArrayData = response.getJSONArray("data");


                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                    SupplierTxnModel model = new SupplierTxnModel();

                                    model.txnId = jsonObjectData.getString("id");
                                    model.customerID = jsonObjectData.getString("customerID");
                                    model.paymentMethod = jsonObjectData.getInt("paymentMethod");
                                    int paymentMethod = jsonObjectData.getInt("paymentMethod");
                                    model.amount = jsonObjectData.getDouble("amount");
                                    double amount   = jsonObjectData.getDouble("amount");
                                    model.paymentTime = jsonObjectData.getString("paymentTime");
                                    model.paymentDate = jsonObjectData.getString("paymentDate");
                                    model.addNote = jsonObjectData.getString("addNote");
                                    model.createdBy = jsonObjectData.getString("createdBy");
                                    model.billImage = jsonObjectData.getString("billImage");

                                    model.ipaddress = jsonObjectData.getString("ipaddress");
                                    model.created_at = jsonObjectData.getString("created_at");
                                    model.updated_at = jsonObjectData.getString("updated_at");
                                    model.customName = jsonObjectData.getString("customerName");

                                    txnArrayList.add(model);

                                    if(paymentMethod==1){
                                        netCredit = netCredit+amount;
                                        credCount++;
                                    }
                                    if(paymentMethod==2){
                                        netPayment = netPayment+amount;
                                        payCount++;
                                    }
                                    netBalance = netPayment - netCredit;
                                    if (netBalance>0.0){
                                        currentStatus = "ADVANCE";
                                        tv_currentBal.setTextColor(getResources().getColor(R.color.green_500));

                                    }else if (netBalance<0.0){
                                        currentStatus = "DUE";
                                        netBalance = -1.0 * netBalance;
                                        tv_currentBal.setTextColor(getResources().getColor(R.color.red_500));
                                    }
                                }
                                Log.d("Cashbook", "onBindViewHolder: cr"+netCredit);
                                Log.d("Cashbook", "onBindViewHolder: dr"+netPayment);
                                Log.d("Cashbook", "onBindViewHolder: bal"+netBalance);
                                Log.d("Cashbook", "onBindViewHolder: st"+currentStatus);
                                Log.d("Cashbook", "onBindViewHolder: no of cr"+credCount);
                                Log.d("Cashbook", "onBindViewHolder: no of dr"+payCount);

                                tv_payment.setText("₹ " +String.valueOf(netPayment));
                                tv_credit.setText("₹ " +String.valueOf(netCredit));
                                tv_currentBal.setText("₹ " +String.valueOf(netBalance));
                                tv_currentStatus.setText(String.valueOf(currentStatus));
                                tv_payCount.setText(String.valueOf(payCount)+" PAYMENT");
                                tv_credCount.setText(String.valueOf(credCount)+" CREDIT");

                                adapter.notifyDataSetChanged();

                                //  Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();

                            } else if (response.get("success").equals(false)) {
                                ll_not_found.setVisibility(View.VISIBLE);
                                lazzyLoader.setVisibility(View.GONE);
                                card_dashboard.setVisibility(View.GONE);
                                rv_supplier.setVisibility(View.GONE);
                                lazzyLoader.stopShimmerAnimation();
                                String resMsg = response.getString("msg");
                                //  Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException ex) {
                            ll_not_found.setVisibility(View.VISIBLE);
                            lazzyLoader.setVisibility(View.GONE);
                            card_dashboard.setVisibility(View.GONE);

                            rv_supplier.setVisibility(View.GONE);
                            lazzyLoader.stopShimmerAnimation();
                            progressDialog.cancel();
                            ex.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                        ll_not_found.setVisibility(View.VISIBLE);
                        lazzyLoader.setVisibility(View.GONE);
                        rv_supplier.setVisibility(View.GONE);
                        card_dashboard.setVisibility(View.GONE);

                        lazzyLoader.stopShimmerAnimation();
                        progressDialog.cancel();
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