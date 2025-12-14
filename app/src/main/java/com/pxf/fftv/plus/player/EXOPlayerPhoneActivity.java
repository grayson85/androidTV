package com.pxf.fftv.plus.player;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.DefaultHlsExtractorFactory;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.PlayerView;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.InternalFileSaveUtil;
import com.pxf.fftv.plus.common.ModernDialog;
import com.pxf.fftv.plus.contract.history.VideoHistory;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

@UnstableApi
public class EXOPlayerPhoneActivity extends AppCompatActivity {

    PlayerView player_view;

    TextView titleView;

    TextView messageView;

    View menuRoot;

    SeekBar seekbar;

    TextView loadingText;

    View loadingViewRoot;

    private String videoUrl;
    private String title;
    private String subTitle;
    private String picUrl;
    private int currentPartPosition;
    private int lastPosition;
    private int videoId;

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

    private ExoPlayer mPlayer;
    private DefaultTrackSelector mTrackSelector;
    private int currentAudioTrack = 0;

    ImageView btnNextEpisode;

    ImageView btnPrevEpisode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player_phone);

        player_view = findViewById(R.id.player_view);
        titleView = findViewById(R.id.title);
        messageView = findViewById(R.id.message);
        menuRoot = findViewById(R.id.menu);
        seekbar = findViewById(R.id.seekbar);
        loadingText = findViewById(R.id.loading_text);
        loadingViewRoot = findViewById(R.id.loading_view_root);
        btnNextEpisode = findViewById(R.id.btn_next_episode);
        btnPrevEpisode = findViewById(R.id.btn_prev_episode);

        View btnSkipBack = findViewById(R.id.btn_skip_back);
        if (btnSkipBack != null) {
            btnSkipBack.setOnClickListener(v -> onSkipBackClick());
        }
        View btnSkipForward = findViewById(R.id.btn_skip_forward);
        if (btnSkipForward != null) {
            btnSkipForward.setOnClickListener(v -> onSkipForwardClick());
        }
        if (btnPrevEpisode != null) {
            btnPrevEpisode.setOnClickListener(v -> onPrevEpisodeClick());
        }
        if (btnNextEpisode != null) {
            btnNextEpisode.setOnClickListener(v -> onNextEpisodeClick());
        }
        View btnPlayPause = findViewById(R.id.btn_play_pause);
        if (btnPlayPause != null) {
            btnPlayPause.setOnClickListener(v -> onPlayPauseClick());
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

        videoUrl = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_URL);
        Toast.makeText(EXOPlayerPhoneActivity.this, "EXOPlayer", Toast.LENGTH_SHORT).show();
        title = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_TITLE);
        subTitle = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_SUB_TITLE);
        picUrl = getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_PIC);
        currentPartPosition = getIntent().getIntExtra(VideoPlayer.KEY_VIDEO_CURRENT_PART, -1);
        lastPosition = getIntent().getIntExtra(VideoPlayer.KEY_VIDEO_LAST_POSITION, -1);
        videoId = getIntent().getIntExtra(VideoPlayer.KEY_VIDEO_ID, 0);

        titleView.setText(title);
        messageView.setText(subTitle);
        loadingText.setText("准备播放 <<" + title + ">> " + subTitle + "...");

        mTrackSelector = new DefaultTrackSelector(this);
        mPlayer = new ExoPlayer.Builder(this).setTrackSelector(mTrackSelector).build();
        player_view.setPlayer(mPlayer);

        mPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY && loadingViewRoot.getVisibility() == View.VISIBLE) {
                    // 准备开始播放
                    isPrepare = true;
                    loadingViewRoot.setVisibility(View.GONE);
                    seekbar.setMax((int) (mPlayer.getDuration()) / 1000);
                    videoDuration = (int) (mPlayer.getDuration()) / 1000;

                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isFinishing()) {
                                        videoCurrentPosition = (int) (mPlayer.getCurrentPosition()) / 1000;
                                    }

                                }
                            });
                        }
                    }, 0, 1000);

                    mPlayer.setPlayWhenReady(true);

                    if (lastPosition > 0) {
                        Toast.makeText(EXOPlayerPhoneActivity.this, "正在跳转至历史播放位置", Toast.LENGTH_LONG).show();
                        mPlayer.seekTo(lastPosition * 1000);
                    }
                }
                if (playbackState == Player.STATE_ENDED) {
                    // 播放下一集
                    if (currentPartPosition != -1) {
                        Toast.makeText(EXOPlayerPhoneActivity.this, "即将自动播放下一集", Toast.LENGTH_SHORT).show();
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
            }
        });

        // player_view.setUseController(false);
        MediaSource videoSource;

        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(Util.getUserAgent(this, "fftvplus"))
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setAllowCrossProtocolRedirects(true);
        DefaultHlsExtractorFactory defaultHlsExtractorFactory = new DefaultHlsExtractorFactory();
        if (videoUrl.endsWith("m3u8")) {
            videoSource = new HlsMediaSource.Factory(dataSourceFactory).setExtractorFactory(defaultHlsExtractorFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)));
        } else {
            videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)));
        }

        mPlayer.setMediaSource(videoSource);
        mPlayer.prepare();

        // Wire up exo_custom controller elements
        View exoSwitchPlayer = player_view.findViewById(R.id.custom_switch_player);
        if (exoSwitchPlayer != null) {
            exoSwitchPlayer.setOnClickListener(v -> showSwitchPlayerDialog());
        }

        // Set title and subtitle in the exo_custom controller
        TextView exoTitle = player_view.findViewById(R.id.custom_title);
        if (exoTitle != null) {
            exoTitle.setText(title);
        }
        TextView exoSubtitle = player_view.findViewById(R.id.custom_subtitle);
        if (exoSubtitle != null) {
            exoSubtitle.setText(subTitle);
        }

        // Wire up Prev/Next Episode buttons from exo_custom controller
        View customPrevEpisode = player_view.findViewById(R.id.custom_prev_episode);
        View customNextEpisode = player_view.findViewById(R.id.custom_next_episode);

        if (customPrevEpisode != null) {
            customPrevEpisode.setOnClickListener(v -> onPrevEpisodeClick());
        }
        if (customNextEpisode != null) {
            customNextEpisode.setOnClickListener(v -> onNextEpisodeClick());
        }

        // Show/hide episode navigation buttons based on whether we have episodes
        if (currentPartPosition != -1) {
            if (customNextEpisode != null)
                customNextEpisode.setVisibility(View.VISIBLE);
            if (customPrevEpisode != null)
                customPrevEpisode.setVisibility(View.VISIBLE);
        } else {
            if (customNextEpisode != null)
                customNextEpisode.setVisibility(View.GONE);
            if (customPrevEpisode != null)
                customPrevEpisode.setVisibility(View.GONE);
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume player when returning from background
        if (mPlayer != null && !mPlayer.isPlaying() && isPrepare) {
            mPlayer.play();
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
        videoHistory.setId(videoId);

        android.util.Log.d("SaveHistory", "Saving history: title=" + title + ", url=" + videoUrl + ", id=" + videoId);

        LinkedList<VideoHistory> historyList = (LinkedList<VideoHistory>) InternalFileSaveUtil.getInstance(this)
                .get("video_history");
        if (historyList == null) {
            historyList = new LinkedList<>();
            android.util.Log.d("SaveHistory", "Created new history list");
        } else {
            android.util.Log.d("SaveHistory", "Loaded existing history list with " + historyList.size() + " items");
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
        boolean saved = InternalFileSaveUtil.getInstance(this).put("video_history", historyList);
        android.util.Log.d("SaveHistory", "History saved: " + saved + ", total items: " + historyList.size());
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

    public void onSwitchTrackClick() {
        // Simplified track switching for Media3
        Toast.makeText(this, "音轨切换", Toast.LENGTH_LONG).show();
    }

    public void onSkipBackClick() {
        if (mPlayer != null && isPrepare) {
            long targetPos = mPlayer.getCurrentPosition() - 10000;
            if (targetPos < 0)
                targetPos = 0;
            mPlayer.seekTo(targetPos);
            Toast.makeText(this, "-10s", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPlayPauseClick() {
        if (mPlayer != null && isPrepare) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            } else {
                mPlayer.play();
            }
            updatePlayPauseButton();
        }
    }

    private void updatePlayPauseButton() {
        View btnPlayPause = findViewById(R.id.btn_play_pause);
        if (btnPlayPause != null && mPlayer != null) {
            if (mPlayer.isPlaying()) {
                ((android.widget.ImageView) btnPlayPause).setImageResource(android.R.drawable.ic_media_pause);
            } else {
                ((android.widget.ImageView) btnPlayPause).setImageResource(android.R.drawable.ic_media_play);
            }
        }
    }

    public void onSkipForwardClick() {
        if (mPlayer != null && isPrepare) {
            long targetPos = mPlayer.getCurrentPosition() + 10000;
            if (targetPos >= mPlayer.getDuration())
                targetPos = mPlayer.getDuration() - 3000;
            mPlayer.seekTo(targetPos);
            Toast.makeText(this, "+10s", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPrevEpisodeClick() {
        if (currentPartPosition > 0) {
            Toast.makeText(this, "播放上一集", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().postSticky(new AutoNextEvent(currentPartPosition - 2));
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

    private void showSwitchPlayerDialog() {
        final String[] items = { "原生播放器", "IJK播放器", "EXO播放器" };
        final String[] engines = { Const.PLAY_1, Const.PLAY_3, Const.PLAY_4 };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("选择播放器");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                com.pxf.fftv.plus.model.Model.getData().setPlayerEngine(EXOPlayerPhoneActivity.this, engines[which]);
                Toast.makeText(EXOPlayerPhoneActivity.this, "已切换为 " + items[which], Toast.LENGTH_SHORT).show();

                // Restart playback with new engine
                if (mPlayer != null) {
                    mPlayer.release();
                    mPlayer = null;
                }

                IVideoPlayer player = VideoPlayer.getVideoPlayer(EXOPlayerPhoneActivity.this);
                player.play(EXOPlayerPhoneActivity.this, videoUrl, title, subTitle, currentPartPosition, picUrl,
                        lastPosition, 0);
                finish();
            }
        });
        builder.show();
    }
}
