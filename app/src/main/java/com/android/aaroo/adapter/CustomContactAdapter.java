package com.android.aaroo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.aaroo.R;
import com.android.aaroo.activity.customer.AddCustomer;
import com.android.aaroo.model.ContactModel;

import java.util.ArrayList;
import java.util.List;

public class CustomContactAdapter extends RecyclerView.Adapter<CustomContactAdapter.ViewHolder> implements Filterable {
    List<ContactModel> arrayList;
    List<ContactModel> filteredArrayList;
   public  Context context;
   AddCustomer addCustomer;
    public CustomContactAdapter(AddCustomer addCustomer, Context context, ArrayList<ContactModel> arrayList) {
        this.addCustomer = addCustomer;
        this.context = context;
        this.arrayList = arrayList;
        filteredArrayList = new ArrayList<>(arrayList);
       notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list,parent,false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ContactModel contactModel = arrayList.get(position);
        holder.tvName.setText(contactModel.getName());
        holder.tvNumber.setText(contactModel.getNumber());

        holder.ll_parent.setOnClickListener(v -> {
        addCustomer.getContactData(contactModel.getName(),contactModel.getNumber());

        });

    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvNumber;
        LinearLayout ll_parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_mobile);
            ll_parent = itemView.findViewById(R.id.ll_parent);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<ContactModel> filteredList = new ArrayList<>();
                String searchItemString = charSequence.toString().toLowerCase().trim();
                if (searchItemString.isEmpty()) {
                    filteredList.addAll(filteredArrayList);
                } else {
                    for (ContactModel filteredDonorList : filteredArrayList) {
                        if (filteredDonorList.getName().toLowerCase().contains(searchItemString.toLowerCase()) || filteredDonorList.getNumber().toLowerCase().contains(searchItemString.toLowerCase())) {
                            filteredList.add(filteredDonorList);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try {
                    arrayList.clear();
                    arrayList.addAll((List<ContactModel>) filterResults.values);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}