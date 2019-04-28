package bms.device.webapi.api.wifi;

import android.support.annotation.NonNull;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;



import java.util.List;

public final class MyWiFiManager {

    private static final int REQUEST_CODE_ASK = 1055;


    private static MyWiFiManager instance = null;
    public static MyWiFiManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyWiFiManager(context);
        }
        return instance;
    }

    public static MyWiFiManager getInstance() {
        if (instance == null) {
            throw new NullPointerException();
        }
        return instance;
    }

    private WifiManager wifiManager;

    private MyWiFiManager(Context context) {
        this.wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }



    List<ScanResult> getScanResults() {
        return wifiManager.getScanResults();
    }

    List<WifiConfiguration> getConfiguredNetworks() {
        return wifiManager.getConfiguredNetworks();
    }
}
