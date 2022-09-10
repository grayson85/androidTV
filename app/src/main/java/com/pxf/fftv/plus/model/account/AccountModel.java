package com.pxf.fftv.plus.model.account;

import android.util.Log;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.model.IAccountModel;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.pxf.fftv.plus.Const.CARD_CODE_URL;
import static com.pxf.fftv.plus.Const.LOGIN_URL;
import static com.pxf.fftv.plus.Const.REGISTER_URL;

public class AccountModel implements IAccountModel {

    private volatile static AccountModel mInstance;

    private AccountModel() {

    }

    public static AccountModel getInstance() {
        if (mInstance == null) {
            synchronized (AccountModel.class) {
                if (mInstance == null) {
                    mInstance = new AccountModel();
                }
            }
        }
        return mInstance;
    }

    // 登录 Q七五六叁2五六四七
    public static void login(Account account, Observer<String> observer) {
        String requestBody = "username=" + account.getUsername() + "&password=" + account.getPassword();

        if (account.getDeviceCode() != null && !account.getDeviceCode().isEmpty()) {
            requestBody += "&logcode=" + account.getDeviceCode();
        }

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, requestBody);
        final Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Observable
                .create((ObservableOnSubscribe<String>) emitter -> {
                    Thread.sleep(1000);
                    Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                    if (response.isSuccessful()) {
                        // 去掉返回结果的前两位特殊字符
                        String result = response.body().string().substring(2);
                        emitter.onNext(result);
                    } else {
                        emitter.onNext("error");
                    }
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public RegisterResult register(Account account) {
        String requestBody = "&username=" + account.getUsername() + "&password=" + account.getPassword();

        if (account.getSuperPassword() != null && !account.getSuperPassword().isEmpty()) {
            requestBody += "&superpass=" + account.getSuperPassword();
        } else {
            requestBody += "&superpass=" + account.getPassword();
        }
        if (account.getInvitation() != null && !account.getInvitation().isEmpty()) {
            requestBody += "&inv=" + account.getInvitation();
        }
        if (account.getDeviceCode() != null && !account.getDeviceCode().isEmpty()) {
            requestBody += "&regcode=" + account.getDeviceCode();
        }
        if (account.getNickname() != null && !account.getNickname().isEmpty()) {
            requestBody += "&name=" + account.getNickname();
        }

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, requestBody);
        final Request request = new Request.Builder()
                .url(REGISTER_URL)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        RegisterResult registerResult = new RegisterResult();
        // 默认值
        registerResult.setSuccess(false);
        registerResult.setMessage("注册失败，未知错误");

        try {
            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string().substring(2);
                switch (result) {
                    case "105":
                        registerResult.setMessage("该手机号已被注册");
                        break;
                    case "106":
                        registerResult.setMessage("该IP已注册");
                        break;
                    case "200":
                        registerResult.setSuccess(true);
                        registerResult.setMessage("注册成功");
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return registerResult;
    }

    @Override
    public LoginResult login(Account account) {
        String requestBody = "username=" + account.getUsername() + "&password=" + account.getPassword();

        if (account.getDeviceCode() != null && !account.getDeviceCode().isEmpty()) {
            requestBody += "&logcode=" + account.getDeviceCode();
        }

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, requestBody);
        final Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        LoginResult loginResult = new LoginResult();
        loginResult.setExpirationDate(Const.ACCOUNT_NO_VIP);
        loginResult.setSuccess(false);
        loginResult.setMessage("登录失败，未知错误");

        try {
            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string().substring(2);
                switch (result) {
                    case "101":
                        loginResult.setMessage("账号为空");
                        break;
                    case "102":
                        loginResult.setMessage("密码为空");
                        break;
                    case "104":
                        loginResult.setMessage("机器码为空");
                        break;
                    case "110":
                        loginResult.setMessage("账号或密码错误");
                        break;
                    case "108":
                        loginResult.setMessage("机器码不匹配");
                        break;
                    case "112":
                        loginResult.setMessage("禁止登录");
                        break;
                    default:
                        try {
                            LoginResultBean bean = CommonUtils.getGson().fromJson(result, LoginResultBean.class);
                            if (bean != null && bean.getVip() != null) {
                                loginResult.setSuccess(true);
                                loginResult.setMessage("登录成功");
                                loginResult.setToken(bean.getToken());
                                if (bean.getVip().equals("888888888") || bean.getVip().equals("999999999")) {
                                    loginResult.setExpirationDate(Const.ACCOUNT_EVER_VIP);
                                } else {
                                    loginResult.setExpirationDate(Long.parseLong(bean.getVip()) * 1000);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginResult;
    }

    @Override
    public CardCodeResult verifyCardCode(String username, String cardCode) {
        String requestBody = "username=" + username + "&kami=" + cardCode;
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, requestBody);
        final Request request = new Request.Builder()
                .url(CARD_CODE_URL)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        CardCodeResult result = new CardCodeResult();
        result.setSuccess(false);
        result.setMsg("使用失败");

        try {
            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String resultCode = response.body().string().substring(2);
                switch (resultCode) {
                    case "200":
                        result.setSuccess(true);
                        result.setMsg("卡密使用成功");
                        break;
                    case "101":
                        result.setSuccess(false);
                        result.setMsg("用户名为空");
                        break;
                    case "130":
                        result.setSuccess(false);
                        result.setMsg("卡密为空");
                        break;
                    case "131":
                        result.setSuccess(false);
                        result.setMsg("卡密有误");
                        break;
                    case "132":
                        result.setSuccess(false);
                        result.setMsg("卡密已被使用");
                        break;
                    case "151":
                        result.setSuccess(false);
                        result.setMsg("token已过期");
                        break;
                    case "134":
                        result.setSuccess(false);
                        result.setMsg("已是永久会员");
                        break;
                    case "135":
                        result.setSuccess(false);
                        result.setMsg("使用失败");
                        break;
                    default:
                        result.setSuccess(false);
                        result.setMsg("未知错误");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }
}
