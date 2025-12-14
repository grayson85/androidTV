package com.pxf.fftv.plus.contract.live;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.bean.M3uChannel;

import java.util.List;

public class M3uChannelAdapter extends RecyclerView.Adapter<M3uChannelAdapter.ViewHolder> {

    private Context context;
    private List<M3uChannel> channels;
    private OnChannelClickListener listener;
    private int selectedPosition = -1;

    public interface OnChannelClickListener {
        void onChannelClick(M3uChannel channel, int position);

        void onChannelFocus(M3uChannel channel, int position);
    }

    public M3uChannelAdapter(Context context, List<M3uChannel> channels, OnChannelClickListener listener) {
        this.context = context;
        this.channels = channels;
        this.listener = listener;
    }

    public void setChannels(List<M3uChannel> channels) {
        this.channels = channels;
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int old = selectedPosition;
        selectedPosition = position;
        if (old >= 0)
            notifyItemChanged(old);
        if (position >= 0)
            notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_m3u_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        M3uChannel channel = channels.get(position);
        holder.tvName.setText(channel.getName());

        // 加载 Logo
        if (channel.getLogo() != null && !channel.getLogo().isEmpty()) {
            Glide.with(context)
                    .load(channel.getLogo())
                    .placeholder(R.drawable.ic_tv_placeholder)
                    .error(R.drawable.ic_tv_placeholder)
                    .into(holder.ivLogo);
        } else {
            holder.ivLogo.setImageResource(R.drawable.ic_tv_placeholder);
        }

        // 选中状态
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FF6090"));
            holder.tvName.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.tvName.setTextColor(Color.parseColor("#DDDDDD"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                setSelectedPosition(position);
                listener.onChannelClick(channel, position);
            }
        });

        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.itemView.setBackgroundColor(Color.parseColor("#FF6090"));
                holder.tvName.setTextColor(Color.WHITE);
                if (listener != null) {
                    listener.onChannelFocus(channel, position);
                }
            } else if (position != selectedPosition) {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                holder.tvName.setTextColor(Color.parseColor("#DDDDDD"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return channels != null ? channels.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.iv_logo);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
