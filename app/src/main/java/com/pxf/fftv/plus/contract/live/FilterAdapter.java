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

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    public static class FilterItem {
        public String name;
        public int id;
        public int type; // 1=football, 2=basketball
        public String value;

        public FilterItem(String name, int id, int type) {
            this.name = name;
            this.id = id;
            this.type = type;
            this.value = String.valueOf(id);
        }

        public FilterItem(String name, String value) {
            this.name = name;
            this.value = value;
            this.id = -1;
            this.type = 1; // default football
        }
    }

    private Context context;
    private List<FilterItem> items;
    private int selectedPosition = 0;
    private OnFilterSelectedListener listener;

    public interface OnFilterSelectedListener {
        void onFilterSelected(FilterItem item);
    }

    public FilterAdapter(Context context, List<FilterItem> items, OnFilterSelectedListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilterItem item = items.get(position);
        holder.textView.setText(item.name);

        // Highlight selected item
        if (position == selectedPosition) {
            holder.textView.setBackgroundColor(Color.parseColor("#FF4081"));
            holder.textView.setTextColor(Color.WHITE);
        } else {
            holder.textView.setBackgroundColor(Color.parseColor("#555555"));
            holder.textView.setTextColor(Color.parseColor("#CCCCCC"));
        }

        // Only trigger callback on click, not on focus
        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onFilterSelected(item);
            }
        });

        // Update visual highlight on focus but don't trigger callback
        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.textView.setBackgroundColor(Color.parseColor("#FF6090"));
            } else {
                if (holder.getAdapterPosition() == selectedPosition) {
                    holder.textView.setBackgroundColor(Color.parseColor("#FF4081"));
                } else {
                    holder.textView.setBackgroundColor(Color.parseColor("#555555"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_filter_item);
        }
    }
}
