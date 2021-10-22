package com.android.aaroo.activity.supplier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.aaroo.MainActivity;
import com.android.aaroo.R;
import com.android.aaroo.adapter.ContactAdapter;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.android.aaroo.model.ContactModel;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class AddSupplier extends AppCompatActivity {
    RecyclerView rv_contactList;
    ArrayList<ContactModel> arrayList =new ArrayList<>();
    ContactAdapter adapter;
    BottomSheetDialog bottomSheetDialogContactData;
    AppPrefrence appPrefrence;
    ProgressDialog progressDialog;
    public static final String TAG = AddSupplier.class.getSimpleName();
    Button btnSubmit;
    private static final String[] PROJECTION = new String[]{

            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supplier);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rv_contactList = findViewById(R.id.rv_list);
        appPrefrence = new AppPrefrence(this);
        progressDialog = new ProgressDialog(this);
        checkPermission();
    }

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(AddSupplier.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(AddSupplier.this,new String[]{Manifest.permission.READ_CONTACTS},100);
        }
        else {
            getContactList();
        }

    }

    private void getContactList() {

        ContentResolver cr = getContentResolver();

        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            HashSet<String> mobileNoSet = new HashSet<String>();
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");
                    if (!mobileNoSet.contains(number)) {
                        ContactModel model = new ContactModel();
                        model.setName(name);
                        model.setNumber(number);
                        arrayList.add(model);
                        // arrayList.add(new ContactModel(name, number));
                        mobileNoSet.add(number);

                    }
                }
            } finally {
                cursor.close();
            }

            rv_contactList.setLayoutManager(new LinearLayoutManager(this));
            adapter= new ContactAdapter(AddSupplier.this,AddSupplier.this, arrayList);
            rv_contactList.setAdapter(adapter);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==100 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            getContactList();
        }else {
            Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();

            checkPermission();
        }

    }


    public void getContactData(String name, String number) {

        getBottomSheetContactData(name,number);

    }

    private void getBottomSheetContactData(String name, String number) {

        bottomSheetDialogContactData = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.contact_data_layout,
                        (ConstraintLayout) findViewById(R.id.data_cons));
        bottomSheetView.setOnClickListener(null);

        EditText edtName = (EditText) bottomSheetView.findViewById(R.id.edt_name);
        EditText edtNumber = (EditText) bottomSheetView.findViewById(R.id.edt_number);
        btnSubmit = (Button) bottomSheetView.findViewById(R.id.card_view_submit);

        edtName.setText(name);
        edtNumber.setText(number);

        btnSubmit.setOnClickListener(v -> {

            String nameStr = edtName.getText().toString();
            String numberStr = edtNumber.getText().toString();

            getSupplierAdd(nameStr,numberStr);
            bottomSheetDialogContactData.cancel();

        });


        bottomSheetDialogContactData.setContentView(bottomSheetView);
        bottomSheetDialogContactData.show();
    }


    private void getSupplierAdd(String name,String number) {

        JSONObject request_data = new JSONObject();
        try {
            //  request_data.put("createdBy", appPrefrence.getUserID());
            request_data.put("createdBy", appPrefrence.getUserID());
            request_data.put("customerName",name);
            request_data.put("mobile",number);
            request_data.put("customerType","2");

            Log.d(TAG, "getCustomer Add: "+request_data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(APIs.SUPPLIER_ADD_API)
                .addJSONObjectBody(request_data)
                .setTag("Supplier Add API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.cancel();
                            btnSubmit.setEnabled(true);
                            Log.d(TAG, "onResponse: "+response.toString());

                            if(response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                               // Toast.makeText(AddSupplier.this, resMsg, Toast.LENGTH_SHORT).show();

                                JSONArray jsonArrayData = response.getJSONArray("data");

                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);

                                    String id =jsonObjectData.getString("id");
                                    String name = jsonObjectData.getString("customerName");
                                    String mobile = jsonObjectData.getString("mobile");
                                    String customerType = jsonObjectData.getString("customerType");
                                    String createdBy = jsonObjectData.getString("createdBy");
                                    String created_at = jsonObjectData.getString("created_at");
                                    String updated_at = jsonObjectData.getString("updated_at");

                                }

                                /*Intent returnIntent = new Intent();
                                setResult(SupplierFragment.RESULT_CODE,returnIntent);
                                finish();*/

                              /*  Intent returnIntent = new Intent();
                                setResult(RESULT_OK,returnIntent);
                                finish();*/
                                Intent intent  = new Intent(AddSupplier.this, MainActivity.class);
                                intent.putExtra("FRAGMENT_ID", 1);
                                startActivity(intent);
                                finish();

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(AddSupplier.this, resMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException ex) {
                            progressDialog.cancel();
                            btnSubmit.setEnabled(true);
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.cancel();
                        btnSubmit.setEnabled(true);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
//        searchView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);///not working
        searchView.setQueryHint("Search...");
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}