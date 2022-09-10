package com.pxf.fftv.plus.contract.collect;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.InternalFileSaveUtil;
import com.pxf.fftv.plus.common.Ui;
import com.pxf.fftv.plus.contract.detail.VideoDetailActivity;
import com.pxf.fftv.plus.contract.detail.VideoDetailEvent;
import com.pxf.fftv.plus.contract.history.VideoHistory;
import com.pxf.fftv.plus.contract.history.VideoHistoryAdapter;
import com.pxf.fftv.plus.player.VideoPlayer;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoCollectActivity extends AppCompatActivity implements VideoCollectAdapter.OnCollectItemClickListener {

    @BindView(R.id.video_history_recycler_view)
    RecyclerView video_history_recycler_view;

    private LinkedList<VideoCollect> collectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_history);
        ButterKnife.bind(this);

        Ui.configTopBar(this, "选中视频时菜单键可删除");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        collectList = (LinkedList<VideoCollect>)InternalFileSaveUtil.getInstance(this).get("video_collect");
        video_history_recycler_view.setLayoutManager(new GridLayoutManager(this, 5) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return FFTVApplication.screenHeight / 2;
            }
        });
        if (collectList != null) {
            VideoCollectAdapter adapter = new VideoCollectAdapter(this, collectList, this);
            video_history_recycler_view.setAdapter(adapter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onCollectItemClick(int position) {
        VideoCollect item = collectList.get(position);

        Intent intent = new Intent(this, VideoDetailActivity.class);
        startActivity(intent);

        EventBus.getDefault().postSticky(new VideoDetailEvent(item.getVideo()));
    }

    @Override
    public void onCollectItemMenuClick(int position) {
        collectList.remove(position);
        InternalFileSaveUtil.getInstance(this).put("video_collect", collectList);
        VideoCollectAdapter adapter = new VideoCollectAdapter(this, collectList, this);
        video_history_recycler_view.setAdapter(adapter);
    }
}
