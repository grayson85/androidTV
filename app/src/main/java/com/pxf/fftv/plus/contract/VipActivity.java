package com.pxf.fftv.plus.contract;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.bean.BaseDataBean;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.common.Ui;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

import static com.pxf.fftv.plus.Const.ACCOUNT_EVER_VIP;
import static com.pxf.fftv.plus.Const.BASE_DATA_URL;

public class VipActivity extends AppCompatActivity {

    @BindView(R.id.vip_tv_title)
    TextView vip_tv_title;
    
    @BindView(R.id.vip_tv_day_title)
    TextView vip_tv_day_title;
    
    @BindView(R.id.vip_tv_day_message)
    TextView vip_tv_day_message;
    
    @BindView(R.id.vip_tv_day_price)
    TextView vip_tv_day_price;

    @BindView(R.id.vip_tv_week_title)
    TextView vip_tv_week_title;

    @BindView(R.id.vip_tv_week_message)
    TextView vip_tv_week_message;

    @BindView(R.id.vip_tv_week_price)
    TextView vip_tv_week_price;

    @BindView(R.id.vip_tv_month_title)
    TextView vip_tv_month_title;

    @BindView(R.id.vip_tv_month_message)
    TextView vip_tv_month_message;

    @BindView(R.id.vip_tv_month_price)
    TextView vip_tv_month_price;

    @BindView(R.id.vip_tv_season_title)
    TextView vip_tv_season_title;

    @BindView(R.id.vip_tv_season_message)
    TextView vip_tv_season_message;

    @BindView(R.id.vip_tv_season_price)
    TextView vip_tv_season_price;

    @BindView(R.id.vip_tv_year_title)
    TextView vip_tv_year_title;

    @BindView(R.id.vip_tv_year_message)
    TextView vip_tv_year_message;

    @BindView(R.id.vip_tv_year_price)
    TextView vip_tv_year_price;

    @BindView(R.id.vip_tv_ever_title)
    TextView vip_tv_ever_title;

    @BindView(R.id.vip_tv_ever_message)
    TextView vip_tv_ever_message;

    @BindView(R.id.vip_tv_ever_price)
    TextView vip_tv_ever_price;

    @BindView(R.id.vip_root_card)
    View vip_root_card;

    @BindView(R.id.vip_iv_card)
    ImageView vip_iv_card;

    @BindView(R.id.vip_tv_card)
    TextView vip_tv_card;

    private float dayPrice = 1.00f;
    private float weekPrice = 7.00f;
    private float monthPrice = 30.00f;
    private float seasonPrice = 60.00f;
    private float yearPrice = 90.00f;
    private float everPrice = 188.00f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        ButterKnife.bind(this);

        initView();

