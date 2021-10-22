package com.android.aaroo.activity.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.android.aaroo.adapter.CategoryTypeAdapter;
import com.android.aaroo.fragment.customer.CustomerModel;
import com.android.aaroo.fragment.supplier.SupplierAdaper;
import com.android.aaroo.fragment.supplier.SupplierModel;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.android.aaroo.helper.Tools;
import com.android.aaroo.helper.ViewAnimation;
import com.android.aaroo.model.CategoryModel;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.aaroo.helper.Tools.toggleArrow;

public class ProfileActivity extends AppCompatActivity implements PickiTCallbacks {

    LinearLayout ll_edit_profile, ll_view, ll_edit, ll_save_profile, ll_other_info, ell_other_info,
            ll_reataile_shop, ll_distributor, ll_online_service,
            ll_personal_use, ll_shareBusiCard, ell_shareBusiCard;
    ImageView back_img, camAction, toggle, etoggle;
    BottomSheetDialog bottomSheetDialog, bsDialogBusinessType, bsDialogCategory, bsDialogShareBusiness;
    NestedScrollView nested;
    ProgressDialog progressDialog;

    String businessType = "";

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 111;

    private static final int CAMERA_PICTURE = 1;
    private static final int GALLERY_PICTURE = 2;

    File fileImageCam1, filePath;
    String file1Str, categoryId;
    CircleImageView profImage, eprofileImg, cirProfileImg;
    PickiT pickiT;

    RecyclerView rv_category;
    ArrayList<CategoryModel> catArrayList;
    GridLayoutManager layoutManager;
    CategoryTypeAdapter adapter;

    AppPrefrence appPrefrence;
    EditText etv_businessType, etv_categoryType, etv_businessName,
            etv_mobile, etv_address, etv_email, etv_about, etv_contPerson;

    TextView tv_businessName, tv_mobile, tv_businessType,
            tv_category, tv_address, tv_email, tv_about, tv_contPerson;

    HashMap<String, String>
            catClassMap = new HashMap<>();

    String cat_idStr = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressDialog = new ProgressDialog(this);
        appPrefrence = new AppPrefrence(this);

        pickiT = new PickiT(this, this, this);

        cirProfileImg = findViewById(R.id.cirProfileImg);
        tv_contPerson = findViewById(R.id.tv_contPerson);
        tv_businessName = findViewById(R.id.tv_businessName);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_businessType = findViewById(R.id.tv_businessType);
        tv_category = findViewById(R.id.tv_category);
        tv_address = findViewById(R.id.tv_address);
        tv_email = findViewById(R.id.tv_email);
        tv_about = findViewById(R.id.tv_about);

        etv_businessName = findViewById(R.id.etv_businessName);
        etv_mobile = findViewById(R.id.etv_mobile);
        etv_address = findViewById(R.id.etv_address);
        etv_email = findViewById(R.id.etv_email);
        etv_about = findViewById(R.id.etv_about);
        etv_contPerson = findViewById(R.id.etv_contPerson);

        ll_edit = findViewById(R.id.ll_edit);
        ll_edit_profile = findViewById(R.id.ll_edit_profile);
        ll_view = findViewById(R.id.ll_view);
        back_img = findViewById(R.id.back);
        ll_save_profile = findViewById(R.id.ll_save_profile);
        camAction = findViewById(R.id.camera_action);
        eprofileImg = findViewById(R.id.eprofileImg);
        toggle = findViewById(R.id.toggle);
        ll_other_info = findViewById(R.id.ll_other_info);
        ell_other_info = findViewById(R.id.ell_other_info);
        nested = findViewById(R.id.nested);
        etoggle = findViewById(R.id.etoggle);
        etv_businessType = findViewById(R.id.etv_businessType);
        etv_categoryType = findViewById(R.id.etv_category);
        ll_shareBusiCard = findViewById(R.id.ll_shareBusiCard);
        ell_shareBusiCard = findViewById(R.id.ell_shareBusiCard);

        ll_view.setVisibility(View.VISIBLE);
        ll_edit_profile.setVisibility(View.VISIBLE);
        ll_edit.setVisibility(View.GONE);
        ll_save_profile.setVisibility(View.GONE);


