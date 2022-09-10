package com.pxf.fftv.plus.contract;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.common.Ui;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SearchNewActivity extends AppCompatActivity implements SearchNewAdapter.OnSearchResultItemClickListener{

    @BindView(R.id.search_root_delete)
    View search_root_delete;

    @BindView(R.id.search_iv_delete)
    ImageView search_iv_delete;

    @BindView(R.id.search_tv_delete)
    TextView search_tv_delete;

    @BindView(R.id.search_root_search)
    View search_root_search;

    @BindView(R.id.search_iv_search)
    ImageView search_iv_search;

    @BindView(R.id.search_tv_search)
    TextView search_tv_search;

    @BindView(R.id.search_line)
    View search_line;

    @BindView(R.id.search_recycler_view_letter)
    RecyclerView search_recycler_view_letter;

    @BindView(R.id.search_et_keywords)
    EditText search_et_keywords;

    @BindView(R.id.search_recycler_view_result)
    RecyclerView search_recycler_view_result;

    @BindView(R.id.search_root_result)
    View search_root_result;

    @BindView(R.id.search_tv_result_title)
    TextView search_tv_result_title;

    @BindView(R.id.search_root_loading)
    View search_root_loading;

    private static final String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "O", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V"
            , "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    private CompositeDisposable mDisposable;
    private ArrayList<Video> mResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDisposable = new CompositeDisposable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    private void initView() {
        Ui.setMenuFocusAnimator(this, search_root_delete, new FocusAction() {
            @Override
            public void onFocus() {
                search_iv_delete.setImageResource(R.drawable.ic_delete_focus);
                search_tv_delete.setTextColor(getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                search_iv_delete.setImageResource(R.drawable.ic_delete_normal);
                search_tv_delete.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });

        Ui.setMenuFocusAnimator(this, search_root_search, new FocusAction() {
            @Override
            public void onFocus() {
                search_iv_search.setImageResource(R.drawable.ic_search_focus);
                search_tv_search.setTextColor(getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                search_iv_search.setImageResource(R.drawable.ic_search_normal);
                search_tv_search.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });

        search_recycler_view_letter.setLayoutManager(new GridLayoutManager(this, 6));
        search_recycler_view_letter.setAdapter(new LetterAdapter(this, letters, new OnLetterClickListener() {
            @Override
            public void onClick(int position, String letter) {
                search_et_keywords.setText(search_et_keywords.getText().toString() + letter);
            }
        }));

        search_recycler_view_result.setLayoutManager(new GridLayoutManager(this, 4));
        // 优化
        // search_recycler_view_result.setHasFixedSize(true);
        // 卡片最大缓存数量，该数量以内的卡片能保证动画效果不卡顿
        // search_recycler_view_result.setItemViewCacheSize(500);

        search_line.requestFocus();
    }

    @OnClick(R.id.search_root_delete)
    public void onDeleteClick() {
        search_et_keywords.setText("");
    }

    @OnClick(R.id.search_root_search)
    public void onSearchClick() {
        String keyWords = search_et_keywords.getText().toString().trim();
        if (!keyWords.isEmpty()) {
            search_root_loading.setVisibility(View.VISIBLE);
            search_root_result.setVisibility(View.GONE);
            search_tv_result_title.setText(keyWords + " 的搜索结果");

            DisposableObserver<ArrayList<Video>> observer = new DisposableObserver<ArrayList<Video>>() {
                @Override
                public void onNext(ArrayList<Video> videos) {
                    mResultList = videos;
                    if (videos.size() == 0) {
                        search_tv_result_title.setText("搜索不到相关结果");
                    } else {
                        search_recycler_view_result.setAdapter(new SearchNewAdapter(
                                SearchNewActivity.this, mResultList, SearchNewActivity.this
                        ));
                    }
                    search_root_loading.setVisibility(View.GONE);
                    search_root_result.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };
            Observable
                    .create(new ObservableOnSubscribe<ArrayList<Video>>() {
                        @Override
                        public void subscribe(ObservableEmitter<ArrayList<Video>> emitter) throws Exception {
                            emitter.onNext(Model.getSearchEngine(SearchNewActivity.this).getVideoListFromJson(keyWords));
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
            mDisposable.add(observer);
        }
    }

    @Override
    public void onSearchItemClick(int position) {
        Intent intent = new Intent(this, VideoDetailActivity.class);
        startActivity(intent);

        EventBus.getDefault().postSticky(new VideoDetailEvent(mResultList.get(position)));
    }

    static class LetterAdapter extends RecyclerView.Adapter<LetterViewHolder> {

        private Activity activity;

        private String[] letters;

        private OnLetterClickListener listener;

        public LetterAdapter(Activity activity, String[] letters, OnLetterClickListener listener) {
            this.activity = activity;
            this.letters = letters;
            this.listener = listener;
        }

        @NonNull
        @Override
        public LetterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_search_letter, parent, false);
            return new LetterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LetterViewHolder holder, int position) {
            holder.search_item_tv_letter.setText(letters[position]);
            holder.search_item_tv_letter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(holder.getAdapterPosition(), letters[holder.getAdapterPosition()]);
                }
            });
        }

        @Override
        public int getItemCount() {
            return letters.length;
        }
    }

    interface OnLetterClickListener {
        void onClick(int position, String letter);
    }

    static class LetterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.search_item_tv_letter)
        TextView search_item_tv_letter;

        public LetterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}