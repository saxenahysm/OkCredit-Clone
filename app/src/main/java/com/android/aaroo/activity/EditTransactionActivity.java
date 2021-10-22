package com.android.aaroo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.android.aaroo.R;
import com.android.aaroo.activity.profile.ProfileActivity;
import com.android.aaroo.activity.transactioncustomer.CustomerTransactionRoom;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditTransactionActivity extends AppCompatActivity implements PickiTCallbacks {
    LinearLayout ll_edit_transaction, ll_edit_notes, ll_delete_transaction;
    TextView text_view_notification, text_view_currency, text_view_name, text_view_added_by, text_view_notes, text_view_amount, text_view_edit_bill, text_view_delete_transaction, text_view_date;
    ImageView imageViewProfile, imageViewBack, imageViewBill1;
    String stringName, stringCustomerID, stringPaymentID, stringDate, stringAmount, stringNotes, stringAddedBy, stringBillPath, stringProfileImage;
    BottomSheetDialog bottomSheetDialogPayment, bottomSheetDialogCredit, bottomSheetDialogBills, bsDialogShareBusiness;
    private int mYear, mMonth, mDay, intPaymentType;
    String TAG = EditTransactionActivity.class.getSimpleName();
    AppPrefrence appPrefrence;

    private static final int CAMERA_PICTURE = 1;
    private static final int GALLERY_PICTURE = 2;
    File fileImageCam1, filePath;
    String file1Str, categoryId;
    PickiT pickiT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_transectioin);
        appPrefrence = new AppPrefrence(this);
        pickiT = new PickiT(this, this, this);
        text_view_added_by = findViewById(R.id.text_view_added_by);
        text_view_edit_bill = findViewById(R.id.text_view_edit_bill);
        text_view_notes = findViewById(R.id.text_view_notes);
        text_view_amount = findViewById(R.id.text_view_amount);
        text_view_currency = findViewById(R.id.text_view_currency);
        text_view_added_by = findViewById(R.id.text_view_added_by);
        text_view_date = findViewById(R.id.text_view_date);
        text_view_delete_transaction = findViewById(R.id.text_view_delete_transaction);
        text_view_notification = findViewById(R.id.text_view_notification);
        text_view_name = findViewById(R.id.text_view_name);
        imageViewProfile = findViewById(R.id.profile_img);
        imageViewBill1 = findViewById(R.id.imageViewBill1);
        imageViewBack = findViewById(R.id.back);

        ll_edit_transaction = findViewById(R.id.ll_edit_transaction);
        ll_delete_transaction = findViewById(R.id.ll_delete_transaction);
        ll_edit_notes = findViewById(R.id.ll_edit_notes);


        if (getIntent().getExtras() != null) {
            stringAmount = String.valueOf(getIntent().getExtras().getDouble("Amount"));
            stringName = getIntent().getExtras().getString("Name");
            stringDate = getIntent().getExtras().getString("Date");
            stringNotes = getIntent().getExtras().getString("Notes");
            stringAddedBy = getIntent().getExtras().getString("AddedBy");
            intPaymentType = getIntent().getIntExtra("PaymentType", 0);
            stringCustomerID = getIntent().getExtras().getString("CustomerID");
            stringPaymentID = getIntent().getExtras().getString("PaymentID");
            stringBillPath = getIntent().getExtras().getString("BillPath");
        }
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (!stringDate.equals(currentDate)) {
            text_view_delete_transaction.setText("You can only delete current date transaction");
            text_view_delete_transaction.setTextColor(getResources().getColor(R.color.grey_600));
            ll_delete_transaction.setEnabled(false);
            ll_delete_transaction.setClickable(false);
            ll_edit_notes.setClickable(false);
            ll_edit_notes.setEnabled(false);
            ll_edit_transaction.setVisibility(View.GONE);
            text_view_edit_bill.setClickable(false);
            text_view_edit_bill.setEnabled(false);
        }
        if (intPaymentType == 1) {
            text_view_amount.setTextColor(Color.parseColor("#E57373"));
            text_view_currency.setTextColor(Color.parseColor("#E57373"));
        } else {
            text_view_amount.setTextColor(Color.parseColor("#81C784"));
            text_view_currency.setTextColor(Color.parseColor("#81C784"));
        }

        if (!stringNotes.equals("null") || stringNotes.isEmpty()) {
            text_view_notes.setText(stringNotes);
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }

        text_view_added_by.setText(stringAddedBy);
        text_view_name.setText(stringName);
        text_view_amount.setText(stringAmount);
        text_view_date.setText(stringDate);
        if (stringBillPath != null) {
            Toast.makeText(this, "image ", Toast.LENGTH_SHORT).show();
            imageViewBill1.setVisibility(View.VISIBLE);
            Picasso.get().load(APIs.BASE_IMAGE_URL + stringBillPath).into(imageViewBill1);
        }

        imageViewBack.setOnClickListener(v -> finish());
        ll_edit_transaction.setOnClickListener(v ->
                bottomSheetEditTransaction(intPaymentType, stringAmount, stringNotes, stringDate, stringDate, stringBillPath, stringPaymentID));
        ll_edit_notes.setOnClickListener(v ->
                bottomSheetEditTransaction(intPaymentType, stringAmount, stringNotes, stringDate, stringDate, stringBillPath, stringPaymentID));
        text_view_edit_bill.setOnClickListener(v ->
                bottomSheetEditTransaction(intPaymentType, stringAmount, stringNotes, stringDate, stringDate, stringBillPath, stringPaymentID));

        ll_delete_transaction.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setPositiveButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
                dialog.cancel();
            });
            alertDialog.setNegativeButton("Yes! Delete", (dialog, which) -> {
                dialog.dismiss();
                dialog.cancel();
                deleteSingleTransaction(stringPaymentID);
            });
            alertDialog.setTitle("Sure to delete transaction ?");
            alertDialog.show();
        });
    }

    void bottomSheetEditTransaction(int paymentType, String amount, String notes, String strDate, String time, String imgPath, String payment_id) {
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
        imageViewBill1 = dialog.findViewById(R.id.Imgbill);
        ImageView image_view_back = dialog.findViewById(R.id.image_view_back);
        ImageView image_view_profile = dialog.findViewById(R.id.image_view_profile);
        CardView btnSubmit = dialog.findViewById(R.id.card_view_submit);
        TextView text_view_currency = dialog.findViewById(R.id.text_view_currency);
        TextView tvCamera = dialog.findViewById(R.id.tvcamera);
        TextView tvCalander = dialog.findViewById(R.id.tv_calander);
        LinearLayout ll_calender = dialog.findViewById(R.id.ll_calender);
        LinearLayout ll_notes = dialog.findViewById(R.id.ll_notes);

        edtAmount.setText(amount);
        edtNotes.setText(notes);
        if (stringBillPath != null) {
            imageViewBill1.setVisibility(View.VISIBLE);
            Picasso.get().load(APIs.BASE_IMAGE_URL + stringBillPath).placeholder(R.drawable.ic_person).into(imageViewBill1);
            Picasso.get().load(APIs.BASE_IMAGE_URL + stringBillPath).placeholder(R.drawable.ic_person).into(image_view_profile);
            Toast.makeText(this, APIs.BASE_IMAGE_URL + stringBillPath, Toast.LENGTH_SHORT).show();
        }
        ll_calender.setVisibility(View.VISIBLE);
        ll_notes.setVisibility(View.VISIBLE);
        tvCamera.setVisibility(View.VISIBLE);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);
        tvCalander.setText(dateString);
        Log.d(TAG, "onCreate: " + dateString);
        if (paymentType == 1) {
            text_view_currency.setTextColor(Color.RED);
        }
        imageViewBill1.setVisibility(View.GONE);

        tvCamera.setOnClickListener(v -> {
            getBillBottomSheet();
        });

        tvCalander.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EditTransactionActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        tvCalander.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        stringDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        btnSubmit.setOnClickListener(v -> {
            btnSubmit.setEnabled(false);
            String amountStr = edtAmount.getText().toString();
            String notesStr = edtNotes.getText().toString();
            editCurrentDateSingleTransaction(amountStr, notesStr, currentTime, tvCalander.getText().toString(), null, paymentType, stringPaymentID);
            dialog.dismiss();
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
                                Toast.makeText(EditTransactionActivity.this, "delete success", Toast.LENGTH_SHORT).show();
//                               finishActivity(RESULT_OK);
                                finish();
                            } else if (response.get("success").equals(false)) {
                                String resMsg = response.getString("msg");
                                Toast.makeText(EditTransactionActivity.this, resMsg, Toast.LENGTH_SHORT).show();
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

    public void editCurrentDateSingleTransaction(String amountStr, String notesStr, String txnTime, String txnDate,
                                                 File filePath, int paymentType, String payment_id) {

        Map<String, File> multiPartFileMap = new HashMap<>();
        multiPartFileMap.put("billImage", filePath);

        AndroidNetworking.upload(APIs.TRANSACTION_SINGLE_UPDATE_API)
                .addMultipartParameter("userid", appPrefrence.getUserID())
                .addMultipartParameter("customerID", stringCustomerID)
                .addMultipartParameter("id", payment_id)
                .addMultipartParameter("paymentMethod", String.valueOf(paymentType))
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
//                                finishActivity(RESULT_OK);
                                Toast.makeText(EditTransactionActivity.this, "edit success ", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (response.get("success").equals(false)) {
                                String resMsg = response.getString("msg");
                                Toast.makeText(EditTransactionActivity.this, resMsg, Toast.LENGTH_SHORT).show();
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

    private void getBillBottomSheet() {
        bottomSheetDialogBills = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.camera_layout,
                        (ConstraintLayout) findViewById(R.id.profile_action));
        bottomSheetView.setOnClickListener(null);

        ImageView camera = (ImageView) bottomSheetView.findViewById(R.id.cam);
        ImageView gallery = (ImageView) bottomSheetView.findViewById(R.id.gal);

        camera.setOnClickListener(v -> {
            picCameraImage();
        });

        gallery.setOnClickListener(v -> {
//            bottomSheetDialogBills.cancel();
            picGalleryImage();
        });

        bottomSheetDialogBills.setContentView(bottomSheetView);
        bottomSheetDialogBills.show();
    }

    private void picCameraImage() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PICTURE);
    }

    private void picGalleryImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, GALLERY_PICTURE);
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
                        imageViewBill1.setImageBitmap(thumb);
                        file1Str = destination.getPath();
                        UploadTask uploadTask = new UploadTask();
                        uploadTask.execute(new String[]{file1Str});
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

            }
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
        imageViewBill1.setImageBitmap(bitmap);
        file1Str = path;
        Log.d(TAG, "onActivityResult: gal" + file1Str);
        UploadTask uploadTask = new UploadTask();
        uploadTask.execute(new String[]{file1Str});
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

                bottomSheetDialogBills.cancel();
                Toast.makeText(EditTransactionActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditTransactionActivity.this, "Please Upload File", Toast.LENGTH_SHORT).show();
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

}