        getUserDetail();
        getCategoryFetch();
        //  Picasso.get().load(cate_image).into(holder.cate_img);
        etv_businessType.setOnClickListener(v -> {
            openBusinessType();
        });

        etv_categoryType.setOnClickListener(v -> {
            openCategory();
        });

        camAction.setOnClickListener(v -> {
            openBottomSheet();
        });
        ell_shareBusiCard.setOnClickListener(v -> {
            openShareBusinessCard();
        });

        ll_shareBusiCard.setOnClickListener(v -> {
            openShareBusinessCard();
        });

        ViewAnimation.collapse(ll_other_info);

        toggle.setOnClickListener(v -> {
            boolean show = toggleArrow(v);
            if (show) {

                ViewAnimation.expand(ll_other_info, new ViewAnimation.AnimListener() {
                    @Override
                    public void onFinish() {
                        Tools.nestedScrollTo(nested, ll_other_info);
                    }
                });
            } else {
                ViewAnimation.collapse(ll_other_info);
            }

        });

        ViewAnimation.collapse(ell_other_info);

        etoggle.setOnClickListener(v -> {

            boolean show = toggleArrow(v);
            if (show) {
                ViewAnimation.expand(ell_other_info, new ViewAnimation.AnimListener() {
                    @Override
                    public void onFinish() {
                        Tools.nestedScrollTo(nested, ell_other_info);

                    }
                });
            } else {
                ViewAnimation.collapse(ell_other_info);
            }

        });

        ll_edit_profile.setOnClickListener(v -> {

            ll_view.setVisibility(View.GONE);
            ll_edit.setVisibility(View.VISIBLE);
            ll_save_profile.setVisibility(View.VISIBLE);
            ll_edit_profile.setVisibility(View.GONE);

        });

        ll_save_profile.setOnClickListener(v -> {

            ll_view.setVisibility(View.VISIBLE);
            ll_edit.setVisibility(View.GONE);
            ll_edit_profile.setVisibility(View.VISIBLE);
            ll_save_profile.setVisibility(View.GONE);
            ll_save_profile.setEnabled(false);
            getUserDataUpdate();

        });

