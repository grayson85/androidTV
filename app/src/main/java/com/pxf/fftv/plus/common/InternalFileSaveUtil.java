package com.pxf.fftv.plus.common;

import android.content.Context;
import android.util.Log;

import com.pxf.fftv.plus.Const;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class InternalFileSaveUtil {

    private static String internalSavePath;

    private volatile static InternalFileSaveUtil mInstance;

    public static InternalFileSaveUtil getInstance(Context context) {
        internalSavePath = context.getFilesDir().getAbsolutePath();
        if (mInstance == null) {
            synchronized (InternalFileSaveUtil.class) {
                if (mInstance == null) {
                    mInstance = new InternalFileSaveUtil();
                }
            }
        }
        return mInstance;
    }

    public boolean put(String key, Serializable object) {
        String path = internalSavePath + "/" + key;
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(object);
        } catch (Exception e) {
            Log.e(Const.LOG_TAG, "[InternalFileSaveUtil]put LinkedHashSet<Object> error", e);
            return false;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public Serializable get(String key) {
        String path = internalSavePath + "/" + key;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        Serializable result;

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            result = (Serializable) ois.readObject();
        } catch (Exception e) {
            Log.e(Const.LOG_TAG, "[InternalFileSaveUtil]get error", e);
            return null;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public boolean deleteFile(String key) {
        String path = internalSavePath + "/" + key;
        File file = new File(path);
        return file.exists() && file.delete();
    }
}
