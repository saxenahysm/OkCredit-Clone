package com.android.aaroo.activity.transactionsupplier;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.aaroo.R;
import com.android.aaroo.activity.transactioncustomer.CustomerTransactionModel;
import com.android.aaroo.helper.APIs;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SupplierTransactionListAdapter extends RecyclerView.Adapter<SupplierTransactionListAdapter.ViewHolder> {
    SupplierTransactionRoom supplierTransactionRoom;
    private ArrayList<SupplierTransactionModel> list;
    private Context context;

    private double tempAmount = 0.0;
    String advance = " ADVANCE";
    String due = " DUE";
    private static String paymentStatus = "";

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    public SupplierTransactionListAdapter(SupplierTransactionRoom supplierTransactionRoom, ArrayList<SupplierTransactionModel> list, Context context) {
        this.list = list;
        this.context = context;
        this.supplierTransactionRoom = supplierTransactionRoom;

    }

    @NonNull
    @Override
    public SupplierTransactionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_txn_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_txn_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        SupplierTransactionModel customerModel = list.get(position);

        String billImage = customerModel.getBillImage();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        holder.tv_time.setText(customerModel.getPaymentTime() + "\n" + customerModel.getPaymentDate());
        holder.tv_amount.setText("₹ " + String.valueOf(customerModel.getAmount()));
        holder.tv_calculate_amount.setText("₹ 0 DUE");

        if (!customerModel.getAddNote().equals("") && !customerModel.getAddNote().equals("null")) {
            holder.tv_msg.setText(list.get(position).getAddNote());
            holder.tv_msg.setVisibility(View.VISIBLE);
        }

        if (!billImage.equals("") && !billImage.equals("null")) {
            Picasso.get().load(APIs.BASE_IMAGE_URL + billImage).into(holder.billImage);
        } else {
            holder.billImage.setVisibility(View.GONE);
        }

        // PaymentMethod_1=cr-due
        // PaymentMethod_2=dr-advance

        if (position == 0) {
            tempAmount = customerModel.getAmount();
            if (customerModel.getPaymentMethod() == 1) {
                holder.tv_calculate_amount.setText("₹" + customerModel.getAmount() + due);
                paymentStatus = due;
            } else if (customerModel.getPaymentMethod() == 2) {
                holder.tv_calculate_amount.setText("₹" + customerModel.getAmount() + advance);
                paymentStatus = advance;
            }
        }
        if (position > 0) {
            if (customerModel.getPaymentMethod() == 1 && paymentStatus.equals(due)) {
                tempAmount = tempAmount + customerModel.getAmount();
                holder.tv_calculate_amount.setText("₹" + tempAmount + paymentStatus);
            } else if (customerModel.getPaymentMethod() == 1 && paymentStatus.equals(advance)) {
                tempAmount = tempAmount - customerModel.getAmount();
                if (tempAmount < 0) {
                    tempAmount = tempAmount * (-1);
                }
                if (tempAmount > customerModel.getAmount()) {
                    paymentStatus = advance;
                } else {
                    paymentStatus = due;
                }
                holder.tv_calculate_amount.setText("₹" + tempAmount + paymentStatus);
            } else if (customerModel.getPaymentMethod() == 2 && paymentStatus.equals(advance)) {
                tempAmount = tempAmount + customerModel.getAmount();
                paymentStatus = advance;
                holder.tv_calculate_amount.setText("₹" + tempAmount + paymentStatus);
            } else if (customerModel.getPaymentMethod() == 2 && paymentStatus.equals(due)) {

                if (tempAmount > customerModel.getAmount()) {
                    paymentStatus = due;
                } else {
                    paymentStatus = advance;
                }
                tempAmount = tempAmount - customerModel.getAmount();
                if (tempAmount < 0) {
                    tempAmount = tempAmount * (-1);
                }
                holder.tv_calculate_amount.setText("₹" + tempAmount + paymentStatus);
            }
        }
        holder.itemView.setOnClickListener(v -> {
//            if (currentDate.equals(customerModel.getPaymentDate())) {
            supplierTransactionRoom.bottomSheetEditTransaction(
                    customerModel.getPaymentMethod(),
                    customerModel.getAmount(),
                    customerModel.getAddNote(),
                    customerModel.getPaymentDate(),
                    customerModel.getPaymentTime(),
                    customerModel.getBillImage(),
                    customerModel.getTxnId(),
                    customerModel.getCustomerID(),
                    customerModel.getBillImage()
            );
//            }
        });
        if (position == list.size() - 1) {
            supplierTransactionRoom.setReminderAmount(tempAmount);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.size() != 0) {
            if (list.get(position).paymentMethod == 2) {
                return MSG_TYPE_RIGHT;
            }
            if (list.get(position).paymentMethod == 1) {
                return MSG_TYPE_LEFT;
            } else {
                return MSG_TYPE_LEFT;
            }
        } else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_calculate_amount, tv_time, tv_amount, tv_msg;
        ImageView billImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            billImage = itemView.findViewById(R.id.imageBill);
            tv_amount = itemView.findViewById(R.id.tvamount);
            tv_msg = itemView.findViewById(R.id.tvmsg);
            tv_time = itemView.findViewById(R.id.tvtime);
            tv_calculate_amount = itemView.findViewById(R.id.tvcalculate_amount);
        }
    }
}
