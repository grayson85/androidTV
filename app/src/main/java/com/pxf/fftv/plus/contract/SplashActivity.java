package com.pxf.fftv.plus.contract;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.contract.home.HomeActivity;

/**
 * 启动画面 - 显示动画logo和应用名称
 * 适用于手机和电视
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5秒

    private LottieAnimationView lottieView;
    private TextView appNameText;
    private View logoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 隐藏系统UI实现全屏
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        lottieView = findViewById(R.id.splash_lottie);
        appNameText = findViewById(R.id.splash_app_name);
        logoContainer = findViewById(R.id.splash_logo_container);

        // 启动动画
        startAnimations();

        // 延迟后跳转到主页
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            // 添加过渡动画
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DURATION);
    }

    private void startAnimations() {
        // Lottie动画自动播放
        lottieView.playAnimation();

        // Logo容器缩放 + 渐入动画
        AnimationSet logoAnim = new AnimationSet(true);

        ScaleAnimation scaleAnim = new ScaleAnimation(
                0.5f, 1.0f, 0.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(800);

        AlphaAnimation alphaAnim = new AlphaAnimation(0f, 1f);
        alphaAnim.setDuration(800);

        logoAnim.addAnimation(scaleAnim);
        logoAnim.addAnimation(alphaAnim);
        logoContainer.startAnimation(logoAnim);

        // 文字延迟渐入动画
        appNameText.setAlpha(0f);
        appNameText.animate()
                .alpha(1f)
                .setStartDelay(500)
                .setDuration(800)
                .start();
    }

    @Override
    public void onBackPressed() {
        // 禁用返回键
    }
}
