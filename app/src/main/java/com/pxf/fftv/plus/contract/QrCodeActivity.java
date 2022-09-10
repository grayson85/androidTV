package com.pxf.fftv.plus.contract;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QrCodeActivity extends AppCompatActivity {

    @BindView(R.id.qrcode_root)
    View qrcode_root;

    @BindView(R.id.ad_tv_title)
    TextView ad_tv_title;

    @BindView(R.id.ad_iv_qrcode)
    ImageView ad_iv_qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        // 使Activity全屏
        ViewGroup.LayoutParams params = qrcode_root.getLayoutParams();
        params.width = CommonUtils.getScreenResolutions(this)[0];
        params.height = CommonUtils.getScreenResolutions(this)[1];
        qrcode_root.setLayoutParams(params);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onQrCodeEvent(QrCodeEvent event) {
        ad_tv_title.setText(event.getTitle());
        Bitmap qrcode = CommonUtils.createQRCodeBitmap(event.getUrl(),
                getResources().getDimensionPixelOffset(R.dimen.vip_pay_qrcode_size),
                getResources().getDimensionPixelOffset(R.dimen.vip_pay_qrcode_size));
        ad_iv_qrcode.setImageBitmap(qrcode);
        EventBus.getDefault().removeStickyEvent(event);
    }
}
