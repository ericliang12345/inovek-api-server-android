package bms.device.webapi.api.wifi;

import android.support.annotation.NonNull;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;







import java.util.List;

public final class MyWiFiManager {

    private Context context;

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
    private List<ScanResult> mScanResultList;

    private MyWiFiManager(Context context) {

        this.context = context;

        this.wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    public void startWiFiScan() {

        // Register the Receiver in some part os fragment...
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiScanReceive();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();

        // Inside the receiver:
    }

    private void wifiScanReceive(){
        // the result.size() is 0 after update to Android v6.0, same code working in older devices.
        mScanResultList =  wifiManager.getScanResults();

    }

    boolean getWiFiState() { return wifiManager.isWifiEnabled(); }


    List<ScanResult> getScanResults() {
        return mScanResultList;  //wifiManager.getScanResults();
    }

    List<WifiConfiguration> getConfiguredNetworks() {
        return wifiManager.getConfiguredNetworks();
    }
}
