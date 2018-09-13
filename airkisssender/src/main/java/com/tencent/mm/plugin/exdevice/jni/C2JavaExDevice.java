package com.tencent.mm.plugin.exdevice.jni;

import android.util.Log;

import cn.com.startai.airkisssender.StartaiAirkissManager;

/**
 * Created by Robin on 2018/4/25.
 * qq: 419109715 彬影
 */

public class C2JavaExDevice {

    private static final String TAG = "C2JavaExDevice";
    public static C2JavaExDevice mInstance = null;
    private static Object mLock = new byte[1];

    private OnAirKissListener listener;

    private void C2JavaExDevice() {
    }

    public static C2JavaExDevice getInstance() {
        if (mInstance == null)
            synchronized (mLock) {
                if (mInstance == null) {
                    C2JavaExDevice localC2JavaExDevice1 = new C2JavaExDevice();
                    mInstance = localC2JavaExDevice1;
                    return localC2JavaExDevice1;
                }
                C2JavaExDevice localC2JavaExDevice2 = mInstance;
                return localC2JavaExDevice2;
            }
        return mInstance;
    }

    public static void onAirKissCallback(int paramInt1, int paramInt2) {
        Log.i("C2JavaExDevice", "onAirKissCallback errType = " + paramInt1 + " errCode =" + paramInt2);
        if (getInstance().listener != null) {
            if (paramInt1 == 0) {
                getInstance().listener.onAirKissSuccess();
            } else {
                getInstance().listener.onAirKissFailed(paramInt2);
            }
        }
    }


    public void setAirKissListener(OnAirKissListener listener) {
        getInstance().listener = listener;
    }

    public static interface OnAirKissListener {
        void onAirKissSuccess();

        void onAirKissFailed(int error);
    }
}
