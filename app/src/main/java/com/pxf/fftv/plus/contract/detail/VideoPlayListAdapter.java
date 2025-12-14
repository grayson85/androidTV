package com.pxf.fftv.plus.contract.detail;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.databinding.VideoDetailItemPartBinding; // Import the generated binding
import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;

// No longer need to import ButterKnife
// import butterknife.BindView;
// import butterknife.ButterKnife;

import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;

public class VideoPlayListAdapter extends RecyclerView.Adapter<VideoPlayListAdapter.ViewHolder> {

    private final Activity activity;
    private ArrayList<Video.Part> partList;
    private final OnPartClickListener listener;

    public VideoPlayListAdapter(Activity activity, ArrayList<Video.Part> partList, OnPartClickListener listener) {
        this.activity = activity;
        this.partList = partList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 1. Inflate the layout using the generated binding class
        VideoDetailItemPartBinding binding = VideoDetailItemPartBinding.inflate(LayoutInflater.from(activity), parent,
                false);

        // The View is the root of the binding
        View view = binding.getRoot();
        TextView video_item = binding.videoItem; // Access the view via the binding object

        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                video_item.setBackground(ContextCompat.getDrawable(activity, R.drawable.video_detail_content_focus));
                video_item.setTextColor(ContextCompat.getColor(activity, R.color.colorTextFocus));

                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE)
                        .setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        video_item.setScaleX((float) animation.getAnimatedValue());
                        video_item.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        video_item.setScaleX((float) animation.getAnimatedValue());
                        video_item.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorSecond.cancel(); // Use animatorSecond here
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                video_item.setBackground(ContextCompat.getDrawable(activity, R.drawable.video_detail_content_normal));
                video_item.setTextColor(ContextCompat.getColor(activity, R.color.colorTextNormal));

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    video_item.setScaleX((float) animation.getAnimatedValue());
                    video_item.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });
        // 2. Pass the binding object to the ViewHolder
        return new ViewHolder(binding, listener);
    }

    // 20220923 - Added new feature indicate multiple source
    public void updateReceiptsList(ArrayList<Video.Part> newlist) {
        partList = newlist;
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 3. Access views via the ViewHolder's binding object
        holder.binding.videoItem.setText(partList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return partList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        // 4. The ViewHolder holds the binding object, not the individual views
        VideoDetailItemPartBinding binding;

        ViewHolder(@NonNull VideoDetailItemPartBinding binding, OnPartClickListener listener) {
            super(binding.getRoot());
            // 5. Store the binding object
            this.binding = binding;
            // ButterKnife.bind is no longer needed

            // Set the click listener on the root view
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onPartClick(position);
                    }
                }
            });
        }
    }

    interface OnPartClickListener {
        void onPartClick(int position);
    }
}
