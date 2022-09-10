package com.pxf.fftv.plus.contract;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.GlideApp;
import com.pxf.fftv.plus.model.video.Video;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;

public class SearchNewAdapter extends RecyclerView.Adapter<SearchNewAdapter.ViewHolder> {

    private Activity activity;

    private List<Video> dataList;

    private OnSearchResultItemClickListener listener;

    SearchNewAdapter(Activity activity, List<Video> dataList, OnSearchResultItemClickListener listener) {
        this.activity = activity;
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.video_list_item, parent, false);
        setVideoCardFocusAnimator((ViewGroup) view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video item = dataList.get(position);
        GlideApp.with(activity).load(item.getImageUrl()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.video_list_item_iv_image);
        holder.video_list_item_tv_title.setText(item.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSearchItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void setVideoCardFocusAnimator(ViewGroup viewGroup) {
        viewGroup.setOnFocusChangeListener((v, hasFocus) -> {
            ViewGroup root = viewGroup.findViewById(R.id.video_list_item_root);
            TextView title = viewGroup.findViewById(R.id.video_list_item_tv_title);
            View playIcon = viewGroup.findViewById(R.id.video_list_item_iv_icon);
            if (hasFocus) {
                title.setSelected(true);
                title.setTextColor(activity.getResources().getColor(R.color.colorVideoCardTextFocus));
                root.setBackgroundColor(activity.getResources().getColor(R.color.colorVideoCardFocus));

                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE).setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE).setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (viewGroup.isFocused()) {
                        root.setScaleX((float) animation.getAnimatedValue());
                        root.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (viewGroup.isFocused()) {
                        root.setScaleX((float) animation.getAnimatedValue());
                        root.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();

                //Play icon 动画
                ValueAnimator animatorPlayIcon = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animatorPlayIcon.addUpdateListener(animation -> {
                    playIcon.setScaleX((float) animation.getAnimatedValue());
                    playIcon.setScaleY((float) animation.getAnimatedValue());
                });
                animatorPlayIcon.start();
            } else {
                title.setSelected(false);
                title.setTextColor(activity.getResources().getColor(R.color.colorVideoCardTextNormal));
                root.setBackground(null);

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    root.setScaleX((float) animation.getAnimatedValue());
                    root.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();

                //Play icon 动画
                ValueAnimator animatorPlayIcon = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animatorPlayIcon.addUpdateListener(animation -> {
                    playIcon.setScaleX((float) animation.getAnimatedValue());
                    playIcon.setScaleY((float) animation.getAnimatedValue());
                });
                animatorPlayIcon.start();
            }
        });
    }

    interface OnSearchResultItemClickListener {

        void onSearchItemClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_list_item_root)
        ViewGroup video_list_item_root;

        @BindView(R.id.video_list_item_iv_image)
        ImageView video_list_item_iv_image;

        @BindView(R.id.video_list_item_tv_title)
        TextView video_list_item_tv_title;

        @BindView(R.id.video_list_item_iv_icon)
        ImageView video_list_item_iv_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
