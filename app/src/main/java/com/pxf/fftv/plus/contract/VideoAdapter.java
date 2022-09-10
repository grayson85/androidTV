package com.pxf.fftv.plus.contract;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.KeyEvent;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;
import static com.pxf.fftv.plus.Const.VIDEO_LIST_COLUMN;

class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context context;

    private OnClickListener listener;

    private ArrayList<Video> videoList;

    private int leftFocus = -1;

    private boolean handleKey = false;

    OnStartLoadMoreListener onStartLoadMoreListener;

    private int currentFocusIndex = 0;

    public VideoAdapter(Context context, OnClickListener listener, ArrayList<Video> videoList, int leftFocus, OnStartLoadMoreListener onStartLoadMoreListener) {
        this.context = context;
        this.listener = listener;
        this.videoList = videoList;
        this.leftFocus = leftFocus;
        this.onStartLoadMoreListener = onStartLoadMoreListener;
    }

    public int getCurrentFocusIndex() {
        return currentFocusIndex;
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
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GlideApp.with(context).load(videoList.get(position).getImageUrl()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.video_list_item_iv_image);
        holder.video_list_item_tv_title.setText(videoList.get(position).getTitle());
        setVideoCardFocusAnimator((ViewGroup) holder.itemView, position);
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

        @BindView(R.id.video_list_item_root)
        ViewGroup video_list_item_root;

        @BindView(R.id.video_list_item_iv_image)
        ImageView video_list_item_iv_image;

        @BindView(R.id.video_list_item_tv_title)
        TextView video_list_item_tv_title;

        @BindView(R.id.video_list_item_iv_icon)
        ImageView video_list_item_iv_icon;

        private Timer handleKeyTimer;

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onVideoItemClick(getAdapterPosition());
            }
        };

        private View.OnKeyListener onKeyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
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
            }
        };

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(onClickListener);
            itemView.setOnKeyListener(onKeyListener);
        }
    }

    private void setVideoCardFocusAnimator(ViewGroup viewGroup, int position) {
        viewGroup.setOnFocusChangeListener((v, hasFocus) -> {
            ViewGroup root = viewGroup.findViewById(R.id.video_list_item_root);
            TextView title = viewGroup.findViewById(R.id.video_list_item_tv_title);
            View playIcon = viewGroup.findViewById(R.id.video_list_item_iv_icon);
            if (hasFocus) {
                currentFocusIndex = position;
                title.setSelected(true);
                title.setTextColor(context.getResources().getColor(R.color.colorVideoCardTextFocus));
                root.setBackgroundColor(context.getResources().getColor(R.color.colorVideoCardFocus));

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
                title.setTextColor(context.getResources().getColor(R.color.colorVideoCardTextNormal));
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

    interface OnClickListener {

        void onVideoItemClick(int position);
    }
}
