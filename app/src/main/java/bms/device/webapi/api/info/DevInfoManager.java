package bms.device.webapi.api.info;

import android.content.Context;


final class PublicInfo {
    String model_id   = "";
    String model_name = "Inovek-100"; // Public only
    String serial_number = "";
    String player_name = "My customized name"; // Public only
    String fw_version = "";
    String restful_api_version = "v1"; // Public only

    PublicInfo() {

    }
}

final class DevInfo {
    String fw_version          = "v1.0.1";
    String restful_api_version = "1.0.1";
    String os_type             = "Android";
    String model_id            = "Inovek-100";
    String ip_address          = "192.168.1.41";
    String up_time             = "0:37:04";
    int    available_storage_size_mb = 6000;
    int    total_storage_size_mb     = 6283;
    int    available_memory_size_mb  = 1575;
    int    total_memory_size_mb      = 2048;
    String cpu_usage_percent   = "0.7%";
    String screen_resolution   = "1920x1080";
    String serial_number       = "04Z001T100461";
    String eth_mac             = "18:65:71:0b:e5:38";
    String wifi_mac            = "18:65:71:0b:e5:36";
    String device_uuid         = "cc009f47-5a8d-42b4-af5a-1865710be538";
    String wifi_ip             = "0.0.0.0";
    String ethernet_ip         = "192.168.1.41";

    DevInfo() {

    }
}

/*
 *    { "results":{ "item1":"data1", "item2":2 } }
 *
 * */
final class GetJSONResultsResponse {
    Object results;

    GetJSONResultsResponse(Object result) {
        this.results = result;
    }
}


public class DevInfoManager {

    private static DevInfoManager instance = null;
    public static DevInfoManager getInstance(Context context) {
        if (instance == null) {
            instance = new DevInfoManager(context);
        }
        return instance;
    }

    public static DevInfoManager getInstance() {
        if (instance == null) {
            throw new NullPointerException();
        }
        return instance;
    }


    private PublicInfo mPublicInfo;
    private DevInfo mDevInfo;

    private DevInfoManager(Context context) {
        // To get model id, serial number, storage size: available/total/
        initDevInfo();
    }

    private void initDevInfo(){

        mDevInfo = new DevInfo();
        // Not Finish => To get dev info (eg. serial number, platform name ... from eeprom )

        mPublicInfo = new PublicInfo();

        mPublicInfo.model_id   = mDevInfo.model_id;
        mPublicInfo.serial_number = mDevInfo.serial_number;
        mPublicInfo.fw_version = mDevInfo.fw_version;
    }

    public DevInfo getDevInfo(){

        return mDevInfo;
    }

    public PublicInfo getPublicInfo() {
        return mPublicInfo;
    }




}
