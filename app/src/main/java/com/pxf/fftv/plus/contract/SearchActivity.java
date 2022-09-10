package com.pxf.fftv.plus.contract;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.contract.detail.VideoDetailActivity;
import com.pxf.fftv.plus.contract.detail.VideoDetailEvent;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.video.Video;

import org.greenrobot.eventbus.EventBus;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.pxf.fftv.plus.Const.ANIMATION_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;

public class SearchActivity extends Activity implements SearchAdapter.OnClickListener {

    @BindView(R.id.search_iv_search)
    ImageView search_iv_search;

    @BindView(R.id.search_et_input)
    EditText search_et_input;

    @BindView(R.id.search_recycler_view_result)
    RecyclerView search_recycler_view_result;

    @BindView(R.id.search_tv_no_result)
    TextView search_tv_no_result;

    private ArrayList<Video> mVideoList;
    private SearchAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        search_iv_search.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                search_iv_search.setImageResource(R.drawable.search_ic_search_focused);
                ValueAnimator animator = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_OUT_SCALE).setDuration(ANIMATION_DURATION);

                animator.addUpdateListener(animation -> {
                    if (v.isFocused()) {
                        v.setScaleX((float) animation.getAnimatedValue());
                        v.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animator.cancel();
                    }
                });

                animator.setInterpolator(new OvershootInterpolator());
                animator.start();
            } else {
                search_iv_search.setImageResource(R.drawable.search_ic_search_default);
                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    v.setScaleX((float) animation.getAnimatedValue());
                    v.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });


        search_et_input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(search_et_input, InputMethodManager.SHOW_FORCED);
            }
        });
        search_et_input.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_et_input.getWindowToken(), 0);
                onSearchClick();
                return true;
            }
            return false;
        });

        search_recycler_view_result.setLayoutManager(new LinearLayoutManager(this));
    }


    @OnClick(R.id.search_iv_search)
    public void onSearchClick() {
        final String searchText = search_et_input.getText().toString().trim();
        if (searchText.isEmpty()) {
            return;
        }

        Observable
                .create(new ObservableOnSubscribe<ArrayList<Video>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<Video>> emitter) throws Exception {
                        emitter.onNext(Model.getSearchEngine(SearchActivity.this).getVideoListFromJson(searchText));
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Video>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Video> videos) {
                        Log.d("fftv", "onNext " + videos.size());
                        if (videos.size() == 0) {
                            search_tv_no_result.setVisibility(View.VISIBLE);
                            search_recycler_view_result.setVisibility(View.GONE);
                        } else {
                            mVideoList = videos;
                            mAdapter = new SearchAdapter(SearchActivity.this, mVideoList, SearchActivity.this);
                            search_recycler_view_result.setAdapter(mAdapter);
                            search_tv_no_result.setVisibility(View.GONE);
                            search_recycler_view_result.setVisibility(View.VISIBLE);
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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, VideoDetailActivity.class);
        startActivity(intent);

        EventBus.getDefault().postSticky(new VideoDetailEvent(mVideoList.get(position)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
