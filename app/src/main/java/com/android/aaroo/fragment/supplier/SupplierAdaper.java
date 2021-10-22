package com.android.aaroo.fragment.supplier;

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

public class SupplierAdaper extends RecyclerView.Adapter<SupplierAdaper.ViewHolder>{
    public ArrayList<SupplierModel> supplierList = new ArrayList<>();
    public Context context;
SupplierFragment supplierFragment;
    public SupplierAdaper(ArrayList<SupplierModel> supplierList, Context context,SupplierFragment supplierFragment) {
        this.supplierList = supplierList;
        this.context = context;
        this.supplierFragment = supplierFragment;
    }

    @NonNull
    @Override
    public SupplierAdaper.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_layout, parent, false);

        return new SupplierAdaper.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierAdaper.ViewHolder holder, int position) {
        SupplierModel supplierModel = supplierList.get(position);
          String profImage = supplierModel.getProfile();


        if (supplierModel.getAmount() < 0.0) {
            // negative Due
            double minusAmount = (-1)*(supplierModel.getAmount());
            holder.amount.setTextColor(context.getResources().getColor(R.color.red_700));
            holder.amount.setText("₹ "+String.valueOf(minusAmount));
            holder.status.setText("DUE");


        } else if (supplierModel.getAmount() == 0.0){
            // it's a positive Advance
            holder.amount.setTextColor(context.getResources().getColor(R.color.green_700));
            holder.amount.setText("₹ "+String.valueOf(supplierModel.getAmount()));
            holder.status.setText("DUE");
        }
        else {
            // it's a positive Advance
            holder.amount.setTextColor(context.getResources().getColor(R.color.green_700));
            holder.amount.setText("₹ "+String.valueOf(supplierModel.getAmount()));
            holder.status.setText("ADVANCE");
        }
        //   holder.profile.setImageResource(customerModel.getProfile());
        if(!profImage.equals("") && !profImage.equals(null)) {

            Picasso.get().load(APIs.BASE_IMAGE_URL+profImage).placeholder(R.drawable.ic_customer_profile).into(holder.profile);
        }
        else {
            holder.profile.setImageResource(R.drawable.ic_customer_profile);
        }

        holder.name.setText(supplierModel.getName());
     //   holder.lastMsg.setText(supplierModel.getLastMsg());
        holder.ll_parent.setOnClickListener(v -> {

            supplierFragment.itemClick(supplierModel.getSid(),supplierModel.getName(),supplierModel.getProfile()
            ,supplierModel.getCustomerType(),supplierModel.getMobile(),supplierModel.getAmount());

        });

    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,lastMsg,amount,status;
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
