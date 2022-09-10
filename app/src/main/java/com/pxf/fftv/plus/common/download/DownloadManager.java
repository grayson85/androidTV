package com.pxf.fftv.plus.common.download;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadManager {

    private static final String TAG = "DownloadManager";

    private HashMap<String, DownloadTask> taskMap = new HashMap<>();

    private HashMap<String, Call> taskCallMap = new HashMap<>();

    private OkHttpClient client;

    private volatile static DownloadManager mInstance;

    public interface DownloadListener {

        void onConnectedFail(int responseCode);

        void onStarted(long completedSize, long totalSize);

        void onDownloading(long completedSize, long totalSize);

        void onPaused();

        void onResumed();

        void onFinished();

        void onError(@Nullable Exception e, String error);
    }

    private DownloadManager(){}

    public static DownloadManager getInstance(){
        if(mInstance == null){
            synchronized(DownloadManager.class){
                if(mInstance == null){
                    mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }


    public void startTask(final DownloadTask task) {
        if (taskMap.containsKey(task.getTaskId())) {
            cancelTask(task.getTaskId());
        }

        taskMap.put(task.getTaskId(), task);

        Request request = new Request.Builder()
                .url(task.getUrl())
                .build();

        // 获取文件大小
        getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                updateUI(new Action() {
                    @Override
                    public void run() throws Exception {
                        task.getListener().onError(e, "startTask error");
                    }
                });
                taskMap.remove(task.getTaskId());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                if (response.isSuccessful()) {
                    task.setTotalSize(response.body().contentLength());

                    preDownload(task);
                } else {
                    updateUI(new Action() {
                        @Override
                        public void run() throws Exception {
                            task.getListener().onConnectedFail(response.code());
                        }
                    });
                    taskMap.remove(task.getTaskId());
                }
            }
        });
    }

    public void pauseTask(final String taskId) {
        try {
            taskCallMap.get(taskId).cancel();
            final DownloadTask task = taskMap.get(taskId);
            updateUI(new Action() {
                @Override
                public void run() throws Exception {
                    task.getListener().onPaused();
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "pauseTask error " + e.toString(), e);
        } finally {
            taskCallMap.remove(taskId);
        }
    }

    public void resumeTask(final String taskId) {
       final DownloadTask task = taskMap.get(taskId);
       if (task != null) {
           updateUI(new Action() {
               @Override
               public void run() throws Exception {
                   task.getListener().onResumed();
                   Observable.create(new ObservableOnSubscribe() {
                       @Override
                       public void subscribe(ObservableEmitter emitter) throws Exception {
                           preDownload(task);
                       }
                   }).observeOn(Schedulers.newThread()).subscribe();
               }
           });
       } else {
           Log.d(TAG, "resumeTask error / the task is null");
       }
    }

    public void cancelTask(String taskId) {
        try {
            taskCallMap.get(taskId).cancel();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            taskCallMap.remove(taskId);
            taskMap.remove(taskId);
        }
    }

    @Nullable
    public DownloadTask getTask(String taskId) {
        return taskMap.get(taskId);
    }

    public int getTaskMapSize() {
        return taskMap.size();
    }

    private OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }

    private void updateUI(Action action) {
        Observable.empty().observeOn(AndroidSchedulers.mainThread()).doOnComplete(action).subscribe();
    }

    private void preDownload(final DownloadTask task) {
        File file = new File(task.getPath());

        if (file.exists()) {
            FileInputStream in = null;
            FileChannel channel = null;
            try {
                in = new FileInputStream(file);
                channel = in.getChannel();
                executeDownload(task, channel.size());
            } catch (final Exception e) {
                e.printStackTrace();
                updateUI(new Action() {
                    @Override
                    public void run() throws Exception {
                        task.getListener().onError(e, "preDownload error");
                    }
                });
                taskMap.remove(task.getTaskId());
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (channel != null) {
                        channel.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            executeDownload(task, 0);
        }
    }

    private void executeDownload(final DownloadTask task, final long start) {
        updateUI(new Action() {
            @Override
            public void run() throws Exception {
                task.getListener().onStarted(start, task.getTotalSize());
            }
        });

        Request request = new Request.Builder()
                .url(task.getUrl())
                .header("RANGE", "bytes=" + start + "-")
                .build();

        Call call = getClient().newCall(request);
        taskCallMap.put(task.getTaskId(), call);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                updateUI(new Action() {
                    @Override
                    public void run() throws Exception {
                        task.getListener().onError(e, "executeDownload error");
                    }
                });
                taskMap.remove(task.getTaskId());
                taskCallMap.remove(task.getTaskId());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                FileOutputStream out = null;
                InputStream in = null;
                byte[] buffer = new byte[2048];
                try {
                    in = response.body().byteStream();
                    File file = new File(task.getPath());
                    out = new FileOutputStream(file, true);

                    long sum = 0;
                    int len;

                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        sum += len;

                        final long completedSize = sum + start;

                        task.setCompletedSize(completedSize);
                        updateUI(new Action() {
                            @Override
                            public void run() throws Exception {
                                task.getListener().onDownloading(completedSize, task.getTotalSize());
                            }
                        });

                        // 防止len无法获取-1来结束造成异常，使用这种方式判断结束下载
                        if (completedSize == task.getTotalSize()) {
                            break;
                        }
                    }
                    taskMap.remove(task.getTaskId());
                    taskCallMap.remove(task.getTaskId());

                    updateUI(new Action() {
                        @Override
                        public void run() throws Exception {
                            task.getListener().onFinished();
                        }
                    });
                    out.flush();
                } catch (final SocketException e) {
                    Log.d(TAG, "Task has been canceled");
                    updateUI(new Action() {
                        @Override
                        public void run() throws Exception {
                            task.getListener().onError(e, "Task has been canceled");
                        }
                    });
                } catch (final Exception e) {
                    updateUI(new Action() {
                        @Override
                        public void run() throws Exception {
                            task.getListener().onError(e, "executeDownload error2");
                        }
                    });
                    taskMap.remove(task.getTaskId());
                    taskCallMap.remove(task.getTaskId());
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
