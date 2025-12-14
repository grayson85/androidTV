package com.pxf.fftv.plus.contract.live;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;

import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.ModernDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class IjkTVLiveActivity extends Activity implements TitleAdapter.OnClickListener {

    private IjkMediaPlayer mPlayer;

    SurfaceView surface_play;

    ProgressBar progress;

    View tv_live_menu;

    RecyclerView tv_live_recycler_view_title;

    RecyclerView tv_live_recycler_view_sub_title;

    RecyclerView rv_category_filter;

    RecyclerView rv_language_filter;

    private SurfaceHolder mHolder;

    private List<com.pxf.fftv.plus.bean.MatchBean.DataBean.ListBean> mMatchList = new ArrayList<>();
    private ArrayList<String> mTitleList = new ArrayList<>();
    private ArrayList<ArrayList<String>> mSubTitleList = new ArrayList<>();

    private MatchAdapter mMatchAdapter;
    private TitleAdapter mSubTitleAdapter;
    private FilterAdapter categoryAdapter;
    private FilterAdapter languageAdapter;

    private int currentTitlePosition = 0;
    private int selectedCategoryId = -1; // No default - user must select category
    private int selectedType = 2; // Default: Basketball
    private String selectedLanguage = "zh"; // Default: Chinese
    private String currentPlayerType = "IJK"; // IJK or EXO
    private String currentPlayingUrl = ""; // Track current playing URL for player switching

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_tv_live);

        surface_play = findViewById(R.id.surface_play);
        progress = findViewById(R.id.progress);
        tv_live_menu = findViewById(R.id.tv_live_menu);
        tv_live_recycler_view_title = findViewById(R.id.tv_live_recycler_view_title);
        tv_live_recycler_view_sub_title = findViewById(R.id.tv_live_recycler_view_sub_title);
        rv_category_filter = findViewById(R.id.rv_category_filter);
        rv_language_filter = findViewById(R.id.rv_language_filter);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

        // Show language selection dialog on first entry
        showLanguageSelectionDialog();

        surface_play.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mHolder = holder;
                // Don't auto-load - wait for user to select category
                // getLiveListAndPlay(); // Removed auto-load
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

        // Add touch support for mobile users
        surface_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_live_menu.getVisibility() == View.VISIBLE) {
                    tv_live_menu.setVisibility(View.GONE);
                } else {
                    tv_live_menu.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initView() {
        // Custom LayoutManager that pre-renders ALL items for TV focus navigation
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                // Force RecyclerView to render all items off-screen
                // This ensures focus can navigate to items beyond the visible area
                return 10000; // Large enough to render all items
            }
        };
        tv_live_recycler_view_title.setLayoutManager(layoutManager);

        // Critical for TV navigation - pre-render more items for focus
        tv_live_recycler_view_title.setItemViewCacheSize(50);
        tv_live_recycler_view_title.setHasFixedSize(false);

        tv_live_recycler_view_sub_title.setLayoutManager(new LinearLayoutManager(this));
        tv_live_menu.setVisibility(View.GONE);
    }

    private void showLanguageSelectionDialog() {
        String[] languages = { "中文 (Chinese)", "English" };

        ModernDialog.showList(this, "选择语言 / Select Language", languages, position -> {
            String language = position == 0 ? "zh" : "en";
            selectedLanguage = language;

            // Initialize after language selection
            initView();
            initFilters();
            initPlayerSwitcher();

            // Add touch support for phones (tap video area to toggle menu)
            findViewById(R.id.surface_play).setOnClickListener(v -> toggleMenu());
            mPlayerView = findViewById(R.id.player_view);
            if (mPlayerView != null) {
                mPlayerView.setOnClickListener(v -> toggleMenu());
            }

            // Hide progress and show menu since we're not auto-loading
            progress.setVisibility(View.GONE);
            tv_live_menu.setVisibility(View.VISIBLE);

            // Request focus on first category item for D-pad navigation
            rv_category_filter.post(() -> {
                if (rv_category_filter.getChildCount() > 0) {
                    rv_category_filter.getChildAt(0).requestFocus();
                }
            });
        });
    }

    private void toggleMenu() {
        if (tv_live_menu.getVisibility() == View.VISIBLE) {
            tv_live_menu.setVisibility(View.GONE);
        } else {
            tv_live_menu.setVisibility(View.VISIBLE);
            // Request focus to first item after a small delay to ensure layout is ready
            tv_live_menu.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (rv_category_filter.getChildCount() > 0) {
                        rv_category_filter.getChildAt(0).requestFocus();
                    } else if (tv_live_recycler_view_title.getChildCount() > 0) {
                        tv_live_recycler_view_title.getChildAt(0).requestFocus();
                    }
                }
            }, 100);
        }
    }

    private void initPlayerSwitcher() {
        android.widget.TextView btnSwitchPlayer = findViewById(R.id.btn_switch_player);

        // Load saved player preference
        android.content.SharedPreferences prefs = getSharedPreferences("LiveSettings", MODE_PRIVATE);
        currentPlayerType = prefs.getString("player_type", "IJK");
        btnSwitchPlayer.setText("Player: " + currentPlayerType);

        // Handle click
        btnSwitchPlayer.setOnClickListener(v -> {
            // Toggle player
            currentPlayerType = currentPlayerType.equals("IJK") ? "EXO" : "IJK";
            btnSwitchPlayer.setText("Player: " + currentPlayerType);

            // Save preference
            prefs.edit().putString("player_type", currentPlayerType).apply();

            // Restart current stream with new player if something is playing
            if (!currentPlayingUrl.isEmpty()) {
                android.widget.Toast.makeText(this, "Switching to " + currentPlayerType + " Player...",
                        android.widget.Toast.LENGTH_SHORT).show();
                releasePlayer();
                play(currentPlayingUrl);
            } else {
                android.widget.Toast.makeText(this, currentPlayerType + " Player selected",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Add focus change listener for TV
        btnSwitchPlayer.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                btnSwitchPlayer.setBackgroundColor(android.graphics.Color.parseColor("#FF6090"));
            } else {
                btnSwitchPlayer.setBackgroundColor(android.graphics.Color.parseColor("#555555"));
            }
        });
    }

    private void initFilters() {
        // Category filter with type (1=football, 2=basketball)
        List<FilterAdapter.FilterItem> categories = new ArrayList<>();
        categories.add(new FilterAdapter.FilterItem("NBA", 1, 2)); // Basketball (default)
        categories.add(new FilterAdapter.FilterItem("英超", 82, 1)); // Football
        categories.add(new FilterAdapter.FilterItem("西甲", 120, 1));
        categories.add(new FilterAdapter.FilterItem("德甲", 129, 1));
        categories.add(new FilterAdapter.FilterItem("意甲", 108, 1));
        categories.add(new FilterAdapter.FilterItem("法甲", 142, 1));
        categories.add(new FilterAdapter.FilterItem("欧冠", 46, 1));
        categories.add(new FilterAdapter.FilterItem("欧联", 47, 1));
        categories.add(new FilterAdapter.FilterItem("欧协联", 3265, 1));
        categories.add(new FilterAdapter.FilterItem("国际友谊", 34, 1));

        categoryAdapter = new FilterAdapter(this, categories, item -> {
            selectedCategoryId = item.id;
            selectedType = item.type;
            refreshMatches();
        });
        rv_category_filter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_category_filter.setAdapter(categoryAdapter);

        // Language filter removed - now selected once at app start
        rv_language_filter.setVisibility(View.GONE);
    }

    // Refresh matches without auto-playing (used when changing filters)
    private void refreshMatches() {
        fetchMatches(false);
    }

    // Initial load with auto-play
    private void getLiveListAndPlay() {
        fetchMatches(true);
    }

    private void fetchMatches(final boolean autoPlay) {
        // Get current date for starttime parameter
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(new java.util.Date());

        final String url = "https://your-live-api.com/prod-api/match/list/new"
                + "?isfanye=1&type=" + selectedType + "&cid=" + selectedCategoryId
                + "&ishot=-1&pn=1&ps=20&level=&name="
                + "&langtype=" + selectedLanguage + "&starttime=" + dateStr
                + "&pid=4&zoneId=Asia%2FSingapore&zhuboType=0";

        Observable
                .create(new ObservableOnSubscribe<List<com.pxf.fftv.plus.bean.MatchBean.DataBean.ListBean>>() {
                    @Override
                    public void subscribe(
                            ObservableEmitter<List<com.pxf.fftv.plus.bean.MatchBean.DataBean.ListBean>> emitter)
                            throws Exception {

                        Request request = new Request.Builder()
                                .url(url)
                                .header("Accept", "application/json, text/plain, */*")
                                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                                .header("Referer", "https://your-live-api.com/")
                                .header("Origin", "https://your-live-api.com")
                                .header("User-Agent",
                                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                                .header("sec-ch-ua",
                                        "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"")
                                .header("sec-ch-ua-mobile", "?0")
                                .header("sec-ch-ua-platform", "\"Windows\"")
                                .header("Sec-Fetch-Dest", "empty")
                                .header("Sec-Fetch-Mode", "cors")
                                .header("Sec-Fetch-Site", "same-origin")
                                .build();

                        Response response = null;
                        try {
                            response = CommonUtils.getOkHttpClient().newCall(request).execute();
                            android.util.Log.d("LiveMatch", "Response code: " + response.code());

                            if (response.isSuccessful() && response.body() != null) {
                                String json = response.body().string();
                                android.util.Log.d("LiveMatch", "Response length: " + json.length() + " chars");
                                android.util.Log.d("LiveMatch",
                                        "First 200 chars: " + (json.length() > 200 ? json.substring(0, 200) : json));

                                com.pxf.fftv.plus.bean.MatchBean bean = CommonUtils.getGson().fromJson(json,
                                        com.pxf.fftv.plus.bean.MatchBean.class);

                                if (bean != null && bean.getData() != null && bean.getData().getList() != null) {
                                    android.util.Log.d("LiveMatch",
                                            "Parsed bean with " + bean.getData().getList().size() + " matches");
                                    emitter.onNext(bean.getData().getList());
                                } else {
                                    android.util.Log
                                            .e("LiveMatch",
                                                    "Parsing issue - bean: " + (bean == null ? "null" : "not null") +
                                                            ", data: "
                                                            + (bean != null && bean.getData() == null ? "null"
                                                                    : "not null")
                                                            +
                                                            ", list: "
                                                            + (bean != null && bean.getData() != null
                                                                    && bean.getData().getList() == null ? "null"
                                                                            : "not null"));
                                    emitter.onNext(new ArrayList<>());
                                }
                            } else {
                                android.util.Log.e("LiveMatch", "Request failed - code: " + response.code());
                                emitter.onNext(new ArrayList<>());
                            }
                        } catch (Exception e) {
                            android.util.Log.e("LiveMatch", "Exception in API call", e);
                            e.printStackTrace();
                            emitter.onNext(new ArrayList<>());
                        } finally {
                            if (response != null) {
                                response.close();
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<com.pxf.fftv.plus.bean.MatchBean.DataBean.ListBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<com.pxf.fftv.plus.bean.MatchBean.DataBean.ListBean> matchList) {
                        android.util.Log.d("LiveMatch",
                                "Received " + (matchList == null ? "null" : matchList.size()) + " matches from API");

                        currentTitlePosition = 0;
                        mMatchList = new ArrayList<>(); // Reset filtered list
                        mTitleList = new ArrayList<>();
                        mSubTitleList = new ArrayList<>();

                        // Get yesterday's date (start of day) for comparison
                        // This allows late-night matches (e.g., 11:30 PM) from yesterday to still show
                        java.util.Calendar yesterdayCalendar = java.util.Calendar.getInstance();
                        yesterdayCalendar.add(java.util.Calendar.DAY_OF_YEAR, -1); // Go back 1 day
                        yesterdayCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        yesterdayCalendar.set(java.util.Calendar.MINUTE, 0);
                        yesterdayCalendar.set(java.util.Calendar.SECOND, 0);
                        yesterdayCalendar.set(java.util.Calendar.MILLISECOND, 0);
                        java.util.Date yesterdayStart = yesterdayCalendar.getTime();

                        if (matchList != null) {
                            for (com.pxf.fftv.plus.bean.MatchBean.DataBean.ListBean match : matchList) {
                                try {
                                    // Filter by status: 10=finished (skip), 2=playing (show), 1=upcoming (show with
                                    // indicator)
                                    if (match.getStatus_up() == 10) {
                                        continue; // Skip finished matches
                                    }

                                    // Client-side date filtering: only show matches from today onwards
                                    try {
                                        String matchDateStr = match.getMatchtime();
                                        if (matchDateStr == null || matchDateStr.isEmpty()) {
                                            matchDateStr = match.getKick_off_time();
                                        }

                                        if (matchDateStr != null && !matchDateStr.isEmpty()) {
                                            // Parse the match date (try multiple formats)
                                            java.util.Date matchDate = null;

                                            // Try format: "2025-12-06 10:30:00" or "2025-12-06"
                                            try {
                                                java.text.SimpleDateFormat sdfFull = new java.text.SimpleDateFormat(
                                                        "yyyy-MM-dd HH:mm:ss");
                                                matchDate = sdfFull.parse(matchDateStr);
                                            } catch (Exception e1) {
                                                try {
                                                    java.text.SimpleDateFormat sdfDate = new java.text.SimpleDateFormat(
                                                            "yyyy-MM-dd");
                                                    matchDate = sdfDate.parse(matchDateStr);
                                                } catch (Exception e2) {
                                                    // Can't parse date, allow the match through
                                                    android.util.Log.w("LiveMatch",
                                                            "Could not parse match date: " + matchDateStr);
                                                }
                                            }

                                            // Skip matches older than yesterday (keeps yesterday + today + future)
                                            if (matchDate != null && matchDate.before(yesterdayStart)) {
                                                android.util.Log.d("LiveMatch",
                                                        "Skipping old match from " + matchDateStr + ": " +
                                                                match.getHteam_name() + " vs " + match.getAteam_name());
                                                continue;
                                            }
                                        }
                                    } catch (Exception e) {
                                        android.util.Log.w("LiveMatch", "Error parsing match date", e);
                                        // Continue with the match if date parsing fails
                                    }

                                    java.util.Set<String> urls = new java.util.LinkedHashSet<>();
                                    if (match.getVideo_url() != null && !match.getVideo_url().isEmpty()) {
                                        urls.add(match.getVideo_url());
                                    }
                                    if (match.getLive_urls() != null) {
                                        for (com.pxf.fftv.plus.bean.MatchBean.DataBean.ListBean.UrlBean u : match
                                                .getLive_urls()) {
                                            if (u.getUrl() != null && !u.getUrl().isEmpty()) {
                                                urls.add(u.getUrl());
                                            }
                                        }
                                    }
                                    if (match.getMirror_live_urls() != null) {
                                        for (com.pxf.fftv.plus.bean.MatchBean.DataBean.ListBean.UrlBean u : match
                                                .getMirror_live_urls()) {
                                            if (u.getUrl() != null && !u.getUrl().isEmpty()) {
                                                urls.add(u.getUrl());
                                            }
                                        }
                                    }

                                    if (!urls.isEmpty()) {
                                        // Only add matches that have stream URLs
                                        mMatchList.add(match);

                                        // Add status indicator for upcoming matches
                                        String title;
                                        if (match.getStatus_up() == 1) {
                                            title = "[即将开始] " + match.getHteam_name() + " vs " + match.getAteam_name();
                                        } else {
                                            title = match.getHteam_name() + " vs " + match.getAteam_name();
                                        }
                                        mTitleList.add(title);
                                        mSubTitleList.add(new ArrayList<>(urls));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        android.util.Log.d("LiveMatch", "Filtered to " + mMatchList.size() + " matches with URLs");

                        // Use MatchAdapter for match titles with logos
                        mMatchAdapter = new MatchAdapter(IjkTVLiveActivity.this, mMatchList,
                                new MatchAdapter.OnMatchClickListener() {
                                    @Override
                                    public void onMatchFocus(int position) {
                                        IjkTVLiveActivity.this.onTitleFocus(position);
                                    }
                                });
                        tv_live_recycler_view_title.setAdapter(mMatchAdapter);

                        // Show subtitle list beside matches (horizontal layout)
                        tv_live_recycler_view_sub_title.setVisibility(View.VISIBLE);

                        // Always show menu (categories) even if no matches
                        tv_live_menu.setVisibility(View.VISIBLE);

                        if (mTitleList.size() > 0) {
                            // Request focus on first match item to fix navigation
                            tv_live_recycler_view_title.post(new Runnable() {
                                @Override
                                public void run() {
                                    View firstItem = tv_live_recycler_view_title.getChildAt(0);
                                    if (firstItem != null) {
                                        firstItem.requestFocus();
                                    }
                                }
                            });
                        } else {
                            // Only show "no matches" if this was an actual API call (not initial state)
                            // Don't show on initial load when no category selected yet
                            if (mMatchList != null && mMatchList.size() == 0) {
                                android.widget.Toast.makeText(IjkTVLiveActivity.this, "暂无直播赛事",
                                        android.widget.Toast.LENGTH_LONG).show();
                            }
                            progress.setVisibility(View.GONE);
                            // Focus on category filter when no matches
                            rv_category_filter.requestFocus();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e("LiveMatch", "Error fetching matches", e);
                        android.widget.Toast.makeText(IjkTVLiveActivity.this, "获取赛事失败: " + e.getMessage(),
                                android.widget.Toast.LENGTH_LONG).show();
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // EXO Player fields
    private androidx.media3.exoplayer.ExoPlayer mExoPlayer;
    private androidx.media3.ui.PlayerView mPlayerView;

    private void play(String url) {
        // Track current URL for player switching
        currentPlayingUrl = url;

        releasePlayer(); // Release any existing player first

        if (currentPlayerType.equals("EXO")) {
            // Use Embedded EXO Player
            if (mPlayerView == null) {
                mPlayerView = findViewById(R.id.player_view);
            }

            // Show EXO player and overlay
            mPlayerView.setVisibility(View.VISIBLE);
            findViewById(R.id.surface_play).setVisibility(View.GONE);

            // Show transparent overlay for touch handling
            View exoTouchOverlay = findViewById(R.id.exo_touch_overlay);
            exoTouchOverlay.setVisibility(View.VISIBLE);
            exoTouchOverlay.setOnClickListener(v -> toggleMenu());

            // Disable built-in controller
            mPlayerView.setUseController(false);

            // Initialize EXO Player
            androidx.media3.exoplayer.trackselection.DefaultTrackSelector trackSelector = new androidx.media3.exoplayer.trackselection.DefaultTrackSelector(
                    this);
            androidx.media3.exoplayer.DefaultRenderersFactory renderFactory = new androidx.media3.exoplayer.DefaultRenderersFactory(
                    this);
            renderFactory.setExtensionRendererMode(
                    androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);

            mExoPlayer = new androidx.media3.exoplayer.ExoPlayer.Builder(this, renderFactory)
                    .setTrackSelector(trackSelector)
                    .build();

            mPlayerView.setPlayer(mExoPlayer);

            // Set User Agent and Data Source
            androidx.media3.datasource.DataSource.Factory dataSourceFactory = new androidx.media3.datasource.DefaultHttpDataSource.Factory()
                    .setUserAgent(androidx.media3.common.util.Util.getUserAgent(this, "smtvplus"))
                    .setAllowCrossProtocolRedirects(true);

            androidx.media3.exoplayer.source.MediaSource videoSource;
            if (url.endsWith("m3u8")) {
                videoSource = new androidx.media3.exoplayer.hls.HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(androidx.media3.common.MediaItem.fromUri(android.net.Uri.parse(url)));
            } else {
                videoSource = new androidx.media3.exoplayer.source.ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(androidx.media3.common.MediaItem.fromUri(android.net.Uri.parse(url)));
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

        } else {
            // Use IJK Player (default)
            findViewById(R.id.surface_play).setVisibility(View.VISIBLE);
            // Hide EXO player and overlay
            if (mPlayerView != null) {
                mPlayerView.setVisibility(View.GONE);
            }
            findViewById(R.id.exo_touch_overlay).setVisibility(View.GONE);

            // Show IJK surface

            if (mHolder == null) {
                return;
            }

            mPlayer = new IjkMediaPlayer();
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist",
                    "rtmp,crypto,file,http,https,tcp,tls,udp");
            IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 10 * 1024 * 1024);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 20);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
            try {
                mPlayer.setDisplay(mHolder);
                mPlayer.setDataSource(url);
                mPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    progress.setVisibility(View.GONE);
                }
            });

            mPlayer.start();
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_MENU:
                if (tv_live_menu.getVisibility() == View.VISIBLE) {
                    tv_live_menu.setVisibility(View.GONE);
                } else {
                    tv_live_menu.setVisibility(View.VISIBLE);
                    // Reset focus to match list when menu opens
                    tv_live_recycler_view_title.requestFocus();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("fftvdebug", "onKeyUp keycode " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (tv_live_menu.getVisibility() == View.VISIBLE) {
                    tv_live_menu.setVisibility(View.GONE);
                    return true;
                } else {
                    ModernDialog.showConfirm(IjkTVLiveActivity.this, "退出电视直播？",
                            () -> finish(),
                            () -> {
                            });
                    return true;
                }
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return true;
        }
        return super.onKeyUp(keyCode, event);
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
        finish();
    }

    private String formatTime(int time) {
        String result = "";
        int h = time / 3600;
        int m = (time % 3600) / 60;
        int s = time % 60;
        if (h > 0) {
            result = time + ":";
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

    @Override
    public void onTitleFocus(int position) {
        currentTitlePosition = position;

        // Show stream links for the selected match
        if (position < mSubTitleList.size() && mSubTitleList.get(position).size() > 0) {
            // Create display names "Link 1", "Link 2", etc.
            ArrayList<String> displayNames = new ArrayList<>();
            for (int i = 0; i < mSubTitleList.get(position).size(); i++) {
                displayNames.add("Link " + (i + 1));
            }
            mSubTitleAdapter = new TitleAdapter(IjkTVLiveActivity.this, displayNames,
                    TitleAdapter.TYPE.SUB_TITLE, IjkTVLiveActivity.this);
            tv_live_recycler_view_sub_title.setAdapter(mSubTitleAdapter);
            tv_live_recycler_view_sub_title.setVisibility(View.VISIBLE);
        } else {
            tv_live_recycler_view_sub_title.setVisibility(View.INVISIBLE); // Use INVISIBLE instead of GONE
        }
    }

    @Override
    public void onSubTitleClick(int position) {
        if (currentTitlePosition >= mSubTitleList.size()
                || mSubTitleList.get(currentTitlePosition).size() <= position) {
            android.widget.Toast.makeText(this, "无效的链接", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        String url = mSubTitleList.get(currentTitlePosition).get(position);

        tv_live_menu.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        releasePlayer();
        play(url);
    }
}
