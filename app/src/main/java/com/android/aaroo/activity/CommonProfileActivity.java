package com.android.aaroo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.karumi.dexter.Dexter.*;

public class CommonProfileActivity extends AppCompatActivity implements PickiTCallbacks {
    LinearLayout ll_name, ll_mobileNo, ll_address, ll_deleteCustSupp, ll_saveProfile;
    ImageView camera_action, back;
    CircleImageView profile;
    BottomSheetDialog bottomSheetDialogCamera, bottomSheetDialogName, bottomSheetDialogMobile, bottomSheetDialogAddress;
    private static final String TAG = CommonProfileActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 111;

    private static final int CAMERA_PICTURE = 1;
    private static final int GALLERY_PICTURE = 2;

    File fileImageCam1, filePath;
    String file1Str;
    CircleImageView profImage;
    PickiT pickiT;
    EditText edtName, edtMob, edtAddress;
    //  TextView ctv_name,ctv_mobile,ctv_address;
    TextView tv_mobile, tv_name, tv_address;
    String idStr, nameStr, mobileStr, addressStr, smsStatusStr = "0";
    double amountDbl;
    int customerTypeInt;
    SwitchCompat switchSmsStatus;
    AppPrefrence appPrefrence;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_profile);

        appPrefrence = new AppPrefrence(this);
        progressDialog = new ProgressDialog(this);

        pickiT = new PickiT(CommonProfileActivity.this, this, CommonProfileActivity.this);
        ll_name = findViewById(R.id.ll_name);
        ll_mobileNo = findViewById(R.id.ll_mob);
        ll_address = findViewById(R.id.ll_address);
        ll_deleteCustSupp = findViewById(R.id.ll_deleteCustSupp);
        camera_action = findViewById(R.id.camera_action);
        profImage = findViewById(R.id.profileImg);
        ll_saveProfile = findViewById(R.id.ll_saveProfile);
        back = findViewById(R.id.back);

        tv_mobile = findViewById(R.id.tv_mobile);
        tv_name = findViewById(R.id.tv_name);
        tv_address = findViewById(R.id.tv_address);
        switchSmsStatus = findViewById(R.id.sw_status);

        if (getIntent().hasExtra("id")) {
            idStr = getIntent().getStringExtra("id");
            customerTypeInt = getIntent().getIntExtra("custType", 0);
            amountDbl = getIntent().getDoubleExtra("amount", 0);
        }

        back.setOnClickListener(v -> {
            onBackPressed();
        });

        ll_name.setOnClickListener(v -> {
            openNameBottomSheet();
        });

        ll_mobileNo.setOnClickListener(v -> {
            openMobileBottomSheet();
        });

        ll_address.setOnClickListener(v -> {
            openAddressBottomSheet();
        });

        camera_action.setOnClickListener(v -> {
            openCameraBottomSheet();
        });

        ll_deleteCustSupp.setOnClickListener(v -> {

            if (amountDbl == 0.0) {

                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Delete");
                alertDialog.setMessage("Are you sure ?");

                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", (dialogInterface, i) -> {
                    alertDialog.cancel();
                });

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", (dialogInterface, i) -> {
                    alertDialog.cancel();
                    getDeleteCustSupp();
                });
                alertDialog.setCancelable(false);
                alertDialog.show();

            } else if (amountDbl < 0.0) {

                getAlert();

            } else if (amountDbl > 0.0) {
                getAlert();
            }

        });
        progressDialog.setMessage("please wait...");
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        getCommonDetail();

        ll_saveProfile.setOnClickListener(v -> {
            getSaveData();

            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
        });


        if (appPrefrence.getSmsStatus().equals("1")) {
            switchSmsStatus.setChecked(true);
        } else {
            switchSmsStatus.setChecked(false);

        }

        switchSmsStatus.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                //open job.
                // startActivity(new Intent(SettingActivity.this, SequrityPinActivity.class));
                smsStatusStr = "1";
                appPrefrence.setSmsStatus(smsStatusStr);
            } else {
                //close job.
                smsStatusStr = "0";
                appPrefrence.setSmsStatus(smsStatusStr);
            }
        });
    }

    private void getAlert() {

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert !");
        alertDialog.setMessage("Please make customer balance 0 to delete");
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialogInterface, i) -> {
            alertDialog.cancel();
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", (dialogInterface, i) -> {
            alertDialog.cancel();
            onBackPressed();

        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    private void getSaveData() {

        Map<String, File> multiPartFileMap = new HashMap<>();
        multiPartFileMap.put("image", filePath);

        AndroidNetworking.upload(APIs.CUSTOMER_AND_SUPPLIER_UPDATE_API)
                .addMultipartParameter("id", idStr)
                .addMultipartParameter("customerName", nameStr)
                .addMultipartParameter("customerType", String.valueOf(customerTypeInt))
                .addMultipartParameter("address", addressStr)
                .addMultipartFile(multiPartFileMap)
                .addMultipartParameter("mobile", mobileStr)
                .addMultipartParameter("smsStatus", smsStatusStr)
                .setTag("Common Data Update API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //    progressDialog.cancel();
                            //  ll_save_profile.setEnabled(true);
                            Log.d(TAG, "onResponse Update: " + response.toString());

                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                                //    appPrefrence.setBizName(etv_businessName.getText().toString());
                                //  appPrefrence.setUsername(etv_contPerson.getText().toString());
                                Toast.makeText(CommonProfileActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                                // getUserDetail();

                                startActivity(new Intent(CommonProfileActivity.this, MainActivity.class));
                                finish();

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                //  Toast.makeText(ProfileActivity.this, resMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException ex) {
                            //   progressDialog.cancel();
                            //  ll_save_profile.setEnabled(true);
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        //   progressDialog.cancel();
                        // ll_save_profile.setEnabled(true);
                    }
                });
    }

    private void getDeleteCustSupp() {

        AndroidNetworking.get(APIs.CUSTOMER_AND_SUPPLIER_DELETE_API)
                .addQueryParameter("id", idStr)
                .setTag("Common Profile Delete API")
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

                                Toast.makeText(CommonProfileActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                                //JSONObject jsonObjectData = response.getJSONObject("data");

                                //    String id = jsonObjectData.getString("id");
                                startActivity(new Intent(CommonProfileActivity.this, MainActivity.class));
                                finish();

                                if (customerTypeInt == 1) {
                                    Intent intent = new Intent(CommonProfileActivity.this, MainActivity.class);
                                    intent.putExtra("FRAGMENT_ID", 0);
                                    startActivity(intent);
                                    finish();
                                } else if (customerTypeInt == 2) {
                                    Intent intent = new Intent(CommonProfileActivity.this, MainActivity.class);
                                    intent.putExtra("FRAGMENT_ID", 1);
                                    startActivity(intent);
                                    finish();
                                }


                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(CommonProfileActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                                //  JSONObject jsonObjectData = response.getJSONObject("data");

                                //String error = jsonObjectData.getString("error");
                            }

                        } catch (JSONException ex) {

                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // progressDialog.cancel();
                    }
                });
    }

    private void getCommonDetail() {

        AndroidNetworking.get(APIs.CUSTOMER_AND_SUPPLIER_EDIT_API + idStr + "/edit")
                .setTag("Common Profile API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.cancel();
                            Log.d(TAG, "onResponse: " + response.toString());
                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                                //   Toast.makeText(ProfileActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                                JSONObject jsonObjectData = response.getJSONObject("data");

                                String id = jsonObjectData.getString("id");
                                String name = jsonObjectData.getString("customerName");

                                if (name.equals("null") || name.equals("")) {

                                    //   edtName.getText().clear();
                                    //   edtName.setText("");
                                    tv_name.setText("NA");

                                } else {

                                    //  edtName.setText(name);
                                    nameStr = jsonObjectData.getString("customerName");
                                    tv_name.setText(name);
                                }

                                String mobile = jsonObjectData.getString("mobile");

                                if (mobile.equals("null") || mobile.equals("")) {
                                    //   edtMob.setText("");
                                    tv_mobile.setText("");
                                } else {
                                    // edtMob.setText(mobile);
                                    mobileStr = jsonObjectData.getString("mobile");
                                    tv_mobile.setText(mobile);
                                }

                                String customerType = jsonObjectData.getString("customerType");

                                String address = jsonObjectData.getString("address");

                                if (address.equals("null") || address.equals("")) {

                                    //  edtAddress.getText().clear();
                                    //  edtAddress.setText("");
                                    tv_address.setText("NA");

                                } else {
                                    addressStr = jsonObjectData.getString("address");
                                    tv_address.setText(address);
                                }

                                String image = jsonObjectData.getString("image");

                                if (!image.equals("") && !image.equals("null")) {
                                    Picasso.get().load(APIs.BASE_IMAGE_URL + image).into(profImage);
                                }

                                String smsStatus = jsonObjectData.getString("smsStatus");
                                String status = jsonObjectData.getString("status");
                                String createdBy = jsonObjectData.getString("createdBy");
                                String ipaddress = jsonObjectData.getString("ipaddress");
                                String created_at = jsonObjectData.getString("created_at");
                                String updated_at = jsonObjectData.getString("updated_at");

                            } else if (response.get("success").equals(false)) {
                                progressDialog.cancel();
                                String resMsg = response.getString("msg");

                                Toast.makeText(CommonProfileActivity.this, resMsg, Toast.LENGTH_SHORT).show();
                                JSONObject jsonObjectData = response.getJSONObject("data");

                                String error = jsonObjectData.getString("error");
                            }

                        } catch (JSONException ex) {
                            progressDialog.cancel();
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.cancel();
                    }
                });
    }

    private void openNameBottomSheet() {

        bottomSheetDialogName = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.name_layout,
                        (ConstraintLayout) findViewById(R.id.name_cons));
        bottomSheetView.setOnClickListener(null);

        edtName = (EditText) bottomSheetView.findViewById(R.id.edt_name);

        if (!nameStr.equals("") && !nameStr.equals("null")) {
            edtName.setText(nameStr);
        } else {
            edtName.getText().clear();
        }
        Button btnSubmit = (Button) bottomSheetView.findViewById(R.id.card_view_submit);

        btnSubmit.setOnClickListener(v -> {
            nameStr = edtName.getText().toString();
            tv_name.setText(nameStr);
            bottomSheetDialogName.cancel();
        });


        bottomSheetDialogName.setContentView(bottomSheetView);
        bottomSheetDialogName.show();
    }

    private void openMobileBottomSheet() {

        bottomSheetDialogMobile = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.mobile_layout,
                        (ConstraintLayout) findViewById(R.id.mobile_cons));
        bottomSheetView.setOnClickListener(null);

        edtMob = (EditText) bottomSheetView.findViewById(R.id.edt_contact);
        if (!edtMob.equals("") && !edtMob.equals("null")) {
            edtMob.setText(mobileStr);
        } else {
            edtMob.getText().clear();
        }
        Button btnSubmit = (Button) bottomSheetView.findViewById(R.id.card_view_submit);

        btnSubmit.setOnClickListener(v -> {
            mobileStr = edtMob.getText().toString();
            tv_mobile.setText(mobileStr);
            bottomSheetDialogMobile.cancel();
        });


        bottomSheetDialogMobile.setContentView(bottomSheetView);
        bottomSheetDialogMobile.show();
    }

    private void openAddressBottomSheet() {

        bottomSheetDialogAddress = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.address_layout,
                        (ConstraintLayout) findViewById(R.id.address_cons));
        bottomSheetView.setOnClickListener(null);

        edtAddress = (EditText) bottomSheetView.findViewById(R.id.edt_address);
        if (!edtAddress.equals("") && !edtAddress.equals("null")) {
            edtAddress.setText(addressStr);
        } else {
            edtAddress.getText().clear();
        }


        Button btnSubmit = (Button) bottomSheetView.findViewById(R.id.card_view_submit);

        btnSubmit.setOnClickListener(v -> {
            addressStr = edtAddress.getText().toString();
            tv_address.setText(addressStr);
            bottomSheetDialogAddress.cancel();
        });


        bottomSheetDialogAddress.setContentView(bottomSheetView);
        bottomSheetDialogAddress.show();
    }

    private void openCameraBottomSheet() {

        bottomSheetDialogCamera = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.camera_layout,
                        (ConstraintLayout) findViewById(R.id.profile_action));
        bottomSheetView.setOnClickListener(null);

        ImageView camera = (ImageView) bottomSheetView.findViewById(R.id.cam);
        ImageView gallery = (ImageView) bottomSheetView.findViewById(R.id.gal);

        camera.setOnClickListener(v -> {
            onProfileImageClick(true);
            bottomSheetDialogCamera.cancel();
        });

        gallery.setOnClickListener(v -> {
            onProfileImageClick(false);
            bottomSheetDialogCamera.cancel();
        });

        bottomSheetDialogCamera.setContentView(bottomSheetView);
        bottomSheetDialogCamera.show();
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
                        profImage.setImageBitmap(thumb);
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

    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase("true")) {

                Toast.makeText(CommonProfileActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CommonProfileActivity.this, "Please Upload File", Toast.LENGTH_SHORT).show();
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
        profImage.setImageBitmap(bitmap);
        file1Str = path;
        Log.d(TAG, "onActivityResult: gal" + file1Str);
        UploadTask uploadTask = new UploadTask();
        uploadTask.execute(new String[]{file1Str});
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pickiT.deleteTemporaryFile(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            pickiT.deleteTemporaryFile(this);
        }
    }

    void onProfileImageClick(boolean isTrue) {
        withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (isTrue) {
                                picCameraImage();
                            } else {
                                picGalleryImage();
                            }
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}