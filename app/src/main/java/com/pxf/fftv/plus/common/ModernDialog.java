package com.pxf.fftv.plus.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.pxf.fftv.plus.R;

/**
 * 现代化暗色确认对话框
 * 完全自定义布局，无分隔线，支持遥控器导航
 */
public class ModernDialog {

    public interface OnClickListener {
        void onClick();
    }

    public static void showConfirm(Context context, String title,
            OnClickListener onPositive,
            OnClickListener onNegative) {
        showConfirm(context, title, null, "确定", "取消", onPositive, onNegative);
    }

    public static void showConfirm(Context context, String title, String message,
            String positiveText, String negativeText,
            OnClickListener onPositive,
            OnClickListener onNegative) {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_modern_confirm, null);
        dialog.setContentView(view);

        // 设置透明背景
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // 标题
        TextView titleView = view.findViewById(R.id.dialog_title);
        titleView.setText(title);

        // 消息（可选）
        TextView messageView = view.findViewById(R.id.dialog_message);
        if (message != null && !message.isEmpty()) {
            messageView.setText(message);
            messageView.setVisibility(View.VISIBLE);
        } else {
            messageView.setVisibility(View.GONE);
        }

        // 按钮
        TextView btnPositive = view.findViewById(R.id.dialog_btn_positive);
        TextView btnNegative = view.findViewById(R.id.dialog_btn_negative);

        btnPositive.setText(positiveText);
        btnNegative.setText(negativeText);

        btnPositive.setOnClickListener(v -> {
            dialog.dismiss();
            if (onPositive != null) {
                onPositive.onClick();
            }
        });

        btnNegative.setOnClickListener(v -> {
            dialog.dismiss();
            if (onNegative != null) {
                onNegative.onClick();
            }
        });

        // 默认焦点给确定按钮
        btnPositive.requestFocus();

        dialog.show();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static void showList(Context context, String title, String[] items, OnItemClickListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_modern_list, null);
        dialog.setContentView(view);

        // 设置透明背景
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // 标题
        TextView titleView = view.findViewById(R.id.dialog_list_title);
        titleView.setText(title);

        // 列表容器
        android.widget.LinearLayout container = view.findViewById(R.id.dialog_list_container);

        for (int i = 0; i < items.length; i++) {
            final int position = i;
            TextView itemView = (TextView) LayoutInflater.from(context)
                    .inflate(R.layout.dialog_modern_list_item, container, false);
            itemView.setText(items[i]);
            itemView.setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onItemClick(position);
                }
            });
            container.addView(itemView);

            // 第一个项目获得焦点
            if (i == 0) {
                itemView.requestFocus();
            }
        }

        dialog.show();
    }
}
