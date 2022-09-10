package com.pxf.fftv.plus.contract.live;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder> {

    enum TYPE {
        TITLE,
        SUB_TITLE
    }

    private Context context;

    private ArrayList<String> dataList;

    private TYPE type;

    private OnClickListener listener;

    public TitleAdapter(Context context, ArrayList<String> dataList, TYPE type, OnClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.type = type;
        this.listener = listener;
    }

    public void refresh(ArrayList<String> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tv_live_title_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_live_item_title.setText(dataList.get(position));
        if (type == TYPE.TITLE) {
            holder.tv_live_item_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            holder.tv_live_item_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        listener.onTitleFocus(holder.getAdapterPosition());
                    }
                }
            });
        } else {
            holder.tv_live_item_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            holder.tv_live_item_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSubTitleClick(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    interface OnClickListener {

        void onTitleFocus(int position);

        void onSubTitleClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_live_item_title)
        TextView tv_live_item_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
