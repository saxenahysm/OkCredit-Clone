package com.android.aaroo.fragment.supplier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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

import com.android.aaroo.activity.supplier.AddSupplier;
import com.android.aaroo.activity.transactionsupplier.SupplierTransactionRoom;
import com.android.aaroo.fragment.customer.CustomerFragment;
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

public class SupplierFragment extends Fragment {
    public static final String TAG = SupplierFragment.class.getSimpleName();

    public SupplierFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    public static final int REQUEST_CODE = 11;
    public static final int RESULT_CODE = 12;
    RecyclerView rv_supplier;
    ArrayList<SupplierModel> supplierArrayList;
    LinearLayoutManager layoutManager;
    SupplierAdaper adapter;
    ShimmerFrameLayout lazzyLoader;
    Button btn_add_supplier;
    AppPrefrence appPrefrence;
    LinearLayout ll_notFound;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_supplier, container, false);
        lazzyLoader = (ShimmerFrameLayout) root.findViewById(R.id.shimmer_view_container);
        lazzyLoader.startShimmerAnimation();

        progressDialog = new ProgressDialog(getActivity());
        appPrefrence = new AppPrefrence(getActivity());

        supplierArrayList = new ArrayList<>();

        ll_notFound = root.findViewById(R.id.ll_not_found);

        btn_add_supplier = root.findViewById(R.id.btn_add_supplier);
        rv_supplier = root.findViewById(R.id.rv_supplier);
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_supplier.setLayoutManager(layoutManager);

        rv_supplier.setVisibility(View.GONE);

        lazzyLoader.setVisibility(View.VISIBLE);
        rv_supplier.setVisibility(View.GONE);

        ll_notFound.setVisibility(View.GONE);

        progressDialog.setMessage("please wait...");
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        getSupplierList();
        if (getActivity() != null) {

            adapter = new SupplierAdaper(supplierArrayList, getActivity(),SupplierFragment.this);
            rv_supplier.setAdapter(adapter);

        }


        btn_add_supplier.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddSupplier.class);
            //startActivity(intent);
            getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE);

        });

        return root;
    }

    private void getSupplierList() {
        supplierArrayList.clear();
        AndroidNetworking.get(APIs.SUPPLIER_ALL_DATA_API)
                .addQueryParameter("userid", appPrefrence.getUserID())
                .setTag("Supplier All Data API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d(TAG, "onResponse: " + response.toString());

                            lazzyLoader.setVisibility(View.GONE);
                            lazzyLoader.stopShimmerAnimation();
                            progressDialog.cancel();

                            if (response.get("success").equals(true)) {

                                rv_supplier.setVisibility(View.VISIBLE);
                                ll_notFound.setVisibility(View.GONE);

                                String resMsg = response.getString("msg");
                                JSONArray jsonArrayData = response.getJSONArray("data");

                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    SupplierModel model = new SupplierModel();

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                    model.sid = jsonObjectData.getString("id");
                                    model.mobile = jsonObjectData.getString("mobile");
                                    model.name = jsonObjectData.getString("customerName");
                                    model.customerType = jsonObjectData.getInt("customerType");
                                    model.profile = jsonObjectData.getString("image");
                                    model.amount = jsonObjectData.getDouble("currentBal");

                                    supplierArrayList.add(model);

                                }
                                adapter.notifyDataSetChanged();

                            } else if (response.get("success").equals(false)) {
                                lazzyLoader.setVisibility(View.VISIBLE);
                                rv_supplier.setVisibility(View.GONE);
                                lazzyLoader.stopShimmerAnimation();
                                ll_notFound.setVisibility(View.VISIBLE);
                                progressDialog.cancel();
                                String resMsg = response.getString("msg");


                            }

                        } catch (JSONException ex) {
                            lazzyLoader.setVisibility(View.VISIBLE);
                            rv_supplier.setVisibility(View.GONE);
                            lazzyLoader.stopShimmerAnimation();
                            ll_notFound.setVisibility(View.VISIBLE);
                            progressDialog.cancel();
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        lazzyLoader.stopShimmerAnimation();
                        ll_notFound.setVisibility(View.VISIBLE);
                        progressDialog.cancel();
                        lazzyLoader.setVisibility(View.VISIBLE);
                        rv_supplier.setVisibility(View.GONE);
                    }
                });
    }

    public void itemClick(String sid, String name, String profile, int customerType, String mobile, double amount) {

        Intent i = new Intent(getActivity(), SupplierTransactionRoom.class);
        i.putExtra("id",sid);
        i.putExtra("name",name);
        i.putExtra("profile",profile);
        i.putExtra("mobile",mobile);
        i.putExtra("customerType",customerType);
        i.putExtra("amount",amount);
        startActivity(i);
        getActivity().finish();
    }
}