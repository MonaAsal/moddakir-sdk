package com.moddakir.call.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.moddakir.call.view.widget.TextViewUniqueLight;
import com.moddakir.call.Model.ConnectingBanner;
import com.moddakir.call.R;
import java.util.List;

public class ConnectingScreenAdapter extends RecyclerView.Adapter<ConnectingScreenAdapter.PaymentMethodViewHolder> {
    List<ConnectingBanner> connectingBanner;
    private Context context;


    public ConnectingScreenAdapter(Context context, List<ConnectingBanner> connectingBanner) {
        this.connectingBanner = connectingBanner;
        this.context = context;
    }

    @NonNull
    @Override
    public PaymentMethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connecting_item, parent, false);
        PaymentMethodViewHolder holder = new PaymentMethodViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodViewHolder holder, int position) {
        holder.bind(connectingBanner.get(position));


    }

    @Override
    public int getItemCount() {
        return connectingBanner.size();
    }


    public interface OnPaymentSelectedListener {
        public void onPaymentSelected(int position);
    }

    public class PaymentMethodViewHolder extends RecyclerView.ViewHolder {
        ImageView connecting_iv;
        TextViewUniqueLight connecting_text;
        public PaymentMethodViewHolder(@NonNull View itemView) {
            super(itemView);
            connecting_iv = itemView.findViewById(R.id.connecting_iv);
            connecting_text = itemView.findViewById(R.id.connecting_text);
        }

        public void bind(ConnectingBanner connectingBanner) {
            try {
                Glide.with(context)
                        .load(connectingBanner.getUrl())
                        .into( connecting_iv);
            }catch (Exception e){

            }

            connecting_text.setText(connectingBanner.getTitle());
        }


    }
}
