package com.android.aaroo.fragment.customer;

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
import com.android.aaroo.activity.transactioncustomer.CustomerTransactionRoom;
import com.android.aaroo.activity.transactionsupplier.SupplierTransactionRoom;
import com.android.aaroo.helper.APIs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
    public ArrayList<CustomerModel> customerList = new ArrayList<>();
    public Context context;
    CustomerFragment customerFragment;

    public CustomerAdapter(ArrayList<CustomerModel> customerList, Context context, CustomerFragment customerFragment) {
        this.customerList = customerList;
        this.context = context;
        this.customerFragment = customerFragment;
    }

    @NonNull
    @Override
    public CustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.ViewHolder holder, int position) {
        CustomerModel customerModel = customerList.get(position);
        String profImage = customerModel.getProfile();

        if (customerModel.getAmount() < 0.0) {
            // negative Due
            double minusAmount = (-1) * (customerModel.getAmount());

            holder.amount.setTextColor(context.getResources().getColor(R.color.red_700));
            holder.amount.setText("₹ " + String.valueOf(minusAmount));
            holder.status.setText("DUE");


        } else if (customerModel.getAmount() == 0.0) {
            // it's a positive Advance
            holder.amount.setTextColor(context.getResources().getColor(R.color.green_700));
            holder.amount.setText("₹ " + String.valueOf(customerModel.getAmount()));
            holder.status.setText("DUE");
        } else {
            // it's a positive Advance
            holder.amount.setTextColor(context.getResources().getColor(R.color.green_700));
            holder.amount.setText("₹ " + String.valueOf(customerModel.getAmount()));
            holder.status.setText("ADVANCE");
        }
        //   holder.profile.setImageResource(customerModel.getProfile());
        if (!profImage.equals("") && !profImage.equals(null)) {

            Picasso.get().load(APIs.BASE_IMAGE_URL + profImage).placeholder(R.drawable.ic_customer_profile).into(holder.profile);
        } else {
            holder.profile.setImageResource(R.drawable.ic_customer_profile);
        }
        holder.name.setText(customerModel.getName());

        holder.ll_parent.setOnClickListener(v -> {

            customerFragment.itemClicked(customerModel.getCid(), customerModel.getName()
                    , customerModel.getProfile(), customerModel.getCustomerType(), customerModel.getMobile(),
                    customerModel.getAmount());

        });

        // holder.lastMsg.setText(customerModel.getLastMsg());
        // holder.status.setText(customerModel.getStatus());
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, lastMsg, amount, status;
        LinearLayout ll_parent;
        CircleImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_parent = itemView.findViewById(R.id.ll_parent);
            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            lastMsg = itemView.findViewById(R.id.msg);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
