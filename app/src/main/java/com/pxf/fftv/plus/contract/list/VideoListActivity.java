package com.pxf.fftv.plus.contract.list;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.VideoConfig;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.common.Ui;
import com.pxf.fftv.plus.contract.VideoItem;
import com.pxf.fftv.plus.contract.detail.VideoDetailActivity;
import com.pxf.fftv.plus.contract.detail.VideoDetailEvent;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.VideoEngineParam;
import com.pxf.fftv.plus.model.video.Video;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import static com.pxf.fftv.plus.Const.VIDEO_LIST_COLUMN;

public class VideoListActivity extends AppCompatActivity implements VideoAdapter.OnClickListener, OnStartLoadMoreListener {

    @BindView(R.id.video_list_title)
    TextView video_list_title;

    @BindView(R.id.video_list_title_0)
    TextView video_list_title_0;

    @BindView(R.id.video_list_title_1)
    TextView video_list_title_1;

    @BindView(R.id.video_list_title_2)
    TextView video_list_title_2;

    @BindView(R.id.video_list_title_3)
    TextView video_list_title_3;

    @BindView(R.id.video_list_title_4)
    TextView video_list_title_4;

    @BindView(R.id.video_list_title_5)
    TextView video_list_title_5;

    @BindView(R.id.video_list_title_6)
    TextView video_list_title_6;

    @BindView(R.id.video_list_title_7)
    TextView video_list_title_7;

    @BindView(R.id.video_list_title_8)
    TextView video_list_title_8;

    @BindView(R.id.video_list_title_9)
    TextView video_list_title_9;

    @BindView(R.id.video_list_title_10)
    TextView video_list_title_10;

    @BindView(R.id.video_list_title_11)
    TextView video_list_title_11;

    @BindView(R.id.video_list_title_12)
    TextView video_list_title_12;

    @BindView(R.id.video_list_title_13)
    TextView video_list_title_13;

    @BindView(R.id.video_list_title_14)
    TextView video_list_title_14;

    @BindView(R.id.video_list_title_15)
    TextView video_list_title_15;

    @BindView(R.id.video_list_recycler_view)
    RecyclerView video_list_recycler_view;

    @BindView(R.id.video_list_refresh)
    SmartRefreshLayout video_list_refresh;

    @BindView(R.id.top_bar_menu_right_note)
    TextView top_bar_menu_right_note;

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
    private ArrayList<Video> videoList_10 = new ArrayList<>();
    private ArrayList<Video> videoList_11 = new ArrayList<>();
    private ArrayList<Video> videoList_12 = new ArrayList<>();
    private ArrayList<Video> videoList_13 = new ArrayList<>();
    private ArrayList<Video> videoList_14 = new ArrayList<>();
    private ArrayList<Video> videoList_15 = new ArrayList<>();

    private ArrayList<VideoItem> itemList = new ArrayList<>();
    private ArrayList<TextView> titleList = new ArrayList<>();
    private ArrayList<ArrayList<Video>> videoList = new ArrayList<>();

    private VideoConfig.Video1 video1;
    private VideoConfig.Video2[] video2s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        ButterKnife.bind(this);

