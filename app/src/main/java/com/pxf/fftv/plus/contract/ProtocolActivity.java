package com.pxf.fftv.plus.contract;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.pxf.fftv.plus.R;

public class ProtocolActivity extends AppCompatActivity {

    TextView protocol_title;

    TextView protocol_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        protocol_title = findViewById(R.id.protocol_title);
        protocol_content = findViewById(R.id.protocol_content);
        initView();
    }

    private void initView() {
        protocol_title.setText(getString(R.string.app_name) + "软件许可和服务协议");
        protocol_content.setText(getString(R.string.protocal, getString(R.string.app_name)));
    }
}