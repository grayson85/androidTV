package com.pxf.fftv.plus.contract;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import static com.pxf.fftv.plus.Const.REQUEST_CODE_INSTALL_UNKNOWN_APK;

public class UpdateActivity extends AppCompatActivity {

    View update_root;

    TextView update_tv_title;

    TextView update_tv_message;

    View update_root_download_progress;

    SeekBar update_seek_bar_download_progress;

    TextView update_tv_download_progress;

    TextView update_tv_download_title;

    View update_tv_not_update;

    private String apkUrl;

    private boolean forceUpdate = false;

    // Use app private directory for Android 10+ Scoped Storage compatibility
    private File downloadDir;
    private File apkFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        update_root = findViewById(R.id.update_root);
        update_tv_title = findViewById(R.id.update_tv_title);
        update_tv_message = findViewById(R.id.update_tv_message);
        update_root_download_progress = findViewById(R.id.update_root_download_progress);
        update_seek_bar_download_progress = findViewById(R.id.update_seek_bar_download_progress);
        update_tv_download_progress = findViewById(R.id.update_tv_download_progress);
        update_tv_download_title = findViewById(R.id.update_tv_download_title);
        update_tv_not_update = findViewById(R.id.update_tv_not_update);

        findViewById(R.id.update_tv_now_update).setOnClickListener(v -> onNowUpdate());
        update_tv_not_update.setOnClickListener(v -> onNotUpdate());
        findViewById(R.id.update_tv_now_update_manual).setOnClickListener(v -> onUpdateManualClick());

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
        forceUpdate = event.isForceUpdate();

        // 强制更新时隐藏"暂不更新"按钮
        if (forceUpdate) {
            update_tv_not_update.setVisibility(View.GONE);
        }

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onBackPressed() {
        // 强制更新时禁止返回
        if (forceUpdate) {
            Toast.makeText(this, "请先更新应用", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    public void onNowUpdate() {
        update_tv_download_title.setText("正在下载更新...");
        update_root_download_progress.setVisibility(View.VISIBLE);
        downloadApp(apkUrl);
    }

    public void onNotUpdate() {
        finish();
    }

    public void onUpdateManualClick() {
        Intent intent = new Intent(this, QrCodeActivity.class);
        startActivity(intent);

        EventBus.getDefault().postSticky(new QrCodeEvent(apkUrl, "请扫描二维码获取最新APK"));
    }

    private void downloadApp(String url) {
        // Use app private directory for Android 10+ Scoped Storage compatibility
        downloadDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadDir == null) {
            downloadDir = getFilesDir(); // Fallback to internal storage
        }

        if (!downloadDir.exists()) {
            boolean created = downloadDir.mkdirs();
            android.util.Log.d("UpdateActivity",
                    "Created directory: " + created + " path: " + downloadDir.getAbsolutePath());
        }

        apkFile = new File(downloadDir, "update.apk");
        if (apkFile.exists()) {
            apkFile.delete();
        }

        String downloadPath = apkFile.getAbsolutePath();
        android.util.Log.d("UpdateActivity", "Download path: " + downloadPath);

        final int maxProgress = 100;
        android.util.Log.d("UpdateActivity", "Starting download from: " + url);

        DownloadManager.getInstance().startTask(new DownloadTask(
                String.valueOf(System.currentTimeMillis()), url, downloadPath,
                new DownloadManager.DownloadListener() {
                    @Override
                    public void onConnectedFail(int responseCode) {
                        android.util.Log.e("UpdateActivity", "Connection failed: " + responseCode);
                        Toast.makeText(UpdateActivity.this,
                                "连接失败，错误码: " + responseCode, Toast.LENGTH_LONG).show();
                        update_tv_download_title.setText("下载失败");
                    }

                    @Override
                    public void onStarted(long completedSize, long totalSize) {
                        android.util.Log.d("UpdateActivity", "Download started: " + completedSize + "/" + totalSize);
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
                        android.util.Log.e("UpdateActivity", "Download error: " + error, e);
                        Toast.makeText(UpdateActivity.this,
                                "下载错误: " + error, Toast.LENGTH_LONG).show();
                        update_tv_download_title.setText("下载失败");
                    }
                }));
    }

    private void installApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ModernAlertDialog)
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

        // Use the apkFile instance variable that was set in downloadApp()
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(this, "com.pxf.fftv.plus.fileprovider", apkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }

        startActivity(intent);
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
