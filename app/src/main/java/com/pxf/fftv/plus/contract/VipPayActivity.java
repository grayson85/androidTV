package com.pxf.fftv.plus.contract;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.account.Account;
import com.pxf.fftv.plus.model.account.LoginResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.pxf.fftv.plus.Const.VIP_ORDER_QUERY;
import static com.pxf.fftv.plus.Const.VIP_PAY_URL;

public class VipPayActivity extends AppCompatActivity {

    @BindView(R.id.vip_pay_root)
    View vip_pay_root;

    @BindView(R.id.vip_pay_tv_title)
    TextView vip_pay_tv_title;

    @BindView(R.id.vip_pay_iv_qrcode_ali)
    ImageView vip_pay_iv_qrcode_ali;

    @BindView(R.id.vip_pay_iv_qrcode_wx)
    ImageView vip_pay_iv_qrcode_wx;

    @BindView(R.id.vip_pay_iv_qrcode_qq)
    ImageView vip_pay_iv_qrcode_qq;

    private String aliOrder;
    private String wxOrder;
    private String qqOrder;

    private Timer aliTimer;
    private Timer wxTimer;
    private Timer qqTimer;

    private float price;
    private long addSecond;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_pay);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mDisposable = new CompositeDisposable();
        aliTimer = new Timer();
        wxTimer = new Timer();
        qqTimer = new Timer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        mDisposable.clear();
        mDisposable = null;
        if (aliTimer != null) {
            aliTimer.cancel();
            aliTimer.purge();
        }
        if (wxTimer != null) {
            wxTimer.cancel();
            wxTimer.purge();
        }
        if (qqTimer != null) {
            qqTimer.cancel();
            qqTimer.purge();
        }
    }

    private void initView() {
        // 使Activity全屏
        ViewGroup.LayoutParams params = vip_pay_root.getLayoutParams();
        params.width = CommonUtils.getScreenResolutions(this)[0];
        params.height = CommonUtils.getScreenResolutions(this)[1];
        vip_pay_root.setLayoutParams(params);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onVipPayEvent(VipPayEvent event) {
        vip_pay_tv_title.setText(event.getTitle() + " 请选择支付方式");
        price = event.getPrice();
        addSecond = event.getAddSecond();
        EventBus.getDefault().removeStickyEvent(event);

        loadPrice();
    }

    private void loadPrice() {
        // 支付宝
        DisposableObserver<OrderBean> aliObserver = new DisposableObserver<OrderBean>() {
            @Override
            public void onNext(OrderBean orderBean) {
                aliOrder = orderBean.getOrderno();
                Bitmap qrcode = CommonUtils.createQRCodeBitmap(orderBean.getUrl(),
                        getResources().getDimensionPixelOffset(R.dimen.vip_pay_qrcode_size),
                        getResources().getDimensionPixelOffset(R.dimen.vip_pay_qrcode_size));
                vip_pay_iv_qrcode_ali.setImageBitmap(qrcode);
                startOrderTimer(aliTimer, aliOrder);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        Observable
                .create((ObservableOnSubscribe<OrderBean>) emitter -> {
                    String requestBody = "token=" + FFTVApplication.token + "&paytype=alipay&amount=" + price;
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, requestBody);
                    final Request request = new Request.Builder()
                            .url(VIP_PAY_URL)
                            .post(body)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();
                    Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        String result = response.body().string().substring(2);
                        try {
                            OrderBean bean = CommonUtils.getGson().fromJson(result, OrderBean.class);
                            emitter.onNext(bean);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aliObserver);
        mDisposable.add(aliObserver);

        // 微信
        DisposableObserver<OrderBean> wxObserver = new DisposableObserver<OrderBean>() {
            @Override
            public void onNext(OrderBean orderBean) {
                wxOrder = orderBean.getOrderno();
                Bitmap qrcode = CommonUtils.createQRCodeBitmap(orderBean.getUrl(),
                        getResources().getDimensionPixelOffset(R.dimen.vip_pay_qrcode_size),
                        getResources().getDimensionPixelOffset(R.dimen.vip_pay_qrcode_size));
                vip_pay_iv_qrcode_wx.setImageBitmap(qrcode);
                startOrderTimer(wxTimer, wxOrder);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        Observable
                .create((ObservableOnSubscribe<OrderBean>) emitter -> {
                    String requestBody = "token=" + FFTVApplication.token + "&paytype=wxpay&amount=" + price;
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, requestBody);
                    final Request request = new Request.Builder()
                            .url(VIP_PAY_URL)
                            .post(body)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();
                    Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        String result = response.body().string().substring(2);
                        try {
                            OrderBean bean = CommonUtils.getGson().fromJson(result, OrderBean.class);
                            emitter.onNext(bean);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(wxObserver);
        mDisposable.add(wxObserver);

        // QQ
        DisposableObserver<OrderBean> qqObserver = new DisposableObserver<OrderBean>() {
            @Override
            public void onNext(OrderBean orderBean) {
                qqOrder = orderBean.getOrderno();
                Bitmap qrcode = CommonUtils.createQRCodeBitmap(orderBean.getUrl(),
                        getResources().getDimensionPixelOffset(R.dimen.vip_pay_qrcode_size),
                        getResources().getDimensionPixelOffset(R.dimen.vip_pay_qrcode_size));
                vip_pay_iv_qrcode_qq.setImageBitmap(qrcode);
                startOrderTimer(qqTimer, qqOrder);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        Observable
                .create((ObservableOnSubscribe<OrderBean>) emitter -> {
                    String requestBody = "token=" + FFTVApplication.token + "&paytype=qqpay&amount=" + price;
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, requestBody);
                    final Request request = new Request.Builder()
                            .url(VIP_PAY_URL)
                            .post(body)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();
                    Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        String result = response.body().string().substring(2);
                        try {
                            OrderBean bean = CommonUtils.getGson().fromJson(result, OrderBean.class);
                            emitter.onNext(bean);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qqObserver);
        mDisposable.add(qqObserver);
    }

    private void startOrderTimer(Timer timer, String order) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DisposableObserver<Boolean> orderObserver = new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean pay) {
                        if (pay) {
                            Toast.makeText(VipPayActivity.this, "支付成功", Toast.LENGTH_LONG).show();
                            refreshVipDate();
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
                        .create((ObservableOnSubscribe<Boolean>) emitter -> {
                            String requestBody = "token=" + FFTVApplication.token + "&orderno=" + order + "&tianshu=" + addSecond;
                            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                            RequestBody body = RequestBody.create(mediaType, requestBody);
                            final Request request = new Request.Builder()
                                    .url(VIP_ORDER_QUERY)
                                    .post(body)
                                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                    .build();
                            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                            if (response.isSuccessful() && response.body() != null) {
                                String result = response.body().string().substring(2);
                                OrderPayBean bean = CommonUtils.getGson().fromJson(result, OrderPayBean.class);
                                if (bean.getStatus().equals("1")) {
                                    emitter.onNext(true);
                                } else {
                                    emitter.onNext(false);
                                }
                                emitter.onComplete();
                            }

                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(orderObserver);
                mDisposable.add(orderObserver);
            }
        }, 1000, 1000);

    }

    private void refreshVipDate() {
        String name = FFTVApplication.account;
        String password = FFTVApplication.password;
        Observable
                .create((ObservableOnSubscribe<LoginResult>) emitter -> {
                    Account account = new Account();
                    account.setUsername(name);
                    account.setPassword(password);
                    emitter.onNext(Model.getAccountModel().login(account));
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LoginResult loginResult) {
                        if (loginResult.isSuccess()) {
                            FFTVApplication.vipDate = loginResult.getExpirationDate();
                            FFTVApplication.token = loginResult.getToken();
                        }
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
