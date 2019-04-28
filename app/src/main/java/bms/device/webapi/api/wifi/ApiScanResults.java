package bms.device.webapi.api.wifi;

import java.util.ArrayList;
import java.util.List;

import bms.device.webapi.api.Api;

final class ScanResult {
    String BSSID;
    String SSID;
    String capabilities;
    int frequency;
    int level;
    long timestamp;
}

final class ScanResultsResponse {
    Object[] results;

    ScanResultsResponse(Object[] results) {
        this.results = results;
    }
}

public final class ApiScanResults extends Api {

    public static String name() {
        return "/wifi/scan_results";
    }


    @Override
    public Output execute(Action action, String uri, String json) {
        if (action != Action.READ) {
            return new Output(Result.BAD_REQUEST);
        }

        List<ScanResult> results = new ArrayList<>();
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
