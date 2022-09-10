package com.pxf.fftv.plus.player;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class NativePlayerActivity extends AppCompatActivity {

    @BindView(R.id.video_time)
    TextView video_time;

    @BindView(R.id.video_view)
    VideoView videoView;

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
        setContentView(R.layout.activity_native_player);
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
        loadingText.setText("准备播放" + title + " " + subTitle + "...");

        videoView.setVideoURI(Uri.parse(videoUrl));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                isPrepare = true;
                loadingViewRoot.setVisibility(View.GONE);
                seekbar.setMax((int) (videoView.getDuration() / 1000));

                videoDuration = videoView.getDuration() / 1000;

                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    videoCurrentPosition = videoView.getCurrentPosition() / 1000;
                                    seekbar.setProgress(videoView.getCurrentPosition() / 1000);
                                    seekbar.setSecondaryProgress(videoDuration * videoView.getBufferPercentage() / 100);
                                    video_time.setText(
                                            formatTime(videoView.getCurrentPosition() / 1000) + " / " + formatTime(videoView.getDuration() / 1000)
                                    );
                                }

                            }
                        });
                    }
                }, 0, 1000);

                if (lastPosition > 0) {
                    Toast.makeText(NativePlayerActivity.this, "正在跳转至历史播放位置", Toast.LENGTH_LONG).show();
                    videoView.seekTo(lastPosition * 1000);
                    videoView.start();
                }
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // 播放下一集
                if (currentPartPosition != -1) {
                    Toast.makeText(NativePlayerActivity.this, "即将自动播放下一集", Toast.LENGTH_SHORT).show();
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

        videoView.start();

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
                                targetPosition = videoView.getCurrentPosition() / 1000 - videoDuration / 100;
                            } else {
                                targetPosition = targetPosition - videoDuration / 100;
                            }
                            // 最小不小于0
                            if (targetPosition < 0) {
                                targetPosition = 0;
                            }

                            if (targetPosition < videoView.getCurrentPosition() / 1000) {
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
                                targetPosition = videoView.getCurrentPosition() / 1000 + videoDuration / 100;
                            } else {
                                targetPosition = targetPosition + videoDuration / 100;
                            }
                            // 最大不超过最终时间-3秒
                            if (targetPosition >= videoDuration - 3) {
                                targetPosition = videoDuration - 3;
                            }

                            if (targetPosition < videoView.getCurrentPosition() / 1000) {
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
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    videoView.start();
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
                    targetPosition = videoView.getCurrentPosition() / 1000 - videoDuration / 100;
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
                if (targetPosition < videoView.getCurrentPosition() / 1000) {
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
                    targetPosition = videoView.getCurrentPosition() / 1000 + videoDuration / 100;
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

                if (targetPosition < videoView.getCurrentPosition() / 1000) {
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
                        videoView.seekTo(targetPosition * 1000);
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
