package com.pxf.fftv.plus.contract.detail;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.common.Ui;
import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;

public class VideoPlayListAdapter extends RecyclerView.Adapter<VideoPlayListAdapter.ViewHolder> {

    private Activity activity;

    private ArrayList<Video.Part> partList;

    OnPartClickListener listener;

    public VideoPlayListAdapter(Activity activity, ArrayList<Video.Part> partList, OnPartClickListener listener) {
        this.activity = activity;
        this.partList = partList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.video_detail_item_part, parent, false);
        TextView video_item = view.findViewById(R.id.video_item);
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                video_item.setBackground(activity.getResources().getDrawable(R.drawable.video_detail_content_focus));
                video_item.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));

                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE).setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE).setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        video_item.setScaleX((float)animation.getAnimatedValue());
                        video_item.setScaleY((float)animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        video_item.setScaleX((float)animation.getAnimatedValue());
                        video_item.setScaleY((float)animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                video_item.setBackground(activity.getResources().getDrawable(R.drawable.video_detail_content_normal));
                video_item.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    video_item.setScaleX((float)animation.getAnimatedValue());
                    video_item.setScaleY((float)animation.getAnimatedValue());
                });
                animator.start();
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.video_item.setText(partList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return partList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_item)
        TextView video_item;

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPartClick(getAdapterPosition());
            }
        };

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(onClickListener);
        }
    }

    interface OnPartClickListener {

        void onPartClick(int position);
    }
}
