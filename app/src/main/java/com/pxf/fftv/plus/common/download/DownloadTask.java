package com.pxf.fftv.plus.common.download;


import androidx.annotation.NonNull;

public class DownloadTask {

    private long completedSize;

    private long totalSize;

    private String taskId;

    private String url;

    private String path;

    private DownloadManager.DownloadListener listener;

    public DownloadTask(@NonNull String taskId, @NonNull String url,
                        @NonNull String path, @NonNull DownloadManager.DownloadListener listener) {
        this.taskId = taskId;
        this.url = url;
        this.path = path;
        this.listener = listener;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getCompletedSize() {
        return completedSize;
    }

    public void setCompletedSize(long completedSize) {
        this.completedSize = completedSize;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public DownloadManager.DownloadListener getListener() {
        return listener;
    }
}