        initPrice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTitle();
    }

    private void initView() {
        Ui.setViewFocusScaleAnimator(vip_root_card, new FocusAction() {
            @Override
            public void onFocus() {
                vip_root_card.setBackgroundResource(R.drawable.bg_vip_menu_focus);
                vip_iv_card.setImageResource(R.drawable.ic_vip_focus);
                vip_tv_card.setTextColor(getResources().getColor(R.color.colorVipTextFocus));
            }

            @Override
            public void onLoseFocus() {
                vip_iv_card.setImageResource(R.drawable.ic_vip_normal);
                vip_root_card.setBackgroundResource(R.drawable.bg_vip_menu_normal);
                vip_tv_card.setTextColor(getResources().getColor(R.color.colorVipTextNormal));
            }
        });

        if (Const.FEATURE_1) {
            vip_root_card.setVisibility(View.VISIBLE);
        } else {
            vip_root_card.setVisibility(View.GONE);
        }
    }


    private void initTitle() {
        if (FFTVApplication.vipDate == ACCOUNT_EVER_VIP) {
            vip_tv_title.setText("您已经是永久VIP会员");
        } else if (FFTVApplication.vipDate > System.currentTimeMillis()) {
            SimpleDateFormat format = new SimpleDateFormat("您的账号将于 yyyy-MM-dd 到期，请及时续费");
            Date date = new Date(FFTVApplication.vipDate);
            vip_tv_title.setText(format.format(date));
        } else {
            vip_tv_title.setText("您的VIP会员已过期，请及时续费");
        }
    }

    private void initPrice() {
        DisposableObserver<BaseDataBean> observer = new DisposableObserver<BaseDataBean>() {
            @Override
            public void onNext(BaseDataBean adBean) {
                String[] price = adBean.getHBrjjg().split("-");
                try {
                    dayPrice = Float.parseFloat(price[2]);
                    weekPrice = Float.parseFloat(price[4]);
                    monthPrice = Float.parseFloat(price[6]);
                    seasonPrice = Float.parseFloat(price[8]);
                    yearPrice = Float.parseFloat(price[10]);
                    everPrice = Float.parseFloat(price[12]);

                    vip_tv_day_message.setText("体验VIP特权，欣赏全部精彩影片");
                    vip_tv_day_price.setText(price[2].substring(0, price[2].length() - 3) + " 元");
                    vip_tv_week_message.setText("每天仅需 " + String.format("%.2f", Float.parseFloat(price[4]) / 7)  + " 元");
                    vip_tv_week_price.setText(price[4].substring(0, price[4].length() - 3) + " 元");
                    vip_tv_month_message.setText("每天仅需 " + String.format("%.2f", Float.parseFloat(price[6]) / 30)  + " 元");
                    vip_tv_month_price.setText(price[6].substring(0, price[6].length() - 3) + " 元");
                    vip_tv_season_message.setText("每天仅需 " + String.format("%.2f", Float.parseFloat(price[8]) / 90)  + " 元");
                    vip_tv_season_price.setText(price[8].substring(0, price[8].length() - 3) + " 元");
                    vip_tv_year_message.setText("每天仅需 " + String.format("%.2f", Float.parseFloat(price[10]) / 365)  + " 元");
                    vip_tv_year_price.setText(price[10].substring(0, price[10].length() - 3) + " 元");
                    vip_tv_ever_message.setText("开通后享受永久VIP");
                    vip_tv_ever_price.setText(price[12].substring(0, price[12].length() - 3) + " 元");
                } catch (Exception e) {
                    e.printStackTrace();
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
                .create((ObservableOnSubscribe<BaseDataBean>) emitter -> {
                    try {
                        Request request = new Request.Builder()
                                .url(BASE_DATA_URL)
                                .build();
                        Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String result = response.body().string();
                            emitter.onNext(CommonUtils.getGson().fromJson(result.trim().substring(2), BaseDataBean.class));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @OnClick(R.id.vip_root_day)
    public void onDayClick() {
        Intent intent = new Intent(this, VipPayActivity.class);
        startActivity(intent);
        EventBus.getDefault().postSticky(new VipPayEvent("正在支付VIP天卡", dayPrice, 60 * 60 * 24));
    }

    @OnClick(R.id.vip_root_week)
    public void onWeekClick() {
        Intent intent = new Intent(this, VipPayActivity.class);
        startActivity(intent);
        EventBus.getDefault().postSticky(new VipPayEvent("正在支付VIP周卡", weekPrice, 60 * 60 * 24 * 7));
    }

    @OnClick(R.id.vip_root_month)
    public void onMonthClick() {
        Intent intent = new Intent(this, VipPayActivity.class);
        startActivity(intent);
        EventBus.getDefault().postSticky(new VipPayEvent("正在支付VIP月卡", monthPrice, 60 * 60 * 24 * 30));
    }

    @OnClick(R.id.vip_root_season)
    public void onSeasonClick() {
        Intent intent = new Intent(this, VipPayActivity.class);
        startActivity(intent);
        EventBus.getDefault().postSticky(new VipPayEvent("正在支付VIP季卡", seasonPrice, 60 * 60 * 24 * 90));
    }

    @OnClick(R.id.vip_root_year)
    public void onYearClick() {
        Intent intent = new Intent(this, VipPayActivity.class);
        startActivity(intent);
        EventBus.getDefault().postSticky(new VipPayEvent("正在支付VIP年卡", yearPrice, 60 * 60 * 24 * 365));
    }

    @OnClick(R.id.vip_root_ever)
    public void onEverClick() {
        Intent intent = new Intent(this, VipPayActivity.class);
        startActivity(intent);
        EventBus.getDefault().postSticky(new VipPayEvent("正在支付VIP季度卡", everPrice, 888888888));
    }

    @OnClick(R.id.vip_root_card)
    public void onCardClick() {
        Intent intent = new Intent(this, CardActivity.class);
        startActivity(intent);
    }
}
