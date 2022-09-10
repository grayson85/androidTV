package com.pxf.fftv.plus.contract.collect;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pxf.fftv.plus.Const.ANIMATION_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;

public class VideoCollectAdapter extends RecyclerView.Adapter<VideoCollectAdapter.ViewHolder> {

    private Activity activity;

    private List<VideoCollect> dataList;

    private OnCollectItemClickListener listener;

    VideoCollectAdapter(Activity activity, List<VideoCollect> dataList, OnCollectItemClickListener listener) {
        this.activity = activity;
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.video_collect_item, parent, false);
        RelativeLayout video_collect_card_root = view.findViewById(R.id.video_collect_card_root);
        setVideoCardFocusAnimator(video_collect_card_root);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoCollect item = dataList.get(position);
        GlideApp.with(activity).load(item.getVideo().getImageUrl()).into(holder.video_collect_card_image);
        holder.video_collect_card_text.setText(item.getVideo().getTitle());
        holder.video_collect_card_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCollectItemClick(holder.getAdapterPosition());
            }
        });
        holder.video_collect_card_root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                            .setTitle("是否删除该视频？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    listener.onCollectItemMenuClick(holder.getAdapterPosition());
                                }
                            });
                    builder.create().show();
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void setVideoCardFocusAnimator(ViewGroup viewGroup) {
        viewGroup.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                viewGroup.getChildAt(1).setSelected(true);
                ((TextView) viewGroup.getChildAt(1)).setTextColor(activity.getResources().getColor(R.color.colorVideoCardTextFocus));

                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_OUT_SCALE).setDuration(ANIMATION_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (viewGroup.isFocused()) {
                        viewGroup.setScaleX((float) animation.getAnimatedValue());
                        viewGroup.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });

                animatorFirst.setInterpolator(new OvershootInterpolator());
                animatorFirst.start();
            } else {
                viewGroup.getChildAt(1).setSelected(false);
                ((TextView) viewGroup.getChildAt(1)).setTextColor(activity.getResources().getColor(R.color.colorVideoCardTextNormal));

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    viewGroup.setScaleX((float) animation.getAnimatedValue());
                    viewGroup.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });
    }

    interface OnCollectItemClickListener {

        void onCollectItemClick(int position);

        void onCollectItemMenuClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_collect_card_root)
        RelativeLayout video_collect_card_root;

        @BindView(R.id.video_collect_card_image)
        ImageView video_collect_card_image;

        @BindView(R.id.video_collect_card_text)
        TextView video_collect_card_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
