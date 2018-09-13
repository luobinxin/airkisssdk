package cn.com.startai.testairkiss;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.plugin.exdevice.jni.C2JavaExDevice;
import com.tencent.mm.plugin.exdevice.jni.Java2CExDevice;

import java.util.List;

import cn.com.startai.airkisssender.StartaiAirkissManager;
import cn.com.startai.testairkiss.utils.NetworkUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private String ssid;

    private TextView tvSSID;

    private EditText etPwd;

    private LocalBroadcastManager mBraodcastManager;
    private Button btSend;
    private Button btCancel;

    //    private Handler mHandler;
    private long t;
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvSSID = findViewById(R.id.tv_ssid);
        etPwd = findViewById(R.id.et_password);
        btSend = findViewById(R.id.bt_send);
        btCancel = findViewById(R.id.bt_cancel);

        tvLog = findViewById(R.id.tv_log);
        tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());

//        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//
//                Log.i(TAG, "what = " + msg.what + " arg1 = " + msg.arg1 + " arg2 = " + msg.arg2);
//
//                switch (msg.what) {
//                    case 1:
//
//                        if (msg.arg1 == 0) {
//                            tvLog.append("\n配置成功 用时 " + ((System.currentTimeMillis() - t) / 1000) + " s");
//                        } else {
//                            int errorCode = msg.arg2;
//                            tvLog.append("\n配置失败 errorCode = " + errorCode);
//                            Java2CExDevice.stopAirKiss();
//                        }
//
//                        break;
//
//                    default:
//                        break;
//                }
//
//                return false;
//            }
//        });
//        C2JavaExDevice.getInstance().setHandler(mHandler);
        StartaiAirkissManager.getInstance().setAirKissListener(new C2JavaExDevice.OnAirKissListener() {
            @Override
            public void onAirKissSuccess() {
                appendLog("配置成功 用时 " + ((System.currentTimeMillis() - t) / 1000) + " s\n");
            }

            @Override
            public void onAirKissFailed(int error) {
                appendLog("配置失败 errorCode = " + error + "\n");
                StartaiAirkissManager.getInstance().stopAirKiss();
            }
        });


        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isWifi = NetworkUtils.isWIFI(getApplicationContext());
                if (!isWifi) {
                    appendLog("请先打开wifi");
                    return;
                }

                int security = SECURITY_NONE;

                WifiManager mWifiManager = (WifiManager) getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = mWifiManager.getConnectionInfo();
                // 得到配置好的网络连接
                List<WifiConfiguration> wifiConfigList = mWifiManager.getConfiguredNetworks();
                //判断 当前连接的网络的加密码类型 如果 是OPEN 则密码可以为空
                for (WifiConfiguration wifiConfiguration : wifiConfigList) {
                    if (wifiConfiguration == null) {
                        continue;

                    }
                    //配置过的SSID
                    String configSSid = wifiConfiguration.SSID;
                    if (TextUtils.isEmpty(configSSid)) {
                        continue;

                    }
                    configSSid = configSSid.replace("\"", "");

                    //当前连接SSID
                    String currentSSid = info.getSSID();
                    currentSSid = currentSSid.replace("\"", "");

                    //比较networkId，防止配置网络保存相同的SSID
                    if (currentSSid.equals(configSSid) && info.getNetworkId() == wifiConfiguration.networkId) {

                        security = getSecurity(wifiConfiguration);
                        Log.e("river", "当前网络安全性：" + security);
                        break;
                    }
                }

                int freq = 0;
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    freq = wifiInfo.getFrequency();
                } else {
                    String ssid = wifiInfo.getSSID();
                    if (ssid != null && ssid.length() > 2) {
                        String ssidTemp = ssid.substring(1, ssid.length() - 1);
                        List<ScanResult> scanResults = mWifiManager.getScanResults();
                        for (ScanResult scanResult : scanResults) {
                            if (scanResult.SSID.equals(ssidTemp)) {
                                freq = scanResult.frequency;
                                break;
                            }
                        }
                    }
                }

                if (freq > 4900 && freq < 5900) {
                    appendLog("暂不支持5G网络");
                    return;
                }


                String ssid = tvSSID.getText().toString();
                String pwd = etPwd.getText().toString();
                String aesKey = "";
                int processPeroid = 0;
                int datePeroid = 5;


                //如果需要密码却没有密码 提示需要密码
                if (security != SECURITY_NONE && TextUtils.isEmpty(pwd)) {
                    appendLog("密码不能为空");
                    return;
                }


                t = System.currentTimeMillis();
                tvLog.append("\n开始配置... \nssid = " + ssid + " \npwd = " + pwd);

                long timeout = 1000 * 90;
                StartaiAirkissManager.getInstance().startAirKiss(pwd, ssid, aesKey.getBytes(), timeout, processPeroid, datePeroid);


            }
        });


        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Java2CExDevice.stopAirKiss();

                tvLog.append("\n停止配置");
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume");


        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mWifiReceiver, filter);


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        unregisterReceiver(mWifiReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            //wifi连接上与否
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    //已断开
                    ssid = "";
                    Toast.makeText(getApplicationContext(), "wifi已断开", Toast.LENGTH_SHORT).show();
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    //已连接
                    ssid = NetworkUtils.getWifiSSID(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "wifi已经连接", Toast.LENGTH_SHORT).show();
                }
            } else {
                ssid = "";
            }

            tvSSID.setText(ssid);

        }

    };

    private String TAG = this.getClass().getSimpleName();

    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;

    static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    private void appendLog(final String log) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                tvLog.append("\n" + log);

            }
        });

    }

}

