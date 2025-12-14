package com.pxf.fftv.plus.contract.detail;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.FocusAction;

import java.util.List;

import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;

public class VideoPageAdapter extends RecyclerView.Adapter<VideoPageAdapter.ViewHolder> {

    private Activity activity;
    private List<String> pageList;
    private OnPageClickListener listener;
    private int selectedPosition = 0;

    public VideoPageAdapter(Activity activity, List<String> pageList, OnPageClickListener listener) {
        this.activity = activity;
        this.pageList = pageList;
        this.listener = listener;
    }

    public void setSelection(int position) {
        int oldPosition = this.selectedPosition;
        this.selectedPosition = position;
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.video_detail_item_page, parent, false);
        TextView video_item = view.findViewById(R.id.video_item);

        // Reuse the existing part item layout but maybe we need to adjust it slightly
        // if needed.
        // For now, using the same layout is fine as it's just a text view.

        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                video_item.setBackground(activity.getResources().getDrawable(R.drawable.video_detail_content_focus));
                video_item.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));

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
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                video_item.setBackground(activity.getResources().getDrawable(R.drawable.video_detail_content_normal));
                video_item.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    video_item.setScaleX((float) animation.getAnimatedValue());
                    video_item.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.video_item.setText(pageList.get(position));
        if (selectedPosition == position) {
            holder.video_item.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));
        } else {
            holder.video_item.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));
        }
    }

    @Override
    public int getItemCount() {
        return pageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView video_item;

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPageClick(getAdapterPosition());
            }
        };

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            video_item = itemView.findViewById(R.id.video_item);
            itemView.setOnClickListener(onClickListener);
        }
    }

    public interface OnPageClickListener {
        void onPageClick(int position);
    }
}
