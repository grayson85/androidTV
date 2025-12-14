package com.pxf.fftv.plus.common;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.dnsoverhttps.DnsOverHttps;
import okhttp3.HttpUrl;

public class CommonUtils {

    public static int[] getScreenResolutions(Activity activity) {
        int[] resolutions = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        resolutions[0] = dm.widthPixels;
        resolutions[1] = dm.heightPixels;
        return resolutions;
    }

    public static int[] getGoneViewSize(View view) {
        int size[] = new int[2];
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        size[0] = view.getMeasuredWidth();
        size[1] = view.getMeasuredHeight();
        return size;
    }

    public static Gson getGson() {
        return new Gson();
    }

    public static OkHttpClient getOkHttpClient() {

        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                            throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                            throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(8000, TimeUnit.MILLISECONDS)
                .writeTimeout(8000, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

        // Configure DNS based on Preferences
        String dnsServer = com.pxf.fftv.plus.model.Model.getData()
                .getDnsServer(com.pxf.fftv.plus.FFTVApplication.getInstance());

        if ("8.8.8.8".equals(dnsServer)) {
            // Configure DNS over HTTPS (Google Public DNS)
            try {
                final DnsOverHttps dns = new DnsOverHttps.Builder().client(builder.build())
                        .url(HttpUrl.get("https://dns.google/dns-query"))
                        .bootstrapDnsHosts(InetAddress.getByName("8.8.8.8"), InetAddress.getByName("8.8.4.4"))
                        .build();

                builder.dns(new okhttp3.Dns() {
                    @Override
                    public java.util.List<InetAddress> lookup(String hostname) throws UnknownHostException {
                        // Fallback to system DNS for local emulator addresses to avoid NXDOMAIN from
                        // 8.8.8.8
                        if (hostname.equals("10.0.2.2") || hostname.equals("localhost")) {
                            return okhttp3.Dns.SYSTEM.lookup(hostname);
                        }
                        // Skip DoH for IP addresses - they don't need DNS resolution
                        // IP addresses will fail with NXDOMAIN if sent to DnsOverHttps
                        if (isIpAddress(hostname)) {
                            return java.util.Collections.singletonList(InetAddress.getByName(hostname));
                        }
                        try {
                            return dns.lookup(hostname);
                        } catch (UnknownHostException e) {
                            throw e;
                        }
                    }

                    private boolean isIpAddress(String host) {
                        // Simple check for IPv4 addresses (xxx.xxx.xxx.xxx format)
                        return host.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
                    }
                });
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        OkHttpClient client = builder.build();

        return client;
        /*
         * return new OkHttpClient.Builder()
         * .readTimeout(8000, TimeUnit.MILLISECONDS)
         * .writeTimeout(8000, TimeUnit.MILLISECONDS)
         * .sslSocketFactory(SSLCertificate.SSLSocketFactorygetSSLSocketFactory())
         * .hostnameVerifier(new HostnameVerifier() {
         * 
         * @Override
         * public boolean verify(String s, SSLSession sslSession) {
         * return true;
         * }
         * })
         * .build();
         * 
         */
    }

    public static Bitmap createQRCodeBitmap(String content, int width, int height) {
        return createQRCodeBitmap(content, width, height,
                "UTF-8", "H", "2", Color.BLACK, Color.WHITE);
    }

    public static Bitmap createQRCodeBitmap(String content, int width, int height,
            String character_set, String error_correction_level,
            String margin, int color_black, int color_white) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set);
            }
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
            }
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;// 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
