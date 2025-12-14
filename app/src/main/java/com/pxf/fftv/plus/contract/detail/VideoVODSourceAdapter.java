package com.pxf.fftv.plus.contract.detail;

import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.pxf.fftv.plus.R;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;

//20220923 - Added new feature indicate multiple source
public class VideoVODSourceAdapter extends RecyclerView.Adapter<VideoVODSourceAdapter.ViewHolder> {
    private Activity activity;
    OnVODClickListener listener;
    private ArrayList<Video.VodSource> vod;
    private int selectedPosition = 0;

    public VideoVODSourceAdapter(Activity activity, ArrayList<Video.VodSource> vod, int selectedPosition,
            OnVODClickListener listener) {
        this.activity = activity;
        this.vod = vod;
        this.selectedPosition = selectedPosition;
        this.listener = listener;
    }

    public void setSelection(int position) {
        int oldPosition = this.selectedPosition;
        this.selectedPosition = position;

        // Only notify the changed items instead of the entire dataset
        // This prevents the focused item from losing focus
        if (oldPosition >= 0 && oldPosition < getItemCount()) {
            notifyItemChanged(oldPosition);
        }
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.item_vod_source, parent, false);
        TextView tvSource = view.findViewById(R.id.tvSource);

        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tvSource.setBackground(ContextCompat.getDrawable(activity, R.drawable.video_detail_content_focus));
                tvSource.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));

                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE)
                        .setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        tvSource.setScaleX((float) animation.getAnimatedValue());
                        tvSource.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        tvSource.setScaleX((float) animation.getAnimatedValue());
                        tvSource.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                tvSource.setBackground(ContextCompat.getDrawable(activity, R.drawable.video_detail_content_normal));
                tvSource.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    tvSource.setScaleX((float) animation.getAnimatedValue());
                    tvSource.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });

        // Return the completed view to render on screen
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvSource.setText(vod.get(position).sourceName);
        if (selectedPosition == position) {
            holder.tvSource.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));
        } else {
            holder.tvSource.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));
        }
    }

    @Override
    public int getItemCount() {
        return vod == null ? 0 : vod.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSource;

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onVODSourceClick(getAdapterPosition());
            }
        };

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSource = itemView.findViewById(R.id.tvSource);
            itemView.setOnClickListener(onClickListener);
        }
    }

    interface OnVODClickListener {
        void onVODSourceClick(int position);
    }
}
