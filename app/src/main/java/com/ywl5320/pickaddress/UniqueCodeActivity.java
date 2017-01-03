package com.ywl5320.pickaddress;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UniqueCodeActivity extends Activity {

    StringBuilder deviceId;

    @Bind(R.id.btn_code)
    Button btnCode;
    @Bind(R.id.txt_code)
    TextView txtCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unique_code);
        ButterKnife.bind(this);
//        btnCode.setOnClickListener(this);
        initDate();
    }

    public void initDate() {
        deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        //The IMEI: 仅仅只对Android手机有效:
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        try {
            String imei = tm.getDeviceId();
            Log.e("IMEI------>", imei);
            addDeviceId(imei);
        } catch (Exception e) {
            Log.e("IMEI------>", e.getMessage());
        }
        //序列号（sn）
        try {
            String sn = tm.getSimSerialNumber();
            Log.e("SN------>", sn);
            addDeviceId(sn);
        } catch (Exception e) {
            Log.e("SN------>", e.getMessage());
        }
        //The Android ID
        try {
            String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.e("Android ID------>", m_szAndroidID);
            addDeviceId(m_szAndroidID);
        } catch (Exception e) {
            Log.e("Android ID------>", e.getMessage());
        }
        //Pseudo-Unique ID, 这个在任何Android手机中都有效
        try {
            String m_szDevIDShort = "35" + //we make this look like a valid IMEI

                    Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 +
                    Build.HOST.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 +
                    Build.TYPE.length() % 10 +
                    Build.USER.length() % 10; //13 digits
            Log.e("Pseudo-Unique ID------>", m_szDevIDShort);
            addDeviceId(m_szDevIDShort);
        } catch (Exception e) {
            Log.e("Pseudo-Unique ID------>", e.getMessage());
        }
        //The WLAN MAC Address string
        try {
            WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
            Log.e("WLAN MAC------>", m_szWLANMAC);
            addDeviceId(m_szWLANMAC);
        } catch (Exception e) {
            Log.e("WLAN MAC------>", e.getMessage());
        }
    }

    private void addDeviceId(String sz) {
        if (!TextUtils.equals(sz, "")) {
            deviceId.append(sz);
        }
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_code:
//                txtCode.setText(strMd5(deviceId.toString()));
//                break;
//            default:
//                break;
//        }
//    }

    //MD5编码 可产生32位的16进制数据:
    private String strMd5(String uniqueId) {
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(uniqueId.getBytes(), 0, uniqueId.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;
    }

    @OnClick(R.id.btn_code)
    public void onClick() {
        txtCode.setText(strMd5(deviceId.toString()));
    }

}
