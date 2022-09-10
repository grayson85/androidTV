package com.pxf.fftv.plus.player;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
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

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.InternalFileSaveUtil;
import com.pxf.fftv.plus.contract.history.VideoHistory;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

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

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static com.google.android.exoplayer2.C.TRACK_TYPE_AUDIO;
import static com.google.android.exoplayer2.RendererCapabilities.FORMAT_HANDLED;
import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES;
import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_IGNORE_SPLICE_INFO_STREAM;
import static com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_PLAYABLE_TRACKS;

public class EXOPlayerActivity extends AppCompatActivity {

    @BindView(R.id.video_time)
    TextView video_time;

    @BindView(R.id.player_view)
    PlayerView player_view;

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

    @BindView(R.id.exo_player_iv_switch_track)
    View exo_player_iv_switch_track;

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

    private SimpleExoPlayer mPlayer;
    private DefaultTrackSelector mTrackSelector;
    private DefaultRenderersFactory mRenderFactory;
    private int currentAudioTrack = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);
        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

        if (Const.FEATURE_6) {
            exo_player_iv_switch_track.setVisibility(View.VISIBLE);
        } else {
            exo_player_iv_switch_track.setVisibility(View.GONE);
        }

        videoUrl = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_URL);
        Log.d("EXOPlayer", videoUrl);
        title = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_TITLE);
        subTitle = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_SUB_TITLE);
        picUrl = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_PIC);
        currentPartPosition = getIntent().getIntExtra(VideoPlayer.KEY_VIDEO_CURRENT_PART, -1);
        lastPosition = getIntent().getIntExtra(VideoPlayer.KEY_VIDEO_LAST_POSITION, -1);

        titleView.setText(title);
        messageView.setText(subTitle);
        loadingText.setText("准备播放" + title + " " + subTitle + "...");

        mTrackSelector = new DefaultTrackSelector(this);
        mRenderFactory = new DefaultRenderersFactory(this);
        mRenderFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        mPlayer = new SimpleExoPlayer.Builder(this, mRenderFactory).setTrackSelector(mTrackSelector).build();
        player_view.setPlayer(mPlayer);

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
                                targetPosition = (int) (mPlayer.getCurrentPosition()) / 1000 - videoDuration / 100;
                            } else {
                                targetPosition = targetPosition - videoDuration / 100;
                            }
                            // 最小不小于0
                            if (targetPosition < 0) {
                                targetPosition = 0;
                            }

                            if (targetPosition < (int) (mPlayer.getCurrentPosition()) / 1000) {
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
                                targetPosition = (int) (mPlayer.getCurrentPosition()) / 1000 + videoDuration / 100;
                            } else {
                                targetPosition = targetPosition + videoDuration / 100;
                            }
                            // 最大不超过最终时间-3秒
                            if (targetPosition >= videoDuration - 3) {
                                targetPosition = videoDuration - 3;
                            }

                            if (targetPosition < (int) (mPlayer.getCurrentPosition()) / 1000) {
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

        // videoUrl = "https://meet1-1251849728.cos.ap-beijing-1.myqcloud.com/%E8%A7%86%E9%A2%91/%E5%91%A8%E6%B7%B1-%E6%9D%A5%E4%B8%8D%E5%8F%8A%E5%8B%87%E6%95%A2-%E5%9B%BD%E8%AF%AD-%E6%B5%81%E8%A1%8C.mkv";

        // videoUrl = "http://storage.googleapis.com/videos.siku.org/10005/dash/master.m3u8";

        mPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_READY && loadingViewRoot.getVisibility() == View.VISIBLE) {
                    // 准备开始播放
                    isPrepare = true;
                    loadingViewRoot.setVisibility(View.GONE);
                    seekbar.setMax((int) (mPlayer.getDuration()) / 1000);
                    videoDuration = (int) (mPlayer.getDuration()) / 1000;
                    Log.d("EXOPlayer", "loading");
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isFinishing()) {
                                        videoCurrentPosition = (int) (mPlayer.getCurrentPosition()) / 1000;
                                        seekbar.setProgress((int) (mPlayer.getCurrentPosition()) / 1000);
                                        seekbar.setSecondaryProgress((int) mPlayer.getBufferedPosition());
                                        video_time.setText(
                                                formatTime((int) (mPlayer.getCurrentPosition()) / 1000) + " / " + formatTime((int) (mPlayer.getDuration()) / 1000)
                                        );
                                    }

                                }
                            });
                        }
                    }, 0, 1000);

                    mPlayer.setPlayWhenReady(true);

                    if (lastPosition > 0) {
                        Toast.makeText(EXOPlayerActivity.this, "正在跳转至历史播放位置", Toast.LENGTH_LONG).show();
                        mPlayer.seekTo(lastPosition * 1000);
                    }

                    if (mTrackSelector.getCurrentMappedTrackInfo().getRendererSupport(TRACK_TYPE_AUDIO) != RENDERER_SUPPORT_PLAYABLE_TRACKS) {
                        Toast.makeText(EXOPlayerActivity.this, "设备不支持该资源音频的解码", Toast.LENGTH_LONG).show();
                    }
                }
                if (playbackState == Player.STATE_ENDED) {
                    // 播放下一集
                    if (currentPartPosition != -1) {
                        Toast.makeText(EXOPlayerActivity.this, "即将自动播放下一集", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().postSticky(new AutoNextEvent(currentPartPosition));
                        Observable.empty().delay(3000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                finish();
                            }
                        }).subscribe();
                    }
                }
            }
        });


        player_view.setUseController(false);
        MediaSource videoSource;

        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(this, "smtvplus"), null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true);
        DefaultHlsExtractorFactory defaultHlsExtractorFactory = new DefaultHlsExtractorFactory(FLAG_IGNORE_SPLICE_INFO_STREAM,true);
        if (videoUrl.endsWith("m3u8")) {
            videoSource = new HlsMediaSource.Factory(dataSourceFactory).setExtractorFactory(defaultHlsExtractorFactory).createMediaSource(Uri.parse(videoUrl));
        } else {
            videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoUrl));
        }

        mPlayer.prepare(videoSource);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
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
                    return true;
                } else {
                    return false;
                }
            case KeyEvent.KEYCODE_DPAD_DOWN:
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
                if (menuRoot.getVisibility() == View.GONE) {
                    if (!isPrepare) {
                        return false;
                    }
                    if (mPlayer.isPlaying()) {
                        mPlayer.setPlayWhenReady(false);
                    } else {
                        mPlayer.setPlayWhenReady(true);
                    }
                    return true;
                } else {
                    return false;
                }
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_SPACE:
                if (!isPrepare) {
                    return false;
                }
                if (mPlayer.isPlaying()) {
                    mPlayer.setPlayWhenReady(false);
                } else {
                    mPlayer.setPlayWhenReady(true);
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

    @OnClick(R.id.exo_player_iv_switch_track)
    public void onSwitchTrackClick() {
        if (++currentAudioTrack >= mTrackSelector.getCurrentMappedTrackInfo().getTrackGroups(TRACK_TYPE_AUDIO).length) {
            currentAudioTrack = 0;
        }

        if (mTrackSelector.getCurrentMappedTrackInfo().getTrackSupport(TRACK_TYPE_AUDIO, currentAudioTrack, 0) == FORMAT_HANDLED) {
            mTrackSelector.setParameters(mTrackSelector.buildUponParameters().setSelectionOverride(TRACK_TYPE_AUDIO, mTrackSelector.getCurrentMappedTrackInfo().getTrackGroups(TRACK_TYPE_AUDIO),
                    new DefaultTrackSelector.SelectionOverride(currentAudioTrack, 0)));
            if (currentAudioTrack == 0) {
                Toast.makeText(this, "切换为原唱", Toast.LENGTH_LONG).show();
            }
            if (currentAudioTrack == 1) {
                Toast.makeText(this, "切换为伴唱", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "设备不支持切换音频的解码", Toast.LENGTH_LONG).show();
        }
    }
}
