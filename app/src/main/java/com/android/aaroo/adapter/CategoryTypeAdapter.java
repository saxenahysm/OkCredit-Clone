package com.android.aaroo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.aaroo.R;
import com.android.aaroo.activity.profile.ProfileActivity;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.model.CategoryModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryTypeAdapter extends RecyclerView.Adapter<CategoryTypeAdapter.ViewHolder>{
    public ArrayList<CategoryModel> categoryList = new ArrayList<>();
    public Context context;
    ProfileActivity profileActivity;
    public CategoryTypeAdapter(ArrayList<CategoryModel> categoryList, Context context,ProfileActivity profileActivity) {
        this.categoryList = categoryList;
        this.context = context;
        this.profileActivity = profileActivity;

    }

    @NonNull
    @Override
    public CategoryTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);

        return new CategoryTypeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryTypeAdapter.ViewHolder holder, int position) {
        CategoryModel categoryModel = categoryList.get(position);
            String catImage = categoryModel.getImage();

            if (!catImage.equals("")) {

                Picasso.get().load(APIs.BASE_IMAGE_URL + catImage).into(holder.catImage);
            }

        holder.name.setText(categoryModel.getCategoryName());

        holder.catImage.setOnClickListener(v -> {
            profileActivity.getClickedCategory(position,categoryModel.getId(),categoryModel.getCategoryName());
        });

        holder.ll_parent.setOnClickListener(v -> {
            profileActivity.getClickedCategory(position,categoryModel.getId(),categoryModel.getCategoryName());
        });

    }

    @Override
    public int getItemCount() {
        if (categoryList != null) {
            return categoryList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout ll_parent;
        CircleImageView catImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_parent = itemView.findViewById(R.id.ll_parent);

            name = itemView.findViewById(R.id.tvCatName);
            catImage = itemView.findViewById(R.id.imageCat);

        }
    }
}
