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

    // Get required permissions based on Android version
    private static String[] getRequiredPermissions() {
        ArrayList<String> permissions = new ArrayList<>();

        // Phone state permission - always needed
        permissions.add(Manifest.permission.READ_PHONE_STATE);

        // Storage permissions - only needed on Android 9 (API 28) and below
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            // Android 10-12: READ_EXTERNAL_STORAGE might still be useful
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        // Android 13+: No storage permission needed (uses Media Store or app-specific
        // storage)

        return permissions.toArray(new String[0]);
    }

    private static final String REQUEST_PERMISSION = "request_permission";
    private static final int REQUEST_PERMISSION_CODE = 1000;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String[] requestPermissions = intent.getStringArrayExtra(REQUEST_PERMISSION);
        if (requestPermissions != null && requestPermissions.length > 0) {
            requestPermissions(requestPermissions, REQUEST_PERMISSION_CODE);
        } else {
            // No permissions needed, go to target activity
            Intent targetIntent = new Intent(this, mTargetActivityClass);
            startActivity(targetIntent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                // Show message but still allow app to run (non-critical permissions)
                Toast.makeText(this, "部分权限未授予，某些功能可能受限", Toast.LENGTH_LONG).show();
            }

            // Always proceed to target activity
            Intent intent = new Intent(this, mTargetActivityClass);
            startActivity(intent);
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean needRequestPermission(Activity activity) {
        mTargetActivityClass = activity.getClass();
        ArrayList<String> requestPermissionsList = new ArrayList<>();

        for (String permission : getRequiredPermissions()) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionsList.add(permission);
            }
        }

        if (requestPermissionsList.isEmpty()) {
            return false;
        } else {
            requestPermission(activity, requestPermissionsList.toArray(new String[] {}));
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
