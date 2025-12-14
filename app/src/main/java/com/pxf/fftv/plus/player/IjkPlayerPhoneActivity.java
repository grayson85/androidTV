package com.pxf.fftv.plus.player;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.InternalFileSaveUtil;
import com.pxf.fftv.plus.common.ModernDialog;
import com.pxf.fftv.plus.contract.history.VideoHistory;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class IjkPlayerPhoneActivity extends AppCompatActivity {

    SurfaceView surface_play;

    TextView titleView;

    TextView messageView;

    View controlsRoot;

    SeekBar seekbar;

    TextView video_time;

    TextView loadingText;

    View loadingViewRoot;

    ImageView btnPlayPause;

    ImageView btnNextEpisode;

    ImageView btnPrevEpisode;

    private IjkMediaPlayer mPlayer;

    private String videoUrl;
    private String title;
    private String subTitle;
    private String picUrl;
    private int currentPartPosition;
    private int lastPosition;

    private Timer mTimer = new Timer();
    private Timer mControlsTimer;

    private boolean isPrepare = false;
    private int videoDuration = 0;
    private int videoCurrentPosition = 0;
    private boolean isSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_player_phone);

        surface_play = findViewById(R.id.surface_play);
        titleView = findViewById(R.id.title);
        messageView = findViewById(R.id.message);
        controlsRoot = findViewById(R.id.controls_root);
        seekbar = findViewById(R.id.seekbar);
        video_time = findViewById(R.id.video_time);
        loadingText = findViewById(R.id.loading_text);
        loadingViewRoot = findViewById(R.id.loading_view_root);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnNextEpisode = findViewById(R.id.btn_next_episode);
        btnPrevEpisode = findViewById(R.id.btn_prev_episode);

        btnPlayPause.setOnClickListener(v -> onPlayPauseClick());
        findViewById(R.id.btn_skip_back).setOnClickListener(v -> onSkipBackClick());
        findViewById(R.id.btn_skip_forward).setOnClickListener(v -> onSkipForwardClick());
        btnPrevEpisode.setOnClickListener(v -> onPrevEpisodeClick());
        btnNextEpisode.setOnClickListener(v -> onNextEpisodeClick());

        View btnSwitchPlayer = findViewById(R.id.btn_switch_player);
        if (btnSwitchPlayer != null) {
            btnSwitchPlayer.setOnClickListener(v -> showSwitchPlayerDialog());
        }

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
        loadingText.setText("准备播放 <<" + title + ">> " + subTitle + "...");

        // Show/hide episode navigation buttons
        if (currentPartPosition != -1) {
            btnNextEpisode.setVisibility(View.VISIBLE);
            btnPrevEpisode.setVisibility(View.VISIBLE);
        } else {
            btnNextEpisode.setVisibility(View.GONE);
            btnPrevEpisode.setVisibility(View.GONE);
        }

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
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mPlayer != null) {
                    mPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        controlsRoot.setVisibility(View.GONE);
    }

    private void play(SurfaceHolder holder) {
        mPlayer = new IjkMediaPlayer();
        Toast.makeText(IjkPlayerPhoneActivity.this, "IjkPlayer (Phone)", Toast.LENGTH_SHORT).show();

        // Set IjkPlayer options
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist",
                "async,cache,crypto,file,http,https,ijkhttphook,ijkinject,ijklive hook,ijklongurl,ijksegment,ijktcphook,pipe,rtp,tcp,tls,udp,ijkurlhook,data");
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 3);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1000 * 1024);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max_delay", 0);
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
                if (currentPartPosition != -1) {
                    Toast.makeText(IjkPlayerPhoneActivity.this, "即将自动播放下一集", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().postSticky(new AutoNextEvent(currentPartPosition));
                    Observable.empty().delay(3000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete(new Action() {
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
                                            formatTime((int) (mPlayer.getCurrentPosition() / 1000)) + " / "
                                                    + formatTime((int) (mPlayer.getDuration() / 1000)));

                                    // Update play/pause button
                                    if (mPlayer.isPlaying()) {
                                        btnPlayPause.setImageResource(R.drawable.ic_pause);
                                    } else {
                                        btnPlayPause.setImageResource(R.drawable.ic_play);
                                    }
                                }
                            }
                        });
                    }
                }, 0, 1000);

                if (lastPosition > 0) {
                    Toast.makeText(IjkPlayerPhoneActivity.this, "正在跳转至历史播放位置", Toast.LENGTH_LONG).show();
                    mPlayer.seekTo(lastPosition * 1000);
                }
            }
        });

        mPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                Toast.makeText(IjkPlayerPhoneActivity.this, "播放错误", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mPlayer.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            toggleControls();
        }
        return super.onTouchEvent(event);
    }

    private void toggleControls() {
        if (controlsRoot.getVisibility() == View.VISIBLE) {
            hideControls();
        } else {
            showControls();
        }
    }

    private void showControls() {
        controlsRoot.setVisibility(View.VISIBLE);
        startControlsTimer();
    }

    private void hideControls() {
        controlsRoot.setVisibility(View.GONE);
        if (mControlsTimer != null) {
            mControlsTimer.cancel();
            mControlsTimer.purge();
        }
    }

    private void startControlsTimer() {
        if (mControlsTimer != null) {
            mControlsTimer.cancel();
            mControlsTimer.purge();
        }
        mControlsTimer = new Timer();
        mControlsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            hideControls();
                        }
                    }
                });
            }
        }, 5000);
    }

    public void onPlayPauseClick() {
        if (mPlayer != null && isPrepare) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                btnPlayPause.setImageResource(R.drawable.ic_play);
            } else {
                mPlayer.start();
                btnPlayPause.setImageResource(R.drawable.ic_pause);
            }
        }
        startControlsTimer();
    }

    public void onSkipBackClick() {
        if (mPlayer != null && isPrepare) {
            int targetPos = (int) (mPlayer.getCurrentPosition() / 1000) - 10;
            if (targetPos < 0)
                targetPos = 0;
            mPlayer.seekTo(targetPos * 1000);
            Toast.makeText(this, "-10s", Toast.LENGTH_SHORT).show();
        }
        startControlsTimer();
    }

    public void onSkipForwardClick() {
        if (mPlayer != null && isPrepare) {
            int targetPos = (int) (mPlayer.getCurrentPosition() / 1000) + 10;
            if (targetPos >= videoDuration)
                targetPos = videoDuration - 3;
            mPlayer.seekTo(targetPos * 1000);
            Toast.makeText(this, "+10s", Toast.LENGTH_SHORT).show();
        }
        startControlsTimer();
    }

    public void onPrevEpisodeClick() {
        if (currentPartPosition > 0) {
            Toast.makeText(this, "播放上一集", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().postSticky(new AutoNextEvent(currentPartPosition - 2)); // -2 because it will be
                                                                                          // incremented
            finish();
        } else {
            Toast.makeText(this, "已经是第一集了", Toast.LENGTH_SHORT).show();
        }
    }

    public void onNextEpisodeClick() {
        if (currentPartPosition != -1) {
            Toast.makeText(this, "播放下一集", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().postSticky(new AutoNextEvent(currentPartPosition));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        ModernDialog.showConfirm(this, "是否确认退出？",
                () -> {
                    saveHistory();
                    isSave = true;
                    finish();
                },
                () -> {
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume player when returning from background
        if (mPlayer != null && isPrepare) {
            mPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause player when going to background to prevent destruction
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
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

        VideoHistory videoHistory = new VideoHistory();

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

        LinkedList<VideoHistory> historyList = (LinkedList<VideoHistory>) InternalFileSaveUtil.getInstance(this)
                .get("video_history");
        if (historyList == null) {
            historyList = new LinkedList<>();
        }
        historyList.add(0, videoHistory);

        if (historyList.size() > 1) {
            for (int i = 1; i < historyList.size(); i++) {
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
        if (mControlsTimer != null) {
            mControlsTimer.cancel();
            mControlsTimer.purge();
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

    private void showSwitchPlayerDialog() {
        final String[] items = { "原生播放器", "IJK播放器", "EXO播放器" };
        final String[] engines = { Const.PLAY_1, Const.PLAY_3, Const.PLAY_4 };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ModernAlertDialog);
        builder.setTitle("选择播放器");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                com.pxf.fftv.plus.model.Model.getData().setPlayerEngine(IjkPlayerPhoneActivity.this, engines[which]);
                Toast.makeText(IjkPlayerPhoneActivity.this, "已切换为 " + items[which], Toast.LENGTH_SHORT).show();

                // Restart playback with new engine
                if (mPlayer != null) {
                    mPlayer.release();
                    mPlayer = null;
                }

                IVideoPlayer player = VideoPlayer.getVideoPlayer(IjkPlayerPhoneActivity.this);
                player.play(IjkPlayerPhoneActivity.this, videoUrl, title, subTitle, currentPartPosition, picUrl,
                        lastPosition, 0);
                finish();
            }
        });
        builder.show();
    }
}
