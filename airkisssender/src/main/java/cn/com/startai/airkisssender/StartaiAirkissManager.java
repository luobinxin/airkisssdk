package cn.com.startai.airkisssender;

import com.tencent.mm.plugin.exdevice.jni.C2JavaExDevice;
import com.tencent.mm.plugin.exdevice.jni.Java2CExDevice;

/**
 * Created by Robin on 2018/9/10.
 * qq: 419109715 彬影
 */

public class StartaiAirkissManager {
    private static final StartaiAirkissManager ourInstance = new StartaiAirkissManager();

    public static StartaiAirkissManager getInstance() {
        return ourInstance;
    }

    private StartaiAirkissManager() {
    }


    public void startAirKiss(String pwd, String ssid, byte[] aesKey, long timeout, int processPeroid, int datePeroid) {
        Java2CExDevice.startAirKissWithInter(pwd, ssid, aesKey, timeout, processPeroid, datePeroid);
    }


    public void setAirKissListener(C2JavaExDevice.OnAirKissListener listener) {
        C2JavaExDevice.getInstance().setAirKissListener(listener);
    }

    public void stopAirKiss() {
        Java2CExDevice.stopAirKiss();
    }

}
