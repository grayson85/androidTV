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

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class HomeVideoFragment extends BaseVideoFragment {

    public static final String VIDEO_PARAM = "video_param";

    RelativeLayout video_card_root_1;

    RelativeLayout video_card_root_2;

    RelativeLayout video_card_root_3;

    RelativeLayout video_card_root_4;

    RelativeLayout video_card_root_5;

    RelativeLayout video_card_root_6;

    RelativeLayout video_card_root_7;

    RelativeLayout video_card_root_8;

    RelativeLayout video_card_root_9;

    RelativeLayout video_card_root_10;

    RelativeLayout video_card_root_11;

    RelativeLayout video_card_root_12;

    RelativeLayout video_card_root_13;

    RelativeLayout video_card_root_14;

    RelativeLayout video_card_root_15;

    RelativeLayout video_card_root_16;

    RelativeLayout video_card_root_17;

    RelativeLayout video_card_root_18;

    RelativeLayout video_card_root_19;

    RelativeLayout video_card_root_20;

    RelativeLayout video_card_root_21;

    RelativeLayout video_card_root_22;

    RelativeLayout video_card_root_23;

    RelativeLayout video_card_root_24;

    RelativeLayout video_card_root_25;

    RelativeLayout video_card_root_26;

    RelativeLayout video_card_root_27;

    RelativeLayout video_card_root_28;

    RelativeLayout video_card_root_29;

    RelativeLayout video_card_root_30;

    TextView video_type_1;

    TextView video_type_2;

    TextView video_type_3;

    TextView video_type_4;

    TextView video_type_5;

    TextView video_type_6;

    TextView video_type_7;

    TextView video_type_8;

    View video_root_ad;

    ImageView video_iv_ad;

    TextView video_title_0;

    TextView video_title_1;

    TextView video_title_2;

    TextView video_title_3;

    TextView video_title_4;

    View video_sub_title_root;

    View video_title_root_0;

    View video_title_root_1;

    View video_title_root_2;

    View video_title_root_3;

    View video_title_root_4;

    View video_root_0;

    View video_root_1;

    View video_root_2;

    View video_root_3;

    View video_root_4;

    private ArrayList<Video> video_list_0;
    private ArrayList<Video> video_list_1;
    private ArrayList<Video> video_list_2;
    private ArrayList<Video> video_list_3;
    private ArrayList<Video> video_list_4;

    private VideoConfig.Video1 video1;
    private VideoConfig.Video2[] video2s;

    public HomeVideoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_video, container, false);
        video_card_root_1 = view.findViewById(R.id.video_card_root_1);
        video_card_root_2 = view.findViewById(R.id.video_card_root_2);
        video_card_root_3 = view.findViewById(R.id.video_card_root_3);
        video_card_root_4 = view.findViewById(R.id.video_card_root_4);
        video_card_root_5 = view.findViewById(R.id.video_card_root_5);
        video_card_root_6 = view.findViewById(R.id.video_card_root_6);
        video_card_root_7 = view.findViewById(R.id.video_card_root_7);
        video_card_root_8 = view.findViewById(R.id.video_card_root_8);
        video_card_root_9 = view.findViewById(R.id.video_card_root_9);
        video_card_root_10 = view.findViewById(R.id.video_card_root_10);
        video_card_root_11 = view.findViewById(R.id.video_card_root_11);
        video_card_root_12 = view.findViewById(R.id.video_card_root_12);
        video_card_root_13 = view.findViewById(R.id.video_card_root_13);
        video_card_root_14 = view.findViewById(R.id.video_card_root_14);
        video_card_root_15 = view.findViewById(R.id.video_card_root_15);
        video_card_root_16 = view.findViewById(R.id.video_card_root_16);
        video_card_root_17 = view.findViewById(R.id.video_card_root_17);
        video_card_root_18 = view.findViewById(R.id.video_card_root_18);
        video_card_root_19 = view.findViewById(R.id.video_card_root_19);
        video_card_root_20 = view.findViewById(R.id.video_card_root_20);
        video_card_root_21 = view.findViewById(R.id.video_card_root_21);
        video_card_root_22 = view.findViewById(R.id.video_card_root_22);
        video_card_root_23 = view.findViewById(R.id.video_card_root_23);
        video_card_root_24 = view.findViewById(R.id.video_card_root_24);
        video_card_root_25 = view.findViewById(R.id.video_card_root_25);
        video_card_root_26 = view.findViewById(R.id.video_card_root_26);
        video_card_root_27 = view.findViewById(R.id.video_card_root_27);
        video_card_root_28 = view.findViewById(R.id.video_card_root_28);
        video_card_root_29 = view.findViewById(R.id.video_card_root_29);
        video_card_root_30 = view.findViewById(R.id.video_card_root_30);
        video_type_1 = view.findViewById(R.id.video_type_1);
        video_type_2 = view.findViewById(R.id.video_type_2);
        video_type_3 = view.findViewById(R.id.video_type_3);
        video_type_4 = view.findViewById(R.id.video_type_4);
        video_type_5 = view.findViewById(R.id.video_type_5);
        video_type_6 = view.findViewById(R.id.video_type_6);
        video_type_7 = view.findViewById(R.id.video_type_7);
        video_type_8 = view.findViewById(R.id.video_type_8);
        video_root_ad = view.findViewById(R.id.video_root_ad);
        video_iv_ad = view.findViewById(R.id.video_iv_ad);
        video_title_0 = view.findViewById(R.id.video_title_0);
        video_title_1 = view.findViewById(R.id.video_title_1);
        video_title_2 = view.findViewById(R.id.video_title_2);
        video_title_3 = view.findViewById(R.id.video_title_3);
        video_title_4 = view.findViewById(R.id.video_title_4);
        video_sub_title_root = view.findViewById(R.id.video_sub_title_root);
        video_title_root_0 = view.findViewById(R.id.video_title_root_0);
        video_title_root_1 = view.findViewById(R.id.video_title_root_1);
        video_title_root_2 = view.findViewById(R.id.video_title_root_2);
        video_title_root_3 = view.findViewById(R.id.video_title_root_3);
        video_title_root_4 = view.findViewById(R.id.video_title_root_4);
        video_root_0 = view.findViewById(R.id.video_root_0);
        video_root_1 = view.findViewById(R.id.video_root_1);
        video_root_2 = view.findViewById(R.id.video_root_2);
        video_root_3 = view.findViewById(R.id.video_root_3);
        video_root_4 = view.findViewById(R.id.video_root_4);

        video_card_root_1.setOnClickListener(v -> onVideoCardRoot1Click());
        video_card_root_2.setOnClickListener(v -> onVideoCardRoot2Click());
        video_card_root_3.setOnClickListener(v -> onVideoCardRoot3Click());
        video_card_root_4.setOnClickListener(v -> onVideoCardRoot4Click());
        video_card_root_5.setOnClickListener(v -> onVideoCardRoot5Click());
        video_card_root_6.setOnClickListener(v -> onVideoCardRoot6Click());
        video_card_root_7.setOnClickListener(v -> onVideoCardRoot7Click());
        video_card_root_8.setOnClickListener(v -> onVideoCardRoot8Click());
        video_card_root_9.setOnClickListener(v -> onVideoCardRoot9Click());
        video_card_root_10.setOnClickListener(v -> onVideoCardRoot10Click());
        video_card_root_11.setOnClickListener(v -> onVideoCardRoot11Click());
        video_card_root_12.setOnClickListener(v -> onVideoCardRoot12Click());
        video_card_root_13.setOnClickListener(v -> onVideoCardRoot13Click());
        video_card_root_14.setOnClickListener(v -> onVideoCardRoot14Click());
        video_card_root_15.setOnClickListener(v -> onVideoCardRoot15Click());
        video_card_root_16.setOnClickListener(v -> onVideoCardRoot16Click());
        video_card_root_17.setOnClickListener(v -> onVideoCardRoot17Click());
        video_card_root_18.setOnClickListener(v -> onVideoCardRoot18Click());
        video_card_root_19.setOnClickListener(v -> onVideoCardRoot19Click());
        video_card_root_20.setOnClickListener(v -> onVideoCardRoot20Click());
        video_card_root_21.setOnClickListener(v -> onVideoCardRoot21Click());
        video_card_root_22.setOnClickListener(v -> onVideoCardRoot22Click());
        video_card_root_23.setOnClickListener(v -> onVideoCardRoot23Click());
        video_card_root_24.setOnClickListener(v -> onVideoCardRoot24Click());
        video_card_root_25.setOnClickListener(v -> onVideoCardRoot25Click());
        video_card_root_26.setOnClickListener(v -> onVideoCardRoot26Click());
        video_card_root_27.setOnClickListener(v -> onVideoCardRoot27Click());
        video_card_root_28.setOnClickListener(v -> onVideoCardRoot28Click());
        video_card_root_29.setOnClickListener(v -> onVideoCardRoot29Click());
        video_card_root_30.setOnClickListener(v -> onVideoCardRoot30Click());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup left focus navigation for leftmost cards in each row
        setupLeftFocusNavigation(
                video_card_root_1, // First card in row 1
                video_card_root_7, // First card in row 2
                video_card_root_13, // First card in row 3
                video_card_root_19 // First card in row 4
        );

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
        setVideoCardFocusAnimator(video_card_root_25);
        setVideoCardFocusAnimator(video_card_root_26);
        setVideoCardFocusAnimator(video_card_root_27);
        setVideoCardFocusAnimator(video_card_root_28);
        setVideoCardFocusAnimator(video_card_root_29);
        setVideoCardFocusAnimator(video_card_root_30);

        setTypeFocusAnimator(video_type_1);
        setTypeFocusAnimator(video_type_2);
        setTypeFocusAnimator(video_type_3);
        setTypeFocusAnimator(video_type_4);
        setTypeFocusAnimator(video_type_5);
        setTypeFocusAnimator(video_type_6);
        setTypeFocusAnimator(video_type_7);
        setTypeFocusAnimator(video_type_8);

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
            if (video2s.length > 4) {
                video_title_4.setText(video2s[4].getTitle());
                video_root_4.setVisibility(View.VISIBLE);
                video_title_root_4.setVisibility(View.VISIBLE);
            } else {
                video_root_4.setVisibility(View.GONE);
                video_title_root_4.setVisibility(View.GONE);
            }

            if (video1.isHideSubTitle()) {
                video_sub_title_root.setVisibility(View.GONE);
            } else if (video2s.length > 0) {
                video_sub_title_root.setVisibility(View.VISIBLE);
                // Show buttons only if there are corresponding Video2 items, hide parent
                // container otherwise
                if (video2s.length >= 1) {
                    configVideoSubTitle(video_type_1, 0);
                    ((View) video_type_1.getParent()).setVisibility(View.VISIBLE);
                } else {
                    ((View) video_type_1.getParent()).setVisibility(View.GONE);
                }
                if (video2s.length >= 2) {
                    configVideoSubTitle(video_type_2, 1);
                    ((View) video_type_2.getParent()).setVisibility(View.VISIBLE);
                } else {
                    ((View) video_type_2.getParent()).setVisibility(View.GONE);
                }
                if (video2s.length >= 3) {
                    configVideoSubTitle(video_type_3, 2);
                    ((View) video_type_3.getParent()).setVisibility(View.VISIBLE);
                } else {
                    ((View) video_type_3.getParent()).setVisibility(View.GONE);
                }
                if (video2s.length >= 4) {
                    configVideoSubTitle(video_type_4, 3);
                    ((View) video_type_4.getParent()).setVisibility(View.VISIBLE);
                } else {
                    ((View) video_type_4.getParent()).setVisibility(View.GONE);
                }
                if (video2s.length >= 5) {
                    configVideoSubTitle(video_type_5, 4);
                    ((View) video_type_5.getParent()).setVisibility(View.VISIBLE);
                } else {
                    ((View) video_type_5.getParent()).setVisibility(View.GONE);
                }
                if (video2s.length >= 6) {
                    configVideoSubTitle(video_type_6, 5);
                    ((View) video_type_6.getParent()).setVisibility(View.VISIBLE);
                } else {
                    ((View) video_type_6.getParent()).setVisibility(View.GONE);
                }
                if (video2s.length >= 7) {
                    configVideoSubTitle(video_type_7, 6);
                    ((View) video_type_7.getParent()).setVisibility(View.VISIBLE);
                } else {
                    ((View) video_type_7.getParent()).setVisibility(View.GONE);
                }
                if (video2s.length >= 8) {
                    configVideoSubTitle(video_type_8, 7);
                    ((View) video_type_8.getParent()).setVisibility(View.VISIBLE);
                } else {
                    ((View) video_type_8.getParent()).setVisibility(View.GONE);
                }
            }
            if (video1.isHideAD()) {
                video_root_ad.setVisibility(View.GONE);
            } else {
                video_root_ad.setVisibility(View.VISIBLE);
            }
        }
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
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

    public void onVideoCardRoot1Click() {
        if (video_list_0.size() > 0)
            startVideoDetailActivity(video_list_0.get(0));
    }

    public void onVideoCardRoot2Click() {
        if (video_list_0.size() > 1)
            startVideoDetailActivity(video_list_0.get(1));
    }

    public void onVideoCardRoot3Click() {
        if (video_list_0.size() > 2)
            startVideoDetailActivity(video_list_0.get(2));
    }

    public void onVideoCardRoot4Click() {
        if (video_list_0.size() > 3)
            startVideoDetailActivity(video_list_0.get(3));
    }

    public void onVideoCardRoot5Click() {
        if (video_list_0.size() > 4)
            startVideoDetailActivity(video_list_0.get(4));
    }

    public void onVideoCardRoot6Click() {
        if (video_list_0.size() > 5)
            startVideoDetailActivity(video_list_0.get(5));
    }

    public void onVideoCardRoot7Click() {
        if (video_list_1.size() > 0)
            startVideoDetailActivity(video_list_1.get(0));
    }

    public void onVideoCardRoot8Click() {
        if (video_list_1.size() > 1)
            startVideoDetailActivity(video_list_1.get(1));
    }

    public void onVideoCardRoot9Click() {
        if (video_list_1.size() > 2)
            startVideoDetailActivity(video_list_1.get(2));
    }

    public void onVideoCardRoot10Click() {
        if (video_list_1.size() > 3)
            startVideoDetailActivity(video_list_1.get(3));
    }

    public void onVideoCardRoot11Click() {
        if (video_list_1.size() > 4)
            startVideoDetailActivity(video_list_1.get(4));
    }

    public void onVideoCardRoot12Click() {
        if (video_list_1.size() > 5)
            startVideoDetailActivity(video_list_1.get(5));
    }

    public void onVideoCardRoot13Click() {
        if (video_list_2.size() > 0)
            startVideoDetailActivity(video_list_2.get(0));
    }

    public void onVideoCardRoot14Click() {
        if (video_list_2.size() > 1)
            startVideoDetailActivity(video_list_2.get(1));
    }

    public void onVideoCardRoot15Click() {
        if (video_list_2.size() > 2)
            startVideoDetailActivity(video_list_2.get(2));
    }

    public void onVideoCardRoot16Click() {
        if (video_list_2.size() > 3)
            startVideoDetailActivity(video_list_2.get(3));
    }

    public void onVideoCardRoot17Click() {
        if (video_list_2.size() > 4)
            startVideoDetailActivity(video_list_2.get(4));
    }

    public void onVideoCardRoot18Click() {
        if (video_list_2.size() > 5)
            startVideoDetailActivity(video_list_2.get(5));
    }

    public void onVideoCardRoot19Click() {
        if (video_list_3.size() > 0)
            startVideoDetailActivity(video_list_3.get(0));
    }

    public void onVideoCardRoot20Click() {
        if (video_list_3.size() > 1)
            startVideoDetailActivity(video_list_3.get(1));
    }

    public void onVideoCardRoot21Click() {
        if (video_list_3.size() > 2)
            startVideoDetailActivity(video_list_3.get(2));
    }

    public void onVideoCardRoot22Click() {
        if (video_list_3.size() > 3)
            startVideoDetailActivity(video_list_3.get(3));
    }

    public void onVideoCardRoot23Click() {
        if (video_list_3.size() > 4)
            startVideoDetailActivity(video_list_3.get(4));
    }

    public void onVideoCardRoot24Click() {
        if (video_list_3.size() > 5)
            startVideoDetailActivity(video_list_3.get(5));
    }

    public void onVideoCardRoot25Click() {
        if (video_list_4.size() > 0)
            startVideoDetailActivity(video_list_4.get(0));
    }

    public void onVideoCardRoot26Click() {
        if (video_list_4.size() > 1)
            startVideoDetailActivity(video_list_4.get(1));
    }

    public void onVideoCardRoot27Click() {
        if (video_list_4.size() > 2)
            startVideoDetailActivity(video_list_4.get(2));
    }

    public void onVideoCardRoot28Click() {
        if (video_list_4.size() > 3)
            startVideoDetailActivity(video_list_4.get(3));
    }

    public void onVideoCardRoot29Click() {
        if (video_list_4.size() > 4)
            startVideoDetailActivity(video_list_4.get(4));
    }

    public void onVideoCardRoot30Click() {
        if (video_list_4.size() > 5)
            startVideoDetailActivity(video_list_4.get(5));
    }

    private void startVideoDetailActivity(Video video) {
        // Post sticky event BEFORE starting activity so it's available when activity
        // registers
        EventBus.getDefault().postSticky(new VideoDetailEvent(video));

        Intent intent = new Intent(mActivity, VideoDetailActivity.class);
        startActivity(intent);
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
                    // Debug and force layout update
                    if (getView() != null) {
                        android.util.Log.e("HOME_SCROLL",
                                "getView() parent class: "
                                        + (getView().getParent() != null ? getView().getParent().getClass().getName()
                                                : "null"));
                        getView().postDelayed(() -> {
                            getView().requestLayout();
                            if (getView().getParent() instanceof View) {
                                View parent = (View) getView().getParent();
                                parent.requestLayout();
                                // Try to scroll down slightly
                                if (parent.canScrollVertically(1)) {
                                    parent.scrollBy(0, 100);
                                    android.util.Log.e("HOME_SCROLL", "Scrolled parent by 100px");
                                }
                            }
                        }, 200);
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
                    // Scroll by 1px to force scroll range update
                    if (getView() != null && getView().getParent() instanceof View) {
                        View parent = (View) getView().getParent();
                        parent.postDelayed(() -> {
                            parent.scrollBy(0, 1);
                            parent.postDelayed(() -> parent.scrollBy(0, -1), 50);
                        }, 100);
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
                    // Scroll by 1px to force scroll range update
                    if (getView() != null && getView().getParent() instanceof View) {
                        View parent = (View) getView().getParent();
                        parent.postDelayed(() -> {
                            parent.scrollBy(0, 1);
                            parent.postDelayed(() -> parent.scrollBy(0, -1), 50);
                        }, 100);
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
                    // Scroll by 1px to force scroll range update
                    if (getView() != null && getView().getParent() instanceof View) {
                        View parent = (View) getView().getParent();
                        parent.postDelayed(() -> {
                            parent.scrollBy(0, 1);
                            parent.postDelayed(() -> parent.scrollBy(0, -1), 50);
                        }, 100);
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

        if (video2s.length > 4) {
            DisposableObserver<ArrayList<Video>> row5Observer = new DisposableObserver<ArrayList<Video>>() {
                @Override
                public void onNext(ArrayList<Video> videos) {
                    video_list_4 = videos;
                    if (videos.size() > 0) {
                        setCardData(video_card_root_25, video_list_4.get(0));
                    }
                    if (videos.size() > 1) {
                        setCardData(video_card_root_26, video_list_4.get(1));
                    }
                    if (videos.size() > 2) {
                        setCardData(video_card_root_27, video_list_4.get(2));
                    }
                    if (videos.size() > 3) {
                        setCardData(video_card_root_28, video_list_4.get(3));
                    }
                    if (videos.size() > 4) {
                        setCardData(video_card_root_29, video_list_4.get(4));
                    }
                    if (videos.size() > 5) {
                        setCardData(video_card_root_30, video_list_4.get(5));
                    }
                    // Scroll by 1px to force scroll range update
                    if (getView() != null && getView().getParent() instanceof View) {
                        View parent = (View) getView().getParent();
                        parent.postDelayed(() -> {
                            parent.scrollBy(0, 1);
                            parent.postDelayed(() -> parent.scrollBy(0, -1), 50);
                        }, 100);
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
                                new VideoEngineParam(video1.getTitle(), video2s[4].getTitle(), video2s[4].getUrl()), 1);
                        emitter.onNext(videos);
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(row5Observer);

            mDisposable.add(row5Observer);
        }
    }
}
