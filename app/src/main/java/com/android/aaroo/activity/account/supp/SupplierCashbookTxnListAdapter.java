package com.android.aaroo.activity.account.supp;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.aaroo.R;

import java.util.ArrayList;

public class SupplierCashbookTxnListAdapter extends RecyclerView.Adapter<SupplierCashbookTxnListAdapter.ViewHolder>{

    private ArrayList<SupplierTxnModel> list;
    private Context context;

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    public SupplierCashbookTxnListAdapter(ArrayList<SupplierTxnModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SupplierCashbookTxnListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_LEFT) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supp_credit_txn_layout, parent, false);

        } else {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supp_payment_txn_layout, parent, false);

        }
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        SupplierTxnModel model = list.get(position);
        String billImage = model.getBillImage();

        holder.tv_time.setText(model.getPaymentTime());
        holder.tv_amount.setText("₹ "+String.valueOf(model.getAmount()));
        holder.tv_date.setText(model.getPaymentDate()+" ");

        holder.tv_name.setText(model.getCustomName());

        if (!model.getAddNote().equals("")&&!model.getAddNote().equals("null")){
            holder.tv_msg.setText(list.get(position).getAddNote());
            holder.tv_msg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.size() != 0) {
                if (list.get(position).paymentMethod==2){

                    return MSG_TYPE_RIGHT;
            }
            if (list.get(position).paymentMethod==1) {
                return MSG_TYPE_LEFT;
            }
            else {
                return MSG_TYPE_LEFT;
            }
        } else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_date, tv_time,tv_amount,tv_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_amount = itemView.findViewById(R.id.tvamount);
            tv_msg = itemView.findViewById(R.id.tvmsg);
            tv_time = itemView.findViewById(R.id.tvtime);
            tv_date = itemView.findViewById(R.id.tvdate);
            tv_name = itemView.findViewById(R.id.tvname);
        }
    }

}
