package com.pxf.fftv.plus.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.InternalFileSaveUtil;
import com.pxf.fftv.plus.contract.history.VideoHistory;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class IjkMediaPlayerActivity extends Activity {

    @BindView(R.id.video_time)
    TextView video_time;

    @BindView(R.id.surface_play)
    SurfaceView surface_play;

    @BindView(R.id.title)
    TextView titleView;

    @BindView(R.id.message)
    TextView messageView;

    @BindView(R.id.menu)
    View menuRoot;

    @BindView(R.id.seekbar)
    SeekBar seekbar;

    @BindView(R.id.loading_text)
    TextView loadingText;

    @BindView(R.id.loading_view_root)
    View loadingViewRoot;

    private IjkMediaPlayer mPlayer;

    private String videoUrl;
    private String title;
    private String subTitle;
    private String picUrl;
    private int currentPartPosition;
    private int lastPosition;

    private Dialog mProgressDialog;
    private Timer mProgressTimer;
    private Timer mMenuTimer;
    private Timer mTimer = new Timer();

    // 标记视频是否已经开始播放
    private boolean isPrepare = false;
    private int videoDuration = 0;
    private int targetPosition = -1;

    // 用于记录历史
    private int videoCurrentPosition = 0;

    private boolean isSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_media_player);
        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

        videoUrl = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_URL);
        title = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_TITLE);
        subTitle = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_SUB_TITLE);
        picUrl = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_PIC);
        currentPartPosition = getIntent().getIntExtra(VideoPlayer.KEY_VIDEO_CURRENT_PART, -1);
        lastPosition = getIntent().getIntExtra(VideoPlayer.KEY_VIDEO_LAST_POSITION, -1);

        titleView.setText(title);
        messageView.setText(subTitle);
        menuRoot.setVisibility(View.GONE);
        seekbar.setFocusable(false);

        surface_play.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                play(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (surface_play != null) {
                    surface_play.getHolder().removeCallback(this);
                    surface_play = null;
                }
            }
        });


        View view = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null, false);
        mProgressDialog = new AlertDialog.Builder(this).setView(view).setCancelable(false).create();
        mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    ImageView icon = mProgressDialog.findViewById(R.id.dialog_progress_icon);
                    TextView text = mProgressDialog.findViewById(R.id.dialog_progress_time);
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            if (!isPrepare) {
                                return false;
                            }
                            // 快退
                            if (targetPosition == -1) {
                                targetPosition = (int)(mPlayer.getCurrentPosition()) / 1000 - videoDuration / 100;
                            } else {
                                targetPosition = targetPosition - videoDuration / 100;
                            }
                            // 最小不小于0
                            if (targetPosition < 0) {
                                targetPosition = 0;
                            }

                            if (targetPosition <  (int)(mPlayer.getCurrentPosition()) / 1000) {
                                icon.setImageResource(R.drawable.video_back);
                                text.setText("快退至 " + formatTime(targetPosition));
                            } else {
                                icon.setImageResource(R.drawable.video_forward);
                                text.setText("快进至 " + formatTime(targetPosition));
                            }
                            startProgressTimer();
                            return true;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            if (!isPrepare) {
                                return false;
                            }
                            // 快进
                            if (targetPosition == -1) {
                                targetPosition =  (int)(mPlayer.getCurrentPosition()) / 1000 + videoDuration / 100;
                            } else {
                                targetPosition = targetPosition + videoDuration / 100;
                            }
                            // 最大不超过最终时间-3秒
                            if (targetPosition >= videoDuration - 3) {
                                targetPosition = videoDuration - 3;
                            }

                            if (targetPosition <  (int)(mPlayer.getCurrentPosition()) / 1000) {
                                icon.setImageResource(R.drawable.video_back);
                                text.setText("快退至 " + formatTime(targetPosition));
                            } else {
                                icon.setImageResource(R.drawable.video_forward);
                                text.setText("快进至 " + formatTime(targetPosition));
                            }
                            startProgressTimer();
                            return true;
                    }
                }
                return false;
            }
        });
    }

    private void play(SurfaceHolder holder) {
        mPlayer = new IjkMediaPlayer();
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "async,cache,crypto,file,http,https,ijkhttphook,ijkinject,ijklivehook,ijklongurl,ijksegment,ijktcphook,pipe,rtp,tcp,tls,udp,ijkurlhook,data");
        //开启硬解码
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"soundtouch",1);
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"analyzeduration",3);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"packet-buffering",1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 100 * 1024);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"framedrop",5);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max_delay", 0);
        //mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 20);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        try {
            mPlayer.setDisplay(holder);
            mPlayer.setDataSource(videoUrl);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                // 播放下一集
                if (currentPartPosition != -1) {
                    Toast.makeText(IjkMediaPlayerActivity.this, "即将自动播放下一集", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().postSticky(new AutoNextEvent(currentPartPosition));
                    Observable.empty().delay(3000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {
                        @Override
                        public void run() throws Exception {
                            finish();
                        }
                    }).subscribe();
                }

            }
        });

        mPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                isPrepare = true;
                loadingViewRoot.setVisibility(View.GONE);
                seekbar.setMax((int) (mPlayer.getDuration() / 1000));

                videoDuration = (int) (mPlayer.getDuration() / 1000);

                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    videoCurrentPosition = (int) (mPlayer.getCurrentPosition() / 1000);
                                    seekbar.setProgress((int) (mPlayer.getCurrentPosition() / 1000));
                                    seekbar.setSecondaryProgress((int) (mPlayer.getVideoCachedDuration() / 1000));
                                    video_time.setText(
                                            formatTime((int) (mPlayer.getCurrentPosition() / 1000)) + " / " + formatTime((int) (mPlayer.getDuration() / 1000))
                                    );
                                }

                            }
                        });
                    }
                }, 0, 1000);

                if (lastPosition > 0) {
                    Toast.makeText(IjkMediaPlayerActivity.this, "正在跳转至历史播放位置", Toast.LENGTH_LONG).show();
                    mPlayer.seekTo(lastPosition * 1000);
                }
            }
        });

        mPlayer.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!isPrepare) {
                    return false;
                }
                if (menuRoot.getVisibility() == View.GONE) {
                    menuRoot.setVisibility(View.VISIBLE);
                    mMenuTimer = new Timer();
                    mMenuTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isFinishing()) {
                                        menuRoot.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }, 5000);
                } else {
                    menuRoot.setVisibility(View.GONE);
                    mMenuTimer.cancel();
                    mMenuTimer.purge();
                }
                return true;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_SPACE:
                if (!isPrepare) {
                    return false;
                }
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                } else {
                    mPlayer.start();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_META_LEFT:
            case KeyEvent.KEYCODE_CTRL_LEFT:
                if (!isPrepare) {
                    return false;
                }

                // 快退
                if (targetPosition == -1) {
                    targetPosition = (int) (mPlayer.getCurrentPosition() / 1000) - videoDuration / 100;
                } else {
                    targetPosition = targetPosition - videoDuration / 100;
                }
                // 最小不小于0
                if (targetPosition < 0) {
                    targetPosition = 0;
                }

                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                    Window dialogWindow = mProgressDialog.getWindow();
                    dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
                }

                ImageView icon = mProgressDialog.findViewById(R.id.dialog_progress_icon);
                TextView text = mProgressDialog.findViewById(R.id.dialog_progress_time);
                if (targetPosition < (int) (mPlayer.getCurrentPosition() / 1000)) {
                    icon.setImageResource(R.drawable.video_back);
                    text.setText("快退至 " + formatTime(targetPosition));
                } else {
                    icon.setImageResource(R.drawable.video_forward);
                    text.setText("快进至 " + formatTime(targetPosition));
                }
                startProgressTimer();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_CTRL_RIGHT:
            case KeyEvent.KEYCODE_META_RIGHT:
                if (!isPrepare) {
                    return false;
                }
                // 快进
                if (targetPosition == -1) {
                    targetPosition = (int) (mPlayer.getCurrentPosition() / 1000) + videoDuration / 100;
                } else {
                    targetPosition = targetPosition + videoDuration / 100;
                }

                // 最大不超过最终时间-3秒
                if (targetPosition >= videoDuration - 3) {
                    targetPosition = videoDuration - 3;
                }

                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }

                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                    Window dialogWindow2 = mProgressDialog.getWindow();
                    dialogWindow2.setBackgroundDrawableResource(android.R.color.transparent);
                }

                icon = mProgressDialog.findViewById(R.id.dialog_progress_icon);
                text = mProgressDialog.findViewById(R.id.dialog_progress_time);

                if (targetPosition < (int) (mPlayer.getCurrentPosition() / 1000)) {
                    icon.setImageResource(R.drawable.video_back);
                    text.setText("快退至 " + formatTime(targetPosition));
                } else {
                    icon.setImageResource(R.drawable.video_forward);
                    text.setText("快进至 " + formatTime(targetPosition));
                }
                startProgressTimer();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void startProgressTimer() {
        if (mProgressTimer != null) {
            mProgressTimer.cancel();
            mProgressTimer.purge();
        }
        mProgressTimer = new Timer();
        mProgressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        mPlayer.seekTo(targetPosition * 1000);
                        targetPosition = -1;
                    }
                });
            }
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("是否确认退出？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveHistory();
                        isSave = true;
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveHistory();
    }

    private void saveHistory() {
        if (isSave) {
            return;
        }

        // 保存历史
        VideoHistory videoHistory = new VideoHistory();

        // 历史最后只到视频99%
        if (videoCurrentPosition > (int) (videoDuration * 0.99)) {
            videoHistory.setLastPosition((int) (videoDuration * 0.99));
        } else {
            videoHistory.setLastPosition(videoCurrentPosition);
        }
        videoHistory.setTitle(title);
        videoHistory.setSubTitle(subTitle);
        videoHistory.setUrl(videoUrl);
        videoHistory.setDuration(videoDuration);
        videoHistory.setPicUrl(picUrl);

        LinkedList<VideoHistory> historyList = (LinkedList<VideoHistory>) InternalFileSaveUtil.getInstance(this).get("video_history");
        if (historyList == null) {
            historyList = new LinkedList<>();
        }
        historyList.add(0, videoHistory);
        // 同名视频只添加去除之前的历史
        if (historyList.size() > 1) {
            for (int i = 1 ; i < historyList.size() ; i++) {
                if (historyList.get(i).getTitle().equals(title)) {
                    historyList.remove(i);
                    break;
                }
            }
        }
        if (historyList.size() > Const.VIDEO_HISTORY_NUM) {
            historyList.remove(historyList.size() - 1);
        }
        InternalFileSaveUtil.getInstance(this).put("video_history", historyList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
        if (mMenuTimer != null) {
            mMenuTimer.cancel();
            mMenuTimer.purge();
        }
        if (mProgressTimer != null) {
            mProgressTimer.cancel();
            mProgressTimer.purge();
        }
        if (mPlayer != null) {
            mPlayer.release();
        }
    }

    private String formatTime(int time) {
        String result = "";
        int h = time / 3600;
        int m = (time % 3600) / 60;
        int s = time % 60;
        if (h > 0) {
            result = h + ":";
        }
        if (m < 10) {
            result = result + "0" + m + ":";
        } else {
            result = result + m + ":";
        }
        if (s < 10) {
            result = result + "0" + s;
        } else {
            result = result + s;
        }
        return result;
    }
}
