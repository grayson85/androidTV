package com.pxf.fftv.plus.contract.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.bean.RefreshTokenBean;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.common.GlideApp;
import com.pxf.fftv.plus.common.InternalFileSaveUtil;
import com.pxf.fftv.plus.common.Ui;
import com.pxf.fftv.plus.contract.collect.VideoCollect;
import com.pxf.fftv.plus.contract.personal.AccountActivity;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.video.Video;
import com.pxf.fftv.plus.model.video.cms.CMSVideoEngine;
import com.pxf.fftv.plus.model.video.weiduo.WeiduoVideoBean2;
import com.pxf.fftv.plus.model.video.weiduo.WeiduoVideoBean3;
import com.pxf.fftv.plus.model.video.weiduo.WeiduoVideoEngine;
import com.pxf.fftv.plus.player.AutoNextEvent;
import com.pxf.fftv.plus.player.VideoPlayer;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.pxf.fftv.plus.Const.ACCOUNT_EVER_VIP;
import static com.pxf.fftv.plus.Const.ACCOUNT_NO_VIP;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.REFRESH_TOKEN_URL;
import static com.pxf.fftv.plus.Const.RETURN_URL;

public class VideoDetailActivity extends AppCompatActivity implements VideoPlayListAdapter.OnPartClickListener,
        VideoVODSourceAdapter.OnVODClickListener, VideoPageAdapter.OnPageClickListener {

    View detail_root;

    View top_bar_menu_root_home;

    View top_bar_menu_root_search;

    View detail_root_content;

    TextView detail_tv_content;

    TextView detail_tv_content_more;

    View detail_menu_root_collect;

    ImageView detail_iv_collect;

    TextView detail_tv_collect;

    View detail_menu_root_continue;
    TextView detail_tv_continue;

    RecyclerView video_detail_recycler_view;

    ImageView detail_iv_image;

    TextView detail_tv_title;

    TextView detail_tv_video_tag;

    TextView detail_tv_director;

    TextView detail_tv_actor;

    View detail_menu_root_return_error;

    ImageView detail_iv_return_error;

    TextView detail_tv_return_error;

    TextView top_bar_menu_right_note;

    // 20220910 - Added new feature sorting
    View detail_tv_sort;

    TextView tvSort;

    ImageView detail_tv_sort_image;

    // 20220923 - Added new feature indicate multiple source
    RecyclerView detail_tv_vod_source;

    RecyclerView detail_recycler_view_pages;

    private VideoPlayListAdapter mAdapter;
    private VideoVODSourceAdapter vAdapter;
    private VideoPageAdapter pAdapter;
    private Video mVideo;
    private int selected = 0;
    private int selectedPage = 0;
    private ArrayList<Video.Part> currentParts = new ArrayList<>();

    private CompositeDisposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        detail_root = findViewById(R.id.detail_root);
        top_bar_menu_root_home = findViewById(R.id.top_bar_menu_root_home);
        top_bar_menu_root_search = findViewById(R.id.top_bar_menu_root_search);
        detail_root_content = findViewById(R.id.detail_root_content);
        detail_tv_content = findViewById(R.id.detail_tv_content);
        detail_tv_content_more = findViewById(R.id.detail_tv_content_more);
        detail_menu_root_collect = findViewById(R.id.detail_menu_root_collect);
        detail_iv_collect = findViewById(R.id.detail_iv_collect);
        detail_tv_collect = findViewById(R.id.detail_tv_collect);
        video_detail_recycler_view = findViewById(R.id.video_detail_recycler_view);
        detail_iv_image = findViewById(R.id.detail_iv_image);
        detail_tv_title = findViewById(R.id.detail_tv_title);
        detail_tv_video_tag = findViewById(R.id.detail_tv_video_tag);
        detail_tv_director = findViewById(R.id.detail_tv_director);
        detail_tv_actor = findViewById(R.id.detail_tv_actor);
        detail_menu_root_return_error = findViewById(R.id.detail_menu_root_return_error);
        detail_iv_return_error = findViewById(R.id.detail_iv_return_error);
        detail_tv_return_error = findViewById(R.id.detail_tv_return_error);
        detail_menu_root_continue = findViewById(R.id.detail_menu_root_continue);
        detail_tv_continue = findViewById(R.id.detail_tv_continue);
        top_bar_menu_right_note = findViewById(R.id.top_bar_menu_right_note);
        detail_tv_sort = findViewById(R.id.detail_tv_sort);
        tvSort = findViewById(R.id.tvSort);
        detail_tv_sort_image = findViewById(R.id.detail_tv_sort_image);
        detail_tv_vod_source = findViewById(R.id.detail_tv_vod_source);
        detail_recycler_view_pages = findViewById(R.id.detail_recycler_view_pages);

        detail_menu_root_return_error.setOnClickListener(v -> onReturnClick());
        detail_menu_root_collect.setOnClickListener(v -> onCollectClick());
        detail_menu_root_continue.setOnClickListener(v -> {
            if (lastPlayedIndex >= 0) {
                if (mVideo.getVodSource().get(selected).part.size() > 100) {
                    int page = lastPlayedIndex / 100;
                    if (page != selectedPage) {
                        updatePartsForPage(page);
                    }
                    int indexInPage = lastPlayedIndex % 100;
                    if (isReverseSort) {
                        // If reversed, the item at indexInPage (ascending) is at (size-1 - indexInPage)
                        indexInPage = (mVideo.getVodSource().get(selected).part.isEmpty() ? 0 : currentParts.size() - 1)
                                - indexInPage;
                    }
                    if (indexInPage >= 0 && indexInPage < currentParts.size()) {
                        onPartClick(indexInPage);
                    }
                } else {
                    int indexInPage = lastPlayedIndex;
                    if (isReverseSort) {
                        indexInPage = (currentParts.size() - 1) - lastPlayedIndex;
                    }
                    if (indexInPage >= 0 && indexInPage < currentParts.size()) {
                        onPartClick(indexInPage);
                    }
                }
            }
        });
        detail_tv_sort.setOnClickListener(v -> onSortClick());

        // Show description dialog on click
        detail_root_content.setOnClickListener(v -> {
            if (mVideo != null) {
                Ui.showTextDialog(this, "剧情简介", mVideo.getDescription());
            }
        });

        initView();

        mDisposable = new CompositeDisposable();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
        mDisposable.clear();
    }

    private void initView() {
        top_bar_menu_root_home.setNextFocusDownId(R.id.detail_root_content);
        top_bar_menu_root_search.setNextFocusDownId(R.id.detail_root_content);
        initGongGao();
        int[] bgArray = new int[] { R.drawable.bg_detail_1, R.drawable.bg_detail_2, R.drawable.bg_detail_3,
                R.drawable.bg_detail_4 };
        detail_root.setBackground(ContextCompat.getDrawable(this, bgArray[(int) (Math.random() * 4)]));

        Ui.configTopBar(this);
        setContentFocusAnimator(this, detail_root_content, new FocusAction() {
            @Override
            public void onFocus() {
                detail_tv_content.setTextColor(getResources().getColor(R.color.colorTextFocus));
                detail_tv_content_more.setTextColor(getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                detail_tv_content.setTextColor(getResources().getColor(R.color.colorTextNormal));
                detail_tv_content_more.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });

        Ui.setViewFocusScaleAnimator(detail_menu_root_collect, new FocusAction() {
            @Override
            public void onFocus() {
                detail_tv_collect.setTextColor(getResources().getColor(R.color.colorTextFocus));
                detail_menu_root_collect
                        .setBackground(
                                ContextCompat.getDrawable(VideoDetailActivity.this, R.drawable.bg_common_menu_focus));
                if (detail_tv_collect.getText().equals("收藏")) {
                    detail_iv_collect.setImageResource(R.drawable.ic_collected_focus);
                } else {
                    detail_iv_collect.setImageResource(R.drawable.ic_not_collected_focus);
                }
            }

            @Override
            public void onLoseFocus() {
                detail_menu_root_collect
                        .setBackground(
                                ContextCompat.getDrawable(VideoDetailActivity.this, R.drawable.bg_common_menu_normal));
                detail_tv_collect.setTextColor(getResources().getColor(R.color.colorTextNormal));
                if (detail_tv_collect.getText().equals("收藏")) {
                    detail_iv_collect.setImageResource(R.drawable.ic_collected_normal);
                } else {
                    detail_iv_collect.setImageResource(R.drawable.ic_not_collected_normal);
                }
            }
        });

        Ui.setViewFocusScaleAnimator(detail_menu_root_continue, new FocusAction() {
            @Override
            public void onFocus() {
                detail_tv_continue.setTextColor(getResources().getColor(R.color.colorTextFocus));
                detail_menu_root_continue
                        .setBackground(
                                ContextCompat.getDrawable(VideoDetailActivity.this, R.drawable.bg_common_menu_focus));
            }

            @Override
            public void onLoseFocus() {
                detail_menu_root_continue
                        .setBackground(
                                ContextCompat.getDrawable(VideoDetailActivity.this, R.drawable.bg_common_menu_normal));
                detail_tv_continue.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });
        Ui.setViewFocusScaleAnimator(detail_menu_root_return_error, new FocusAction() {
            @Override
            public void onFocus() {
                detail_menu_root_return_error
                        .setBackground(
                                ContextCompat.getDrawable(VideoDetailActivity.this, R.drawable.bg_common_menu_focus));
                detail_iv_return_error.setImageResource(R.drawable.ic_return_error_focus);
                detail_tv_return_error.setTextColor(getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                detail_menu_root_return_error
                        .setBackground(
                                ContextCompat.getDrawable(VideoDetailActivity.this, R.drawable.bg_common_menu_normal));
                detail_iv_return_error.setImageResource(R.drawable.ic_return_error_normal);
                detail_tv_return_error.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });
        // 20220910 - Added new feature sorting
        Ui.setViewFocusScaleAnimator(detail_tv_sort, new FocusAction() {
            @Override
            public void onFocus() {
                detail_tv_sort.setBackground(
                        ContextCompat.getDrawable(VideoDetailActivity.this, R.drawable.bg_common_menu_focus));
                detail_tv_sort_image.setImageResource(R.drawable.ic_sort_focus);
                tvSort.setTextColor(getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                detail_tv_sort.setBackground(
                        ContextCompat.getDrawable(VideoDetailActivity.this, R.drawable.bg_common_menu_normal));
                detail_tv_sort_image.setImageResource(R.drawable.ic_sort_normal);
                tvSort.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });

        detail_tv_vod_source.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        detail_recycler_view_pages.setLayoutManager(new GridLayoutManager(this, 8));
        video_detail_recycler_view.setLayoutManager(new GridLayoutManager(this, 8));

    }

    private void initGongGao() {
        if (top_bar_menu_right_note != null) {
            top_bar_menu_right_note.setSingleLine(true);
            top_bar_menu_right_note.setSelected(true);
        }
    }

    private static void setContentFocusAnimator(Activity activity, View view, FocusAction action) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                view.setBackground(ContextCompat.getDrawable(activity, R.drawable.video_detail_content_focus));
                if (action != null) {
                    action.onFocus();
                }

                ValueAnimator animatorFirst = ValueAnimator.ofPropertyValuesHolder(
                        PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.02f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f)).setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofPropertyValuesHolder(
                        PropertyValuesHolder.ofFloat("scaleX", 1.02f, 1.01f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.2f, 1.1f)).setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        view.setScaleX((float) animation.getAnimatedValue("scaleX"));
                        view.setScaleY((float) animation.getAnimatedValue("scaleY"));
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        view.setScaleX((float) animation.getAnimatedValue("scaleX"));
                        view.setScaleY((float) animation.getAnimatedValue("scaleY"));
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                view.setBackground(null);
                if (action != null) {
                    action.onLoseFocus();
                }
                ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(
                        PropertyValuesHolder.ofFloat("scaleX", 1.01f, 1.0f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.1f, 1.0f)).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    view.setScaleX((float) animation.getAnimatedValue("scaleX"));
                    view.setScaleY((float) animation.getAnimatedValue("scaleY"));
                });
                animator.start();
            }
        });
    }

    private String historyPartUrl;
    private int lastPlayedIndex = -1;
    private boolean isReverseSort = false;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onVideoDetailEvent(VideoDetailEvent event) {
        mVideo = event.getVideo();
        EventBus.getDefault().removeStickyEvent(event);

        if (mVideo.getVodSource() != null && mVideo.getVodSource().size() == 1 &&
                "History".equals(mVideo.getVodSource().get(0).sourceName) &&
                !mVideo.getVodSource().get(0).part.isEmpty()) {
            historyPartUrl = mVideo.getVodSource().get(0).part.get(0).getUrl();
        }

        // 加载图片和名称
        detail_tv_title.setText(mVideo.getTitle());
        GlideApp.with(this).load(mVideo.getImageUrl()).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(detail_iv_image);

        // 收藏判断
        LinkedList<VideoCollect> videoCollectList = (LinkedList<VideoCollect>) InternalFileSaveUtil.getInstance(this)
                .get("video_collect");
        if (videoCollectList != null) {
            for (int i = 0; i < videoCollectList.size(); i++) {
                if (videoCollectList.get(i).getVideo().getTitle().equals(mVideo.getTitle())
                        && videoCollectList.get(i).getVideoEngine().equals(Model.getData().getVideoEngine(this))) {
                    detail_tv_collect.setText("取消收藏");
                    detail_iv_collect.setImageResource(R.drawable.ic_not_collected_normal);
                    break;
                }
            }
        }

        if (mVideo.getWeiduoUrl() != null && !mVideo.getWeiduoUrl().isEmpty()) {
            // 维多解析
            DisposableObserver<Boolean> observer = new DisposableObserver<Boolean>() {
                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean) {
                        loadData();
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };

            Observable
                    .create((ObservableOnSubscribe<Boolean>) emitter -> {
                        Response response = null;
                        try {
                            Request request = new Request.Builder()
                                    .url(WeiduoVideoEngine.ANALYSIS_URL + mVideo.getWeiduoUrl())
                                    .build();
                            response = CommonUtils.getOkHttpClient().newCall(request).execute();
                            if (response.isSuccessful() && response.body() != null) {
                                WeiduoVideoBean2 bean = CommonUtils.getGson().fromJson(response.body().string(),
                                        WeiduoVideoBean2.class);
                                if (bean.getError() == 0) {
                                    mVideo.setTypeText(bean.getData().getType());
                                    mVideo.setArea(bean.getData().getArea());
                                    mVideo.setYear(bean.getData().getYear());
                                    mVideo.setDescription(bean.getData().getDesc());

                                    ArrayList<Video.Director> directors = new ArrayList<>();
                                    Video.Director director = new Video.Director();
                                    director.setName(bean.getData().getDirector());
                                    directors.add(director);
                                    mVideo.setDirectors(directors);

                                    ArrayList<Video.Actor> actors = new ArrayList<>();
                                    String[] stars = bean.getData().getStar().split("/ ");
                                    for (String s : stars) {
                                        Video.Actor actor = new Video.Actor();
                                        actor.setName(s);
                                        actors.add(actor);
                                    }
                                    mVideo.setActors(actors);

                                    ArrayList<Video.Part> parts = new ArrayList<>();
                                    if ((mVideo.getTypeName() != null && mVideo.getTypeName().equals("电影")) ||
                                            (mVideo.getVideoEngineParam() != null
                                                    && mVideo.getVideoEngineParam().getVideo1Title().equals("电影"))) {
                                        for (int i = 0; i < bean.getData().getUrls().size(); i++) {
                                            WeiduoVideoBean2.DataBean.UrlsBean urlsBean = bean.getData().getUrls()
                                                    .get(i);
                                            if (!urlsBean.getList().isEmpty()) {
                                                Video.Part part = new Video.Part();
                                                part.setUrl(urlsBean.getList().get(0).getLink());
                                                switch (urlsBean.getType()) {
                                                    case "imgo":
                                                        part.setTitle("线路" + (i + 1) + "-芒果TV");
                                                        break;
                                                    case "qq":
                                                        part.setTitle("线路" + (i + 1) + "-腾讯视频");
                                                        break;
                                                    case "qiyi":
                                                        part.setTitle("线路" + (i + 1) + "-爱奇艺");
                                                        break;
                                                    default:
                                                        part.setTitle("线路" + (i + 1));
                                                        break;
                                                }
                                                parts.add(part);
                                            }
                                        }
                                    } else {
                                        if (!bean.getData().getUrls().isEmpty()
                                                && !bean.getData().getUrls().get(0).getList().isEmpty()) {
                                            for (int i = 0; i < bean.getData().getUrls().get(0).getList().size(); i++) {
                                                Video.Part part = new Video.Part();
                                                part.setUrl(bean.getData().getUrls().get(0).getList().get(i).getLink());
                                                part.setTitle(
                                                        bean.getData().getUrls().get(0).getList().get(i).getTitle());
                                                parts.add(part);
                                            }
                                        }
                                    }
                                    mVideo.setParts(parts);

                                    emitter.onNext(true);
                                }
                            }
                        } finally {
                            if (response != null) {
                                response.close();
                            }
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
            mDisposable.add(observer);
        } else {
            loadData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onAutoNextEvent(AutoNextEvent event) {
        int lastPart = event.getPosition();
        EventBus.getDefault().removeStickyEvent(event);

        // Auto-play next episode
        if (Const.FEATURE_7) {
            // Get the full parts list for the selected source
            List<Video.Part> allParts = mVideo.getVodSource().get(selected).part;

            // Calculate target episode (next episode after the one that just finished)
            int targetEpisode = lastPart + 1;

            // Check if target episode exists
            if (targetEpisode >= 0 && targetEpisode < allParts.size()) {
                // Check if we need to switch pages
                if (allParts.size() > 100) {
                    int targetPage = targetEpisode / 100;

                    // Switch to the page containing the target episode if needed
                    if (targetPage != selectedPage) {
                        selectedPage = targetPage;
                        updatePartsForPage(selectedPage);
                        if (pAdapter != null) {
                            pAdapter.setSelection(selectedPage);
                        }
                    }
                }

                // Calculate position within current page
                int positionInPage = targetEpisode % 100;

                // Play the episode
                onPartClick(positionInPage);
            } else {
                Toast.makeText(this, "已经是最后一集了", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPartClick(int position) {
        // String url = mVideo.getParts().get(position).getUrl();
        // 20220923 - Added new feature indicate multiple source
        // String url = mVideo.getVodSource().get(selected).part.get(position).getUrl();
        String url = currentParts.get(position).getUrl();
        // 保存url和名称作为反馈用
        String saveReturnUrl = url.substring(url.indexOf("://") + 3);
        if (saveReturnUrl.indexOf("?from=") > 0) {
            saveReturnUrl = saveReturnUrl.substring(0, saveReturnUrl.indexOf("?from="));
        }
        Model.getData().setLastPlayUrl(this, saveReturnUrl);

        Model.getData().setLastPlayName(this, mVideo.getTitle() + currentParts.get(position).getTitle());

        if (mVideo.getWeiduoUrl() != null && !mVideo.getWeiduoUrl().isEmpty()) {
            // 维多解析
            DisposableObserver<String[]> observer = new DisposableObserver<String[]>() {
                @Override
                public void onNext(String[] url) {
                    if (url[0].isEmpty()) {
                        Toast.makeText(VideoDetailActivity.this, "该视频暂时无法播放", Toast.LENGTH_SHORT).show();
                    } else {
                        playVideo(url[0], position);
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };

            Observable
                    .create((ObservableOnSubscribe<String[]>) emitter -> {
                        String[] result = new String[2];
                        Response response = null;
                        try {
                            Request request = new Request.Builder()
                                    .url(FFTVApplication.weiduo_analysis_play_url + url)
                                    .build();
                            response = CommonUtils.getOkHttpClient().newCall(request).execute();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    String resultResponse = response.body().string();
                                    WeiduoVideoBean3 bean = CommonUtils.getGson().fromJson(resultResponse,
                                            WeiduoVideoBean3.class);
                                    if (bean.getCode().equals("200")) {
                                        result[0] = bean.getUrl();
                                    } else {
                                        result[0] = "";
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                result[0] = "";
                            }

                            /*
                             * Request requestNext = new Request.Builder()
                             * .url(FFTVApplication.weiduo_analysis_play_url + nextUrl)
                             * .build();
                             * Response responseNext =
                             * CommonUtils.getOkHttpClient().newCall(requestNext).execute();
                             * try {
                             * if (responseNext.isSuccessful() && responseNext.body() != null) {
                             * String resultResponse = responseNext.body().string();
                             * WeiduoVideoBean3 bean = CommonUtils.getGson().fromJson(resultResponse,
                             * WeiduoVideoBean3.class);
                             * if (bean.getCode().equals("200")) {
                             * result[1] = bean.getUrl();
                             * } else {
                             * result[1] = "";
                             * }
                             * }
                             * } catch (Exception e) {
                             * e.printStackTrace();
                             * result[1] = "";
                             * }
                             */

                            emitter.onNext(result);
                        } finally {
                            if (response != null) {
                                response.close();
                            }
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
            mDisposable.add(observer);
        } else {
            playVideo(url, position);
        }
    }

    private void playVideo(String url, int currentPart) {
        if (!url.startsWith("http")) {
            Toast.makeText(VideoDetailActivity.this, "线路资源错误无法播放，请更换其它线路", Toast.LENGTH_LONG).show();
            url = "https://" + url;
            // return;
        }

        // 判断账号是否登录
        // if (!BuildConfig.DEBUG) {
        if (false) {
            if (FFTVApplication.token.isEmpty()) {
                Toast.makeText(VideoDetailActivity.this, "请先登录账号", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                return;
            }
        }

        String finalUrl = url;

        // Calculate global position
        int globalPosition = selectedPage * 100 + currentPart;

        // if (BuildConfig.DEBUG || (Const.FEATURE_12 && FFTVApplication.VIP_MODE == 0))
        // {
        if (true) {
            Log.wtf("Play From -", "<" + mVideo.getVodSource().get(selected).sourceName + ">");
            // switch (mVideo.getVodSource().get(selected).sourceName){
            // case "wjm3u8":
            // Log.wtf("VideoDetailActivity","Set Player to IJKPlayer "+
            // mVideo.getVodSource().get(selected).sourceName);
            // if (finalUrl.contains("cdtlas")){
            // VideoPlayer.getVideoPlayer(4).play(VideoDetailActivity.this, finalUrl,
            // mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(),
            // currentPart, mVideo.getImageUrl(), -1);
            // }else{
            // VideoPlayer.getVideoPlayer(3).play(VideoDetailActivity.this, finalUrl,
            // mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(),
            // currentPart, mVideo.getImageUrl(), -1);
            // }
            // break;
            // case "fsm3u8":
            // case "wolong":
            // Log.wtf("VideoDetailActivity","Set Player to EXOPlayer " +
            // mVideo.getVodSource().get(selected).sourceName);
            // VideoPlayer.getVideoPlayer(4).play(VideoDetailActivity.this, finalUrl,
            // mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(),
            // currentPart, mVideo.getImageUrl(), -1);
            // break;
            // }
            VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(VideoDetailActivity.this, finalUrl,
                    mVideo.getTitle(), currentParts.get(currentPart).getTitle(), globalPosition, mVideo.getImageUrl(),
                    -1, mVideo.getId());

        } else {
            if (Const.FEATURE_11) {
                // 限制多端登录
                String requestBody = "username=" + FFTVApplication.account + "&token=" + FFTVApplication.token;
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(mediaType, requestBody);
                final Request request = new Request.Builder()
                        .url(REFRESH_TOKEN_URL)
                        .post(body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();
                DisposableObserver<String> observer = new DisposableObserver<String>() {
                    @Override
                    public void onNext(String result) {
                        switch (result) {
                            case "112":
                                Toast.makeText(VideoDetailActivity.this, "账号已被禁用，无法观看视频", Toast.LENGTH_LONG).show();
                                break;
                            case "151":
                                Toast.makeText(VideoDetailActivity.this, "账号已在其他设备上登录，请重新登录账号", Toast.LENGTH_LONG)
                                        .show();
                                // 注销账号
                                FFTVApplication.token = "";
                                FFTVApplication.login = false;
                                FFTVApplication.account = "";
                                FFTVApplication.password = "";
                                FFTVApplication.vipDate = ACCOUNT_NO_VIP;
                                break;
                            default:
                                try {
                                    RefreshTokenBean bean = CommonUtils.getGson().fromJson(result,
                                            RefreshTokenBean.class);
                                    FFTVApplication.token = bean.getOnline();
                                    // 验证通过开始播放
                                    if (FFTVApplication.vipDate == ACCOUNT_EVER_VIP) {
                                        VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(
                                                VideoDetailActivity.this, finalUrl, mVideo.getTitle(),
                                                currentParts.get(currentPart).getTitle(), globalPosition,
                                                mVideo.getImageUrl(), -1, mVideo.getId());
                                    } else if (FFTVApplication.vipDate < System.currentTimeMillis()) {
                                        Toast.makeText(VideoDetailActivity.this, "您的VIP会员已过期，请及时续费", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (FFTVApplication.vipDate == ACCOUNT_NO_VIP) {
                                        Toast.makeText(VideoDetailActivity.this, "您还不是VIP会员，无法观看", Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(
                                                VideoDetailActivity.this, finalUrl, mVideo.getTitle(),
                                                currentParts.get(currentPart).getTitle(), globalPosition,
                                                mVideo.getImageUrl(), -1, mVideo.getId());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(VideoDetailActivity.this, "账号验证异常，请联系管理员", Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };

                Observable
                        .create((ObservableOnSubscribe<String>) emitter -> {
                            Response response = null;
                            try {
                                response = CommonUtils.getOkHttpClient().newCall(request).execute();
                                if (response.isSuccessful() && response.body() != null) {
                                    String result = response.body().string().substring(2).trim();
                                    emitter.onNext(result);
                                }
                            } finally {
                                if (response != null) {
                                    response.close();
                                }
                            }
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer);
                mDisposable.add(observer);
            } else {
                if (FFTVApplication.vipDate == ACCOUNT_EVER_VIP) {
                    VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(VideoDetailActivity.this, finalUrl,
                            mVideo.getTitle(), currentParts.get(currentPart).getTitle(), globalPosition,
                            mVideo.getImageUrl(), -1, mVideo.getId());
                } else if (FFTVApplication.vipDate < System.currentTimeMillis()) {
                    Toast.makeText(VideoDetailActivity.this, "您的VIP会员已过期，请及时续费", Toast.LENGTH_LONG).show();
                } else if (FFTVApplication.vipDate == ACCOUNT_NO_VIP) {
                    Toast.makeText(VideoDetailActivity.this, "您还不是VIP会员，无法观看", Toast.LENGTH_LONG).show();
                } else {
                    VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(VideoDetailActivity.this, finalUrl,
                            mVideo.getTitle(), currentParts.get(currentPart).getTitle(), globalPosition,
                            mVideo.getImageUrl(), -1, mVideo.getId());
                }
            }
        }
    }

    @SuppressLint({ "SetTextI18n", "ResourceAsColor" })
    private void loadData() {
        // Check if video has play sources
        // Videos from ac=list won't have them, need to fetch via ac=detail&ids=X
        // For history items (sourceName="History"), we definitely need to fetch details
        // even if ID is 0 (legacy history items)
        boolean isHistory = mVideo.getVodSource() != null && mVideo.getVodSource().size() == 1
                && "History".equals(mVideo.getVodSource().get(0).sourceName);

        if (mVideo.getVodSource() == null || mVideo.getVodSource().isEmpty() || isHistory) {
            Log.d("VideoDetailActivity", "No VodSource or History - fetching detail for vod_id: " + mVideo.getId());

            // Fetch complete details from API using video ID
            DisposableObserver<Video> observer = new DisposableObserver<Video>() {
                @Override
                public void onNext(Video detailedVideo) {
                    if (detailedVideo != null && detailedVideo.getVodSource() != null &&
                            !detailedVideo.getVodSource().isEmpty()) {
                        // Update current video with detailed info
                        mVideo.setVodSource(detailedVideo.getVodSource());
                        mVideo.setActors(detailedVideo.getActors());
                        mVideo.setDirectors(detailedVideo.getDirectors());

                        // Update ID if it was missing (e.g. recovered from search)
                        if (mVideo.getId() == 0 && detailedVideo.getId() > 0) {
                            mVideo.setId(detailedVideo.getId());
                        }

                        // Copy missing metadata fields only if they are present in the detail response
                        if (detailedVideo.getDescription() != null && !detailedVideo.getDescription().isEmpty()) {
                            mVideo.setDescription(detailedVideo.getDescription());
                        }
                        if (detailedVideo.getYear() != null && !detailedVideo.getYear().isEmpty()) {
                            mVideo.setYear(detailedVideo.getYear());
                        }
                        if (detailedVideo.getArea() != null && !detailedVideo.getArea().isEmpty()) {
                            mVideo.setArea(detailedVideo.getArea());
                        }
                        if (detailedVideo.getLanguage() != null && !detailedVideo.getLanguage().isEmpty()) {
                            mVideo.setLanguage(detailedVideo.getLanguage());
                        }
                        if (detailedVideo.getTypeText() != null && !detailedVideo.getTypeText().isEmpty()) {
                            mVideo.setTypeText(detailedVideo.getTypeText());
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("VideoDetailActivity", "Error fetching details", e);
                    // Still try to display what we have
                    displayVideoData();
                }

                @Override
                public void onComplete() {
                    // Display data after fetch completes (whether updated or not)
                    displayVideoData();
                }
            };

            CMSVideoEngine engine = (CMSVideoEngine) Model.getVideoEngine(VideoDetailActivity.this);
            // Use RxJava for async execution
            Observable
                    .create((ObservableOnSubscribe<Video>) emitter -> {
                        // This runs on background thread
                        Video detail = null;
                        if (mVideo.getId() > 0) {
                            detail = engine.getVideoDetail(mVideo.getId());
                        } else if (isHistory && mVideo.getTitle() != null && !mVideo.getTitle().isEmpty()) {
                            // Fallback: Search by title if ID is missing (legacy history items)
                            Log.d("VideoDetailActivity",
                                    "Missing ID for history item, searching by title: " + mVideo.getTitle());
                            ArrayList<Video> results = engine.searchVideos(mVideo.getTitle(), 1);
                            if (results != null) {
                                for (Video v : results) {
                                    if (v.getTitle().equals(mVideo.getTitle())) {
                                        detail = v;
                                        break;
                                    }
                                }
                                // If exact match not found, maybe take the first one?
                                // Safer to require strict match to avoid showing wrong video
                                if (detail == null && !results.isEmpty()) {
                                    // Try loose match if exact fail? No, safe is better.
                                }
                            }
                        }

                        if (detail != null) {
                            emitter.onNext(detail);
                        }
                        emitter.onComplete();
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);

            mDisposable.add(observer);
            return;
        }

        displayVideoData();
    }

    private void displayVideoData() {
        StringBuilder tagBuilder = new StringBuilder();
        if (mVideo.getArea() != null && !mVideo.getArea().isEmpty()) {
            tagBuilder.append(mVideo.getArea());
        }
        if (mVideo.getYear() != null && !mVideo.getYear().isEmpty()) {
            if (tagBuilder.length() > 0)
                tagBuilder.append(" · ");
            tagBuilder.append(mVideo.getYear());
        }
        if (mVideo.getTypeText() != null && !mVideo.getTypeText().isEmpty()) {
            if (tagBuilder.length() > 0)
                tagBuilder.append(" · ");
            tagBuilder.append(mVideo.getTypeText());
        }
        if (mVideo.getLanguage() != null && !mVideo.getLanguage().isEmpty()) {
            if (tagBuilder.length() > 0)
                tagBuilder.append(" · ");
            tagBuilder.append(mVideo.getLanguage());
        }
        detail_tv_video_tag.setText(tagBuilder.toString());

        StringBuilder directors = new StringBuilder();
        directors.append("导演：");
        if (mVideo.getDirectors() != null) {
            for (int i = 0; i < mVideo.getDirectors().size(); i++) {
                directors.append(mVideo.getDirectors().get(i).getName()).append(" ");
            }
        }
        if (directors.length() > 3) {
            // Remove trailing space only if we added directors
            // But actually substring might crash if length is exactly "导演：" (3 chars)
            // Use simple check
            // detail_tv_director.setText(directors.substring(0, directors.length() - 1));
            detail_tv_director.setText(directors.toString().trim());
        } else {
            detail_tv_director.setText(directors.toString());
        }

        StringBuilder actors = new StringBuilder();
        actors.append("主演：");
        if (mVideo.getActors() != null) {
            for (int i = 0; i < mVideo.getActors().size(); i++) {
                actors.append(mVideo.getActors().get(i).getName()).append(" ");
            }
        }
        detail_tv_actor.setText(actors.toString().trim());

        detail_tv_content.setText("简介：" + mVideo.getDescription());

        // for (int i = 0; i < mVideo.getVodSource().size(); i++) {
        // Log.wtf("VideoDetailActivity VodSource- ",
        // String.valueOf(mVideo.getVodSource().get(i).part));
        // }

        // 20220923 - Added new feature indicate multiple source
        vAdapter = new VideoVODSourceAdapter(this, mVideo.getVodSource(), selected, this);
        detail_tv_vod_source.setAdapter(vAdapter);

        if (mVideo.getVodSource() != null && !mVideo.getVodSource().isEmpty()
                && mVideo.getVodSource().get(selected) != null) {
            mAdapter = new VideoPlayListAdapter(this, mVideo.getVodSource().get(selected).part, this);
            video_detail_recycler_view.setAdapter(mAdapter);
            initPaging();
            checkHistoryMatch();
        }
    }

    private void checkHistoryMatch() {
        if (historyPartUrl != null && !historyPartUrl.isEmpty() && mVideo.getVodSource() != null
                && !mVideo.getVodSource().isEmpty()) {
            ArrayList<Video.Part> parts = mVideo.getVodSource().get(selected).part;
            for (int i = 0; i < parts.size(); i++) {
                if (historyPartUrl.equals(parts.get(i).getUrl())) {
                    lastPlayedIndex = i;
                    detail_menu_root_continue.setVisibility(View.VISIBLE);
                    detail_tv_continue.setText("继续播放 " + parts.get(i).getTitle());
                    // Optionally scroll to it
                    // video_detail_recycler_view.scrollToPosition(i);
                    // Or if paging is enabled, we might need to switch page.
                    // For now, simple button is enough as requested.
                    break;
                }
            }
        }
    }

    private void initPaging() {
        ArrayList<Video.Part> allParts = mVideo.getVodSource().get(selected).part;
        if (allParts.size() > 100) {
            detail_recycler_view_pages.setVisibility(View.VISIBLE);
            ArrayList<String> pages = new ArrayList<>();
            int pageCount = (allParts.size() + 99) / 100;
            for (int i = 0; i < pageCount; i++) {
                // Get actual start and end items for this page
                int startIndex = i * 100;
                int endIndex = Math.min((i + 1) * 100, allParts.size()) - 1;

                String startTitle = getDigitFromTitle(allParts.get(startIndex).getTitle());
                String endTitle = getDigitFromTitle(allParts.get(endIndex).getTitle());

                pages.add(startTitle + "-" + endTitle);
            }
            pAdapter = new VideoPageAdapter(this, pages, this);
            detail_recycler_view_pages.setAdapter(pAdapter);

            updatePartsForPage(0);
        } else {
            detail_recycler_view_pages.setVisibility(View.GONE);
            currentParts.clear();
            currentParts.addAll(allParts);
            mAdapter.updateReceiptsList(currentParts);
        }
    }

    private String getDigitFromTitle(String title) {
        String regEx = "[^0-9]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(regEx);
        java.util.regex.Matcher m = p.matcher(title);
        String result = m.replaceAll("").trim();
        return result.isEmpty() ? title : result;
    }

    private void updatePartsForPage(int page) {
        selectedPage = page;
        if (pAdapter != null) {
            pAdapter.setSelection(page);
        }
        ArrayList<Video.Part> allParts = mVideo.getVodSource().get(selected).part;
        currentParts.clear();
        int start = page * 100;
        int end = Math.min((page + 1) * 100, allParts.size());
        for (int i = start; i < end; i++) {
            currentParts.add(allParts.get(i));
        }
        if (isReverseSort) {
            Collections.reverse(currentParts);
        }
        mAdapter.updateReceiptsList(currentParts);
    }

    public void onReturnClick() {
        String url = Model.getData().getLastPlayUrl(this);
        if (url.isEmpty()) {
            return;
        }
        url = url.substring(url.indexOf("://") + 3);
        String videoName = Model.getData().getLastPlayName(this);
        String accountName = FFTVApplication.account;

        DisposableObserver<String> observer = new DisposableObserver<String>() {
            @Override
            public void onNext(String code) {
                if (code.equals("200")) {
                    Toast.makeText(VideoDetailActivity.this, "反馈错误视频成功", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(VideoDetailActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        final String urlUp = url;
        Observable
                .create((ObservableOnSubscribe<String>) emitter -> {
                    Response response = null;
                    try {
                        String requestBody = "username=" + accountName + "&title=" + videoName + "&url=" + urlUp;
                        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                        RequestBody body = RequestBody.create(mediaType, requestBody);
                        final Request request = new Request.Builder()
                                .url(RETURN_URL)
                                .post(body)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .build();
                        response = CommonUtils.getOkHttpClient().newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String result = response.body().string().substring(2).trim();
                            emitter.onNext(result);
                        }
                    } finally {
                        if (response != null) {
                            response.close();
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        mDisposable.add(observer);
    }

    public void onCollectClick() {
        LinkedList<VideoCollect> videoCollectList = (LinkedList<VideoCollect>) InternalFileSaveUtil.getInstance(this)
                .get("video_collect");
        if (videoCollectList == null) {
            videoCollectList = new LinkedList<>();
        }

        if (detail_tv_collect.getText().equals("收藏")) {
            // 收藏
            VideoCollect videoCollect = new VideoCollect();
            videoCollect.setVideo(mVideo);
            videoCollect.setVideoEngine(Model.getData().getVideoEngine(this));

            videoCollectList.add(0, videoCollect);
            if (videoCollectList.size() > 1) {
                for (int i = 1; i < videoCollectList.size(); i++) {
                    if (videoCollectList.get(i).getVideo().getTitle().equals(mVideo.getTitle())
                            && videoCollectList.get(i).getVideoEngine().equals(Model.getData().getVideoEngine(this))) {
                        videoCollectList.remove(i);
                        break;
                    }
                }
            }

            if (videoCollectList.size() > Const.VIDEO_COLLECTION_NUM) {
                videoCollectList.remove(videoCollectList.size() - 1);
            }
            InternalFileSaveUtil.getInstance(this).put("video_collect", videoCollectList);

            detail_tv_collect.setText("取消收藏");
            detail_iv_collect.setImageResource(R.drawable.ic_not_collected_focus);
        } else {
            // 取消收藏
            for (int i = 1; i < videoCollectList.size(); i++) {
                if (videoCollectList.get(i).getVideo().getTitle().equals(mVideo.getTitle())
                        && videoCollectList.get(i).getVideoEngine().equals(Model.getData().getVideoEngine(this))) {
                    videoCollectList.remove(i);
                    break;
                }
            }
            InternalFileSaveUtil.getInstance(this).put("video_collect", videoCollectList);

            detail_tv_collect.setText("收藏");
            detail_iv_collect.setImageResource(R.drawable.ic_collected_focus);
        }
    }

    // 20220910 - Added new feature sorting
    public void onSortClick() {
        isReverseSort = !isReverseSort;
        if (isReverseSort) {
            tvSort.setText("正序");
        } else {
            tvSort.setText("倒序");
        }
        updatePartsForPage(selectedPage);
    }

    // 20220923 - Added new feature indicate multiple source
    @Override
    public void onVODSourceClick(int position) {
        selected = position;
        vAdapter.setSelection(position);

        // mAdapter.updateReceiptsList(mVideo.getVodSource().get(position).part);
        initPaging();

        // Don't try to move focus - let it stay on the source chip
        // User can navigate down naturally with remote control
    }

    @Override
    public void onPageClick(int position) {
        updatePartsForPage(position);
    }
}
