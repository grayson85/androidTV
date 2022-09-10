package com.pxf.fftv.plus.contract;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.VideoConfig;
import com.pxf.fftv.plus.contract.detail.VideoDetailActivity;
import com.pxf.fftv.plus.contract.detail.VideoDetailEvent;
import com.pxf.fftv.plus.contract.list.VideoListActivity;
import com.pxf.fftv.plus.contract.list.VideoListEvent;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.VideoEngineParam;
import com.pxf.fftv.plus.model.video.Video;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class HomeVideoFragment extends BaseVideoFragment {

    public static final String VIDEO_PARAM = "video_param";

    @BindView(R.id.video_card_root_1)
    RelativeLayout video_card_root_1;

    @BindView(R.id.video_card_root_2)
    RelativeLayout video_card_root_2;

    @BindView(R.id.video_card_root_3)
    RelativeLayout video_card_root_3;

    @BindView(R.id.video_card_root_4)
    RelativeLayout video_card_root_4;

    @BindView(R.id.video_card_root_5)
    RelativeLayout video_card_root_5;

    @BindView(R.id.video_card_root_6)
    RelativeLayout video_card_root_6;

    @BindView(R.id.video_card_root_7)
    RelativeLayout video_card_root_7;

    @BindView(R.id.video_card_root_8)
    RelativeLayout video_card_root_8;

    @BindView(R.id.video_card_root_9)
    RelativeLayout video_card_root_9;

    @BindView(R.id.video_card_root_10)
    RelativeLayout video_card_root_10;

    @BindView(R.id.video_card_root_11)
    RelativeLayout video_card_root_11;

    @BindView(R.id.video_card_root_12)
    RelativeLayout video_card_root_12;

    @BindView(R.id.video_card_root_13)
    RelativeLayout video_card_root_13;

    @BindView(R.id.video_card_root_14)
    RelativeLayout video_card_root_14;

    @BindView(R.id.video_card_root_15)
    RelativeLayout video_card_root_15;

    @BindView(R.id.video_card_root_16)
    RelativeLayout video_card_root_16;

    @BindView(R.id.video_card_root_17)
    RelativeLayout video_card_root_17;

    @BindView(R.id.video_card_root_18)
    RelativeLayout video_card_root_18;

    @BindView(R.id.video_card_root_19)
    RelativeLayout video_card_root_19;

    @BindView(R.id.video_card_root_20)
    RelativeLayout video_card_root_20;

    @BindView(R.id.video_card_root_21)
    RelativeLayout video_card_root_21;

    @BindView(R.id.video_card_root_22)
    RelativeLayout video_card_root_22;

    @BindView(R.id.video_card_root_23)
    RelativeLayout video_card_root_23;

    @BindView(R.id.video_card_root_24)
    RelativeLayout video_card_root_24;

    @BindView(R.id.video_type_1)
    TextView video_type_1;

    @BindView(R.id.video_type_2)
    TextView video_type_2;

    @BindView(R.id.video_type_3)
    TextView video_type_3;

    @BindView(R.id.video_type_4)
    TextView video_type_4;

    @BindView(R.id.video_type_5)
    TextView video_type_5;

    @BindView(R.id.video_type_6)
    TextView video_type_6;

    @BindView(R.id.video_root_ad)
    View video_root_ad;

    @BindView(R.id.video_iv_ad)
    ImageView video_iv_ad;

    @BindView(R.id.video_title_0)
    TextView video_title_0;

    @BindView(R.id.video_title_1)
    TextView video_title_1;

    @BindView(R.id.video_title_2)
    TextView video_title_2;

    @BindView(R.id.video_title_3)
    TextView video_title_3;

    @BindView(R.id.video_sub_title_root)
    View video_sub_title_root;

    @BindView(R.id.video_title_root_0)
    View video_title_root_0;

    @BindView(R.id.video_title_root_1)
    View video_title_root_1;

    @BindView(R.id.video_title_root_2)
    View video_title_root_2;

    @BindView(R.id.video_title_root_3)
    View video_title_root_3;

    @BindView(R.id.video_root_0)
    View video_root_0;

    @BindView(R.id.video_root_1)
    View video_root_1;

    @BindView(R.id.video_root_2)
    View video_root_2;

    @BindView(R.id.video_root_3)
    View video_root_3;

    private ArrayList<Video> video_list_0;
    private ArrayList<Video> video_list_1;
    private ArrayList<Video> video_list_2;
    private ArrayList<Video> video_list_3;

    private VideoConfig.Video1 video1;
    private VideoConfig.Video2[] video2s;

    public HomeVideoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_video, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setVideoCardFocusAnimator(video_card_root_1);
        setVideoCardFocusAnimator(video_card_root_2);
        setVideoCardFocusAnimator(video_card_root_3);
        setVideoCardFocusAnimator(video_card_root_4);
        setVideoCardFocusAnimator(video_card_root_5);
        setVideoCardFocusAnimator(video_card_root_6);
        setVideoCardFocusAnimator(video_card_root_7);
        setVideoCardFocusAnimator(video_card_root_8);
        setVideoCardFocusAnimator(video_card_root_9);
        setVideoCardFocusAnimator(video_card_root_10);
        setVideoCardFocusAnimator(video_card_root_11);
        setVideoCardFocusAnimator(video_card_root_12);
        setVideoCardFocusAnimator(video_card_root_13);
        setVideoCardFocusAnimator(video_card_root_14);
        setVideoCardFocusAnimator(video_card_root_15);
        setVideoCardFocusAnimator(video_card_root_16);
        setVideoCardFocusAnimator(video_card_root_17);
        setVideoCardFocusAnimator(video_card_root_18);
        setVideoCardFocusAnimator(video_card_root_19);
        setVideoCardFocusAnimator(video_card_root_20);
        setVideoCardFocusAnimator(video_card_root_21);
        setVideoCardFocusAnimator(video_card_root_22);
        setVideoCardFocusAnimator(video_card_root_23);
        setVideoCardFocusAnimator(video_card_root_24);

        setTypeFocusAnimator(video_type_1);
        setTypeFocusAnimator(video_type_2);
        setTypeFocusAnimator(video_type_3);
        setTypeFocusAnimator(video_type_4);
        setTypeFocusAnimator(video_type_5);
        setTypeFocusAnimator(video_type_6);

        if (getArguments() != null) {
            video1 = (VideoConfig.Video1) getArguments().getSerializable(VIDEO_PARAM);
            video2s = video1.getVideo2s();

            // 初始化大标题和视频分类标题
            if (video2s.length > 0) {
                video_title_0.setText(video2s[0].getTitle());
                video_root_0.setVisibility(View.VISIBLE);
                video_title_root_0.setVisibility(View.VISIBLE);
            } else {
                video_root_0.setVisibility(View.GONE);
                video_title_root_0.setVisibility(View.GONE);
            }
            if (video2s.length > 1) {
                video_title_1.setText(video2s[1].getTitle());
                video_root_1.setVisibility(View.VISIBLE);
                video_title_root_1.setVisibility(View.VISIBLE);
            } else {
                video_root_1.setVisibility(View.GONE);
                video_title_root_1.setVisibility(View.GONE);
            }
            if (video2s.length > 2) {
                video_title_2.setText(video2s[2].getTitle());
                video_root_2.setVisibility(View.VISIBLE);
                video_title_root_2.setVisibility(View.VISIBLE);
            } else {
                video_root_2.setVisibility(View.GONE);
                video_title_root_2.setVisibility(View.GONE);
            }
            if (video2s.length > 3) {
                video_title_3.setText(video2s[3].getTitle());
                video_root_3.setVisibility(View.VISIBLE);
                video_title_root_3.setVisibility(View.VISIBLE);
            } else {
                video_root_3.setVisibility(View.GONE);
                video_title_root_3.setVisibility(View.GONE);
            }

            if (video1.isHideSubTitle()) {
                video_sub_title_root.setVisibility(View.GONE);
            } else if (video2s.length > 0) {
                video_sub_title_root.setVisibility(View.VISIBLE);
                // 遍历video2s为视频分类赋值，如果遍历到底则重新遍历
                configVideoSubTitle(video_type_1, video2s.length >= 1 ? 0 : 0 % video2s.length);
                configVideoSubTitle(video_type_2, video2s.length >= 2 ? 1 : 1 % video2s.length);
                configVideoSubTitle(video_type_3, video2s.length >= 3 ? 2 : 2 % video2s.length);
                configVideoSubTitle(video_type_4, video2s.length >= 4 ? 3 : 3 % video2s.length);
                configVideoSubTitle(video_type_5, video2s.length >= 5 ? 4 : 4 % video2s.length);
                configVideoSubTitle(video_type_6, video2s.length >= 6 ? 5 : 5 % video2s.length);
            }
            if (video1.isHideAD()) {
                video_root_ad.setVisibility(View.GONE);
            } else {
                video_root_ad.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        loadData();
    }

    @Override
    protected View getAdRoot() {
        return video_root_ad;
    }

    @Override
    protected ImageView getAdImage() {
        return video_iv_ad;
    }

    private void configVideoSubTitle(TextView textView, int index) {
        VideoConfig.Video2 video2 = video2s[index];
        textView.setText(video2.getSubTitle());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, VideoListActivity.class);
                startActivity(intent);

                postListEvent(index);
            }
        });
    }

    private void postListEvent(int focusIndex) {
        ArrayList<VideoItem> itemList = new ArrayList<>();
        for (int i = 0; i < video2s.length; i++) {
            itemList.add(new VideoItem(i, video2s[i].getUrl(), video2s[i].getTitle()));
        }

        EventBus.getDefault().postSticky(new VideoListEvent(video1.getTitle(), itemList, focusIndex, video1, video2s));
    }

    @OnClick(R.id.video_card_root_1)
    public void onVideoCardRoot1Click() {
        if (video_list_0.size() > 0) startVideoDetailActivity(video_list_0.get(0));
    }

    @OnClick(R.id.video_card_root_2)
    public void onVideoCardRoot2Click() {
        if (video_list_0.size() > 1) startVideoDetailActivity(video_list_0.get(1));
    }

    @OnClick(R.id.video_card_root_3)
    public void onVideoCardRoot3Click() {
        if (video_list_0.size() > 2) startVideoDetailActivity(video_list_0.get(2));
    }

    @OnClick(R.id.video_card_root_4)
    public void onVideoCardRoot4Click() {
        if (video_list_0.size() > 3) startVideoDetailActivity(video_list_0.get(3));
    }

    @OnClick(R.id.video_card_root_5)
    public void onVideoCardRoot5Click() {
        if (video_list_0.size() > 4) startVideoDetailActivity(video_list_0.get(4));
    }

    @OnClick(R.id.video_card_root_6)
    public void onVideoCardRoot6Click() {
        if (video_list_0.size() > 5) startVideoDetailActivity(video_list_0.get(5));
    }

    @OnClick(R.id.video_card_root_7)
    public void onVideoCardRoot7Click() {
        if (video_list_1.size() > 0) startVideoDetailActivity(video_list_1.get(0));
    }

    @OnClick(R.id.video_card_root_8)
    public void onVideoCardRoot8Click() {
        if (video_list_1.size() > 1) startVideoDetailActivity(video_list_1.get(1));
    }

    @OnClick(R.id.video_card_root_9)
    public void onVideoCardRoot9Click() {
        if (video_list_1.size() > 2) startVideoDetailActivity(video_list_1.get(2));
    }

    @OnClick(R.id.video_card_root_10)
    public void onVideoCardRoot10Click() {
        if (video_list_1.size() > 3) startVideoDetailActivity(video_list_1.get(3));
    }

    @OnClick(R.id.video_card_root_11)
    public void onVideoCardRoot11Click() {
        if (video_list_1.size() > 4) startVideoDetailActivity(video_list_1.get(4));
    }

    @OnClick(R.id.video_card_root_12)
    public void onVideoCardRoot12Click() {
        if (video_list_1.size() > 5) startVideoDetailActivity(video_list_1.get(5));
    }

    @OnClick(R.id.video_card_root_13)
    public void onVideoCardRoot13Click() {
        if (video_list_2.size() > 0) startVideoDetailActivity(video_list_2.get(0));
    }

    @OnClick(R.id.video_card_root_14)
    public void onVideoCardRoot14Click() {
        if (video_list_2.size() > 1) startVideoDetailActivity(video_list_2.get(1));
    }

    @OnClick(R.id.video_card_root_15)
    public void onVideoCardRoot15Click() {
        if (video_list_2.size() > 2) startVideoDetailActivity(video_list_2.get(2));
    }

    @OnClick(R.id.video_card_root_16)
    public void onVideoCardRoot16Click() {
        if (video_list_2.size() > 3) startVideoDetailActivity(video_list_2.get(3));
    }

    @OnClick(R.id.video_card_root_17)
    public void onVideoCardRoot17Click() {
        if (video_list_2.size() > 4) startVideoDetailActivity(video_list_2.get(4));
    }

    @OnClick(R.id.video_card_root_18)
    public void onVideoCardRoot18Click() {
        if (video_list_2.size() > 5) startVideoDetailActivity(video_list_2.get(5));
    }

    @OnClick(R.id.video_card_root_19)
    public void onVideoCardRoot19Click() {
        if (video_list_3.size() > 0) startVideoDetailActivity(video_list_3.get(0));
    }

    @OnClick(R.id.video_card_root_20)
    public void onVideoCardRoot20Click() {
        if (video_list_3.size() > 1) startVideoDetailActivity(video_list_3.get(1));
    }

    @OnClick(R.id.video_card_root_21)
    public void onVideoCardRoot21Click() {
        if (video_list_3.size() > 2) startVideoDetailActivity(video_list_3.get(2));
    }

    @OnClick(R.id.video_card_root_22)
    public void onVideoCardRoot22Click() {
        if (video_list_3.size() > 3) startVideoDetailActivity(video_list_3.get(3));
    }

    @OnClick(R.id.video_card_root_23)
    public void onVideoCardRoot23Click() {
        if (video_list_3.size() > 4) startVideoDetailActivity(video_list_3.get(4));
    }

    @OnClick(R.id.video_card_root_24)
    public void onVideoCardRoot24Click() {
        if (video_list_3.size() > 5) startVideoDetailActivity(video_list_3.get(5));
    }

    private void startVideoDetailActivity(Video video) {
        Intent intent = new Intent(mActivity, VideoDetailActivity.class);
        startActivity(intent);

        EventBus.getDefault().postSticky(new VideoDetailEvent(video));
    }

    private void loadData() {
        if (video2s.length > 0) {
            DisposableObserver<ArrayList<Video>> newObserver = new DisposableObserver<ArrayList<Video>>() {
                @Override
                public void onNext(ArrayList<Video> videos) {
                    video_list_0 = videos;
                    if (videos.size() > 0) {
                        setCardData(video_card_root_1, video_list_0.get(0));
                    }
                    if (videos.size() > 1) {
                        setCardData(video_card_root_2, video_list_0.get(1));
                    }
                    if (videos.size() > 2) {
                        setCardData(video_card_root_3, video_list_0.get(2));
                    }
                    if (videos.size() > 3) {
                        setCardData(video_card_root_4, video_list_0.get(3));
                    }
                    if (videos.size() > 4) {
                        setCardData(video_card_root_5, video_list_0.get(4));
                    }
                    if (videos.size() > 5) {
                        setCardData(video_card_root_6, video_list_0.get(5));
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
                    .create((ObservableOnSubscribe<ArrayList<Video>>) emitter -> {
                        ArrayList<Video> videos = Model.getVideoEngine(mActivity).getVideos(
                                new VideoEngineParam(video1.getTitle(), video2s[0].getTitle(), video2s[0].getUrl()), 1);
                        emitter.onNext(videos);
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newObserver);

            mDisposable.add(newObserver);
        }

        if (video2s.length > 1) {
            DisposableObserver<ArrayList<Video>> chinaObserver = new DisposableObserver<ArrayList<Video>>() {
                @Override
                public void onNext(ArrayList<Video> videos) {
                    video_list_1 = videos;
                    if (videos.size() > 0) {
                        setCardData(video_card_root_7, video_list_1.get(0));
                    }
                    if (videos.size() > 1) {
                        setCardData(video_card_root_8, video_list_1.get(1));
                    }
                    if (videos.size() > 2) {
                        setCardData(video_card_root_9, video_list_1.get(2));
                    }
                    if (videos.size() > 3) {
                        setCardData(video_card_root_10, video_list_1.get(3));
                    }
                    if (videos.size() > 4) {
                        setCardData(video_card_root_11, video_list_1.get(4));
                    }
                    if (videos.size() > 5) {
                        setCardData(video_card_root_12, video_list_1.get(5));
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
                    .create((ObservableOnSubscribe<ArrayList<Video>>) emitter -> {
                        ArrayList<Video> videos = Model.getVideoEngine(mActivity).getVideos(
                                new VideoEngineParam(video1.getTitle(), video2s[1].getTitle(), video2s[1].getUrl()), 1);
                        emitter.onNext(videos);
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(chinaObserver);

            mDisposable.add(chinaObserver);
        }

        if (video2s.length > 2) {
            DisposableObserver<ArrayList<Video>> jkObserver = new DisposableObserver<ArrayList<Video>>() {
                @Override
                public void onNext(ArrayList<Video> videos) {
                    video_list_2 = videos;
                    if (videos.size() > 0) {
                        setCardData(video_card_root_13, video_list_2.get(0));
                    }
                    if (videos.size() > 1) {
                        setCardData(video_card_root_14, video_list_2.get(1));
                    }
                    if (videos.size() > 2) {
                        setCardData(video_card_root_15, video_list_2.get(2));
                    }
                    if (videos.size() > 3) {
                        setCardData(video_card_root_16, video_list_2.get(3));
                    }
                    if (videos.size() > 4) {
                        setCardData(video_card_root_17, video_list_2.get(4));
                    }
                    if (videos.size() > 5) {
                        setCardData(video_card_root_18, video_list_2.get(5));
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
                    .create((ObservableOnSubscribe<ArrayList<Video>>) emitter -> {
                        ArrayList<Video> videos = Model.getVideoEngine(mActivity).getVideos(
                                new VideoEngineParam(video1.getTitle(), video2s[2].getTitle(), video2s[2].getUrl()), 1);
                        emitter.onNext(videos);
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(jkObserver);

            mDisposable.add(jkObserver);
        }

        if (video2s.length > 3) {
            DisposableObserver<ArrayList<Video>> eaObserver = new DisposableObserver<ArrayList<Video>>() {
                @Override
                public void onNext(ArrayList<Video> videos) {
                    video_list_3 = videos;
                    if (videos.size() > 0) {
                        setCardData(video_card_root_19, video_list_3.get(0));
                    }
                    if (videos.size() > 1) {
                        setCardData(video_card_root_20, video_list_3.get(1));
                    }
                    if (videos.size() > 2) {
                        setCardData(video_card_root_21, video_list_3.get(2));
                    }
                    if (videos.size() > 3) {
                        setCardData(video_card_root_22, video_list_3.get(3));
                    }
                    if (videos.size() > 4) {
                        setCardData(video_card_root_23, video_list_3.get(4));
                    }
                    if (videos.size() > 5) {
                        setCardData(video_card_root_24, video_list_3.get(5));
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
                    .create((ObservableOnSubscribe<ArrayList<Video>>) emitter -> {
                        ArrayList<Video> videos = Model.getVideoEngine(mActivity).getVideos(
                                new VideoEngineParam(video1.getTitle(), video2s[3].getTitle(), video2s[3].getUrl()), 1);
                        emitter.onNext(videos);
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(eaObserver);

            mDisposable.add(eaObserver);
        }
    }
}
