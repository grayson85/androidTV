package com.pxf.fftv.plus.contract.live;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;

import java.util.ArrayList;
import java.util.List;

public class M3uGroupAdapter extends RecyclerView.Adapter<M3uGroupAdapter.ViewHolder> {

    private Context context;
    private List<String> groups;
    private OnGroupClickListener listener;
    private int selectedPosition = 0;

    public interface OnGroupClickListener {
        void onGroupClick(String group, int position);
    }

    public M3uGroupAdapter(Context context, List<String> groups, OnGroupClickListener listener) {
        this.context = context;
        this.groups = groups != null ? groups : new ArrayList<>();
        this.listener = listener;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups != null ? groups : new ArrayList<>();
        selectedPosition = 0;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_m3u_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String group = groups.get(position);
        holder.tvName.setText(group);

        // 选中状态
        if (position == selectedPosition) {
            holder.tvName.setBackgroundColor(Color.parseColor("#FF6090"));
            holder.tvName.setTextColor(Color.WHITE);
        } else {
            holder.tvName.setBackgroundColor(Color.TRANSPARENT);
            holder.tvName.setTextColor(Color.parseColor("#AAAAAA"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                setSelectedPosition(position);
                listener.onGroupClick(group, position);
            }
        });

        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.tvName.setBackgroundColor(Color.parseColor("#FF6090"));
                holder.tvName.setTextColor(Color.WHITE);
            } else if (position != selectedPosition) {
                holder.tvName.setBackgroundColor(Color.TRANSPARENT);
                holder.tvName.setTextColor(Color.parseColor("#AAAAAA"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_group_name);
        }
    }
}
