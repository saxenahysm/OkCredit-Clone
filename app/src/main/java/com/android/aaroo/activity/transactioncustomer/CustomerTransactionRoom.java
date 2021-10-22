package com.android.aaroo.activity.transactioncustomer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.aaroo.MainActivity;
import com.android.aaroo.R;
import com.android.aaroo.activity.CommonProfileActivity;
import com.android.aaroo.activity.EditTransactionActivity;
import com.android.aaroo.activity.helpsupport.HelpAndSupportActivity;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerTransactionRoom extends AppCompatActivity implements PickiTCallbacks {
    public static final String TAG = CustomerTransactionRoom.class.getSimpleName();

    CustomerTransactionListAdapter adapter;
    public ArrayList<CustomerTransactionModel> txnArrayList;
    RecyclerView rv_txn;
    ProgressDialog progressDialog;
    ImageView back, billImage, call, info;
    LinearLayout ll_profile, ll_transaction2, ll_transaction1;
    private int mYear, mMonth, mDay;
    String nameStr, idStr, profileStr, mobileStr, billPath, txnDate;
    double amountDbl;
    int customerTypeInt;
    TextView tvname, tvBalance;

    private static final int PERMISSION_REQUEST_CODE = 111;
    private static final int BOTTOM_SHEET_REQUEST_CODE = 222;
    private static final int CAMERA_PICTURE = 1;
    private static final int GALLERY_PICTURE = 2;
    File fileImageCam1, filePath;
    PickiT pickiT;

    BottomSheetDialog bottomSheetDialogPayment, bottomSheetDialogCredit, bottomSheetDialogBills, bsDialogShareBusiness;
    AppPrefrence appPrefrence;
    ShimmerFrameLayout lazzyLoader;
    LinearLayout ll_notFound, ll_shareRemind;
    double netCredit = 0.0, netPayment = 0.0, netBalance = 0.0;
    CardView card_remind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_transaction_room);

        progressDialog = new ProgressDialog(this);
        lazzyLoader = (ShimmerFrameLayout) findViewById(R.id.lazzyloader);
        lazzyLoader.startShimmerAnimation();

        appPrefrence = new AppPrefrence(this);
        pickiT = new PickiT(this, this, this);

        card_remind = findViewById(R.id.card_remind);
        ll_shareRemind = findViewById(R.id.ll_shareRemind);
        ll_notFound = findViewById(R.id.ll_not_found);
        rv_txn = findViewById(R.id.rv_msg);
        back = findViewById(R.id.back);
        tvBalance = findViewById(R.id.tvBalance);
        ll_profile = findViewById(R.id.ll_profile);
        ll_transaction2 = findViewById(R.id.ll_transaction2);
        ll_transaction1 = findViewById(R.id.ll_transaction1);
        tvname = findViewById(R.id.tvname);
        call = findViewById(R.id.call);
        info = findViewById(R.id.info);

        if (getIntent().hasExtra("id")) {

            idStr = getIntent().getStringExtra("id");
            nameStr = getIntent().getStringExtra("name");
            profileStr = getIntent().getStringExtra("profile");
            mobileStr = getIntent().getStringExtra("mobile");
            customerTypeInt = getIntent().getIntExtra("customerType", 0);
            amountDbl = getIntent().getDoubleExtra("amount", 0);

        }
        call.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+" + mobileStr));
            startActivity(intent);
        });
        info.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpAndSupportActivity.class));

        });
        tvname.setText(nameStr);

        ll_profile.setOnClickListener(v -> {

            Intent intent = new Intent(this, CommonProfileActivity.class);
            intent.putExtra("id", idStr);
            intent.putExtra("custType", customerTypeInt);
            intent.putExtra("amount", amountDbl);
            startActivity(intent);
            // finish();

        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rv_txn.setLayoutManager(linearLayoutManager);

        txnArrayList = new ArrayList<>();

        lazzyLoader.setVisibility(View.VISIBLE);
        rv_txn.setVisibility(View.GONE);
        ll_notFound.setVisibility(View.GONE);
        card_remind.setVisibility(View.GONE);

        progressDialog.setMessage("please wait...");
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        getAllTransaction();

        adapter = new CustomerTransactionListAdapter(this, txnArrayList, CustomerTransactionRoom.this);
        rv_txn.setAdapter(adapter);

        back.setOnClickListener(v -> onBackPressed());

        ll_transaction1.setOnClickListener(v -> {
            int paymentMethod = 1;
            getBottomSheetTransactionAdd(paymentMethod);
        });

        ll_transaction2.setOnClickListener(v -> {
            int paymentMethod = 2;
            getBottomSheetTransactionAdd(paymentMethod);
        });

        ll_shareRemind.setOnClickListener(v -> openShareReminder());
    }

    private void getBottomSheetTransactionAdd(int paymentType) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.payment_layout);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();


        EditText edtAmount = dialog.findViewById(R.id.edt_amount);
        EditText edtNotes = dialog.findViewById(R.id.edt_notes);
        billImage = dialog.findViewById(R.id.Imgbill);
        CardView btnSubmit = dialog.findViewById(R.id.card_view_submit);
        TextView text_view_currency = dialog.findViewById(R.id.text_view_currency);
        TextView tvCamera = dialog.findViewById(R.id.tvcamera);
        TextView tvCalander = dialog.findViewById(R.id.tv_calander);
        LinearLayout ll_calender = dialog.findViewById(R.id.ll_calender);
        LinearLayout ll_notes = dialog.findViewById(R.id.ll_notes);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);
        tvCalander.setText(dateString);
        Log.d(TAG, "onCreate: " + dateString);
        if (paymentType == 1) {
            text_view_currency.setTextColor(Color.RED);
        }
        billImage.setVisibility(View.GONE);

        tvCamera.setOnClickListener(v -> {
            getBillBottomSheet();
        });

        tvCalander.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CustomerTransactionRoom.this,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        tvCalander.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        txnDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        btnSubmit.setOnClickListener(v -> {

            btnSubmit.setEnabled(false);
            String amountStr = edtAmount.getText().toString();
            String notesStr = edtNotes.getText().toString();

            getPayment(amountStr, notesStr, currentTime, tvCalander.getText().toString(), filePath, paymentType);
            dialog.dismiss();
        });

        edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    ll_calender.setVisibility(View.VISIBLE);
                    ll_notes.setVisibility(View.VISIBLE);
                    tvCamera.setVisibility(View.VISIBLE);
                } else {
                    ll_calender.setVisibility(View.GONE);
                    ll_notes.setVisibility(View.GONE);
                    tvCamera.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    ll_calender.setVisibility(View.VISIBLE);
                    ll_notes.setVisibility(View.VISIBLE);
                    tvCamera.setVisibility(View.VISIBLE);
                } else {
                    ll_calender.setVisibility(View.GONE);
                    ll_notes.setVisibility(View.GONE);
                    tvCamera.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getBillBottomSheet() {
        bottomSheetDialogBills = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.camera_layout,
                        (ConstraintLayout) findViewById(R.id.profile_action));
        bottomSheetView.setOnClickListener(null);

        ImageView camera = (ImageView) bottomSheetView.findViewById(R.id.cam);
        ImageView gallery = (ImageView) bottomSheetView.findViewById(R.id.gal);

        camera.setOnClickListener(v -> picCameraImage());

        gallery.setOnClickListener(v -> {
            picGalleryImage();
        });

        bottomSheetDialogBills.setContentView(bottomSheetView);
        bottomSheetDialogBills.show();
    }

    private void getPayment(String amountStr, String notesStr, String txnTime, String txnDate, File filePath, int paymentType) {


        Map<String, File> multiPartFileMap = new HashMap<>();
        multiPartFileMap.put("billImage", filePath);

        AndroidNetworking.upload(APIs.TRANSACTION_ADD_API)
                .addMultipartParameter("userid", appPrefrence.getUserID())
                .addMultipartParameter("customerID", idStr)
                .addMultipartParameter("paymentMethod", String.valueOf(paymentType))
                .addMultipartParameter("amount", amountStr)
                .addMultipartFile(multiPartFileMap)
                .addMultipartParameter("addNote", notesStr)
                .addMultipartParameter("paymentTime", txnTime)
                .addMultipartParameter("paymentDate", txnDate)
                .setTag("Transaction Add API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // progressDialog.cancel();
                            //  btnSubmit.setEnabled(true);
                            Log.d(TAG, "onResponse: " + response.toString());

                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");

                                getAllTransaction();

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(CustomerTransactionRoom.this, resMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException ex) {
                            //progressDialog.cancel();
                            //  btnSubmit.setEnabled(true);
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        //    progressDialog.cancel();
                        //  btnSubmit.setEnabled(true);
                        error.printStackTrace();

                    }
                });

    }

    /*
     */
    void bottomSheetEditTransaction(int paymentType, double amount, String notes, String strDate, String time,
                                    String imgPath, String payment_id, String customerID, String billPath) /*{

        bottomSheetDialogPayment = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.payment_layout, (ConstraintLayout) findViewById(R.id.data_cons));
        bottomSheetView.setOnClickListener(null);
        EditText edtAmount = (EditText) bottomSheetView.findViewById(R.id.edt_amount);
        EditText edtNotes = (EditText) bottomSheetView.findViewById(R.id.edt_notes);
        billImage = (ImageView) bottomSheetView.findViewById(R.id.Imgbill);
        Button btnSubmit = (Button) bottomSheetView.findViewById(R.id.card_view_submit);
        Button btnDelete = (Button) bottomSheetView.findViewById(R.id.btn_delete);
        TextView tvCamera = (TextView) bottomSheetView.findViewById(R.id.tvcamera);
        TextView tvCalander = (TextView) bottomSheetView.findViewById(R.id.tv_calander);

        btnDelete.setVisibility(View.VISIBLE);
        tvCalander.setText(strDate);
        edtAmount.setText(amount);
        edtNotes.setText(notes);
        txnDate = strDate;

        if (!imgPath.equals("") && !imgPath.equals("null")) {

            billImage.setVisibility(View.VISIBLE);

            Picasso.get().load(APIs.BASE_IMAGE_URL + imgPath).into(billImage);
        } else {
            billImage.setVisibility(View.GONE);
        }
        tvCamera.setOnClickListener(v -> getBillBottomSheet());
        btnDelete.setOnClickListener(v -> {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("Are you sure ?");
            alertDialog.setTitle("Delete Transaction");
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NO", (dialogInterface, i) -> {
                alertDialog.cancel();
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES !", (dialogInterface, i) -> {
                alertDialog.cancel();
                bottomSheetDialogPayment.cancel();
                deleteSingleTransaction(payment_id);
            });
            alertDialog.setCancelable(false);
            alertDialog.show();

        });

        tvCalander.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(CustomerTransactionRoom.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        tvCalander.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        txnDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        btnSubmit.setOnClickListener(v -> {
            btnSubmit.setEnabled(false);
            String amountStr = edtAmount.getText().toString();
            String notesStr = edtNotes.getText().toString();
            editCurrentDateSingleTransaction(amountStr, notesStr, currentTime, tvCalander.getText().toString(), filePath, paymentType, payment_id);
            bottomSheetDialogPayment.cancel();
        });

        bottomSheetDialogPayment.setContentView(bottomSheetView);
        bottomSheetDialogPayment.show();
    }*/ {
        Intent intent = new Intent(CustomerTransactionRoom.this, EditTransactionActivity.class);
        Toast.makeText(this, "" + amount, Toast.LENGTH_SHORT).show();
        intent.putExtra("Name", nameStr);
        intent.putExtra("Amount", amount);
        intent.putExtra("Date", strDate);
        intent.putExtra("Notes", notes);
        intent.putExtra("AddedBy", "test saloj");
        intent.putExtra("PaymentType", paymentType);
        intent.putExtra("CustomerID", customerID);
        intent.putExtra("PaymentID", payment_id);
        intent.putExtra("BillPath", billPath);
        startActivityForResult(intent, BOTTOM_SHEET_REQUEST_CODE);
    }

    public void editCurrentDateSingleTransaction(String amountStr, String notesStr, String txnTime, String txnDate,
                                                 File filePath, String paymentType, String payment_id) {

        Map<String, File> multiPartFileMap = new HashMap<>();
        multiPartFileMap.put("billImage", filePath);

        AndroidNetworking.upload(APIs.TRANSACTION_SINGLE_UPDATE_API)
                .addMultipartParameter("userid", appPrefrence.getUserID())
                .addMultipartParameter("customerID", idStr)
                .addMultipartParameter("id", payment_id)
                .addMultipartParameter("paymentMethod", paymentType)
                .addMultipartParameter("amount", amountStr)
                .addMultipartFile(multiPartFileMap)
                .addMultipartParameter("paymentTime", txnTime)
                .addMultipartParameter("paymentDate", txnDate)
                .addMultipartParameter("addNote", notesStr)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: " + response.toString());
                            if (response.get("success").equals(true)) {
                                getAllTransaction();
                            } else if (response.get("success").equals(false)) {
                                String resMsg = response.getString("msg");
                                Toast.makeText(CustomerTransactionRoom.this, resMsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                    }
                });
    }

    public void deleteSingleTransaction(String payment_id) {
        AndroidNetworking.get(APIs.TRANSACTION_SINGLE_DELETE_API)
                .addQueryParameter("id", payment_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: " + response.toString());
                            if (response.get("success").equals(true)) {
                                getAllTransaction();
                            } else if (response.get("success").equals(false)) {
                                String resMsg = response.getString("msg");
                                Toast.makeText(CustomerTransactionRoom.this, resMsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                    }
                });
    }

    private void getAllTransaction() {

        txnArrayList.clear();

        AndroidNetworking.get(APIs.TRANSACTION_DATA_API)
                .addQueryParameter("userid", appPrefrence.getUserID())
                .addQueryParameter("customerID", idStr)
                .setTag("Transaction All Data API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            progressDialog.cancel();
                            Log.d(TAG, "onResponse: " + response.toString());

                            if (response.get("success").equals(true)) {
                                lazzyLoader.setVisibility(View.GONE);
                                lazzyLoader.stopShimmerAnimation();
                                rv_txn.setVisibility(View.VISIBLE);
                                ll_notFound.setVisibility(View.GONE);

                                String resMsg = response.getString("msg");
                                JSONArray jsonArrayData = response.getJSONArray("data");

                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                    CustomerTransactionModel model = new CustomerTransactionModel();

                                    model.txnId = jsonObjectData.getString("id");
                                    model.customerID = jsonObjectData.getString("customerID");
                                    model.paymentMethod = jsonObjectData.getInt("paymentMethod");
                                    int paymentMethod = jsonObjectData.getInt("paymentMethod");
                                    model.amount = jsonObjectData.getDouble("amount");
                                    double amount = jsonObjectData.getDouble("amount");

                                    model.paymentTime = jsonObjectData.getString("paymentTime");
                                    model.paymentDate = jsonObjectData.getString("paymentDate");
                                    model.addNote = jsonObjectData.getString("addNote");
                                    model.billImage = jsonObjectData.getString("billImage");
                                    model.createdBy = jsonObjectData.getString("createdBy");
                                    model.ipaddress = jsonObjectData.getString("ipaddress");
                                    model.created_at = jsonObjectData.getString("created_at");
                                    model.updated_at = jsonObjectData.getString("updated_at");

                                    txnArrayList.add(model);

                                    if (paymentMethod == 1) {
                                        netCredit = netCredit + amount;
                                        // credCount++;
                                    }
                                    if (paymentMethod == 2) {
                                        netPayment = netPayment + amount;
                                        // payCount++;
                                    }
                                    netBalance = netPayment - netCredit;
                                    if (netBalance > 0.0) {
                                        card_remind.setVisibility(View.GONE);

                                        // currentStatus = "ADVANCE";
                                        //   tv_currentBal.setTextColor(getResources().getColor(R.color.green_500));

                                    } else if (netBalance < 0.0) {
                                        //currentStatus = "DUE";
                                        card_remind.setVisibility(View.VISIBLE);
                                        netBalance = -1.0 * netBalance;
                                        // tv_currentBal.setTextColor(getResources().getColor(R.color.red_500));
                                    }
                                }
                                tvBalance.setText("₹ " + String.valueOf(netBalance));

                                Log.d("Customer", "onBindViewHolder: cr" + netCredit);
                                Log.d("Customer", "onBindViewHolder: dr" + netPayment);
                                Log.d("Customer", "onBindViewHolder: bal" + netBalance);

                                adapter.notifyDataSetChanged();


                            } else if (response.get("success").equals(false)) {
                                lazzyLoader.setVisibility(View.VISIBLE);
                                lazzyLoader.stopShimmerAnimation();
                                ll_notFound.setVisibility(View.VISIBLE);
                                rv_txn.setVisibility(View.GONE);
                                progressDialog.cancel();
                                String resMsg = response.getString("msg");

                            }
                        } catch (JSONException ex) {
                            lazzyLoader.setVisibility(View.VISIBLE);
                            rv_txn.setVisibility(View.GONE);
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
                        lazzyLoader.setVisibility(View.VISIBLE);
                        rv_txn.setVisibility(View.GONE);
                        progressDialog.cancel();
                        error.printStackTrace();
                    }
                });

    }

    private void picCameraImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission(Manifest.permission.CAMERA)) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_PICTURE);
            } else {
                requestPermission(Manifest.permission.CAMERA);
            }
        } else {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_PICTURE);
        }
    }

    private void picGalleryImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, GALLERY_PICTURE);

            } else {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        } else {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent, GALLERY_PICTURE);
        }
    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Toast.makeText(this, "Please Allow Permission", Toast.LENGTH_SHORT).show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    picCameraImage();
                    picGalleryImage();

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {

            switch (requestCode) {
                case 1:
                    if (resultCode == RESULT_OK && requestCode == CAMERA_PICTURE) {

                        if (data == null) {
                            return;
                        }
                        Bitmap thumb = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        thumb.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        File destination = new File(Environment.getExternalStorageDirectory(), "temp.jpg");

                        if (destination.exists()) {
                            destination.delete();
                        }
                        FileOutputStream out;
                        try {
                            out = new FileOutputStream(destination);
                            out.write(bytes.toByteArray());
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (destination != null) {

                            fileImageCam1 = new File(destination.getPath());

                        }
                        billImage.setImageBitmap(thumb);
                        billPath = destination.getPath();
                        UploadTask uploadTask = new UploadTask();
                        uploadTask.execute(new String[]{billPath});
                    }
                    break;

                case 2:
                    if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {

                        if (data == null) {
                            return;
                        }

                        pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);

                    }
                    break;
                case 3:
                    if (resultCode == RESULT_OK && requestCode == BOTTOM_SHEET_REQUEST_CODE) {
                        getAllTransaction();
                    }
                    break;
            }
        } else getAllTransaction();
    }

    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase("true")) {

                billImage.setVisibility(View.VISIBLE);
                bottomSheetDialogBills.cancel();
                Toast.makeText(CustomerTransactionRoom.this, "File Uploaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CustomerTransactionRoom.this, "Please Upload File", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            if (uploadFile(strings[0])) {

                return "true";
            } else {
                return "failed";
            }

        }
    }

    private boolean uploadFile(String path1) {
        try {

            filePath = new File(path1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void PickiTonUriReturned() {
    }

    @Override
    public void PickiTonStartListener() {
    }

    @Override
    public void PickiTonProgressUpdate(int progress) {
    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        billImage.setImageBitmap(bitmap);
        billPath = path;
        Log.d(TAG, "onActivityResult: gal" + billPath);
        UploadTask uploadTask = new UploadTask();
        uploadTask.execute(new String[]{billPath});
    }

    private void openShareReminder() {

        bsDialogShareBusiness = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.reminder_layout,
                        (ConstraintLayout) findViewById(R.id.businessCard_action));
        bottomSheetView.setOnClickListener(null);

        LinearLayout parentLyt = (LinearLayout) bottomSheetView.findViewById(R.id.ll_parent);
        CircleImageView profile = (CircleImageView) bottomSheetView.findViewById(R.id.profile);
        TextView name = (TextView) bottomSheetView.findViewById(R.id.tvname);
        TextView mobile = (TextView) bottomSheetView.findViewById(R.id.tvmobile);
        Button btn_shareCard = (Button) bottomSheetView.findViewById(R.id.btnshareCard);
        TextView tvamount = (TextView) bottomSheetView.findViewById(R.id.tvamount);
        TextView tvDue = (TextView) bottomSheetView.findViewById(R.id.tvDue);

        CardView visitingCard = (CardView) bottomSheetView.findViewById(R.id.card_visiting);


        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
        String dateString = sdf.format(date);
        tvDue.setText("Due as on : " + dateString);


        if (!appPrefrence.getBizName().equals("") && !appPrefrence.getBizName().equals("null")) {
            name.setText(appPrefrence.getBizName());

        } else {
            name.setText("");

        }

        tvamount.setText("₹ " + String.valueOf(netBalance));

        if (!appPrefrence.getMobile().equals("") && !appPrefrence.getMobile().equals("null")) {
            mobile.setText("To : " + mobileStr);

        } else {
            mobile.setText("");
        }


        if (!appPrefrence.getUserProfile().equals("") && !appPrefrence.getUserProfile().equals("null")) {
            Picasso.get().load(APIs.BASE_IMAGE_URL + appPrefrence.getUserProfile()).into(profile);
        }

        btn_shareCard.setOnClickListener(v -> {

            try {
                Bitmap bitmap = getBitmapFromView(visitingCard);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(this, bitmap));
                shareIntent.setType("image/jpeg");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Payment of ₹ " + netBalance + " to " + name.getText().toString() + " is pending.\n Please make payment as soon as possible. Check details at " + "app link " + "If you also have a business , Install");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                bsDialogShareBusiness.cancel();

            } catch (Exception e) {
                e.getMessage();
            }

        });

        bsDialogShareBusiness.setContentView(bottomSheetView);
        bsDialogShareBusiness.show();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                    inImage, "", "");
            return Uri.parse(path);
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    private Bitmap getBitmapFromView(CardView view) {
        try {

            view.setDrawingCacheEnabled(true);

            view.measure(View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

            view.buildDrawingCache(true);
            Bitmap returnedBitmap = Bitmap.createBitmap(view.getDrawingCache());

            //Define a bitmap with the same size as the view
            view.setDrawingCacheEnabled(false);

            return returnedBitmap;
        } catch (Exception e) {

            Log.d(TAG, "getBitmapFromView: " + e);
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CustomerTransactionRoom.this, MainActivity.class);
        intent.putExtra("FRAGMENT_ID", 0);
        startActivity(intent);
        finish();
    }

}