        back_img.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private void getCategoryFetch() {

        AndroidNetworking.get(APIs.CATEGORY_API)
                .setTag("Category API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //progressDialog.cancel();
                            Log.d(TAG, "onResponse: " + response.toString());
                            if (response.get("success").equals(true)) {
                                //    rv_custom.setVisibility(View.VISIBLE);
                                //   lazzyLoader.setVisibility(View.GONE);
                                String resMsg = response.getString("msg");
                                //  Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();
                                JSONArray jsonArrayData = response.getJSONArray("data");

                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);

                                    String id = jsonObjectData.getString("id");
                                    String categoryName = jsonObjectData.getString("categoryName");
                                    String image = jsonObjectData.getString("image");
                                    int type = jsonObjectData.getInt("type");
                                    String created_at = jsonObjectData.getString("created_at");
                                    String updated_at = jsonObjectData.getString("updated_at");

                                    catClassMap.put(id, categoryName);

                                    if (id.equals(cat_idStr)) {
                                        etv_categoryType.setText(categoryName);
                                        tv_category.setText(categoryName);
                                    }

                                }

                            } else if (response.get("success").equals(false)) {
                                String resMsg = response.getString("msg");

                                JSONArray jsonArrayData = new JSONArray("data");

                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                    String error = jsonObjectData.getString("error");
                                }
                            }
                        } catch (JSONException ex) {

                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // progressDialog.cancel();
                        //  lazzyLoader.setVisibility(View.VISIBLE);
                        // rv_custom.setVisibility(View.GONE);
                        error.printStackTrace();
                    }
                });


    }

    private void getUserDetail() {

        AndroidNetworking.get(APIs.SINGLE_USER_DATA_API + appPrefrence.getUserID())
                .addQueryParameter("userid", appPrefrence.getUserID())
                .setTag("User Profile API")
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
                                //   Toast.makeText(ProfileActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                                JSONObject jsonObjectData = response.getJSONObject("data");

                                String id = jsonObjectData.getString("id");
                                String name = jsonObjectData.getString("name");

                                if (name.equals("null") || name.equals("")) {
                                    etv_contPerson.getText().clear();
                                    etv_contPerson.setText("");
                                    tv_contPerson.setText("NA");
                                } else {
                                    etv_contPerson.setText(name);
                                    tv_contPerson.setText(name);
                                    appPrefrence.setUsername(name);
                                }
                                String mobile = jsonObjectData.getString("mobile");
                                if (mobile.equals("null") || mobile.equals("")) {
                                    etv_mobile.setText("");
                                    tv_mobile.setText("");
                                } else {
                                    etv_mobile.setText(mobile);
                                    tv_mobile.setText(mobile);
                                    appPrefrence.setMobile(mobile);
                                }
                                String email = jsonObjectData.getString("email");
                                if (email.equals("null") || email.equals("")) {
                                    etv_email.getText().clear();
                                    etv_email.setText("");
                                    tv_email.setText("NA");
                                } else {
                                    etv_email.setText(email);
                                    tv_email.setText(email);
                                }
                                String about = jsonObjectData.getString("about");

                                if (about.equals("null") || about.equals("")) {
                                    etv_about.getText().clear();
                                    etv_about.setText("");
                                    tv_about.setText("NA");

                                } else {
                                    etv_about.setText(about);
                                    tv_about.setText(about);
                                }
                                String image = jsonObjectData.getString("image");
                                if (!image.equals("") && !image.equals("null")) {
                                    Picasso.get().load(APIs.BASE_IMAGE_URL + image).into(eprofileImg);
                                    Picasso.get().load(APIs.BASE_IMAGE_URL + image).into(cirProfileImg);
                                    appPrefrence.setUserProfile(image);
                                }

                                String address = jsonObjectData.getString("address");

                                if (address.equals("null") || address.equals("")) {
                                    etv_address.getText().clear();
                                    etv_address.setText("");
                                    tv_address.setText("NA");

                                } else {
                                    etv_address.setText(address);
                                    tv_address.setText(address);
                                }
                                String bussinessName = jsonObjectData.getString("bussinessName");

                                if (bussinessName.equals("null") || bussinessName.equals("")) {
                                    etv_businessName.getText().clear();
                                    etv_businessName.setText("");
                                    tv_businessName.setText("NA");

                                } else {
                                    etv_businessName.setText(bussinessName);
                                    tv_businessName.setText(bussinessName);
                                    appPrefrence.setBizName(bussinessName);
                                }

                                String bussinessType = jsonObjectData.getString("bussinessType");

                                if (bussinessType.equals("null") || bussinessType.equals("")) {
                                    etv_businessType.getText().clear();
                                    etv_businessType.setText("");
                                    tv_businessType.setText("NA");

                                } else {
                                    etv_businessType.setText(bussinessType);
                                    tv_businessType.setText(bussinessType);
                                }

                                String bussinessCategory = jsonObjectData.getString("bussinessCategory");


                                if (bussinessCategory.equals("null") || bussinessCategory.equals("")) {

                                    etv_categoryType.getText().clear();
                                    etv_categoryType.setText("");
                                    tv_category.setText("NA");

                                } else {
                                    // etv_categoryType.setText(bussinessCategory);
                                    // tv_category.setText(bussinessCategory);
                                    cat_idStr = jsonObjectData.getString("bussinessCategory");

                                }

                                String email_verified_at = jsonObjectData.getString("email_verified_at");
                                String otp_isverified = jsonObjectData.getString("otp_isverified");
                                String otp = jsonObjectData.getString("otp");
                                String created_at = jsonObjectData.getString("created_at");
                                String updated_at = jsonObjectData.getString("updated_at");


                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(ProfileActivity.this, resMsg, Toast.LENGTH_SHORT).show();
                                JSONObject jsonObjectData = response.getJSONObject("data");

                                String error = jsonObjectData.getString("error");
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

    private void getUserDataUpdate() {

        Map<String, File> multiPartFileMap = new HashMap<>();
        multiPartFileMap.put("image", filePath);

        AndroidNetworking.upload(APIs.USER_DATA_SAVE_API)
                .addMultipartParameter("userid", appPrefrence.getUserID())
                .addMultipartParameter("bussinessName", etv_businessName.getText().toString())
                .addMultipartParameter("bussinessType", etv_businessType.getText().toString())
                .addMultipartParameter("bussinessCategory", categoryId)
                .addMultipartParameter("address", etv_address.getText().toString())
                .addMultipartParameter("email", etv_email.getText().toString())
                .addMultipartParameter("about", etv_about.getText().toString())
                .addMultipartParameter("name", etv_contPerson.getText().toString())
                .addMultipartFile(multiPartFileMap)
                //  .addJSONObjectBody(request_data)
                .setTag("User Data Save API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.cancel();
                            ll_save_profile.setEnabled(true);
                            Log.d(TAG, "onResponse User Update: " + response.toString());

                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                                appPrefrence.setBizName(etv_businessName.getText().toString());
                                appPrefrence.setUsername(etv_contPerson.getText().toString());
                                Toast.makeText(ProfileActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                getUserDetail();
                                overridePendingTransition(0, 0);

                              /*  startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                                finish();*/

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                //  Toast.makeText(ProfileActivity.this, resMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException ex) {
                            progressDialog.cancel();
                            ll_save_profile.setEnabled(true);
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.cancel();
                        ll_save_profile.setEnabled(true);
                    }
                });
    }

    private void openBottomSheet() {

        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.camera_layout,
                        (ConstraintLayout) findViewById(R.id.profile_action));
        bottomSheetView.setOnClickListener(null);

        ImageView camera = (ImageView) bottomSheetView.findViewById(R.id.cam);
        ImageView gallery = (ImageView) bottomSheetView.findViewById(R.id.gal);

        camera.setOnClickListener(v -> {
            onProfileImageClick(true);
        });

        gallery.setOnClickListener(v -> {
            onProfileImageClick(false);
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void openShareBusinessCard() {

        bsDialogShareBusiness = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.business_card_shared_layout,
                        (ConstraintLayout) findViewById(R.id.businessCard_action));
        bottomSheetView.setOnClickListener(null);

        LinearLayout parentLyt = (LinearLayout) bottomSheetView.findViewById(R.id.parent_lyt);
        CircleImageView profile = (CircleImageView) bottomSheetView.findViewById(R.id.profile);
        TextView name = (TextView) bottomSheetView.findViewById(R.id.tvname);
        TextView mobile = (TextView) bottomSheetView.findViewById(R.id.tvmobile);
        Button btn_shareCard = (Button) bottomSheetView.findViewById(R.id.btnshareCard);
        TextView bizName = (TextView) bottomSheetView.findViewById(R.id.tvaddress);
        CardView visitingCard = (CardView) bottomSheetView.findViewById(R.id.card_visiting);

        if (!appPrefrence.getUsername().equals("") && !appPrefrence.getUsername().equals("null")) {
            name.setText(appPrefrence.getUsername());

        } else {
            name.setText("");

        }
        if (!appPrefrence.getBizName().equals("") && !appPrefrence.getBizName().equals("null")) {
            bizName.setText(appPrefrence.getBizName());

        } else {
            bizName.setText("");

        }


        mobile.setText(appPrefrence.getMobile());

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
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Business Card From Aaroo App ");
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

    private void openBusinessType() {

        bsDialogBusinessType = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.business_type_layout,
                        (ConstraintLayout) findViewById(R.id.businessType_action));
        bottomSheetView.setOnClickListener(null);

        ll_reataile_shop = (LinearLayout) bottomSheetView.findViewById(R.id.ll_retailer_shop);
        ll_distributor = (LinearLayout) bottomSheetView.findViewById(R.id.ll_wholesaler_dist_shop);
        ll_online_service = (LinearLayout) bottomSheetView.findViewById(R.id.ll_online_service_shop);
        ll_personal_use = (LinearLayout) bottomSheetView.findViewById(R.id.ll_personal_use);

        ll_reataile_shop.setOnClickListener(v -> {
            businessType = "Retail Shop";
            etv_businessType.setText(businessType);
            bsDialogBusinessType.cancel();
        });

        ll_distributor.setOnClickListener(v -> {
            businessType = "Wholesale/Distributor Shop";
            etv_businessType.setText(businessType);
            bsDialogBusinessType.cancel();
        });

        ll_personal_use.setOnClickListener(v -> {
            businessType = "Personal Use";
            etv_businessType.setText(businessType);
            bsDialogBusinessType.cancel();
        });
        ll_online_service.setOnClickListener(v -> {
            businessType = "Online Service";
            etv_businessType.setText(businessType);
            bsDialogBusinessType.cancel();
        });


        bsDialogBusinessType.setContentView(bottomSheetView);
        bsDialogBusinessType.show();
    }

    private void openCategory() {

        bsDialogCategory = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.category_type_layout,
                        (ConstraintLayout) findViewById(R.id.categoryType_action));
        bottomSheetView.setOnClickListener(null);

        catArrayList = new ArrayList<>();
        rv_category = bottomSheetView.findViewById(R.id.rv_category);

        getCategory(catArrayList);

        layoutManager = new GridLayoutManager(ProfileActivity.this, 4, RecyclerView.VERTICAL, false);
        rv_category.setLayoutManager(layoutManager);

        adapter = new CategoryTypeAdapter(catArrayList, ProfileActivity.this, ProfileActivity.this);
        rv_category.setAdapter(adapter);

        bsDialogCategory.setContentView(bottomSheetView);
        bsDialogCategory.show();
    }

    private void getCategory(ArrayList<CategoryModel> catArrayList) {

        AndroidNetworking.get(APIs.CATEGORY_API)
                .setTag("Category API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //progressDialog.cancel();
                            Log.d(TAG, "onResponse: " + response.toString());
                            if (response.get("success").equals(true)) {
                                //    rv_custom.setVisibility(View.VISIBLE);
                                //   lazzyLoader.setVisibility(View.GONE);
                                String resMsg = response.getString("msg");
                                //  Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();
                                JSONArray jsonArrayData = response.getJSONArray("data");

                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();


                                    model.id = jsonObjectData.getString("id");
                                    model.categoryName = jsonObjectData.getString("categoryName");
                                    model.image = jsonObjectData.getString("image");
                                    model.type = jsonObjectData.getInt("type");
                                    model.created_at = jsonObjectData.getString("created_at");
                                    model.updated_at = jsonObjectData.getString("updated_at");

                                    catArrayList.add(model);

                                }

                                adapter.notifyDataSetChanged();

                            } else if (response.get("success").equals(false)) {
                                //   lazzyLoader.setVisibility(View.VISIBLE);
                                //  rv_custom.setVisibility(View.GONE);
                                String resMsg = response.getString("msg");

                                //    Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();
                                JSONArray jsonArrayData = new JSONArray("data");

                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                    String error = jsonObjectData.getString("error");
                                }
                            }
                        } catch (JSONException ex) {
                            //  lazzyLoader.setVisibility(View.VISIBLE);
                            //  rv_custom.setVisibility(View.GONE);
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // progressDialog.cancel();
                        //  lazzyLoader.setVisibility(View.VISIBLE);
                        // rv_custom.setVisibility(View.GONE);
                        error.printStackTrace();
                    }
                });


    }

    public void getClickedCategory(int position, String id, String name) {

        //   etv_categoryType.setText(catClassMap.get(id));
        //  Log.d(TAG, "getClickedCategory: "+catClassMap.get(id));
        etv_categoryType.setText(name);
        // categoryId = id;
        categoryId = id;
        bsDialogCategory.cancel();

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
                        eprofileImg.setImageBitmap(thumb);
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

                bottomSheetDialog.cancel();
                Toast.makeText(ProfileActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Please Upload File", Toast.LENGTH_SHORT).show();
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
        eprofileImg.setImageBitmap(bitmap);
        file1Str = path;
        Log.d(TAG, "onActivityResult: gal" + file1Str);
        UploadTask uploadTask = new UploadTask();
        uploadTask.execute(new String[]{file1Str});
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pickiT.deleteTemporaryFile(this);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            pickiT.deleteTemporaryFile(this);
        }
    }

    void onProfileImageClick(boolean isTrue) {
        Dexter.withActivity(this)
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