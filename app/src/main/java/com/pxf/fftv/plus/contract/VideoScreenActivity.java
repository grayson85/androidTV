package com.pxf.fftv.plus.contract;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.VideoConfig;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.contract.detail.VideoDetailActivity;
import com.pxf.fftv.plus.contract.detail.VideoDetailEvent;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.VideoEngineParam;
import com.pxf.fftv.plus.model.video.Video;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;

//import com.scwang.smart.refresh.layout.footer.BallPulseFooter;// Correct path;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.footer.BallPulseFooter;

import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;
import static com.pxf.fftv.plus.Const.LOG_TAG;
import static com.pxf.fftv.plus.Const.VIDEO_2;
import static com.pxf.fftv.plus.Const.VIDEO_1;
import static com.pxf.fftv.plus.Const.VIDEO_LIST_COLUMN;

public class VideoScreenActivity extends AppCompatActivity
        implements VideoAdapter.OnClickListener, OnStartLoadMoreListener {

    TextView video_list_title;

    TextView video_list_title_0;

    TextView video_list_title_1;

    TextView video_list_title_2;

    TextView video_list_title_3;

    TextView video_list_title_4;

    TextView video_list_title_5;

    TextView video_list_title_6;

    TextView video_list_title_7;

    TextView video_list_title_8;

    TextView video_list_title_9;

    RecyclerView video_list_recycler_view;

    SmartRefreshLayout video_list_refresh;

    RecyclerView video_screen_recycler_view_rank;

    RecyclerView video_screen_recycler_view_area;

    RecyclerView video_screen_recycler_view_year;

    RecyclerView video_screen_recycler_view_act;

    RecyclerView video_screen_recycler_view_language;

    RecyclerView video_screen_recycler_view_type;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    private TextView currentFocusTitle;
    private HashSet<TextView> titleSet = new HashSet<>();
    private VideoAdapter mVideoAdapter;
    private View lastFocusCard;

    private ArrayList<Video> videoList_0 = new ArrayList<>();
    private ArrayList<Video> videoList_1 = new ArrayList<>();
    private ArrayList<Video> videoList_2 = new ArrayList<>();
    private ArrayList<Video> videoList_3 = new ArrayList<>();
    private ArrayList<Video> videoList_4 = new ArrayList<>();
    private ArrayList<Video> videoList_5 = new ArrayList<>();
    private ArrayList<Video> videoList_6 = new ArrayList<>();
    private ArrayList<Video> videoList_7 = new ArrayList<>();
    private ArrayList<Video> videoList_8 = new ArrayList<>();
    private ArrayList<Video> videoList_9 = new ArrayList<>();

    private ArrayList<VideoItem> itemList = new ArrayList<>();
    private ArrayList<TextView> titleList = new ArrayList<>();
    private ArrayList<ArrayList<Video>> videoList = new ArrayList<>();

    private VideoConfig.VideoScreen[] screens;

    private VideoSubTitleAdapter mRankAdapter;
    private VideoSubTitleAdapter mTypeAdapter;
    private VideoSubTitleAdapter mYearAdapter;
    private VideoSubTitleAdapter mLanguageAdapter;
    private VideoSubTitleAdapter mActAdapter;
    private VideoSubTitleAdapter mAreaAdapter;

    private ArrayList<Video> mList = new ArrayList<>();

    private String baseUrl;

    private int loadMorePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_screen);

        video_list_title = findViewById(R.id.video_list_title);
        video_list_title_0 = findViewById(R.id.video_list_title_0);
        video_list_title_1 = findViewById(R.id.video_list_title_1);
        video_list_title_2 = findViewById(R.id.video_list_title_2);
        video_list_title_3 = findViewById(R.id.video_list_title_3);
        video_list_title_4 = findViewById(R.id.video_list_title_4);
        video_list_title_5 = findViewById(R.id.video_list_title_5);
        video_list_title_6 = findViewById(R.id.video_list_title_6);
        video_list_title_7 = findViewById(R.id.video_list_title_7);
        video_list_title_8 = findViewById(R.id.video_list_title_8);
        video_list_title_9 = findViewById(R.id.video_list_title_9);
        video_list_recycler_view = findViewById(R.id.video_list_recycler_view);
        video_list_refresh = findViewById(R.id.video_list_refresh);
        video_screen_recycler_view_rank = findViewById(R.id.video_screen_recycler_view_rank);
        video_screen_recycler_view_area = findViewById(R.id.video_screen_recycler_view_area);
        video_screen_recycler_view_year = findViewById(R.id.video_screen_recycler_view_year);
        video_screen_recycler_view_act = findViewById(R.id.video_screen_recycler_view_act);
        video_screen_recycler_view_language = findViewById(R.id.video_screen_recycler_view_language);
        video_screen_recycler_view_type = findViewById(R.id.video_screen_recycler_view_type);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        // Reinitialize disposable if it was cleared
        if (mDisposable == null || mDisposable.isDisposed()) {
            mDisposable = new CompositeDisposable();
        }
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Only clear on destroy to prevent cancelling ongoing loads
        if (mDisposable != null) {
            mDisposable.clear();
        }
    }

    private void initView() {
        titleList.add(video_list_title_0);
        titleList.add(video_list_title_1);
        titleList.add(video_list_title_2);
        titleList.add(video_list_title_3);
        titleList.add(video_list_title_4);
        titleList.add(video_list_title_5);
        titleList.add(video_list_title_6);
        titleList.add(video_list_title_7);
        titleList.add(video_list_title_8);
        titleList.add(video_list_title_9);

        videoList.add(videoList_0);
        videoList.add(videoList_1);
        videoList.add(videoList_2);
        videoList.add(videoList_3);
        videoList.add(videoList_4);
        videoList.add(videoList_5);
        videoList.add(videoList_6);
        videoList.add(videoList_7);
        videoList.add(videoList_8);
        videoList.add(videoList_9);

        initVideoRecyclerView();

        // 获取筛选配置
        switch (Model.getData().getVideoEngine(this)) {
            case VIDEO_1:
                screens = VideoConfig.VIDEO_SCREEN_WEIDUO;
                break;
            case VIDEO_2:
                screens = VideoConfig.getVideoScreenCms();
                break;
            default:
                screens = VideoConfig.getVideoScreenCms();
                break;
        }

        video_screen_recycler_view_rank
                .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        video_screen_recycler_view_area
                .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        video_screen_recycler_view_act
                .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        video_screen_recycler_view_year
                .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        video_screen_recycler_view_type
                .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        video_screen_recycler_view_language
                .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        initLeftTitle();

        if (!titleList.isEmpty()) {
            titleList.get(0).requestFocus();
        }
    }

    private void initVideoRecyclerView() {
        video_list_recycler_view.setLayoutManager(new GridLayoutManager(this, VIDEO_LIST_COLUMN) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return FFTVApplication.screenHeight / 2;
            }
        });

        // 优化
        video_list_recycler_view.setHasFixedSize(true);
        // 卡片最大缓存数量，该数量以内的卡片能保证动画效果不卡顿
        video_list_recycler_view.setItemViewCacheSize(500);

        video_list_refresh
                .setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale))
                .setPrimaryColors(getResources().getColor(R.color.colorTheme))
                .setEnableRefresh(false)
                .setEnableLoadMore(true)
                .setOnLoadMoreListener(refreshLayout -> {
                    int viewId = currentFocusTitle.getId();
                    int index = -1;
                    if (viewId == R.id.video_list_title_0)
                        index = 0;
                    else if (viewId == R.id.video_list_title_1)
                        index = 1;
                    else if (viewId == R.id.video_list_title_2)
                        index = 2;
                    else if (viewId == R.id.video_list_title_3)
                        index = 3;
                    else if (viewId == R.id.video_list_title_4)
                        index = 4;
                    else if (viewId == R.id.video_list_title_5)
                        index = 5;
                    else if (viewId == R.id.video_list_title_6)
                        index = 6;
                    else if (viewId == R.id.video_list_title_7)
                        index = 7;
                    else if (viewId == R.id.video_list_title_8)
                        index = 8;
                    else if (viewId == R.id.video_list_title_9)
                        index = 9;

                    if (index >= 0 && screens.length > index) {
                        loadDate(index, new VideoEngineParam(screens[index].getTitle(), "", generateUrl()), true);
                    }
                });
    }

    private void initLeftTitle() {
        for (int i = 0; i < screens.length; i++) {
            titleList.get(i).setVisibility(View.VISIBLE);
            titleList.get(i).setText(screens[i].getTitle());
            int finalI = i;
            configTitleText(titleList.get(i), new FocusAction() {
                @Override
                public void onFocus() {
                    baseUrl = screens[finalI].getBaseUrl();
                    // 处理筛选标题
                    if (screens[finalI].getRanks().length != 0 && screens[finalI].getRanks()[0].isEnable()) {
                        video_screen_recycler_view_rank.setVisibility(View.VISIBLE);

                        if (mRankAdapter == null) {
                            mRankAdapter = new VideoSubTitleAdapter(VideoScreenActivity.this,
                                    screens[finalI].getRanks(), new VideoSubTitleAdapter.OnVideoSubListener() {
                                        @Override
                                        public void onClick(int position, String url) {
                                            loadDate(finalI,
                                                    new VideoEngineParam(screens[finalI].getTitle(), "", generateUrl()),
                                                    false);
                                        }

                                        @Override
                                        public void onFocus(int position) {
                                            video_screen_recycler_view_rank.scrollToPosition(position);
                                        }
                                    }, titleList.get(finalI).getId());
                            video_screen_recycler_view_rank.setAdapter(mRankAdapter);
                        } else {
                            mRankAdapter.refreshList(screens[finalI].getRanks(), 0, titleList.get(finalI).getId());
                        }
                    } else {
                        video_screen_recycler_view_rank.setVisibility(View.GONE);
                    }

                    if (screens[finalI].getTypes().length != 0 && screens[finalI].getTypes()[0].isEnable()) {
                        video_screen_recycler_view_type.setVisibility(View.VISIBLE);

                        if (mTypeAdapter == null) {
                            mTypeAdapter = new VideoSubTitleAdapter(VideoScreenActivity.this,
                                    screens[finalI].getTypes(), new VideoSubTitleAdapter.OnVideoSubListener() {
                                        @Override
                                        public void onClick(int position, String url) {
                                            loadDate(finalI,
                                                    new VideoEngineParam(screens[finalI].getTitle(), "", generateUrl()),
                                                    false);
                                        }

                                        @Override
                                        public void onFocus(int position) {
                                            video_screen_recycler_view_type.scrollToPosition(position);
                                        }
                                    }, titleList.get(finalI).getId());
                            video_screen_recycler_view_type.setAdapter(mTypeAdapter);
                        } else {
                            mTypeAdapter.refreshList(screens[finalI].getTypes(), 0, titleList.get(finalI).getId());
                        }
                    } else {
                        video_screen_recycler_view_type.setVisibility(View.GONE);
                    }

                    if (screens[finalI].getYears().length != 0 && screens[finalI].getYears()[0].isEnable()) {
                        video_screen_recycler_view_year.setVisibility(View.VISIBLE);

                        if (mYearAdapter == null) {
                            mYearAdapter = new VideoSubTitleAdapter(VideoScreenActivity.this,
                                    screens[finalI].getYears(), new VideoSubTitleAdapter.OnVideoSubListener() {
                                        @Override
                                        public void onClick(int position, String url) {
                                            loadDate(finalI,
                                                    new VideoEngineParam(screens[finalI].getTitle(), "", generateUrl()),
                                                    false);
                                        }

                                        @Override
                                        public void onFocus(int position) {
                                            video_screen_recycler_view_year.scrollToPosition(position);
                                        }
                                    }, titleList.get(finalI).getId());
                            video_screen_recycler_view_year.setAdapter(mYearAdapter);
                        } else {
                            mYearAdapter.refreshList(screens[finalI].getYears(), 0, titleList.get(finalI).getId());
                        }
                    } else {
                        video_screen_recycler_view_year.setVisibility(View.GONE);
                    }

                    if (screens[finalI].getLanguages().length != 0 && screens[finalI].getLanguages()[0].isEnable()) {
                        video_screen_recycler_view_language.setVisibility(View.VISIBLE);

                        if (mLanguageAdapter == null) {
                            mLanguageAdapter = new VideoSubTitleAdapter(VideoScreenActivity.this,
                                    screens[finalI].getLanguages(), new VideoSubTitleAdapter.OnVideoSubListener() {
                                        @Override
                                        public void onClick(int position, String url) {
                                            loadDate(finalI,
                                                    new VideoEngineParam(screens[finalI].getTitle(), "", generateUrl()),
                                                    false);
                                        }

                                        @Override
                                        public void onFocus(int position) {
                                            video_screen_recycler_view_language.scrollToPosition(position);
                                        }
                                    }, titleList.get(finalI).getId());
                            video_screen_recycler_view_language.setAdapter(mLanguageAdapter);
                        } else {
                            mLanguageAdapter.refreshList(screens[finalI].getLanguages(), 0,
                                    titleList.get(finalI).getId());
                        }
                    } else {
                        video_screen_recycler_view_language.setVisibility(View.GONE);
                    }

                    if (screens[finalI].getActs().length != 0 && screens[finalI].getActs()[0].isEnable()) {
                        video_screen_recycler_view_act.setVisibility(View.VISIBLE);

                        if (mActAdapter == null) {
                            mActAdapter = new VideoSubTitleAdapter(VideoScreenActivity.this, screens[finalI].getActs(),
                                    new VideoSubTitleAdapter.OnVideoSubListener() {
                                        @Override
                                        public void onClick(int position, String url) {
                                            loadDate(finalI,
                                                    new VideoEngineParam(screens[finalI].getTitle(), "", generateUrl()),
                                                    false);
                                        }

                                        @Override
                                        public void onFocus(int position) {
                                            video_screen_recycler_view_act.scrollToPosition(position);
                                        }
                                    }, titleList.get(finalI).getId());
                            video_screen_recycler_view_act.setAdapter(mActAdapter);
                        } else {
                            mActAdapter.refreshList(screens[finalI].getActs(), 0, titleList.get(finalI).getId());
                        }
                    } else {
                        video_screen_recycler_view_act.setVisibility(View.GONE);
                    }

                    if (screens[finalI].getAreas().length != 0 && screens[finalI].getAreas()[0].isEnable()) {
                        video_screen_recycler_view_area.setVisibility(View.VISIBLE);

                        if (mAreaAdapter == null) {
                            mAreaAdapter = new VideoSubTitleAdapter(VideoScreenActivity.this,
                                    screens[finalI].getAreas(), new VideoSubTitleAdapter.OnVideoSubListener() {
                                        @Override
                                        public void onClick(int position, String url) {
                                            loadDate(finalI,
                                                    new VideoEngineParam(screens[finalI].getTitle(), "", generateUrl()),
                                                    false);
                                        }

                                        @Override
                                        public void onFocus(int position) {
                                            video_screen_recycler_view_area.scrollToPosition(position);
                                        }
                                    }, titleList.get(finalI).getId());
                            video_screen_recycler_view_area.setAdapter(mAreaAdapter);
                        } else {
                            mAreaAdapter.refreshList(screens[finalI].getAreas(), 0, titleList.get(finalI).getId());
                        }
                    } else {
                        video_screen_recycler_view_area.setVisibility(View.GONE);
                    }

                    // 加载数据，此时数据应为最原始未筛选数据
                    loadDate(finalI, new VideoEngineParam(screens[finalI].getTitle(), "", generateUrl()), false);
                }
            });
        }
        for (int i = screens.length; i < titleList.size(); i++) {
            titleList.get(i).setVisibility(View.GONE);
        }

        if (screens.length > 0) {
            titleList.get(screens.length - 1).setNextFocusDownId(titleList.get(0).getId());
        }
        /*
         * if (screens.length > 0) {
         * }
         * video_screen_recycler_view_rank.setAdapter(new VideoSubTitleAdapter(this,
         * screens[0].getTypes(), new VideoSubTitleAdapter.OnVideoSubListener() {
         * 
         * @Override
         * public void onClick(int position, String url) {
         * }
         * }));
         * video_screen_recycler_view_area.setAdapter(new VideoSubTitleAdapter(this,
         * screens[0].getAreas(), new VideoSubTitleAdapter.OnVideoSubListener() {
         * 
         * @Override
         * public void onClick(int position, String url) {
         * video_screen_recycler_view_area.scrollToPosition(position);
         * }
         * }));
         */
    }

    private String generateUrl() {
        String url = baseUrl;
        if (mRankAdapter != null) {
            url += mRankAdapter.getSelectionUrl();
        }
        if (mTypeAdapter != null) {
            url += mTypeAdapter.getSelectionUrl();
        }
        if (mAreaAdapter != null) {
            url += mAreaAdapter.getSelectionUrl();
        }
        if (mActAdapter != null) {
            url += mActAdapter.getSelectionUrl();
        }
        if (mYearAdapter != null) {
            url += mYearAdapter.getSelectionUrl();
        }
        if (mLanguageAdapter != null) {
            url += mLanguageAdapter.getSelectionUrl();
        }
        return url;
    }

    private void configTitleText(TextView textView, @Nullable FocusAction focusAction) {
        titleSet.add(textView);

        textView.setFocusable(true);
        textView.setClickable(true);

        textView.setOnFocusChangeListener((view, focus) -> {
            if (focus) {
                currentFocusTitle = textView;
                setAllTabTextColorDefault();
                textView.setTextColor(getResources().getColor(R.color.colorTextFocus));
                textView.setBackground(getResources().getDrawable(R.drawable.bg_video_list_title_focus));
                if (focusAction != null) {
                    focusAction.onFocus();
                }
                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE)
                        .setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (textView.isFocused()) {
                        textView.setScaleX((float) animation.getAnimatedValue());
                        textView.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (textView.isFocused()) {
                        textView.setScaleX((float) animation.getAnimatedValue());
                        textView.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                if (!(getCurrentFocus() instanceof TextView) || titleSet.contains(getCurrentFocus())) {
                    currentFocusTitle = textView;
                    textView.setTextColor(getResources().getColor(R.color.colorTabTextCurrentButNoFocus));
                }
                textView.setBackground(null);
                if (focusAction != null) {
                    focusAction.onLoseFocus();
                }

                ValueAnimator animator = ValueAnimator.ofFloat(1.1f, 1.0f).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(valueAnimator -> {
                    if (!isFinishing()) {
                        textView.setScaleX((float) valueAnimator.getAnimatedValue());
                        textView.setScaleY((float) valueAnimator.getAnimatedValue());
                    }
                });
                animator.start();
            }
        });

        textView.setTextColor(getResources().getColor(R.color.colorVideoListTitleNormal));
        textView.setBackground(null);
    }

    private void setAllTabTextColorDefault() {
        for (TextView textView : titleSet) {
            textView.setTextColor(getResources().getColor(R.color.colorVideoListTitleNormal));
        }
    }

    private void loadDate(int index, VideoEngineParam videoEngineParam, boolean loadMore) {
        int leftFocus = titleList.get(index).getId();

        if (!loadMore) {
            mList = new ArrayList<>();
            // Reset the "no more data" state when switching categories
            // This allows infinite scroll to work again after switching from a category
            // that reached the end
            video_list_refresh.resetNoMoreData();
        }

        // 分页加载
        int page = 1;
        if (!mList.isEmpty()) {
            // Fixed page loading issue
            int pageItemNum = mList.get(mList.size() - 1).getPageItemNum();
            int pageCount = mList.get(mList.size() - 1).getPageCount();
            // int currentPage = mList.size() / pageItemNum;
            int currentPage = mList.get(mList.size() - 1).getPage();

            if (currentPage < pageCount) {
                page = currentPage + 1;
            } else {
                // Properly signal to SmartRefreshLayout that there's no more data
                // This prevents the loading indicator from being stuck
                if (loadMore) {
                    video_list_refresh.finishLoadMoreWithNoMoreData();
                }
                return;
            }
        }
        DisposableObserver<ArrayList<Video>> observer = new DisposableObserver<ArrayList<Video>>() {
            @Override
            public void onNext(ArrayList<Video> videos) {
                int previousSize = mList.size();
                mList.addAll(videos);

                if (mVideoAdapter != null) {
                    if (previousSize == 0) {
                        mVideoAdapter = new VideoAdapter(VideoScreenActivity.this, VideoScreenActivity.this, mList,
                                leftFocus, VideoScreenActivity.this);
                        video_list_recycler_view.setAdapter(mVideoAdapter);
                    } else {
                        mVideoAdapter.setList(mList);
                        // Use notifyItemRangeInserted to avoid focus loss
                        mVideoAdapter.notifyItemRangeInserted(previousSize, videos.size());
                    }
                } else {
                    mVideoAdapter = new VideoAdapter(VideoScreenActivity.this, VideoScreenActivity.this, mList,
                            leftFocus, VideoScreenActivity.this);
                    video_list_recycler_view.setAdapter(mVideoAdapter);
                }
                if (loadMore) {
                    video_list_refresh.finishLoadMore(0);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "load video error ", e);
            }

            @Override
            public void onComplete() {

            }
        };

        int finalPage = page;
        Observable
                .create((ObservableOnSubscribe<ArrayList<Video>>) emitter -> {
                    emitter.onNext(
                            Model.getVideoEngine(VideoScreenActivity.this).getVideos(videoEngineParam, finalPage));
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        mDisposable.add(observer);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onVideoScreenEvent(VideoScreenEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onVideoItemClick(int position) {
        // Post sticky event BEFORE starting activity so it's available when activity
        // registers
        EventBus.getDefault().postSticky(new VideoDetailEvent(mVideoAdapter.getList().get(position)));

        Intent intent = new Intent(this, VideoDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStartLoadMore() {
        lastFocusCard = getCurrentFocus();

        // Call loadDate directly instead of relying on SmartRefreshLayout's
        // autoLoadMore
        // which seems to have issues after ~100 items
        if (currentFocusTitle != null) {
            for (int i = 0; i < screens.length; i++) {
                if (currentFocusTitle.getId() == titleList.get(i).getId()) {
                    loadDate(i, new VideoEngineParam(screens[i].getTitle(), "", generateUrl()), true);
                    break;
                }
            }
        }
    }
}
