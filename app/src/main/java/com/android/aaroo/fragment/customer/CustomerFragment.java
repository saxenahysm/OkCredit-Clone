package com.android.aaroo.fragment.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.aaroo.MainActivity;
import com.android.aaroo.R;
import com.android.aaroo.activity.customer.AddCustomer;
import com.android.aaroo.activity.otp_verify.OtpVerifyActivity;
import com.android.aaroo.activity.supplier.AddSupplier;
import com.android.aaroo.activity.transactioncustomer.CustomerTransactionRoom;
import com.android.aaroo.fragment.RegisterFragment;
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

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class CustomerFragment extends Fragment {
    public static final String TAG = CustomerFragment.class.getSimpleName();
    public static final int REQUEST_CODE = 11;
    public static final int RESULT_CODE = 12;
    public CustomerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    RecyclerView rv_custom;
    ArrayList<CustomerModel> customerArrayList;
    LinearLayoutManager layoutManager;
    CustomerAdapter adapter;
    ShimmerFrameLayout lazzyLoader;
    Button btn_add_customer;
    AppPrefrence appPrefrence;
    LinearLayout ll_notFound;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_customer, container, false);

        progressDialog = new ProgressDialog(getActivity());

        lazzyLoader = (ShimmerFrameLayout) root.findViewById(R.id.shimmer_view_container);
        lazzyLoader.startShimmerAnimation();

        appPrefrence = new AppPrefrence(getActivity());

        customerArrayList = new ArrayList<>();
        ll_notFound = root.findViewById(R.id.ll_not_found);
        btn_add_customer = root.findViewById(R.id.btn_add_customer);
        rv_custom = root.findViewById(R.id.rv_customer);
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_custom.setLayoutManager(layoutManager);


        lazzyLoader.setVisibility(View.VISIBLE);
        rv_custom.setVisibility(View.GONE);

        ll_notFound.setVisibility(View.GONE);

        progressDialog.setMessage("please wait...");
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        getCustomerList();


        if (getActivity() != null) {

            adapter = new CustomerAdapter(customerArrayList, getActivity(),CustomerFragment.this);
            rv_custom.setAdapter(adapter);

        }

        btn_add_customer.setOnClickListener(v -> {
           // startActivity(new Intent(getActivity(), AddCustomer.class));

            Intent intent=new Intent(getActivity(),AddCustomer.class);
           // startActivity(intent);
            getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE);
        });

        return root;
    }

    private void getCustomerList() {
        customerArrayList.clear();
        AndroidNetworking.get(APIs.CUSTOMER_ALL_DATA_API)
                .addQueryParameter("userid", appPrefrence.getUserID())
                .setTag("Customer All Data API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            lazzyLoader.setVisibility(View.GONE);
                            lazzyLoader.stopShimmerAnimation();
                            progressDialog.cancel();

                            Log.d(TAG, "onResponse: "+response.toString());
                            if (response.get("success").equals(true)) {
                                rv_custom.setVisibility(View.VISIBLE);
                                ll_notFound.setVisibility(View.GONE);

                                String resMsg = response.getString("msg");
                                JSONArray jsonArrayData = response.getJSONArray("data");

                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                   CustomerModel model =new CustomerModel();

                                    model.cid=jsonObjectData.getString("id");
                                    model.mobile = jsonObjectData.getString("mobile");
                                    model.name = jsonObjectData.getString("customerName");
                                    model.customerType = jsonObjectData.getInt("customerType");
                                    model.profile = jsonObjectData.getString("image");
                                    model.amount = jsonObjectData.getDouble("currentBal");

                              customerArrayList.add(model);

                                }

                                adapter.notifyDataSetChanged();

                            } else if (response.get("success").equals(false)) {
                                lazzyLoader.setVisibility(View.VISIBLE);
                                rv_custom.setVisibility(View.GONE);
                                lazzyLoader.stopShimmerAnimation();
                                ll_notFound.setVisibility(View.VISIBLE);
                                progressDialog.cancel();
                                String resMsg = response.getString("msg");

                            //    Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show()
                            }
                        } catch (JSONException ex) {
                            lazzyLoader.setVisibility(View.VISIBLE);
                            rv_custom.setVisibility(View.GONE);
                            lazzyLoader.stopShimmerAnimation();
                            ll_notFound.setVisibility(View.VISIBLE);
                            progressDialog.cancel();
                            ex.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        lazzyLoader.setVisibility(View.VISIBLE);
                        rv_custom.setVisibility(View.GONE);
                        lazzyLoader.stopShimmerAnimation();
                        ll_notFound.setVisibility(View.VISIBLE);
                        progressDialog.cancel();
                        error.printStackTrace();
                    }
                });

    }


    public void itemClicked(String cid, String name, String profile, int customerType, String mobile, double amount) {

        Intent i = new Intent(getActivity(), CustomerTransactionRoom.class);
        i.putExtra("id",cid);
        i.putExtra("name",name);
        i.putExtra("profile",profile);
        i.putExtra("mobile",mobile);
        i.putExtra("customerType",customerType);
        i.putExtra("amount",amount);
        startActivity(i);
    getActivity().finish();
    }
}