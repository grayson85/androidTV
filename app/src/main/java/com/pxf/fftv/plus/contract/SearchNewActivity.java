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
import com.pxf.fftv.plus.databinding.ActivitySearchNewBinding;
import com.pxf.fftv.plus.databinding.ItemSearchLetterBinding;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.video.Video;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SearchNewActivity extends AppCompatActivity implements SearchNewAdapter.OnSearchResultItemClickListener {

    // ViewBinding variable
    private ActivitySearchNewBinding binding;

    private static final String[] letters = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "0", "␣", "-", "_", ".", "⌫", "CLR" };

    private CompositeDisposable mDisposable;
    private ArrayList<Video> mResultList;

    private long mLastKeyDownTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate and set the content view using ViewBinding
        binding = ActivitySearchNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // ButterKnife.bind(this); is no longer needed

        initView();
        // Set up click listeners that were previously handled by @OnClick
        setupClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDisposable = new CompositeDisposable();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.clear();
        }
    }

    private void initView() {
        Ui.setMenuFocusAnimator(this, binding.searchRootDelete, new FocusAction() {
            @Override
            public void onFocus() {
                binding.searchIvDelete.setImageResource(R.drawable.ic_delete_focus);
                binding.searchTvDelete.setTextColor(getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                binding.searchIvDelete.setImageResource(R.drawable.ic_delete_normal);
                binding.searchTvDelete.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });

        Ui.setMenuFocusAnimator(this, binding.searchRootSearch, new FocusAction() {
            @Override
            public void onFocus() {
                binding.searchIvSearch.setImageResource(R.drawable.ic_search_focus);
                binding.searchTvSearch.setTextColor(getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                binding.searchIvSearch.setImageResource(R.drawable.ic_search_normal);
                binding.searchTvSearch.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });

        binding.searchRecyclerViewLetter.setLayoutManager(new GridLayoutManager(this, 7));
        binding.searchRecyclerViewLetter.setAdapter(new LetterAdapter(this, letters, (position, letter) -> {
            if (letter.equals("⌫")) {
                // Backspace
                String current = binding.searchEtKeywords.getText().toString();
                if (current.length() > 0) {
                    binding.searchEtKeywords.setText(current.substring(0, current.length() - 1));
                }
            } else if (letter.equals("CLR")) {
                // Clear
                binding.searchEtKeywords.setText("");
            } else if (letter.equals("␣")) {
                // Space
                binding.searchEtKeywords.setText(binding.searchEtKeywords.getText().toString() + " ");
            } else {
                // Regular character
                binding.searchEtKeywords.setText(binding.searchEtKeywords.getText().toString() + letter);
            }
        }));

        binding.searchRecyclerViewResult.setLayoutManager(new GridLayoutManager(this, 4));
        // 优化
        binding.searchRecyclerViewResult.setHasFixedSize(true);
        // 卡片最大缓存数量，该数量以内的卡片能保证动画效果不卡顿
        binding.searchRecyclerViewResult.setItemViewCacheSize(20);

        // Prevent up/down navigation from going to keyboard - only left key should go
        // there
        binding.searchRecyclerViewResult.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());
        binding.searchRecyclerViewResult.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        // Block up/down focus from leaving results area
        binding.searchRecyclerViewResult.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                View focused = binding.searchRecyclerViewResult.getFocusedChild();
                if (focused != null) {
                    int pos = binding.searchRecyclerViewResult.getChildAdapterPosition(focused);
                    int columns = 4;

                    if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_UP) {
                        // If on first row, block up navigation
                        if (pos < columns) {
                            return true; // Consume event, don't navigate
                        }
                    } else if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_DOWN) {
                        // If on last row, block down navigation
                        RecyclerView.Adapter adapter = binding.searchRecyclerViewResult.getAdapter();
                        if (adapter != null) {
                            int totalItems = adapter.getItemCount();
                            int lastRowStart = (totalItems / columns) * columns;
                            if (totalItems % columns != 0) {
                                // Items don't fill last row completely
                            }
                            if (pos >= totalItems - columns) {
                                return true; // Consume event
                            }
                        }
                    }
                }
            }
            return false;
        });

        // Allow system keyboard when clicking on EditText
        binding.searchEtKeywords.setOnClickListener(v -> {
            binding.searchEtKeywords.setFocusable(true);
            binding.searchEtKeywords.setFocusableInTouchMode(true);
            binding.searchEtKeywords.requestFocus();
            // Show system keyboard
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(
                    android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(binding.searchEtKeywords, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        });

        binding.searchLine.requestFocus();
    }

    // Replaces @OnClick annotations
    private void setupClickListeners() {
        binding.searchRootDelete.setOnClickListener(v -> onDeleteClick());
        binding.searchRootSearch.setOnClickListener(v -> onSearchClick());
    }

    public void onDeleteClick() {
        binding.searchEtKeywords.setText("");
    }

    public void onSearchClick() {
        String keyWords = binding.searchEtKeywords.getText().toString().trim();
        if (!keyWords.isEmpty()) {
            binding.searchRootLoading.setVisibility(View.VISIBLE);
            binding.searchRootResult.setVisibility(View.GONE);
            binding.searchTvResultTitle.setText(keyWords + " 的搜索结果");

            DisposableObserver<ArrayList<Video>> observer = new DisposableObserver<ArrayList<Video>>() {
                @Override
                public void onNext(ArrayList<Video> videos) {
                    // Update existing list instead of replacing to maintain adapter reference
                    if (mResultList == null) {
                        mResultList = new ArrayList<>();
                    }
                    mResultList.clear();
                    mResultList.addAll(videos);

                    if (videos.size() == 0) {
                        binding.searchTvResultTitle.setText("搜索不到相关结果");
                    } else {
                        // Reuse adapter if exists to prevent focus loss
                        RecyclerView.Adapter adapter = binding.searchRecyclerViewResult.getAdapter();
                        if (adapter == null) {
                            binding.searchRecyclerViewResult.setAdapter(new SearchNewAdapter(
                                    SearchNewActivity.this, mResultList, SearchNewActivity.this));
                        } else {
                            // Adapter already references mResultList, just notify
                            adapter.notifyDataSetChanged();
                        }
                    }
                    binding.searchRootLoading.setVisibility(View.GONE);
                    binding.searchRootResult.setVisibility(View.VISIBLE);

                    // Request focus after layout completes
                    if (videos.size() > 0) {
                        // Clear focus from search EditText first
                        binding.searchEtKeywords.clearFocus();
                        // Use post() to wait for layout pass to complete
                        binding.searchRecyclerViewResult.post(() -> {
                            binding.searchRecyclerViewResult.requestFocus();
                        });
                    }
                }

                @Override
                public void onError(Throwable e) {
                    // It's good practice to handle errors
                    binding.searchRootLoading.setVisibility(View.GONE);
                    binding.searchTvResultTitle.setText("搜索时发生错误");
                }

                @Override
                public void onComplete() {

                }
            };
            Observable
                    .create((ObservableOnSubscribe<ArrayList<Video>>) emitter -> emitter
                            .onNext(Model.getSearchEngine(SearchNewActivity.this).getVideoListFromJson(keyWords)))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
            mDisposable.add(observer);
        }
    }

    @Override
    public void onSearchItemClick(int position) {
        // Post sticky event BEFORE starting activity so it's available when activity
        // registers
        EventBus.getDefault().postSticky(new VideoDetailEvent(mResultList.get(position)));

        Intent intent = new Intent(this, VideoDetailActivity.class);
        startActivity(intent);
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
            // Inflate using the generated binding class
            ItemSearchLetterBinding itemBinding = ItemSearchLetterBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new LetterViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull LetterViewHolder holder, int position) {
            holder.binding.searchItemTvLetter.setText(letters[position]);
            holder.binding.searchItemTvLetter.setOnClickListener(v -> {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    listener.onClick(adapterPos, letters[adapterPos]);
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

        // Use the binding object
        ItemSearchLetterBinding binding;

        public LetterViewHolder(ItemSearchLetterBinding itemBinding) {
            super(itemBinding.getRoot());
            // Store the binding
            this.binding = itemBinding;
            // ButterKnife.bind is no longer needed
        }
    }
}
