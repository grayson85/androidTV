package com.pxf.fftv.plus.contract.live;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.bean.M3uChannel;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.M3uParser;
import com.pxf.fftv.plus.common.ModernDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class M3uIptvActivity extends Activity {

    private static final String PREFS_NAME = "M3uIptvPrefs";
    private static final String KEY_CURRENT_SOURCE = "current_source";
    private static final String KEY_PLAYER_TYPE = "player_type";
    private static final String KEY_CUSTOM_URL_PREFIX = "custom_url_";

    // 5个默认M3U源
    private static final String[] DEFAULT_SOURCE_NAMES = {
            "Malaysia",
            "Taiwan",
            "Hong Kong",
            "China",
            "Other Countries"
    };
    private static final String[] DEFAULT_SOURCE_URLS = {
            "https://live.hacks.tools/iptv/languages/malaysia.m3u",
            "https://live.hacks.tools/tv/ipv4/categories/taiwan.m3u",
            "https://live.hacks.tools/tv/ipv4/categories/hong_kong.m3u",
            "https://live.hacks.tools/tv/iptv4.m3u",
            "https://live.hacks.tools/iptv/index.m3u"
    };

    private String[] sourceUrls = new String[5];
    private int currentSourceIndex = 0;

    private SurfaceView surfacePlay;
    private androidx.media3.ui.PlayerView playerView;
    private ProgressBar progress;
    private View menuPanel;
    private RecyclerView rvGroups;
    private RecyclerView rvChannels;
    private TextView tvCurrentChannel;
    private TextView btnSettings;
    private android.os.Handler osdHandler = new android.os.Handler();

    private SurfaceHolder mHolder;
    private IjkMediaPlayer mPlayer;
    private androidx.media3.exoplayer.ExoPlayer mExoPlayer;

    private Map<String, List<M3uChannel>> channelGroups;
    private List<String> groupNames = new ArrayList<>();
    private List<M3uChannel> currentChannels = new ArrayList<>();

    private M3uGroupAdapter groupAdapter;
    private M3uChannelAdapter channelAdapter;

    private String playerType = "IJK";
    private String currentPlayingUrl = "";
    private M3uChannel currentChannel;
    private int currentGroupIndex = 0;
    private int currentChannelIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3u_iptv);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

        initViews();
        loadPreferences();
        setupAdapters();
        loadM3uPlaylist();
    }

    private void initViews() {
        surfacePlay = findViewById(R.id.surface_play);
        playerView = findViewById(R.id.player_view);
        progress = findViewById(R.id.progress);
        menuPanel = findViewById(R.id.menu_panel);
        rvGroups = findViewById(R.id.rv_groups);
        rvChannels = findViewById(R.id.rv_channels);
        tvCurrentChannel = findViewById(R.id.tv_current_channel);
        btnSettings = findViewById(R.id.btn_settings);

        surfacePlay.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mHolder = holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mHolder = null;
            }
        });

        // 点击视频区域切换菜单
        surfacePlay.setOnClickListener(v -> toggleMenu());
        playerView.setOnClickListener(v -> toggleMenu());

        // 设置按钮
        btnSettings.setOnClickListener(v -> showSettingsDialog());
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentSourceIndex = prefs.getInt(KEY_CURRENT_SOURCE, 0);
        playerType = prefs.getString(KEY_PLAYER_TYPE, "IJK");

        // 加载每个源的URL（可能被用户自定义过）
        for (int i = 0; i < 5; i++) {
            sourceUrls[i] = prefs.getString(KEY_CUSTOM_URL_PREFIX + i, DEFAULT_SOURCE_URLS[i]);
        }
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(KEY_CURRENT_SOURCE, currentSourceIndex);
        editor.putString(KEY_PLAYER_TYPE, playerType);

        // 保存每个源的URL
        for (int i = 0; i < 5; i++) {
            editor.putString(KEY_CUSTOM_URL_PREFIX + i, sourceUrls[i]);
        }
        editor.apply();
    }

    private String getCurrentM3uUrl() {
        return sourceUrls[currentSourceIndex];
    }

    private void setupAdapters() {
        // 分组适配器
        groupAdapter = new M3uGroupAdapter(this, groupNames, (group, position) -> {
            currentGroupIndex = position;
            if (channelGroups != null && channelGroups.containsKey(group)) {
                currentChannels = channelGroups.get(group);
                channelAdapter.setChannels(currentChannels);

                // 自动选中第一个频道
                if (currentChannels.size() > 0) {
                    rvChannels.post(() -> {
                        if (rvChannels.getChildCount() > 0) {
                            rvChannels.getChildAt(0).requestFocus();
                        }
                    });
                }
            }
        });
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        rvGroups.setAdapter(groupAdapter);

        // 频道适配器
        channelAdapter = new M3uChannelAdapter(this, currentChannels,
                new M3uChannelAdapter.OnChannelClickListener() {
                    @Override
                    public void onChannelClick(M3uChannel channel, int position) {
                        currentChannelIndex = position;
                        playChannel(channel);
                    }

                    @Override
                    public void onChannelFocus(M3uChannel channel, int position) {
                        // 预览显示频道名
                    }
                });
        rvChannels.setLayoutManager(new LinearLayoutManager(this));
        rvChannels.setAdapter(channelAdapter);
    }

    private void loadM3uPlaylist() {
        progress.setVisibility(View.VISIBLE);

        Observable.create(emitter -> {
            try {
                Request request = new Request.Builder()
                        .url(getCurrentM3uUrl())
                        .header("User-Agent", "Mozilla/5.0")
                        .build();

                Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String content = response.body().string();
                    emitter.onNext(content);
                } else {
                    emitter.onError(new Exception("Failed to load M3U: " + response.code()));
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        content -> {
                            progress.setVisibility(View.GONE);
                            channelGroups = M3uParser.parse((String) content);

                            if (channelGroups.isEmpty()) {
                                Toast.makeText(this, "未找到频道", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            groupNames = new ArrayList<>(channelGroups.keySet());
                            groupAdapter.setGroups(groupNames);

                            // 选中第一个分组
                            if (groupNames.size() > 0) {
                                String firstGroup = groupNames.get(0);
                                currentChannels = channelGroups.get(firstGroup);
                                channelAdapter.setChannels(currentChannels);

                                // 给分组列表焦点
                                rvGroups.post(() -> {
                                    if (rvGroups.getChildCount() > 0) {
                                        rvGroups.getChildAt(0).requestFocus();
                                    }
                                });
                            }
                        },
                        error -> {
                            progress.setVisibility(View.GONE);
                            Toast.makeText(this, "加载失败: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        });
    }

    private void playChannel(M3uChannel channel) {
        if (channel == null || channel.getUrl() == null)
            return;

        currentChannel = channel;
        currentPlayingUrl = channel.getUrl();
        tvCurrentChannel.setText(channel.getName());

        menuPanel.setVisibility(View.GONE);
        showOsd(channel.getName());
        progress.setVisibility(View.VISIBLE);

        releasePlayer();
        play(channel.getUrl());
    }

    private void play(String url) {
        if (playerType.equals("EXO")) {
            playWithExo(url);
        } else {
            playWithIjk(url);
        }
    }

    private void playWithIjk(String url) {
        surfacePlay.setVisibility(View.VISIBLE);
        if (playerView != null)
            playerView.setVisibility(View.GONE);

        if (mHolder == null) {
            Toast.makeText(this, "播放器未就绪", Toast.LENGTH_SHORT).show();
            return;
        }

        mPlayer = new IjkMediaPlayer();
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist",
                "rtmp,crypto,file,http,https,tcp,tls,udp");
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 10 * 1024 * 1024);

        try {
            mPlayer.setDisplay(mHolder);
            mPlayer.setDataSource(url);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
        }

        mPlayer.setOnPreparedListener(mp -> {
            progress.setVisibility(View.GONE);
        });

        mPlayer.setOnErrorListener((mp, what, extra) -> {
            progress.setVisibility(View.GONE);
            Toast.makeText(this, "播放错误", Toast.LENGTH_SHORT).show();
            return true;
        });

        mPlayer.start();
    }

    private void playWithExo(String url) {
        surfacePlay.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
        playerView.setUseController(false);

        androidx.media3.exoplayer.trackselection.DefaultTrackSelector trackSelector = new androidx.media3.exoplayer.trackselection.DefaultTrackSelector(
                this);

        mExoPlayer = new androidx.media3.exoplayer.ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build();

        playerView.setPlayer(mExoPlayer);

        androidx.media3.datasource.DataSource.Factory dataSourceFactory = new androidx.media3.datasource.DefaultHttpDataSource.Factory()
                .setUserAgent("smtvplus")
                .setAllowCrossProtocolRedirects(true);

        androidx.media3.exoplayer.source.MediaSource videoSource;
        if (url.contains("m3u8")) {
            videoSource = new androidx.media3.exoplayer.hls.HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(androidx.media3.common.MediaItem.fromUri(url));
        } else {
            videoSource = new androidx.media3.exoplayer.source.ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(androidx.media3.common.MediaItem.fromUri(url));
        }

        mExoPlayer.setMediaSource(videoSource);
        mExoPlayer.prepare();
        mExoPlayer.setPlayWhenReady(true);

        mExoPlayer.addListener(new androidx.media3.common.Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == androidx.media3.common.Player.STATE_READY) {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.setDisplay(null);
            mPlayer.release();
            mPlayer = null;
        }
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void showOsd(String text) {
        tvCurrentChannel.setText(text);
        tvCurrentChannel.setVisibility(View.VISIBLE);
        osdHandler.removeCallbacksAndMessages(null);
        osdHandler.postDelayed(() -> {
            tvCurrentChannel.setVisibility(View.GONE);
        }, 3000); // 自动3秒后消失
    }

    private void toggleMenu() {
        if (menuPanel.getVisibility() == View.VISIBLE) {
            menuPanel.setVisibility(View.GONE);
        } else {
            menuPanel.setVisibility(View.VISIBLE);
            // 焦点定位到当前频道
            rvChannels.post(() -> {
                if (currentChannelIndex >= 0 && currentChannelIndex < currentChannels.size()) {
                    rvChannels.scrollToPosition(currentChannelIndex);
                    channelAdapter.setSelectedPosition(currentChannelIndex);
                    rvChannels.post(() -> {
                        RecyclerView.ViewHolder vh = rvChannels.findViewHolderForAdapterPosition(currentChannelIndex);
                        if (vh != null) {
                            vh.itemView.requestFocus();
                        } else if (rvChannels.getChildCount() > 0) {
                            rvChannels.getChildAt(0).requestFocus();
                        }
                    });
                } else if (rvChannels.getChildCount() > 0) {
                    rvChannels.getChildAt(0).requestFocus();
                }
            });
        }
    }

    private void showSettingsDialog() {
        String switchTo = playerType.equals("IJK") ? "EXO" : "IJK";
        String currentSource = DEFAULT_SOURCE_NAMES[currentSourceIndex];
        String[] options = {
                "切换源 (" + currentSource + ")",
                "编辑当前源地址",
                "切换到 " + switchTo + " 播放器"
        };

        ModernDialog.showList(this, "设置", options, position -> {
            if (position == 0) {
                showSourceSelectionDialog();
            } else if (position == 1) {
                showEditSourceUrlDialog();
            } else {
                playerType = playerType.equals("IJK") ? "EXO" : "IJK";
                savePreferences();
                Toast.makeText(this, "已切换到 " + playerType + " 播放器", Toast.LENGTH_SHORT).show();

                // 如果正在播放，重新播放
                if (!currentPlayingUrl.isEmpty()) {
                    releasePlayer();
                    play(currentPlayingUrl);
                }
            }
        });
    }

    private void showSourceSelectionDialog() {
        // 构建源选项（带当前标记）
        String[] sourceOptions = new String[5];
        for (int i = 0; i < 5; i++) {
            String marker = (i == currentSourceIndex) ? " ✓" : "";
            sourceOptions[i] = DEFAULT_SOURCE_NAMES[i] + marker;
        }

        ModernDialog.showList(this, "选择 M3U 源", sourceOptions, position -> {
            if (position != currentSourceIndex) {
                currentSourceIndex = position;
                savePreferences();
                Toast.makeText(this, "已切换到 " + DEFAULT_SOURCE_NAMES[position], Toast.LENGTH_SHORT).show();
                loadM3uPlaylist();
            }
        });
    }

    private void showEditSourceUrlDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setTitle("编辑 " + DEFAULT_SOURCE_NAMES[currentSourceIndex] + " 地址");

        final EditText input = new EditText(this);
        input.setText(sourceUrls[currentSourceIndex]);
        input.setTextColor(0xFF000000);
        input.setHintTextColor(0xFF888888);
        input.setBackgroundColor(0xFFFFFFFF);
        input.setPadding(20, 20, 20, 20);
        input.setSingleLine(false);
        input.setMaxLines(3);
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String url = input.getText().toString().trim();
            if (!url.isEmpty()) {
                sourceUrls[currentSourceIndex] = url;
                savePreferences();
                loadM3uPlaylist();
            }
        });

        builder.setNeutralButton("恢复默认", (dialog, which) -> {
            sourceUrls[currentSourceIndex] = DEFAULT_SOURCE_URLS[currentSourceIndex];
            savePreferences();
            Toast.makeText(this, "已恢复默认地址", Toast.LENGTH_SHORT).show();
            loadM3uPlaylist();
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                toggleMenu();
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (menuPanel.getVisibility() == View.VISIBLE) {
                    if (currentChannel != null) {
                        menuPanel.setVisibility(View.GONE);
                        return true;
                    }
                }
                // 退出确认
                ModernDialog.showConfirm(this, "退出 IPTV？",
                        () -> finish(),
                        () -> {
                        });
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
