package com.pxf.fftv.plus.contract;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.Ui;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.account.Account;
import com.pxf.fftv.plus.model.account.AccountModel;
import com.pxf.fftv.plus.model.account.CardCodeResult;
import com.pxf.fftv.plus.model.account.LoginResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CardActivity extends AppCompatActivity {

    @BindView(R.id.vip_card_root)
    View vip_card_root;

    @BindView(R.id.vip_card_et_input)
    EditText vip_card_et_input;

    @BindView(R.id.vip_card_tv_ok)
    TextView vip_card_tv_ok;

    private CompositeDisposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
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
        ViewGroup.LayoutParams params = vip_card_root.getLayoutParams();
        params.width = CommonUtils.getScreenResolutions(this)[0];
        params.height = CommonUtils.getScreenResolutions(this)[1];
        vip_card_root.setLayoutParams(params);

        Ui.setViewFocusScaleAnimator(vip_card_et_input, null);
        Ui.setViewFocusScaleAnimator(vip_card_tv_ok, null);
    }

    @OnClick(R.id.vip_card_tv_ok)
    public void onOkClick() {
        String cardCode = vip_card_et_input.getText().toString().trim();

        if (cardCode.isEmpty()) {
            Toast.makeText(this, "卡密为空", Toast.LENGTH_SHORT).show();
        }

        DisposableObserver<CardCodeResult> observer = new DisposableObserver<CardCodeResult>() {
            @Override
            public void onNext(CardCodeResult cardCodeResult) {
                if (cardCodeResult.getSuccess()) {
                    Toast.makeText(CardActivity.this, "卡密使用成功", Toast.LENGTH_SHORT).show();
                    refreshVipDate();
                } else {
                    Toast.makeText(CardActivity.this, cardCodeResult.getMsg(), Toast.LENGTH_SHORT).show();
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
                .create((ObservableOnSubscribe<CardCodeResult>) emitter -> {
                    emitter.onNext(AccountModel.getInstance().verifyCardCode(FFTVApplication.account, cardCode));
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        mDisposable.add(observer);
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
