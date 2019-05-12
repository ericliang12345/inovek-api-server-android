package bms.device.webapi.api.wifi;

import java.util.ArrayList;
import java.util.List;

import bms.device.webapi.api.Api;



final class ScanResult2 {
    String BSSID;
    String SSID;
    String capabilities;
    int frequency;
    int level;
    long timestamp;
}

final class ScanResultsResponse2 {
    Object[] results;

    ScanResultsResponse2(Object[] results) {
        this.results = results;
    }
}

public final class ApiScanResults extends Api {

    public static String name() {
        return "/wifi-test/scan_results";
    }

    @Override
    public boolean isAuthRequired() {
        return false;
    }


    @Override
    public Output execute(Action action, String uri, String json) {
        if (action != Action.READ) {
            return new Output(Result.BAD_REQUEST);
        }

        MyWiFiManager.getInstance().startWiFiScan();

        List<ScanResult> results = new ArrayList<>();

        try {
            Thread.sleep(10000); // wait 10 second
        } catch (InterruptedException e) {

        }


        for (android.net.wifi.ScanResult scanResult: MyWiFiManager.getInstance().getScanResults()) {
            ScanResult result = new ScanResult();
            result.BSSID = scanResult.BSSID;
            result.SSID = scanResult.SSID;
            result.capabilities = scanResult.capabilities;
            result.frequency = scanResult.frequency;
            result.level = scanResult.level;
            result.timestamp = scanResult.timestamp;

            results.add(result);
        }

        return new Output(Result.OK, gson.toJson(new ScanResultsResponse(results.toArray())));
    }
}
