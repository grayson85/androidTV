package com.pxf.fftv.plus.common;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pxf.fftv.plus.BuildConfig;
import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.contract.QrCodeActivity;
import com.pxf.fftv.plus.contract.QrCodeEvent;
import com.pxf.fftv.plus.contract.history.VideoHistoryActivity;
import com.pxf.fftv.plus.contract.home.HomeActivity;
import com.pxf.fftv.plus.contract.personal.AccountActivity;
import com.pxf.fftv.plus.contract.SearchActivity;
import com.pxf.fftv.plus.contract.SearchNewActivity;
import com.pxf.fftv.plus.contract.VipActivity;
import com.pxf.fftv.plus.contract.live.IjkTVLiveActivity;
import com.pxf.fftv.plus.contract.live.M3uIptvActivity;

import org.greenrobot.eventbus.EventBus;

import static com.pxf.fftv.plus.Const.ACCOUNT_EVER_VIP;
import static com.pxf.fftv.plus.Const.ACCOUNT_NO_VIP;
import static com.pxf.fftv.plus.Const.ANIMATION_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;

public class Ui {

    public static void configTopBar(Activity activity) {
        configTopBar(activity, Const.GONGGAO);
    }

    public static void configTopBar(Activity activity, String rightNote) {
        View top_bar_menu_root_home = activity.findViewById(R.id.top_bar_menu_root_home);
        View top_bar_menu_root_history = activity.findViewById(R.id.top_bar_menu_root_history);
        View top_bar_menu_root_search = activity.findViewById(R.id.top_bar_menu_root_search);
        View top_bar_menu_root_vip = activity.findViewById(R.id.top_bar_menu_root_vip);
        View top_bar_menu_root_tv_live = activity.findViewById(R.id.top_bar_menu_root_tv_live);
        ImageView top_bar_iv_home = activity.findViewById(R.id.top_bar_iv_home);
        ImageView top_bar_iv_history = activity.findViewById(R.id.top_bar_iv_history);
        ImageView top_bar_iv_search = activity.findViewById(R.id.top_bar_iv_search);
        ImageView top_bar_iv_vip = activity.findViewById(R.id.top_bar_iv_vip);
        ImageView top_bar_iv_tv_live = activity.findViewById(R.id.top_bar_iv_tv_live);
        TextView top_bar_tv_home = activity.findViewById(R.id.top_bar_tv_home);
        TextView top_bar_tv_history = activity.findViewById(R.id.top_bar_tv_history);
        TextView top_bar_tv_search = activity.findViewById(R.id.top_bar_tv_search);
        TextView top_bar_tv_vip = activity.findViewById(R.id.top_bar_tv_vip);
        TextView top_bar_tv_tv_live = activity.findViewById(R.id.top_bar_tv_tv_live);
        TextView top_bar_menu_right_note = activity.findViewById(R.id.top_bar_menu_right_note);

        if (Const.FEATURE_8) {
            top_bar_menu_root_tv_live.setVisibility(View.VISIBLE);
        } else {
            top_bar_menu_root_tv_live.setVisibility(View.GONE);
        }
        if (Const.FEATURE_9) {
            top_bar_menu_root_history.setVisibility(View.VISIBLE);
        } else {
            top_bar_menu_root_history.setVisibility(View.GONE);
        }
        // Always hide VIP button
        top_bar_menu_root_vip.setVisibility(View.GONE);

        TextView top_bar_menu_right_title = activity.findViewById(R.id.top_bar_menu_right_title);
        top_bar_menu_right_title.setText("新马影视");

        // Date & Time updater - Always show
        TextView top_bar_datetime = activity.findViewById(R.id.top_bar_datetime);
        if (top_bar_datetime != null) {
            final android.os.Handler handler = new android.os.Handler();
            final Runnable updateTime = new Runnable() {
                @Override
                public void run() {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm a",
                            java.util.Locale.ENGLISH);
                    String currentDateAndTime = sdf.format(new java.util.Date());
                    top_bar_datetime.setText(currentDateAndTime);
                    handler.postDelayed(this, 60000); // Update every minute
                }
            };
            handler.post(updateTime);
        }

        if (!rightNote.isEmpty() && top_bar_menu_right_note != null) {
            top_bar_menu_right_note.setVisibility(View.VISIBLE);
            top_bar_menu_right_note.setText(rightNote);
            // Enable marquee scrolling
            top_bar_menu_right_note.setSelected(true);
        }