        initView();
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
        Ui.configTopBar(this);
        initGongGao();
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
        titleList.add(video_list_title_10);
        titleList.add(video_list_title_11);
        titleList.add(video_list_title_12);
        titleList.add(video_list_title_13);
        titleList.add(video_list_title_14);
        titleList.add(video_list_title_15);

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
        videoList.add(videoList_10);
        videoList.add(videoList_11);
        videoList.add(videoList_12);
        videoList.add(videoList_13);
        videoList.add(videoList_14);
        videoList.add(videoList_15);
    }

    private void initGongGao(){
        top_bar_menu_right_note.setSingleLine(true);
        top_bar_menu_right_note.setSelected(true);
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
                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE).setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE).setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (textView.isFocused()) {
                        textView.setScaleX((float)animation.getAnimatedValue());
                        textView.setScaleY((float)animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (textView.isFocused()) {
                        textView.setScaleX((float)animation.getAnimatedValue());
                        textView.setScaleY((float)animation.getAnimatedValue());
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

        textView.setTextColor(getResources().getColor(R.color.colorTextNormal));
        textView.setBackground(null);
    }

    private void setAllTabTextColorDefault() {
        for (TextView textView : titleSet) {
            textView.setTextColor(getResources().getColor(R.color.colorVideoListTitleNormal));
        }
    }

    private void loadDate(int index, VideoEngineParam videoEngineParam, boolean loadMore) {
        ArrayList<Video> list = videoList.get(index);
        int leftFocus = titleList.get(index).getId();

        // 分页加载
        int page = 1;
        if (!list.isEmpty()) {
            //Fixed page loading issue
            int pageItemNum = list.get(list.size() - 1).getPageItemNum();
            int pageCount = list.get(list.size() - 1).getPageCount();
            //int currentPage = list.size() / pageItemNum;
            int currentPage = list.get(list.size() - 1).getPage();

            if (currentPage < pageCount) {
                page = currentPage + 1;
            } else {
                return;
            }
        }
        DisposableObserver<ArrayList<Video>> observer = new DisposableObserver<ArrayList<Video>>() {
            @Override
            public void onNext(ArrayList<Video> videos) {
                int previousSize = list.size();
                list.addAll(videos);
                if (mVideoAdapter != null) {
                    if (previousSize == 0) {
                        mVideoAdapter = new VideoAdapter(VideoListActivity.this, VideoListActivity.this, list, leftFocus, VideoListActivity.this);
                        video_list_recycler_view.setAdapter(mVideoAdapter);
                    } else {
                        mVideoAdapter.setList(list);
                        mVideoAdapter.notifyItemChanged(previousSize, videos.size());
                    }
                } else {
                    mVideoAdapter = new VideoAdapter(VideoListActivity.this, VideoListActivity.this, list, leftFocus, VideoListActivity.this);
                    video_list_recycler_view.setAdapter(mVideoAdapter);
                }
                if (loadMore) {
                    video_list_refresh.finishLoadMore(300);
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
                    emitter.onNext(Model.getVideoEngine(VideoListActivity.this).getVideos(videoEngineParam, finalPage));
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        mDisposable.add(observer);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onVideoListEvent(VideoListEvent event) {
        video1 = event.getVideo1();
        video2s = event.getVideo2s();
        itemList = event.getVideoItemList();
        video_list_title.setText(event.getTitle());

        for (int i = 0 ; i < itemList.size() ; i++) {
            titleList.get(i).setVisibility(View.VISIBLE);
            titleList.get(i).setText(itemList.get(i).getTitle());
            ArrayList<Video> list = videoList.get(i);
            int leftFocus = titleList.get(i).getId();
            int finalI = i;
            configTitleText(titleList.get(i), new FocusAction() {
                @Override
                public void onFocus() {
                    if (list.isEmpty()) {
                        loadDate(finalI, new VideoEngineParam(video1.getTitle(), video2s[finalI].getTitle(), video2s[finalI].getUrl()), false);
                    } else {
                        mVideoAdapter.refreshList(list, leftFocus);
                    }
                }
            });
        }

        for (int i = itemList.size() ; i < titleList.size() ; i++) {
            titleList.get(i).setVisibility(View.GONE);
        }

        if (itemList.size() > 0) {
            titleList.get(itemList.size() - 1).setNextFocusDownId(titleList.get(0).getId());
        }

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
                    switch (currentFocusTitle.getId()) {
                        case R.id.video_list_title_0:
                            if (video2s.length > 0) {
                                loadDate(0, new VideoEngineParam(video1.getTitle(), video2s[0].getTitle(), video2s[0].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_1:
                            if (video2s.length > 1) {
                                loadDate(1, new VideoEngineParam(video1.getTitle(), video2s[1].getTitle(), video2s[1].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_2:
                            if (video2s.length > 2) {
                                loadDate(2, new VideoEngineParam(video1.getTitle(), video2s[2].getTitle(), video2s[2].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_3:
                            if (video2s.length > 3) {
                                loadDate(3, new VideoEngineParam(video1.getTitle(), video2s[3].getTitle(), video2s[3].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_4:
                            if (video2s.length > 4) {
                                loadDate(4, new VideoEngineParam(video1.getTitle(), video2s[4].getTitle(), video2s[4].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_5:
                            if (video2s.length > 5) {
                                loadDate(5, new VideoEngineParam(video1.getTitle(), video2s[5].getTitle(), video2s[5].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_6:
                            if (video2s.length > 6) {
                                loadDate(6, new VideoEngineParam(video1.getTitle(), video2s[6].getTitle(), video2s[6].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_7:
                            if (video2s.length > 7) {
                                loadDate(7, new VideoEngineParam(video1.getTitle(), video2s[7].getTitle(), video2s[7].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_8:
                            if (video2s.length > 8) {
                                loadDate(8, new VideoEngineParam(video1.getTitle(), video2s[8].getTitle(), video2s[8].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_9:
                            if (video2s.length > 9) {
                                loadDate(9, new VideoEngineParam(video1.getTitle(), video2s[9].getTitle(), video2s[9].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_10:
                            if (video2s.length > 10) {
                                loadDate(10, new VideoEngineParam(video1.getTitle(), video2s[10].getTitle(), video2s[10].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_11:
                            if (video2s.length > 11) {
                                loadDate(11, new VideoEngineParam(video1.getTitle(), video2s[11].getTitle(), video2s[11].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_12:
                            if (video2s.length > 12) {
                                loadDate(12, new VideoEngineParam(video1.getTitle(), video2s[12].getTitle(), video2s[12].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_13:
                            if (video2s.length > 13) {
                                loadDate(13, new VideoEngineParam(video1.getTitle(), video2s[13].getTitle(), video2s[13].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_14:
                            if (video2s.length > 14) {
                                loadDate(14, new VideoEngineParam(video1.getTitle(), video2s[14].getTitle(), video2s[14].getUrl()), true);
                            }
                            break;
                        case R.id.video_list_title_15:
                            if (video2s.length > 15) {
                                loadDate(15, new VideoEngineParam(video1.getTitle(), video2s[15].getTitle(), video2s[15].getUrl()), true);
                            }
                            break;
                    }
                });


        titleList.get(event.getFocusIndex()).requestFocus();

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onVideoItemClick(int position) {
        Intent intent = new Intent(this, VideoDetailActivity.class);
        startActivity(intent);

        EventBus.getDefault().postSticky(new VideoDetailEvent(mVideoAdapter.getList().get(position)));
    }

    @Override
    public void onStartLoadMore() {
        lastFocusCard = getCurrentFocus();
        video_list_refresh.autoLoadMore();
    }
}
