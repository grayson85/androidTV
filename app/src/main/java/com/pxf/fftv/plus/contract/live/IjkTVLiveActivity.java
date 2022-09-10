package com.pxf.fftv.plus.contract.live;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.pxf.fftv.plus.bean.TvLiveBean;
import com.pxf.fftv.plus.common.CommonUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @BindView(R.id.surface_play)
    SurfaceView surface_play;

    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.tv_live_menu)
    View tv_live_menu;

    @BindView(R.id.tv_live_recycler_view_title)
    RecyclerView tv_live_recycler_view_title;

    @BindView(R.id.tv_live_recycler_view_sub_title)
    RecyclerView tv_live_recycler_view_sub_title;

    private SurfaceHolder mHolder;

    private TvLiveBean mData;

    private ArrayList<String> mTitleList = new ArrayList<>();
    private ArrayList<ArrayList<String>> mSubTitleList = new ArrayList<>();

    private TitleAdapter mTitleAdapter;
    private TitleAdapter mSubTitleAdapter;

    private int currentTitlePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_tv_live);
        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

        initView();

        surface_play.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mHolder = holder;
                getLiveListAndPlay();
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
    }

    private void initView() {
        tv_live_recycler_view_title.setLayoutManager(new LinearLayoutManager(this));
        tv_live_recycler_view_sub_title.setLayoutManager(new LinearLayoutManager(this));
        tv_live_menu.setVisibility(View.GONE);
    }

    private void getLiveListAndPlay() {
        Observable
                .create(new ObservableOnSubscribe<TvLiveBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<TvLiveBean> emitter) throws Exception {
                        Request request = new Request.Builder()
                                .url("http://yunshi.meetpt.cn/tv.json")
                                .build();

                        Response response = CommonUtils.getOkHttpClient().newCall(request).execute();

                        if (response.isSuccessful() && response.body() != null) {
                            TvLiveBean bean = CommonUtils.getGson().fromJson(response.body().string(), TvLiveBean.class);
                            emitter.onNext(bean);
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TvLiveBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TvLiveBean tvLiveBean) {
                        mData = tvLiveBean;

                        if (mData.getData().size() > 0) {
                            if (mData.getData().get(0).getSub().size() > 0) {
                                play(mData.getData().get(0).getSub().get(0).getUrl());
                            }
                        }
                        currentTitlePosition = 0;
                        mTitleList = new ArrayList<>();
                        mSubTitleList = new ArrayList<>();

                        for (TvLiveBean.DataBean dataBean : mData.getData()) {
                            mTitleList.add(dataBean.getTitle());
                            ArrayList<String> subTitleList = new ArrayList<>();
                            for (TvLiveBean.DataBean.SubBean subBean : dataBean.getSub()) {
                                subTitleList.add(subBean.getTitle());
                            }
                            mSubTitleList.add(subTitleList);
                        }
                        mTitleAdapter = new TitleAdapter(IjkTVLiveActivity.this, mTitleList, TitleAdapter.TYPE.TITLE, IjkTVLiveActivity.this);
                        tv_live_recycler_view_title.setAdapter(mTitleAdapter);

                        if (mSubTitleList.size() > 0) {
                            mSubTitleAdapter = new TitleAdapter(IjkTVLiveActivity.this, mSubTitleList.get(0), TitleAdapter.TYPE.SUB_TITLE, IjkTVLiveActivity.this);
                            tv_live_recycler_view_sub_title.setAdapter(mSubTitleAdapter);
                        }
                        if (mTitleList.size() > 0) {
                            tv_live_menu.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void play(String url) {
        if (mHolder == null) {
            return;
        }
        mPlayer = new IjkMediaPlayer();
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "rtmp,crypto,file,http,https,tcp,tls,udp");
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 10 * 1024 * 1024);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 20);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        try {
            mPlayer.setDisplay(mHolder);
            mPlayer.setDataSource(url);
            // mPlayer.setDataSource("http://39.135.34.151:18890/000000001000/1000000001000021973/1.m3u8?channel-id=ystenlive&Contentid=1000000001000021973&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=4ef1f6fdd53988bdf19472c73151206f21vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000021973&owchid=ystenlive&owsid=1106497909461769539&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRLaQJeR5usCQMKdpIDCZAoYPt4bOuuiUwGxs8%2fKxpb7Wa3xqB26AcGEvjhx3JJlw6");
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

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.setDisplay(null);
            mPlayer.release();
            mPlayer = null;
        }
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(IjkTVLiveActivity.this);
                    builder.setMessage("退出电视直播？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create().show();
                }
                return true;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return true;
        }
        tv_live_menu.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
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
        mSubTitleAdapter.refresh(mSubTitleList.get(position));
        currentTitlePosition = position;
    }

    @Override
    public void onSubTitleClick(int position) {
        String url = mData.getData().get(currentTitlePosition).getSub().get(position).getUrl();

        tv_live_menu.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        releasePlayer();
        play(url);
    }
}
