package com.pxf.fftv.plus.contract;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.VideoConfig;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.common.Ui;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoSubTitleAdapter extends RecyclerView.Adapter<VideoSubTitleAdapter.ViewHolder> {

    private Activity activity;

    private VideoConfig.VideoScreenSub[] data;

    private OnVideoSubListener listener;

    private int selection = 0;

    private int leftFocus;

    public VideoSubTitleAdapter(Activity activity, VideoConfig.VideoScreenSub[] data, OnVideoSubListener listener, int leftFocus) {
        this.activity = activity;
        this.data = data;
        this.listener = listener;
        this.leftFocus = leftFocus;
    }

    public void refreshList(VideoConfig.VideoScreenSub[] data, int selection, int leftFocus) {
        this.data = data;
        this.selection = selection;
        this.leftFocus = leftFocus;
        notifyDataSetChanged();
    }

    public int getSelection() {
        return selection;
    }

    public String getSelectionUrl() {
        return data[selection].getUrl();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_video_screen_sub_title, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.video_scree_item_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lastSelection = selection;
                selection = position;
                notifyItemChanged(selection);
                notifyItemChanged(lastSelection);
                listener.onClick(position, data[position].getUrl());
            }
        });
        holder.video_scree_item_title.setText(data[position].getTitle());
        Ui.setViewFocusScaleAnimator(holder.video_scree_item_title, new FocusAction() {
            @Override
            public void onFocus() {
                super.onFocus();
                holder.video_scree_item_title.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));
                listener.onFocus(position);
            }

            @Override
            public void onLoseFocus() {
                super.onLoseFocus();
                if (selection == position) {
                    holder.video_scree_item_title.setTextColor(activity.getResources().getColor(R.color.colorTextSelection));
                } else {
                    holder.video_scree_item_title.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));
                }
            }
        });
        if (activity.getCurrentFocus() == holder.video_scree_item_title) {
            holder.video_scree_item_title.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));
        } else {
            if (selection == position) {
                holder.video_scree_item_title.setTextColor(activity.getResources().getColor(R.color.colorTextSelection));
            } else {
                holder.video_scree_item_title.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));
            }
        }

        if (position == 0) {
            holder.video_scree_item_title.setNextFocusLeftId(leftFocus);
        }
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    interface OnVideoSubListener {

        void onClick(int position, String url);

        void onFocus(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_scree_item_title)
        TextView video_scree_item_title;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
