package com.android.aaroo.activity.intro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.aaroo.R;
import com.android.aaroo.activity.login.LoginActivity;
import com.android.aaroo.fragment.customer.CustomerModel;
import com.android.aaroo.helper.APIs;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IntroActivity extends AppCompatActivity {
    private static final int MAX_STEP = 4;

    private ViewPager  viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private Button btnNext;
    private String about_title_array[] = {
            "Ready to Travel",
            "Pick the Ticket",
            "Flight to Destination",
            "Enjoy Holiday"
    };
    private String about_description_array[] = {
            "Choose your destination, plan Your trip. Pick the best place for Your holiday",
            "Select the day, pick Your ticket. We give you the best prices. We guarantee!",
            "Safe and Comfort flight is our priority. Professional crew and services.",
            "Enjoy your holiday, Dont forget to feel the moment and take a photo!",
    };
    private int about_images_array[] = {
            R.drawable.ic_b2b1,
            R.drawable.ic_b2b,
            R.drawable.ic_b2b2,
            R.drawable.ic_team
    };

    ArrayList<IntroModel> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initComponent();
    }

    private void initComponent() {

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        btnNext = (Button) findViewById(R.id.btn_next);

        arrayList = new ArrayList<>();

        getIndroSliderData();
        // adding bottom dots
        bottomProgressDots(0);



        btnNext.setOnClickListener(v -> {
            startActivity(new Intent (IntroActivity.this, LoginActivity.class));
            finish();
        });

      /*  btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem() + 1;
                if (current < MAX_STEP) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                   // finish();
                }
            }
        });*/


    }

    private void getIndroSliderData() {
        AndroidNetworking.get(APIs.INTRO_API)
                .setTag("Intro All Data API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //progressDialog.cancel();
                            Log.d("Intro", "onResponse: "+response.toString());
                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                                //  Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();
                                JSONArray jsonArrayData = response.getJSONArray("data");

                                for (int i = 0; i < jsonArrayData.length(); i++) {

                                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                    IntroModel model =new IntroModel();

                                    model.id=jsonObjectData.getString("id");
                                    model.title = jsonObjectData.getString("title");
                                    model.description = jsonObjectData.getString("description");
                                    model.image = jsonObjectData.getString("image");
                                    String created_at = jsonObjectData.getString("created_at");
                                    String updated_at = jsonObjectData.getString("updated_at");

                                    arrayList.add(model);

                                }
                            //    myViewPagerAdapter.notifyDataSetChanged();

                                myViewPagerAdapter = new MyViewPagerAdapter(arrayList);
                                viewPager.setAdapter(myViewPagerAdapter);

                                viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                //    Toast.makeText(getActivity(), resMsg, Toast.LENGTH_SHORT).show();
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
                        error.printStackTrace();
                    }
                });

    }

    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.orange_400), PorterDuff.Mode.SRC_IN);
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);

           /* if (position == about_title_array.length - 1) {
                btnNext.setText("Verify Mobile");
                btnNext.setBackgroundColor(getResources().getColor(R.color.orange_400));
                btnNext.setTextColor(Color.WHITE);

            } else {
                btnNext.setText("Verify Mobile");
                btnNext.setBackgroundColor(getResources().getColor(R.color.grey_10));
                btnNext.setTextColor(getResources().getColor(R.color.grey_90));
            }*/
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        ArrayList<IntroModel> arrayList;
        public MyViewPagerAdapter(ArrayList<IntroModel> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            IntroModel model = arrayList.get(position);

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            String image = model.getImage();

            View view = layoutInflater.inflate(R.layout.item_stepper_wizard, container, false);

            ((TextView) view.findViewById(R.id.title)).setText(model.getTitle());
            ((TextView) view.findViewById(R.id.description)).setText(model.getDescription());
            Picasso.get().load(APIs.BASE_IMAGE_URL+image).into(((ImageView) view.findViewById(R.id.image)));

           container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    finish();

    }
}
