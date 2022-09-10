package com.pxf.fftv.plus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PermissionActivity extends AppCompatActivity {

    private static Class mTargetActivityClass;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String REQUEST_PERMISSION = "request_permission";
    private static final int REQUEST_PERMISSION_CODE = 1000;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String[] requestPermissions = intent.getStringArrayExtra(REQUEST_PERMISSION);
        requestPermissions(requestPermissions, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "APP运行需要相应的权限", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
            Intent intent = new Intent(this, mTargetActivityClass);
            startActivity(intent);
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean needRequestPermission(Activity activity) {
        mTargetActivityClass = activity.getClass();
        ArrayList<String> requestPermissionsList = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionsList.add(permission);
            }
        }
        if (requestPermissionsList.isEmpty()) {
            return false;
        } else {
            requestPermission(activity, requestPermissionsList.toArray(new String[]{}));
            activity.finish();
            return true;
        }
    }

    private static void requestPermission(Context context, String[] permissions) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(REQUEST_PERMISSION, permissions);
        context.startActivity(intent);
    }
}

