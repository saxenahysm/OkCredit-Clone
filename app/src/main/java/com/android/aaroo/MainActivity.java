package com.android.aaroo;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.aaroo.activity.account.AccountStatement;
import com.android.aaroo.activity.helpsupport.HelpAndSupportActivity;
import com.android.aaroo.activity.intro.IntroActivity;
import com.android.aaroo.activity.profile.ProfileActivity;
import com.android.aaroo.activity.setting.SettingActivity;
import com.android.aaroo.activity.supplier.AddSupplier;
import com.android.aaroo.fragment.customer.CustomerFragment;
import com.android.aaroo.fragment.supplier.SupplierFragment;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    LinearLayout ll_header_profile;
    CircleImageView profile;
    TextView name_tv, contact_tv;
    AppPrefrence appPrefrence;
    public static final int RESULT_CODE = 12;
    public static final int REQUEST_CODE = 11;
    public static int fragmentId = 0;
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appPrefrence = new AppPrefrence(this);
        toolbar = findViewById(R.id.toolbarDashboard);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("AAROO");
        toolbar.setTitleTextAppearance(this, R.style.AppToolbarTextAppearance);
        setSupportActionBar(toolbar);

        getUserDetail();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
//        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.RED));
        View nav = navigationView.getHeaderView(0);

        ll_header_profile = nav.findViewById(R.id.ll_profile);
        profile = nav.findViewById(R.id.profile);
        name_tv = nav.findViewById(R.id.tv_name);
        contact_tv = nav.findViewById(R.id.tv_mobile);

        if (!appPrefrence.getUsername().equals("") && !appPrefrence.getUsername().equals("null")) {
            name_tv.setText(appPrefrence.getUsername());

        } else {
            // name_tv.setText("");
            if (!appPrefrence.getBizName().equals("") && !appPrefrence.getBizName().equals("null")) {
                name_tv.setText(appPrefrence.getBizName());

            }
        }

        if (!appPrefrence.getMobile().equals("") && !appPrefrence.getMobile().equals("null")) {

            contact_tv.setText("+91" + appPrefrence.getMobile());
        } else {
            contact_tv.setText("");
        }

        if (!appPrefrence.getUserProfile().equals("") && !appPrefrence.getUserProfile().equals("null")) {
            Picasso.get().load(APIs.BASE_IMAGE_URL + appPrefrence.getUserProfile()).into(profile);
        }


        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryDark));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        tabbed();

        ll_header_profile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

    }

    private void tabbed() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentId = getIntent().getIntExtra("FRAGMENT_ID", 0);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {

        TabbedAdapter adapter = new TabbedAdapter(getSupportFragmentManager());
        adapter.addFragment(new CustomerFragment(), "Customer");
        adapter.addFragment(new SupplierFragment(), "Supplier");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(fragmentId);

    }

    class TabbedAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public TabbedAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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

                                JSONObject jsonObjectData = response.getJSONObject("data");

                                String id = jsonObjectData.getString("id");
                                String name = jsonObjectData.getString("name");
                                if (!name.equals("")) {
                                    appPrefrence.setUsername(name);
                                    if (!name.equals("null")) {
                                        appPrefrence.setUsername(name);
                                    }

                                }
                                String mobile = jsonObjectData.getString("mobile");
                                if (!mobile.equals("")) {
                                    appPrefrence.setMobile(mobile);
                                    if (!mobile.equals("null")) {
                                        appPrefrence.setMobile(mobile);
                                    }

                                }
                                String email = jsonObjectData.getString("email");

                                String about = jsonObjectData.getString("about");

                                String image = jsonObjectData.getString("image");
                                if (!image.equals("")) {
                                    appPrefrence.setUserProfile(image);
                                }
                                String address = jsonObjectData.getString("address");

                                String bussinessName = jsonObjectData.getString("bussinessName");

                                if (!bussinessName.equals("")) {
                                    appPrefrence.setBizName(bussinessName);
                                    if (!bussinessName.equals("null")) {

                                        appPrefrence.setBizName(bussinessName);
                                    }

                                }

                                String bussinessType = jsonObjectData.getString("bussinessType");

                                String bussinessCategory = jsonObjectData.getString("bussinessCategory");

                                String email_verified_at = jsonObjectData.getString("email_verified_at");
                                String otp_isverified = jsonObjectData.getString("otp_isverified");
                                String otp = jsonObjectData.getString("otp");
                                String created_at = jsonObjectData.getString("created_at");
                                String updated_at = jsonObjectData.getString("updated_at");

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(MainActivity.this, resMsg, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
       /* Drawable drawable = menu.findItem(R.id.menu_account).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.grey_600));
        menu.findItem(R.id.menu_account).setIcon(drawable);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_account:
                Intent i = new Intent(getApplicationContext(), AccountStatement.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        removeColor(navigationView);
        item.setChecked(true);
        int id = item.getItemId();
        if (id == R.id.nav_account) {
            Intent i = new Intent(getApplicationContext(), AccountStatement.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

            navigationView.getMenu().getItem(0).setChecked(false);

        } else if (id == R.id.nav_share) {

            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "AAROO share with your friend");
            try {
                Objects.requireNonNull(this).startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp")));
            }
            navigationView.getMenu().getItem(1).setChecked(false);


        } else if (id == R.id.nav_setting) {
            Intent i = new Intent(getApplicationContext(), SettingActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            navigationView.getMenu().getItem(2).setChecked(false);
        } else if (id == R.id.nav_help) {
            Intent i = new Intent(getApplicationContext(), HelpAndSupportActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            navigationView.getMenu().getItem(3).setChecked(false);

        } else if (id == R.id.nav_help) {
            alertDialog();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }


    private void removeColor(NavigationView view) {
        for (int i = 0; i < view.getMenu().size(); i++) {
            MenuItem item = view.getMenu().getItem(i);
            item.setChecked(false);
        }
    }

    private void alertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout !");
        builder.setMessage("Are you sure to Logout ");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                appPrefrence.setIsLogin("0");
                appPrefrence.setUsername("");
                appPrefrence.setUserProfile("");
                appPrefrence.setUserID("");
                appPrefrence.setDeviceId("");
                appPrefrence.setPinset("");
                appPrefrence.setMobile("");
                //  appPrefrence.setBizName("");
                appPrefrence.setPinsetStatus("");
                appPrefrence.setFingerLock("");
                appPrefrence.setSmsStatus("");
                dialog.cancel();
                startActivity(new Intent(MainActivity.this, IntroActivity.class));
                finish();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {

            if (pressedTime + 2000 > System.currentTimeMillis()) {
                // super.onBackPressed();
                //finish();
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    // If there are back-stack entries, leave the FragmentActivity
                    // implementation take care of them.
                    getSupportFragmentManager().popBackStack();
                } else {
                    // Otherwise, ask user if he wants to leave :)
                    super.onBackPressed();
                    finish();
                }
            } else {

                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();

            }
            pressedTime = System.currentTimeMillis();
        }
    }
}