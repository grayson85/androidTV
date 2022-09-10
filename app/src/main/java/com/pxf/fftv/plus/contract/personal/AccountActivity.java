package com.pxf.fftv.plus.contract.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.model.account.Account;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.account.LoginResult;
import com.pxf.fftv.plus.model.account.RegisterResult;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.pxf.fftv.plus.Const.ACCOUNT_EVER_VIP;
import static com.pxf.fftv.plus.Const.ACCOUNT_NO_VIP;
import static com.pxf.fftv.plus.Const.ANIMATION_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.account_et_login_account)
    EditText account_et_login_account;

    @BindView(R.id.account_et_login_password)
    EditText account_et_login_password;

    @BindView(R.id.account_tv_login)
    TextView account_tv_login;

    @BindView(R.id.account_tv_start_register)
    TextView account_tv_start_register;

    @BindView(R.id.account_et_register_account)
    EditText account_et_register_account;

    @BindView(R.id.account_et_register_password)
    EditText account_et_register_password;

    @BindView(R.id.account_et_register_password_again)
    EditText account_et_register_password_again;

    @BindView(R.id.account_tv_register)
    TextView account_tv_register;

    @BindView(R.id.account_root_login)
    View account_root_login;

    @BindView(R.id.account_root_register)
    View account_root_register;

    @BindView(R.id.account_root_have_login)
    View account_root_have_login;

    @BindView(R.id.account_tv_account_name)
    TextView account_tv_account_name;

    @BindView(R.id.account_tv_buy_vip)
    TextView account_tv_buy_vip;

    @BindView(R.id.account_tv_vip)
    TextView account_tv_vip;

    @BindView(R.id.account_vip_date)
    TextView account_vip_date;

    @BindView(R.id.account_tv_logout)
    TextView account_tv_logout;

    @BindView(R.id.account_tv_switch_account)
    TextView account_tv_switch_account;

    private ProgressDialog mProgressDialog;

    private enum Page {
        PAGE_LOGIN,
        PAGE_REGISTER
    }

    private Page currentPage = Page.PAGE_LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        if (FFTVApplication.login) {
            account_root_have_login.setVisibility(View.VISIBLE);
            account_root_login.setVisibility(View.GONE);
            account_tv_account_name.setText(FFTVApplication.account + " 已登录");
        } else {
            account_root_have_login.setVisibility(View.GONE);
            account_root_login.setVisibility(View.VISIBLE);
        }

        if (FFTVApplication.vipDate == ACCOUNT_EVER_VIP) {
            account_tv_vip.setVisibility(View.VISIBLE);
            account_tv_buy_vip.setVisibility(View.GONE);
            account_vip_date.setVisibility(View.VISIBLE);
            account_vip_date.setText("永久会员");
        } else if (FFTVApplication.vipDate == ACCOUNT_NO_VIP) {
            account_tv_vip.setVisibility(View.GONE);
            account_tv_buy_vip.setVisibility(View.VISIBLE);
            account_vip_date.setVisibility(View.GONE);
        } else {
            account_tv_vip.setVisibility(View.VISIBLE);
            account_tv_buy_vip.setVisibility(View.GONE);
            account_vip_date.setVisibility(View.VISIBLE);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm 到期");
            Date date = new Date(FFTVApplication.vipDate);
            account_vip_date.setText(format.format(date));
        }

        setBtnFocusAnimator(account_tv_login, new FocusAction() {
            @Override
            public void onFocus() {
                super.onFocus();
            }
        });
        setBtnFocusAnimator(account_tv_start_register, new FocusAction() {
            @Override
            public void onFocus() {
                super.onFocus();
            }
        });
        setBtnFocusAnimator(account_tv_register, new FocusAction() {
            @Override
            public void onFocus() {
                super.onFocus();
            }
        });
        setBtnFocusAnimator(account_tv_buy_vip, new FocusAction() {
            @Override
            public void onFocus() {
                account_tv_buy_vip.setTextColor(getResources().getColor(R.color.accountVipColorFocus));
            }
            @Override
            public void onLoseFocus() {
                account_tv_buy_vip.setTextColor(getResources().getColor(R.color.accountVipColorNormal));
            }
        });
        setBtnFocusAnimator(account_tv_logout, new FocusAction() {
            @Override
            public void onFocus() {
                super.onFocus();
            }
        });
        setBtnFocusAnimator(account_tv_switch_account, new FocusAction() {
            @Override
            public void onFocus() {
                super.onFocus();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (currentPage == Page.PAGE_REGISTER) {
            switchPage(Page.PAGE_LOGIN);
        } else {
            super.onBackPressed();
        }
    }

    private void switchPage(Page page) {
        int screenWidth = CommonUtils.getScreenResolutions(this)[0];
        ValueAnimator animator = ValueAnimator.ofInt(0, screenWidth).setDuration(ANIMATION_DURATION);

        if (page == Page.PAGE_LOGIN) {
            animator.addUpdateListener(animation -> {
                account_root_login.setTranslationX((int) animation.getAnimatedValue() - screenWidth);
                account_root_register.setTranslationX((int) animation.getAnimatedValue());
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    currentPage = Page.PAGE_LOGIN;
                    account_root_register.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    account_root_login.setTranslationX(screenWidth);
                    account_root_login.setVisibility(View.VISIBLE);
                }
            });
            animator.start();
        }
        if (page == Page.PAGE_REGISTER) {
            animator.addUpdateListener(animation -> {
                account_root_login.setTranslationX((int) animation.getAnimatedValue() * -1);
                account_root_register.setTranslationX(screenWidth - (int) animation.getAnimatedValue());
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    currentPage = Page.PAGE_REGISTER;
                    account_root_login.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    account_root_register.setTranslationX(screenWidth);
                    account_root_register.setVisibility(View.VISIBLE);
                }
            });
            animator.start();
        }
    }

    private void setBtnFocusAnimator(View view, FocusAction focusAction) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                focusAction.onFocus();

                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE).setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE).setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        view.setScaleX((float) animation.getAnimatedValue());
                        view.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        view.setScaleX((float) animation.getAnimatedValue());
                        view.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                focusAction.onLoseFocus();

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    view.setScaleX((float) animation.getAnimatedValue());
                    view.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });
    }

    @OnClick(R.id.account_tv_start_register)
    public void onStartRegisterClick() {
        switchPage(Page.PAGE_REGISTER);
    }

    @OnClick(R.id.account_tv_register)
    public void onRegisterClick() {
        String name = account_et_register_account.getText().toString().trim();
        String password = account_et_register_password.getText().toString().trim();
        String passwordConfirm = account_et_register_password_again.getText().toString().trim();

        if (name.isEmpty()) {
            toast("手机号不能为空");
            return;
        }
        if (password.isEmpty()) {
            toast("密码不能为空");
            return;
        }
        if (passwordConfirm.isEmpty()) {
            toast("请输入确认密码");
            return;
        }
        if (!password.equals(passwordConfirm)) {
            toast("两次输入的密码不一致");
            return;
        }

        showProgressDialog("正在注册...");

        Observable
                .create((ObservableOnSubscribe<RegisterResult>) emitter -> {
                    // 1秒延时避免弹框过快
                    Thread.sleep(1000);
                    // 发起注册
                    Account account = new Account();
                    account.setUsername(name);
                    account.setPassword(password);
                    emitter.onNext(Model.getAccountModel().register(account));
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegisterResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RegisterResult registerResult) {
                        dismissProgressDialog();
                        if (registerResult.isSuccess()) {
                            toast("注册成功");
                            account_et_login_account.setText(name);
                            account_et_login_password.setText(password);
                            switchPage(Page.PAGE_LOGIN);
                            account_tv_login.requestFocus();
                        } else {
                            toast(registerResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        dismissProgressDialog();
                    }
                });
    }

    @OnClick(R.id.account_tv_login)
    public void onLoginClick() {
        String name = account_et_login_account.getText().toString().trim();
        String password = account_et_login_password.getText().toString().trim();

        if (name.isEmpty()) {
            toast("手机号不能为空");
            return;
        }
        if (password.isEmpty()) {
            toast("密码不能为空");
            return;
        }

        showProgressDialog("正在登录...");
        FFTVApplication.token = "";
        FFTVApplication.login = false;
        FFTVApplication.account = "";
        FFTVApplication.password = "";
        FFTVApplication.vipDate = ACCOUNT_NO_VIP;
        Observable
                .create((ObservableOnSubscribe<LoginResult>) emitter -> {
                    // 延时登录避免弹框过快
                    Thread.sleep(1000);
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
                        dismissProgressDialog();
                        if (loginResult.isSuccess()) {
                            toast("登录成功");
                            FFTVApplication.login = true;
                            FFTVApplication.account = name;
                            FFTVApplication.password = password;
                            FFTVApplication.vipDate = loginResult.getExpirationDate();
                            FFTVApplication.token = loginResult.getToken();
                            Model.getData().setAccount(AccountActivity.this, name);
                            Model.getData().setPassword(AccountActivity.this, password);
                            finish();
                        } else {
                            toast(loginResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        dismissProgressDialog();
                    }
                });
    }

    @OnClick(R.id.account_tv_logout)
    public void onLogoutClick() {
        FFTVApplication.token = "";
        FFTVApplication.login = false;
        FFTVApplication.account = "";
        FFTVApplication.password = "";
        FFTVApplication.vipDate = ACCOUNT_NO_VIP;
        finish();
        toast("账号已注销");
    }

    @OnClick(R.id.account_tv_switch_account)
    public void onSwitchAccountClick() {
        FFTVApplication.token = "";
        FFTVApplication.login = false;
        FFTVApplication.account = "";
        FFTVApplication.password = "";
        FFTVApplication.vipDate = ACCOUNT_NO_VIP;
        account_root_have_login.setVisibility(View.GONE);
        account_root_login.setVisibility(View.VISIBLE);
    }

    private void showProgressDialog(String text) {
        mProgressDialog = ProgressDialog.show(
                this, "", text, false, false
        );
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void toast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
