package com.danhtran12797.thd.foodyapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.OrderDetailActivity;
import com.danhtran12797.thd.foodyapp.model.Order;
import com.danhtran12797.thd.foodyapp.model.OrderDetail;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    Context context;
    ArrayList<Order> arrayList;

    public OrderAdapter(Context context, ArrayList<Order> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_order, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = arrayList.get(position);
        ArrayList<OrderDetail> arrOrderDetail = (ArrayList<OrderDetail>) order.getOrderDetail();

        int sizeOrderDetail = arrOrderDetail.size();
        if (sizeOrderDetail > 1) {
            holder.txt_title.setText(arrOrderDetail.get(0).getNameProduct() + " và 0" + (sizeOrderDetail - 1) + " thực đơn khác");
        } else {
            holder.txt_title.setText(arrOrderDetail.get(0).getNameProduct());
        }

        holder.txt_date.setText(order.getDate());
        holder.txt_code_order.setText(System.currentTimeMillis() + order.getId());

        if (order.getStatus().equals("1")) {
            holder.txt_state.setText("Đang xử lý");
            holder.img_state.setImageResource(R.drawable.ic_cached);
        } else if (order.getStatus().equals("2")) {
            holder.txt_state.setText("Đã nhận được hàng");
            holder.img_state.setImageResource(R.drawable.ic_complete_order);
        } else {
            holder.txt_state.setText("Đã hủy");
            holder.img_state.setImageResource(R.drawable.ic_cancle);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_state;
        TextView txt_title;
        TextView txt_date;
        TextView txt_state;
        TextView txt_code_order;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_state = itemView.findViewById(R.id.img_state_order);
            txt_title = itemView.findViewById(R.id.txt_title_order);
            txt_date = itemView.findViewById(R.id.txt_date_order);
            txt_state = itemView.findViewById(R.id.txt_state_order);
            txt_code_order = itemView.findViewById(R.id.txt_code_order);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("order_detail", arrayList.get(getAdapterPosition()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }
}
