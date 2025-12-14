package com.pxf.fftv.plus.contract.live;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.bean.MatchBean;

import java.util.ArrayList;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private Context context;
    private List<MatchBean.DataBean.ListBean> matchList;
    private OnMatchClickListener listener;

    public MatchAdapter(Context context, List<MatchBean.DataBean.ListBean> matchList, OnMatchClickListener listener) {
        this.context = context;
        this.matchList = matchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tv_live_match_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchBean.DataBean.ListBean match = matchList.get(position);

        // Set home team name
        holder.tvHomeName.setText(match.getHteam_name());

        // Set away team name
        holder.tvAwayName.setText(match.getAteam_name());

        // Set match time
        holder.tvMatchTime.setText(match.getMatchtime());

        // Load home team logo
        if (match.getHteam_logo() != null && !match.getHteam_logo().isEmpty()) {
            Glide.with(context)
                    .load(match.getHteam_logo())
                    .into(holder.ivHomeLogo);
        }

        // Load away team logo
        if (match.getAteam_logo() != null && !match.getAteam_logo().isEmpty()) {
            Glide.with(context)
                    .load(match.getAteam_logo())
                    .into(holder.ivAwayLogo);
        }
    }

    @Override
    public int getItemCount() {
        return matchList != null ? matchList.size() : 0;
    }

    public interface OnMatchClickListener {
        void onMatchFocus(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHomeLogo;
        ImageView ivAwayLogo;
        TextView tvHomeName;
        TextView tvAwayName;
        TextView tvMatchTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHomeLogo = itemView.findViewById(R.id.iv_home_logo);
            ivAwayLogo = itemView.findViewById(R.id.iv_away_logo);
            tvHomeName = itemView.findViewById(R.id.tv_home_name);
            tvAwayName = itemView.findViewById(R.id.tv_away_name);
            tvMatchTime = itemView.findViewById(R.id.tv_match_time);

            // Set focus listener once in constructor
            itemView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus && listener != null) {
                    listener.onMatchFocus(getAdapterPosition());
                }
            });

            // Set click listener once in constructor
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMatchFocus(getAdapterPosition());
                }
            });

            // Set next focus right to the subtitle RecyclerView
            itemView.setNextFocusRightId(R.id.tv_live_recycler_view_sub_title);
        }
    }
}
