package com.pxf.fftv.plus.contract;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.download.DownloadManager;
import com.pxf.fftv.plus.common.download.DownloadTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pxf.fftv.plus.Const.EXTERNAL_FILE_PATH;
import static com.pxf.fftv.plus.Const.EXTERNAL_FILE_UPDATE_APK_PATH;
import static com.pxf.fftv.plus.Const.REQUEST_CODE_INSTALL_UNKNOWN_APK;

public class UpdateActivity extends AppCompatActivity {

    @BindView(R.id.update_root)
    View update_root;

    @BindView(R.id.update_tv_title)
    TextView update_tv_title;

    @BindView(R.id.update_tv_message)
    TextView update_tv_message;

    @BindView(R.id.update_root_download_progress)
    View update_root_download_progress;

    @BindView(R.id.update_seek_bar_download_progress)
    SeekBar update_seek_bar_download_progress;

    @BindView(R.id.update_tv_download_progress)
    TextView update_tv_download_progress;

    @BindView(R.id.update_tv_download_title)
    TextView update_tv_download_title;

    private String apkUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        // 使Activity全屏
        ViewGroup.LayoutParams params = update_root.getLayoutParams();
        params.width = CommonUtils.getScreenResolutions(this)[0];
        params.height = CommonUtils.getScreenResolutions(this)[1];
        update_root.setLayoutParams(params);
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
    public void onUpdateEvent(UpdateEvent event) {
        update_tv_title.setText(event.getTitle());
        update_tv_message.setText(event.getMessage());
        apkUrl = event.getApkUrl();
        EventBus.getDefault().removeStickyEvent(event);
    }

    @OnClick(R.id.update_tv_now_update)
    public void onNowUpdate() {
        update_tv_download_title.setText("正在下载更新...");
        update_root_download_progress.setVisibility(View.VISIBLE);
        downloadApp(apkUrl);
    }

    @OnClick(R.id.update_tv_not_update)
    public void onNotUpdate() {
        finish();
    }

    @OnClick(R.id.update_tv_now_update_manual)
    public void onUpdateManualClick() {
        Intent intent = new Intent(this, QrCodeActivity.class);
        startActivity(intent);

        EventBus.getDefault().postSticky(new QrCodeEvent(apkUrl, "请扫描二维码获取最新APK"));
    }

    private void downloadApp(String url) {
        File rootFile = new File(EXTERNAL_FILE_PATH);
        if (!rootFile.exists()) {
            rootFile.mkdir();
        }

        File updateApk = new File(EXTERNAL_FILE_UPDATE_APK_PATH);
        if (updateApk.exists()) {
            updateApk.delete();
        }

        final int maxProgress = 100;
        DownloadManager.getInstance().startTask(new DownloadTask(
                String.valueOf(System.currentTimeMillis()), url, EXTERNAL_FILE_UPDATE_APK_PATH, new DownloadManager.DownloadListener() {
            @Override
            public void onConnectedFail(int responseCode) {

            }

            @Override
            public void onStarted(long completedSize, long totalSize) {
            }

            @Override
            public void onDownloading(long completedSize, long totalSize) {
                int progress = (int) (completedSize * maxProgress / totalSize);
                update_seek_bar_download_progress.setProgress(progress);
                update_tv_download_progress.setText(progress + "%");
            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onResumed() {

            }

            @Override
            public void onFinished() {
                update_tv_download_title.setText("下载完成");
                installApp();
            }

            @Override
            public void onError(@Nullable Exception e, String error) {

            }
        }
        ));
    }

    private void installApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setMessage("更新应用需要允许安装未知应用的权限")
                        .setPositiveButton("去授予权限", (dialog, which) -> {
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                            startActivityForResult(intent, 1);
                        })
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
                builder.create().show();
                return;
            }
        }

        File apkFile = new File(EXTERNAL_FILE_UPDATE_APK_PATH);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(this, "com.pxf.fftv.plus.fileprovider", apkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }

        getApplicationContext().startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_INSTALL_UNKNOWN_APK:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!getPackageManager().canRequestPackageInstalls()) {
                        // 如果用户没有给予权限，更新失败提示
                        Toast.makeText(this, "更新应用需要允许安装未知应用的权限", Toast.LENGTH_LONG).show();
                    } else {
                        installApp();
                    }
                }
                break;
            default:
                break;
        }
    }
}