        top_bar_menu_root_tv_live.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // if (BuildConfig.DEBUG) {
                if (true) {
                    Intent intent = new Intent(activity, IjkTVLiveActivity.class);
                    activity.startActivity(intent);
                } else {
                    if (FFTVApplication.vipDate == ACCOUNT_EVER_VIP) {
                        Intent intent = new Intent(activity, IjkTVLiveActivity.class);
                        activity.startActivity(intent);
                    } else if (FFTVApplication.vipDate < System.currentTimeMillis()) {
                        Toast.makeText(activity, "您的VIP会员已过期，请及时续费", Toast.LENGTH_LONG).show();
                    } else if (FFTVApplication.vipDate == ACCOUNT_NO_VIP) {
                        Toast.makeText(activity, "您还不是VIP会员，无法观看", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(activity, IjkTVLiveActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }
        });
        top_bar_menu_root_history.setOnClickListener(v -> {
            Intent intent = new Intent(activity, VideoHistoryActivity.class);
            activity.startActivity(intent);
        });

        // IPTV Button
        View top_bar_menu_root_iptv = activity.findViewById(R.id.top_bar_menu_root_iptv);
        ImageView top_bar_iv_iptv = activity.findViewById(R.id.top_bar_iv_iptv);
        TextView top_bar_tv_iptv = activity.findViewById(R.id.top_bar_tv_iptv);
        if (top_bar_menu_root_iptv != null) {
            top_bar_menu_root_iptv.setOnClickListener(v -> {
                Intent intent = new Intent(activity, M3uIptvActivity.class);
                activity.startActivity(intent);
            });
            setMenuFocusAnimator(activity, top_bar_menu_root_iptv, new FocusAction() {
                @Override
                public void onFocus() {
                    if (top_bar_iv_iptv != null)
                        top_bar_iv_iptv.setColorFilter(activity.getResources().getColor(R.color.colorTextFocus));
                    if (top_bar_tv_iptv != null)
                        top_bar_tv_iptv.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));
                }

                @Override
                public void onLoseFocus() {
                    if (top_bar_iv_iptv != null)
                        top_bar_iv_iptv.setColorFilter(activity.getResources().getColor(R.color.colorTextNormal));
                    if (top_bar_tv_iptv != null)
                        top_bar_tv_iptv.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));
                }
            });
        }

        top_bar_menu_root_search.setOnClickListener(v -> {
            if (Const.FEATURE_13) {
                Intent intent = new Intent(activity, SearchNewActivity.class);
                activity.startActivity(intent);
            } else {
                Intent intent = new Intent(activity, SearchActivity.class);
                activity.startActivity(intent);
            }
        });
        top_bar_menu_root_vip.setOnClickListener(v -> {
            Intent intent;
            if (FFTVApplication.login) {
                intent = new Intent(activity, VipActivity.class);
            } else {
                Toast.makeText(activity, "请先登录账号", Toast.LENGTH_LONG).show();
                intent = new Intent(activity, AccountActivity.class);
            }
            activity.startActivity(intent);
        });

        setMenuFocusAnimator(activity, top_bar_menu_root_history, new FocusAction() {
            @Override
            public void onFocus() {
                top_bar_iv_history.setImageResource(R.drawable.ic_history_focus);
                top_bar_tv_history.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                top_bar_iv_history.setImageResource(R.drawable.ic_history_normal);
                top_bar_tv_history.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));
            }
        });
        setMenuFocusAnimator(activity, top_bar_menu_root_tv_live, new FocusAction() {
            @Override
            public void onFocus() {
                top_bar_iv_tv_live.setImageResource(R.drawable.ic_tv_live_focus);
                top_bar_tv_tv_live.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                top_bar_iv_tv_live.setImageResource(R.drawable.ic_tv_live_normal);
                top_bar_tv_tv_live.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));
            }
        });
        setMenuFocusAnimator(activity, top_bar_menu_root_home, new FocusAction() {
            @Override
            public void onFocus() {
                top_bar_iv_home.setImageResource(R.drawable.ic_home_foces);
                top_bar_tv_home.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                top_bar_iv_home.setImageResource(R.drawable.ic_home_normal);
                top_bar_tv_home.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));
            }
        });
        setMenuFocusAnimator(activity, top_bar_menu_root_search, new FocusAction() {
            @Override
            public void onFocus() {
                top_bar_iv_search.setImageResource(R.drawable.ic_search_focus);
                top_bar_tv_search.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                top_bar_iv_search.setImageResource(R.drawable.ic_search_normal);
                top_bar_tv_search.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));
            }
        });

        top_bar_menu_root_vip.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                top_bar_menu_root_vip.setBackground(activity.getDrawable(R.drawable.bg_vip_menu_focus));
                top_bar_iv_vip.setImageResource(R.drawable.ic_vip_focus);
                top_bar_tv_vip.setTextColor(activity.getResources().getColor(R.color.colorVipTextFocus));
                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE)
                        .setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (top_bar_menu_root_vip.isFocused()) {
                        top_bar_menu_root_vip.setScaleX((float) animation.getAnimatedValue());
                        top_bar_menu_root_vip.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (top_bar_menu_root_vip.isFocused()) {
                        top_bar_menu_root_vip.setScaleX((float) animation.getAnimatedValue());
                        top_bar_menu_root_vip.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });

                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                top_bar_menu_root_vip.setBackground(activity.getDrawable(R.drawable.bg_vip_menu_normal));
                top_bar_iv_vip.setImageResource(R.drawable.ic_vip_normal);
                top_bar_tv_vip.setTextColor(activity.getResources().getColor(R.color.colorVipTextNormal));
                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    top_bar_menu_root_vip.setScaleX((float) animation.getAnimatedValue());
                    top_bar_menu_root_vip.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });

        top_bar_menu_root_home.setOnClickListener(v -> {
            Intent intent = new Intent(activity, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        });

        View top_bar_menu_root_collect = activity.findViewById(R.id.top_bar_menu_root_collect);
        ImageView top_bar_iv_collect = activity.findViewById(R.id.top_bar_iv_collect);
        TextView top_bar_tv_collect = activity.findViewById(R.id.top_bar_tv_collect);

        if (top_bar_menu_root_collect != null) {
            top_bar_menu_root_collect.setOnClickListener(v -> {
                Intent intent = new Intent(activity, com.pxf.fftv.plus.contract.collect.VideoCollectActivity.class);
                activity.startActivity(intent);
            });

            setMenuFocusAnimator(activity, top_bar_menu_root_collect, new FocusAction() {
                @Override
                public void onFocus() {
                    top_bar_iv_collect.setImageResource(R.drawable.ic_collect_focus);
                    top_bar_tv_collect.setTextColor(activity.getResources().getColor(R.color.colorTextFocus));
                }

                @Override
                public void onLoseFocus() {
                    top_bar_iv_collect.setImageResource(R.drawable.ic_collect_normal);
                    top_bar_tv_collect.setTextColor(activity.getResources().getColor(R.color.colorTextNormal));
                }
            });
        }
    }

    public static void setMenuFocusAnimator(Activity activity, View view, FocusAction action) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                view.setBackground(activity.getDrawable(R.drawable.bg_common_menu_focus));
                if (action != null) {
                    action.onFocus();
                }

                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE)
                        .setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        view.setScaleX((float) animation.getAnimatedValue());
                        view.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        view.setScaleX((float) animation.getAnimatedValue());
                        view.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                view.setBackground(activity.getDrawable(R.drawable.bg_common_menu_normal));
                if (action != null) {
                    action.onLoseFocus();
                }
                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    view.setScaleX((float) animation.getAnimatedValue());
                    view.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });
    }

    public static void setViewFocusScaleAnimator(View view, FocusAction action) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (action != null) {
                    action.onFocus();
                }

                ValueAnimator animator = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_OUT_SCALE)
                        .setDuration(ANIMATION_DURATION);

                animator.addUpdateListener(animation -> {
                    if (v.isFocused()) {
                        v.setScaleX((float) animation.getAnimatedValue());
                        v.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animator.cancel();
                    }
                });

                animator.setInterpolator(new OvershootInterpolator());
                animator.start();
            } else {
                if (action != null) {
                    action.onLoseFocus();
                }

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    v.setScaleX((float) animation.getAnimatedValue());
                    v.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });
    }

    public static void showNotice(Activity activity, String title, String content) {
        if (content.startsWith("e+")) {
            content = content.substring(2);
            Ui.showNoticeQrcode(activity, title, content);
            return;
        }
        if (content.startsWith("t+")) {
            content = content.substring(2);
            Ui.showNoticeText(activity, title, content);
            return;
        }
        Ui.showNoticeText(activity, title, content);
    }

    public static void showTextDialog(Activity activity, String title, String content) {
        showNoticeText(activity, title, content);
    }

    private static void showNoticeText(Activity activity, String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private static void showNoticeQrcode(Activity activity, String title, String url) {
        Intent intent = new Intent(activity, QrCodeActivity.class);
        activity.startActivity(intent);
        EventBus.getDefault().postSticky(new QrCodeEvent(url, title));
    }
}
