package com.pxf.fftv.plus.contract.list;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.GlideApp;
import com.pxf.fftv.plus.databinding.VideoListItemBinding; // Import the generated binding
import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

// No longer need butterknife imports
// import butterknife.BindView;
// import butterknife.ButterKnife;

import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;
import static com.pxf.fftv.plus.Const.VIDEO_LIST_COLUMN;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final Context context;
    private final OnClickListener listener;
    private ArrayList<Video> videoList;
    private int leftFocus = -1;
    private boolean handleKey = false;
    private final OnStartLoadMoreListener onStartLoadMoreListener;

    public VideoAdapter(Context context, OnClickListener listener, ArrayList<Video> videoList, int leftFocus,
            OnStartLoadMoreListener onStartLoadMoreListener) {
        this.context = context;
        this.listener = listener;
        this.videoList = videoList;
        this.leftFocus = leftFocus;
        this.onStartLoadMoreListener = onStartLoadMoreListener;
    }

    void setList(ArrayList<Video> videoList) {
        this.videoList = videoList;
    }

    ArrayList<Video> getList() {
        return videoList;
    }

    void refreshList(ArrayList<Video> videoList, int leftFocus) {
        if (this.videoList.equals(videoList)) {
            return;
        }
        this.videoList = videoList;
        this.leftFocus = leftFocus;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 1. Inflate using the binding class
        VideoListItemBinding binding = VideoListItemBinding.inflate(LayoutInflater.from(context), parent, false);
        setVideoCardFocusAnimator(binding);
        // 2. Pass the binding object to the ViewHolder
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 3. Access views through the holder's binding object
        GlideApp.with(context)
                .load(videoList.get(position).getImageUrl())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.binding.videoListItemIvImage);
        holder.binding.videoListItemTvTitle.setText(videoList.get(position).getTitle());
        if (leftFocus != -1) {
            holder.itemView.setNextFocusLeftId(leftFocus);
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        // 4. The ViewHolder holds the binding object
        VideoListItemBinding binding;

        private Timer handleKeyTimer;

        private final View.OnClickListener onClickListener = v -> listener.onVideoItemClick(getAdapterPosition());

        private final View.OnKeyListener onKeyListener = (v, keyCode, event) -> {
            if (videoList.size() % VIDEO_LIST_COLUMN != 0) {
                if (videoList.size() - (getAdapterPosition() + 1) < videoList.size() % VIDEO_LIST_COLUMN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                        onStartLoadMoreListener.onStartLoadMore();
                        return true;
                    }
                }
            } else {
                if (videoList.size() - (getAdapterPosition() + 1) < VIDEO_LIST_COLUMN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                        onStartLoadMoreListener.onStartLoadMore();
                        return true;
                    }
                }
            }

            // 控制焦点移动最低间隔为100毫秒，避免移动过快导致焦点乱跳
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (handleKey) {
                    return true;
                } else {
                    handleKey = true;
                    if (handleKeyTimer != null) {
                        handleKeyTimer.cancel();
                        handleKeyTimer.purge();
                    }
                    handleKeyTimer = new Timer();
                    handleKeyTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handleKey = false;
                        }
                    }, 100);
                    return false;
                }
            }
            return false;
        };

        // 5. Constructor accepts the binding object
        ViewHolder(@NonNull VideoListItemBinding binding) {
            super(binding.getRoot());
            // 6. Store the binding object and remove ButterKnife
            this.binding = binding;
            // ButterKnife.bind(this, itemView); // No longer needed
            itemView.setOnClickListener(onClickListener);
            itemView.setOnKeyListener(onKeyListener);
        }
    }

    private void setVideoCardFocusAnimator(VideoListItemBinding binding) {
        // The root view is directly accessible from the binding
        binding.getRoot().setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.videoListItemTvTitle.setSelected(true);
                binding.videoListItemTvTitle.setTextColor(context.getResources().getColor(R.color.modernTextPrimary));
                binding.videoListItemRoot.setBackgroundResource(R.drawable.bg_modern_card_focus);

                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE)
                        .setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (binding.getRoot().isFocused()) {
                        binding.videoListItemRoot.setScaleX((float) animation.getAnimatedValue());
                        binding.videoListItemRoot.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (binding.getRoot().isFocused()) {
                        binding.videoListItemRoot.setScaleX((float) animation.getAnimatedValue());
                        binding.videoListItemRoot.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();

                // Play icon 动画
                ValueAnimator animatorPlayIcon = ValueAnimator.ofFloat(0.0f, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animatorPlayIcon.addUpdateListener(animation -> {
                    binding.videoListItemIvIcon.setScaleX((float) animation.getAnimatedValue());
                    binding.videoListItemIvIcon.setScaleY((float) animation.getAnimatedValue());
                });
                animatorPlayIcon.start();
            } else {
                binding.videoListItemTvTitle.setSelected(false);
                binding.videoListItemTvTitle.setTextColor(context.getResources().getColor(R.color.modernTextSecondary));
                binding.videoListItemRoot.setBackgroundResource(R.drawable.bg_modern_card);

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    binding.videoListItemRoot.setScaleX((float) animation.getAnimatedValue());
                    binding.videoListItemRoot.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();

                // Play icon 动画
                ValueAnimator animatorPlayIcon = ValueAnimator.ofFloat(1.0f, 0.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animatorPlayIcon.addUpdateListener(animation -> {
                    binding.videoListItemIvIcon.setScaleX((float) animation.getAnimatedValue());
                    binding.videoListItemIvIcon.setScaleY((float) animation.getAnimatedValue());
                });
                animatorPlayIcon.start();
            }
        });
    }

    interface OnClickListener {
        void onVideoItemClick(int position);
    }
}
